package print.pdf;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.CategoryDatabaseUtils;
import database.utils.MenuDatabaseUtils;
import database.utils.OrderDatabaseUtils;
import database.utils.OrderDetailDatabaseUtils;
import model.Category;
import model.Menu;
import model.Order;
import model.Order_Detail;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.round;

/**
 * Created by Elbert Utama on 04/05/2017.
 */
public class pdf_generator {

    private DBHelper dbHelper = DBHelperSingleton.getInstance();

    private CategoryDatabaseUtils categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);
    private OrderDatabaseUtils orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
    private OrderDetailDatabaseUtils orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
    private MenuDatabaseUtils menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);

    public pdf_generator() {

        try {
            PDDocument pdDocument = new PDDocument();
            PDDocumentInformation pdDocumentInformation = pdDocument.getDocumentInformation();
            pdDocumentInformation.setAuthor("Monacode");
            pdDocumentInformation.setTitle("Recap");
            pdDocumentInformation.setCreator("POS Kemuning");
            Calendar date = new GregorianCalendar();
            Date currdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            date.set(currdate.getYear(),currdate.getMonth(),currdate.getDate());
            pdDocumentInformation.setCreationDate(date);


            PDPage pdPage = new PDPage(PDRectangle.A4);
            float height = pdPage.getMediaBox().getHeight();
            float width = pdPage.getMediaBox().getWidth();
            float currheight = height;
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage);

            String[][] content = {{"a","b", "1"},
                    {"c","d", "2"},
                    {"e","f", "3"},
                    {"g","h", "4"},
                    {"i","j", "5"}} ;

            drawTable(pdPage, contentStream, 700, 100, content);

            contentStream.beginText();
            contentStream.newLineAtOffset(30, height-50);
            contentStream.setFont(PDType1Font.TIMES_BOLD,30);
            contentStream.showText("KEMUNING RESTO");
            contentStream.setLeading(20.5f);
            contentStream.newLine();
            contentStream.setFont(PDType1Font.TIMES_BOLD,20);
            contentStream.showText("Daily Report");
            contentStream.endText();

            currheight =height-(50+30+15);
            contentStream.beginText();
            contentStream.newLineAtOffset(30, currheight);
            contentStream.setFont(PDType1Font.TIMES_ROMAN,14);
            contentStream.showText("Date : "+sdf.format(currdate));
            contentStream.endText();

            currheight -=10;
            contentStream.drawLine(30,currheight,width-60,currheight);

            contentStream.beginText();
            currheight -=30;
            contentStream.newLineAtOffset(50, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,16);
            contentStream.showText("Item Name");
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(width-150, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,16);
            contentStream.showText("Sub Total");
            contentStream.endText();

            double grandtotal = 0;
            double grandnettotal = 0;

            for (Category category:categoryDatabaseUtils.getAllCategory()
                 ) {

                if(checkpagecontent(currheight))
                {
                    contentStream.close();
                    pdDocument.addPage(pdPage);
                    pdPage = new PDPage(PDRectangle.A4);
                    contentStream = new PDPageContentStream(pdDocument, pdPage);
                    currheight = height-50;
                }

                double categorytotel =0;
                contentStream.beginText();
                currheight -=30;
                contentStream.newLineAtOffset(50, currheight);
                contentStream.setFont(PDType1Font.TIMES_ROMAN,16);
                contentStream.showText(category.getName());
                contentStream.endText();
                for (Menu menu:menuDatabaseUtils.getMenuListByCategoryId(categoryDatabaseUtils.getCategoryByName(category.getName()).getId())
                     ) {
                    int quantity = 0;
                    for (Order_Detail orderdetail:orderDetailDatabaseUtils.getOrderDetailByMenuId(menu.getId())
                            ) {
                        for (Order order:orderDatabaseUtils.getOrderListTodayHistory()
                             ) {
                            if(orderdetail.getOrder_id().compareTo(order.getId())==0){
                                quantity +=orderdetail.getQty();
                            }
                        }
                    }
                    if(quantity>0) {
                        if(checkpagecontent(currheight))
                        {
                            contentStream.close();
                            pdDocument.addPage(pdPage);
                            pdPage = new PDPage(PDRectangle.A4);
                            contentStream = new PDPageContentStream(pdDocument, pdPage);
                            currheight = height-50;
                        }

                        contentStream.beginText();
                        currheight -= 20;
                        contentStream.newLineAtOffset(80, currheight);
                        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                        contentStream.showText(menu.getName());
                        contentStream.endText();

                        contentStream.beginText();
                        currheight -= 20;
                        contentStream.newLineAtOffset(90, currheight);
                        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                        contentStream.showText(String.valueOf(quantity) + " x " + menu.getPrice());
                        contentStream.endText();

                        contentStream.beginText();
                        contentStream.newLineAtOffset(width - 150, currheight);
                        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                        contentStream.showText(validatePriceFormat(quantity * menu.getPrice()));
                        contentStream.endText();

                        categorytotel += quantity * menu.getPrice();
                    }

                }

                contentStream.beginText();
                currheight -=20;
                contentStream.newLineAtOffset(80, currheight);
                contentStream.setFont(PDType1Font.TIMES_BOLD,13);
                contentStream.showText("Total : ");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(width - 150, currheight);
                contentStream.setFont(PDType1Font.TIMES_BOLD, 13);
                contentStream.showText(validatePriceFormat(categorytotel));
                contentStream.endText();


                contentStream.beginText();
                currheight -=20;
                contentStream.newLineAtOffset(80, currheight);
                contentStream.setFont(PDType1Font.TIMES_BOLD,13);
                contentStream.showText("PPN : ");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(width - 150, currheight);
                contentStream.setFont(PDType1Font.TIMES_BOLD, 13);
                contentStream.showText(validatePriceFormat(round(categorytotel/11)));
                contentStream.endText();


                contentStream.beginText();
                currheight -=20;
                contentStream.newLineAtOffset(80, currheight);
                contentStream.setFont(PDType1Font.TIMES_BOLD,13);
                contentStream.showText("Net Total : ");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(width - 150, currheight);
                contentStream.setFont(PDType1Font.TIMES_BOLD, 13);
                contentStream.showText(validatePriceFormat(round(categorytotel-(categorytotel/11))));
                contentStream.endText();

                grandtotal += categorytotel;
                grandnettotal += categorytotel-(categorytotel/11);
            }

            contentStream.beginText();
            currheight -= 40;
            contentStream.newLineAtOffset(50, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
            contentStream.showText("Grand Total");
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(width - 150, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
            contentStream.showText(validatePriceFormat(round(grandtotal)));
            contentStream.endText();


            contentStream.beginText();
            currheight -= 40;
            contentStream.newLineAtOffset(50, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
            contentStream.showText("Grand Net Total");
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(width - 150, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
            contentStream.showText(validatePriceFormat(round(grandtotal)));
            contentStream.endText();

            contentStream.close();
            pdDocument.addPage(pdPage);
            pdDocument.save("D:\\"+ sdf.format(currdate) +".pdf");
//            pdDocument.save("E:\\"+ sdf.format(currdate) +".pdf");
//            pdDocument.save("F:\\"+ sdf.format(currdate) +".pdf");
//            pdDocument.save("G:\\"+ sdf.format(currdate) +".pdf");
            pdDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean checkpagecontent(float height)
    {
        if(height<30)
        {
            return true;
        }
        else {
            return false;
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

    public void drawTable(PDPage page, PDPageContentStream contentStream,
                                 float y, float margin,
                                 String[][] content) throws IOException {
        int rows = 40;
        int cols = 3;
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth()-(2*margin);
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin,nexty,margin+tableWidth,nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx,y,nextx,y-tableHeight);
            nextx += colWidth;
        }

        //now add the text
        contentStream.setFont(PDType1Font.HELVETICA_BOLD,12);

        float textx = margin+cellMargin;
        float texty = y-15;

        String head = "Ctegory";
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(textx,texty);
        contentStream.drawString(head);
        contentStream.endText();
        textx += colWidth;

        head = "Menu";
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(textx,texty);
        contentStream.drawString(head);
        contentStream.endText();
        textx += colWidth;

        head = "Quantity";
        contentStream.beginText();
        contentStream.moveTextPositionByAmount(textx,texty);
        contentStream.drawString(head);
        contentStream.endText();

        texty-=rowHeight;
        textx = margin+cellMargin;
        rows++;

//        float textx = margin+cellMargin;
//        float texty = y-15;
//        for(int i = 0; i < content.length; i++){
//            for(int j = 0 ; j < content[i].length; j++){
//                String text = content[i][j];
//                contentStream.beginText();
//                contentStream.moveTextPositionByAmount(textx,texty);
//                contentStream.drawString(text);
//                contentStream.endText();
//                textx += colWidth;
//            }
//            texty-=rowHeight;
//            textx = margin+cellMargin;
//        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateInString = "07-07-2019";
        Date date = new Date();
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Map<String,String> map: orderDetailDatabaseUtils.monthlyOrderDetailRecapMenu(date)) {
            rows++;
            cols = map.size();

            String text = map.get("category_name");
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(textx,texty);
            contentStream.drawString(text);
            contentStream.endText();
            textx += colWidth;
            System.out.println(text);

            text = map.get("menu_name");
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(textx,texty);
            contentStream.drawString(text);
            contentStream.endText();
            textx += colWidth;
            System.out.println(text);

            text = map.get("qty");
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(textx,texty);
            contentStream.drawString(text);
            contentStream.endText();
            System.out.println(text);

            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }
}
