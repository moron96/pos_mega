package print.pdf;

/**
 * Created by ASUS on 05/05/2017.
 */

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.CategoryDatabaseUtils;
import database.utils.MenuDatabaseUtils;
import database.utils.OrderDatabaseUtils;
import database.utils.OrderDetailDatabaseUtils;
import model.*;
import model.Menu;
import print.print.PrinterSetting;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.awt.print.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author All Open source developers
 * @version 1.0.0.0
 * @since 2014/12/22
 */
/*This Printsupport java class was implemented to get printout.
* This class was specially designed to print a Jtable content to a paper.
* Specially this class formated to print 7cm width paper.
* Generally for pos thermel printer.
* Free to customize this source code as you want.
* Illustration of basic invoice is in this code.
* demo by gayan liyanaarachchi

 */

public class PrinterTest implements Printable{

    private ArrayList<Model_Table> listModelTable;

    private double total;

    private double discount;

    private double grandtotal;

    private DBHelper dbHelper = DBHelperSingleton.getInstance();
    private CategoryDatabaseUtils categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);
    private OrderDatabaseUtils orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
    private OrderDetailDatabaseUtils orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
    private MenuDatabaseUtils menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);

    public List<String> getPrinters(){

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);

        List<String> printerList = new ArrayList<>();
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

    private PrintService findPrintService(String printerName,
                                          PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    public void setData(ArrayList<Model_Table> listModelTable, double total, double discount, double grandtotal) {
        this.listModelTable = listModelTable;
        this.total = total;
        this.discount = discount;
        this.grandtotal = grandtotal;
    }

    public void doPrinting()
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if(ok) {

            double subtotal;

            String print = "  RESTO KEMUNING\n"
                    + "  phone  : 0216521917\n"
                    + "  alamat : Jl Danau Sunter Utara B1a no 10\n\n"
                    + " ------------------------------------------\n\n";


            for(int i = 0; i < listModelTable.size(); i++) {

                print += "  " + listModelTable.get(i).getName() + "\n";
                print += String.format("%-30s","  " + String.valueOf(listModelTable.get(i).getQty()) + " * " + validatePriceFormat(listModelTable.get(i).getPrice()));
                subtotal = listModelTable.get(i).getQty() * listModelTable.get(i).getPrice();
                print += validatePriceFormat(subtotal) + "\n\n";
            }


            print += String.format("%-30s","  Total");
            print += validatePriceFormat(total) + "\n";
            print += String .format("%-30s","  Discount");
            print += validatePriceFormat(discount) + "\n";
            print += String .format("%-30s","  Grand Total");
            print += validatePriceFormat(grandtotal) + "\n";
            print += "\n\n\n\n\n";

            printString("EPSON TM-T82 Receipt", print);
            // cut that paper Baby!
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printBytes("EPSON TM-T82 Receipt", cutP);

        }
    }

    public void doPrintDaily()
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if(ok) {

            double subtotal;

            String print = "  RESTO KEMUNING\n"
                    + "  Daily Report\n"
                    + "  phone  : 0216521917\n"
                    + "  alamat : Jl Danau Sunter Utara B1a no 10\n\n"
                    + " ------------------------------------------\n\n";


            double grandtotal = 0;
            double grandnettotal = 0;

            for (Category category:categoryDatabaseUtils.getAllCategory())
            {
                double categorytotel =0;
                print += "  " + category.getName()+ "\n";

                for (Menu menu:menuDatabaseUtils.getMenuListByCategoryId(categoryDatabaseUtils.getCategoryByName(category.getName()).getId()))
                {
                    int quantity = 0;
                    for (Order_Detail orderdetail:orderDetailDatabaseUtils.getOrderDetailByMenuId(menu.getId()))
                    {
                        for (Order order:orderDatabaseUtils.getOrderListTodayHistory())
                        {
                            if(orderdetail.getOrder_id()==order.getId()){
                                quantity +=orderdetail.getQty();
                            }
                        }
                    }
                    if(quantity>0) {

                        print += "    " + menu.getName() + "\n";
                        print += String.format("%-30s","    " + String.valueOf(quantity) + " * " + validatePriceFormat(menu.getPrice()));
                        subtotal = quantity * menu.getPrice();
                        print += validatePriceFormat(subtotal) + "\n";
                        categorytotel += quantity * menu.getPrice();
                    }

                }
                if (category!=null)
                {
                    print += "\n";
                    double categorysubtotal = categorytotel * 100 / 110;
                    double ppn = categorytotel - categorysubtotal;
                    print += String.format("%-30s", "  Total :");
                    print += validatePriceFormat(categorytotel) + "\n";
                    print += String.format("%-30s", "  PPN :");
                    print += validatePriceFormat(ppn) + "\n";
                    print += String.format("%-30s", "  Net Total :");
                    print += validatePriceFormat(categorysubtotal) + "\n\n";
                    grandnettotal += categorysubtotal;
                    grandtotal += categorytotel;
                }
            }


            print += String.format("%-30s","  Grand Total");
            print += validatePriceFormat(grandtotal) + "\n";
            print += String.format("%-30s","  Grand Net Total");
            print += validatePriceFormat(grandnettotal) + "\n";
            print += "\n\n\n\n\n";

            printString("EPSON LX-300+ /II", print);
            // cut that paper Baby!
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printBytes("EPSON LX-300+ /II", cutP);

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