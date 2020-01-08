package print.print;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.*;
import model.Model_Table;
import model.Order;
import model.Payment_Method;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.awt.print.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Elbert Utama on 09/05/2017.
 */
public class PrintBon implements Printable {

    private ArrayList<Model_Table> listModelTable;

    private double total;

    private double discount;

    private double grandtotal;

    private String customerid;

    private Order order;

    private DBHelper dbHelper = DBHelperSingleton.getInstance();
    private PaymentMethodDatabaseUtils paymentMethodDatabaseUtils = new PaymentMethodDatabaseUtils(dbHelper);
    private CategoryDatabaseUtils categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);
    private OrderDatabaseUtils orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
    private OrderDetailDatabaseUtils orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
    private MenuDatabaseUtils menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);

    public java.util.List<String> getPrinters(){

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);

        java.util.List<String> printerList = new ArrayList<>();
        for(PrintService printerService: printServices){
            printerList.add( printerService.getName());
        }

        return printerList;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

/*
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
*/

        return PAGE_EXISTS;
    }

    private PrintService findPrintService(String printerName,
                                          PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }




    public void printString(String printerName, String text) {

        // find the printService of name printerName
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        DocPrintJob job = service.createPrintJob();

        try {

            byte[] bytes;

            // important for umlaut chars
            bytes = text.getBytes("CP437");

            Doc doc = new SimpleDoc(bytes, flavor, null);


            job.print(doc, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void printBytes(String printerName, byte[] bytes) {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        DocPrintJob job = service.createPrintJob();

        try {

            Doc doc = new SimpleDoc(bytes, flavor, null);

            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static double fromCMToPPI(double cm) {
        return toPPI(cm * 0.393700787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }




    public void displayDialog()
    {
        PrinterJob pj = PrinterJob.getPrinterJob();

        if (pj.printDialog()) {
            PageFormat pf = pj.defaultPage();
            Paper paper = pf.getPaper();
            double width = fromCMToPPI(pf.getWidth());
            double height = fromCMToPPI(pf.getHeight());
            paper.setSize(width, height);
/*            paper.setImageableArea(
                fromCMToPPI(1),
                fromCMToPPI(0.5),
                width - fromCMToPPI(0.35),
                height - fromCMToPPI(1));*/

            //pf.setOrientation(PageFormat.LANDSCAPE);
            pf.setPaper(paper);
            pj.setPrintable(this, pf);

            //save settings
            PrinterSetting printerSetting = new PrinterSetting(pj.getPrintService().getName(), pf);
            try {
                printerSetting.writeSettingsFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static PrintService findPrintService(String printerName) {

        printerName = printerName.toLowerCase();

        PrintService service = null;

        // Get array of all print services
        PrintService[] services = PrinterJob.lookupPrintServices();

        // Retrieve a print service from the array
        for (int index = 0; service == null && index < services.length; index++) {

            if (services[index].getName().toLowerCase().indexOf(printerName) >= 0) {
                service = services[index];
            }
        }

        // Return the print service
        return service;
    }

    public void setData(ArrayList<Model_Table> listModelTable, double total, double discount, double grandtotal, String customerid, Order order) {
        this.listModelTable = listModelTable;
        this.total = total;
        this.discount = discount;
        this.grandtotal = grandtotal;
        this.customerid = customerid;
        this.order = order;
    }

    public void doprintbon()
    {

        try {
            PrinterSetting printerSetting = PrinterSetting.loadSettingsFile();

            PrintService service = findPrintService(printerSetting.getPrinterName());
            PrinterJob pj = PrinterJob.getPrinterJob();
            PageFormat pf = pj.defaultPage(printerSetting.getPageFormat());
            pj.setPrintable(this);


            double subtotal;

            Calendar date = new GregorianCalendar();
            Date currdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss", Locale.UK);


            String print = "  MEGA KITCHEN\n"
                    + "  phone  : 0216521917\n"
                    + "  alamat : Jl Danau Sunter Utara B1a no 10\n\n"
                    + "  " + sdf.format(currdate) + "\n\n"
                    + "  Customer : " + customerid + "\n\n"
                    + "  Payment Method : " + paymentMethodDatabaseUtils.getPaymentMethodById(order.getPayment_method_id()).getName() + "\n\n"
                    + " ------------------------------------------\n\n";


            for(int i = 0; i < listModelTable.size(); i++) {

                print += "  " + listModelTable.get(i).getName() + "\n";
                print += String.format("%-30s","  " + String.valueOf(listModelTable.get(i).getQty()) + " * " + validatePriceFormat(listModelTable.get(i).getPrice()));
                subtotal = listModelTable.get(i).getQty() * listModelTable.get(i).getPrice();
                print += validatePriceFormat(subtotal) + "\n\n";
            }


            /*
            print += String.format("%-30s","  Total");
            print += validatePriceFormat(total) + "\n";
            print += String .format("%-30s","  Discount");
            print += validatePriceFormat(discount) + "\n";
            */
            print += String .format("%-30s","  Grand Total");
            print += validatePriceFormat(grandtotal) + "\n";
            print += "\n\n\n\n\n";

            System.out.println(print);

            printString(printerSetting.getPrinterName(), print);
            // cut that paper Baby!
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printBytes(printerSetting.getPrinterName(), cutP);



        } catch (IOException e) {
            displayDialog();
            doprintbon();
        }
    }

    private String validatePriceFormat(double digit) {

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        return kursIndonesia.format(digit);

    }

}
