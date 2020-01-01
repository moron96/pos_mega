package print.print;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.*;
import model.*;
import model.Menu;

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
public class PrintDailyReport implements Printable {

    private DBHelper dbHelper = DBHelperSingleton.getInstance();
    private CategoryDatabaseUtils categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);
    private OrderDatabaseUtils orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
    private OrderDetailDatabaseUtils orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
    private MenuDatabaseUtils menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);
    private PaymentMethodDatabaseUtils paymentMethodDatabaseUtils = new PaymentMethodDatabaseUtils(dbHelper);

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

    public void doPrintDaily()
    {

        try {
            PrinterSetting printerSetting = PrinterSetting.loadSettingsFile();

            PrintService service = findPrintService(printerSetting.getPrinterName());
            PrinterJob pj = PrinterJob.getPrinterJob();
            PageFormat pf = pj.defaultPage(printerSetting.getPageFormat());
            pj.setPrintable(this);

            int makanditempat=0;
            int makantakeaway=0;

            ArrayList<Order> listTodayHistory = orderDatabaseUtils.getOrderListTodayHistory();

            System.out.println("LIST ORDER TODAY");
            for (Order order:listTodayHistory
                 ) {
                System.out.println("TODAY : " +order.getCustomer_code());
                System.out.println(order.getId());
                if(order.getCustomer_code().length()>10)
                {
                    makantakeaway+=1;
                }
                else {
                    makanditempat+=1;
                }
            }

            HashMap<Integer,Double> totals = new HashMap<>();
            HashMap<Integer,Double> sotototals = new HashMap<>();
            HashMap<Integer,Double> grandtotals = new HashMap<>();
            HashMap<Integer,HashMap<Integer,Double>> totalmapper = new HashMap<>();
            for (Payment_Method pm:paymentMethodDatabaseUtils.getAllPaymentMethods()
                 ) {
                totals.put(pm.getId(), Double.valueOf(0));
                sotototals.put(pm.getId(), Double.valueOf(0));
                grandtotals.put(pm.getId(), Double.valueOf(0));
            }

            totalmapper.put(1,totals);
            totalmapper.put(2,totals);


            HashMap<Integer,String> printmapper = new HashMap<>();
            printmapper.put(1,"");
            printmapper.put(2,"");

            double subtotal;

            Calendar date = new GregorianCalendar();
            Date currdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss", Locale.UK);

            int totalcustomer = makanditempat+makantakeaway;

            String finalprint = "  MEGA KITCHEN\n"
                    + "  Daily Report\n"
                    + "  " + sdf.format(currdate) + "\n\n"
                    + "  Dine in    : "+ makanditempat +"\n"
                    + "  Take Away  : "+ makantakeaway +"\n"
                    + "  Total      : "+ totalcustomer +"\n"
                    + " ------------------------------------------\n";

            String printinside = "";

            for (Category category:categoryDatabaseUtils.getAllCategory())
            {
                double categorytotel =0;

                for (Menu menu:menuDatabaseUtils.getMenuListByCategoryId(categoryDatabaseUtils.getCategoryByName(category.getName()).getId()))
                {
                    int quantity = 0;
                    int gojekquantity = 0;

                    for (Order order:listTodayHistory
                         ) {
                        for (Order_Detail orderdetail:orderDetailDatabaseUtils.getOrderDetailsByOrderId(order.getId())
                             ) {
                            if(orderdetail.getMenu_id().compareTo(menu.getId())==0)
                            {
                                quantity +=orderdetail.getQty();
                                if(order.getCustomer_code().length()>10)
                                {
                                    gojekquantity +=orderdetail.getQty();
                                }
                                //fill in totals with K,V (payment method id , price)
                                totals.put(order.getPayment_method_id(),totals.get(order.getPayment_method_id()) + (orderdetail.getQty()*menu.getPrice()));
                            }
                        }
                    }

                    if(quantity>0) {
                        //fill in print with the string to be printed
                        if(quantity-gojekquantity>0)
                        {
                            printinside += "    " + menu.getName() + "\n";
                            printinside += String.format("%-30s","    " + String.valueOf(quantity-gojekquantity) + " * " + validatePriceFormat(menu.getPrice()));
                            subtotal = (quantity-gojekquantity) * menu.getPrice();
                            printinside += validatePriceFormat(subtotal) + "\n";
                        }
                        if(gojekquantity>0)
                        {
                            printinside += "    Gojek - " + menu.getName() + "\n";
                            printinside += String.format("%-30s","    " + String.valueOf(gojekquantity) + " * " + validatePriceFormat(menu.getPrice()));
                            subtotal = gojekquantity * menu.getPrice();
                            printinside += validatePriceFormat(subtotal) + "\n";
                        }
                        categorytotel += quantity * menu.getPrice();
                    }
                }
                if (categorytotel>0)
                {
                    //fill in printmapper with K,V (category id , print)
                    String print = "  " + category.getName()+ "\n";
                    print += printinside+ "\n";


                    //merge result of id 3 & 8
                    if (category.getId()==3 || category.getId()==8) {
                        printmapper.put(1, printmapper.get(1) + "\n" + print);
                        System.out.println("disini " + totals);
                        for (Integer key : totals.keySet()
                                ) {
                            //totals.put(key, totals.get(key) + totalmapper.get(1).get(key));
                            sotototals.put(key, totals.get(key) + sotototals.get(key));
                        }
                        totalmapper.put(1, sotototals);
                        System.out.println("disini " + totalmapper);
                    } else {
                        printmapper.put(2, printmapper.get(2) + "\n" + print);
                        System.out.println("disini " + totals);
                        for (Integer key : totals.keySet()
                                ) {
                            //totals.put(key, totals.get(key) + totalmapper.get(2).get(key));
                            grandtotals.put(key, totals.get(key) + grandtotals.get(key));
                        }
                        totalmapper.put(2, grandtotals);
                        System.out.println("disini " + totalmapper);
                    }

                    //flush the print and totals for next category
                    printinside = "";
                    for (Integer key : totals.keySet()
                            ) {
                        totals.put(key, Double.valueOf(0));
                    }
                }
            }

            //construct print
            System.out.println("disini " + totalmapper);
            System.out.println("disini " + grandtotals);
            System.out.println("disini " + sotototals);

            for (Integer key : printmapper.keySet()) {
                finalprint += printmapper.get(key) + "\n";
                double grandtotal = 0;
                if (totalmapper.get(key) != null) {
                    for (Integer paymentmethod : totalmapper.get(key).keySet()) {
                        if (totalmapper.get(key).get(paymentmethod) > 0) {
                            finalprint += String.format("%-30s", "  " + paymentMethodDatabaseUtils.getPaymentMethodById(paymentmethod).getName().toUpperCase() + " :");
                            finalprint += validatePriceFormat(totalmapper.get(key).get(paymentmethod)) + "\n";
                            grandtotal += totalmapper.get(key).get(paymentmethod);
                        }
                    }
                    finalprint += String.format("%-30s", "  Total :");
                    finalprint += validatePriceFormat(grandtotal) + "\n\n\n\n\n\n";
                }
            }

            //test print out
            System.out.println(finalprint);

            printString(printerSetting.getPrinterName(), finalprint);
            // cut that paper Baby!
            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printBytes(printerSetting.getPrinterName(), cutP);



        } catch (IOException e) {
            displayDialog();
            //doPrintDaily();
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
