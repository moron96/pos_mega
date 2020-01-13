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

    private PDDocument pdDocument;
    private PDPage pdPage;
    private PDPageContentStream contentStream;

    public boolean generateReport(Date date) {
        try {
            pdDocument = new PDDocument();
            PDDocumentInformation pdDocumentInformation = pdDocument.getDocumentInformation();
            pdDocumentInformation.setAuthor("Monacode");
            pdDocumentInformation.setTitle("Recap");
            pdDocumentInformation.setCreator("POS Kemuning");
            Calendar now = new GregorianCalendar();

            Date currdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            now.set(currdate.getYear(),currdate.getMonth(),currdate.getDate());
            pdDocumentInformation.setCreationDate(now);

            pdPage = new PDPage(PDRectangle.A4);
            float height = pdPage.getMediaBox().getHeight();
            float width = pdPage.getMediaBox().getWidth();
            float currheight = height-30;
            contentStream = new PDPageContentStream(pdDocument, pdPage);

            contentStream.setLeading(20.5f);

            contentStream.beginText();
            contentStream.newLineAtOffset(30, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,20);
            currheight -= 20;
            contentStream.showText("KEMUNING RESTO");

            contentStream.newLine();
            contentStream.setFont(PDType1Font.TIMES_BOLD,15);
            currheight -= 15;
            contentStream.showText("Daily Report");

            contentStream.newLine();
            contentStream.setFont(PDType1Font.TIMES_ROMAN,12);
            currheight -= 12;
            contentStream.showText("Date : "+sdf.format(currdate));
            contentStream.endText();

            currheight -=5;
            contentStream.drawLine(30,currheight,width-30,currheight);
            currheight -=20;

            contentStream.beginText();
            contentStream.newLineAtOffset(30, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,15);
            currheight -= 5;
            contentStream.showText("MONTHLY MENU QUANTITIES SOLD");
            contentStream.endText();

            ArrayList<Float> widths = new ArrayList<>();
            widths.add(0.25f);
            widths.add(0.6f);
            widths.add(0.15f);

            currheight = drawTable(currheight, 50, widths,
                    orderDetailDatabaseUtils.monthlyOrderDetailRecapMenu(date));


            currheight -=20;
            contentStream.drawLine(30,currheight,width-30,currheight);
            currheight -=20;

            contentStream.beginText();
            contentStream.newLineAtOffset(30, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,15);
            currheight -= 5;
            contentStream.showText("MONTHLY CATEGORY QUANTITIES SOLD");
            contentStream.endText();

            widths = new ArrayList<>();
            widths.add(0.85f);
            widths.add(0.15f);

            currheight = drawTable(currheight, 50, widths,
                    orderDetailDatabaseUtils.monthlyOrderDetailRecapCategory(date));


            currheight -=20;
            contentStream.drawLine(30,currheight,width-30,currheight);
            currheight -=20;

            contentStream.beginText();
            contentStream.newLineAtOffset(30, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,15);
            currheight -= 5;
            contentStream.showText("MONTHLY PAYMENT METHOD QUANTITIES");
            contentStream.endText();

            widths = new ArrayList<>();
            widths.add(0.85f);
            widths.add(0.15f);

            currheight = drawTable(currheight, 50, widths,
                    orderDetailDatabaseUtils.monthlyOrderDetailRecapPeyment(date));


            currheight -=20;
            contentStream.drawLine(30,currheight,width-30,currheight);
            currheight -=20;

            contentStream.beginText();
            contentStream.newLineAtOffset(30, currheight);
            contentStream.setFont(PDType1Font.TIMES_BOLD,15);
            currheight -= 5;
            contentStream.showText("DAILY AVERAGE TRAFFIC");
            contentStream.endText();

            widths = new ArrayList<>();
            widths.add(0.85f);
            widths.add(0.15f);

            currheight = drawTable(currheight, 50, widths,
                    orderDetailDatabaseUtils.monthlyAverageDailyTraffic(date));

            contentStream.close();
            pdDocument.addPage(pdPage);
            pdDocument.save("D:\\"+ sdf.format(currdate) +".pdf");
//            pdDocument.save("E:\\"+ sdf.format(currdate) +".pdf");
//            pdDocument.save("F:\\"+ sdf.format(currdate) +".pdf");
//            pdDocument.save("G:\\"+ sdf.format(currdate) +".pdf");
            pdDocument.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public pdf_generator() {

    }

    private float checkForNewPage(float height)
    {
        if(height<30)
        {
            try {
                contentStream.close();

                pdDocument.addPage(pdPage);
                pdPage = new PDPage(PDRectangle.A4);
                contentStream = new PDPageContentStream(pdDocument, pdPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pdPage.getMediaBox().getHeight()-30;
        }
        else {
            return height;
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

    public float drawTable(float y, float x,
                           ArrayList<Float> widths, 
                           ArrayList<Map<String, String>> data) throws IOException {
        float cellMargin=5f;
        float rowHeight = 20f;
        float tableWidth = pdPage.getMediaBox().getWidth()-(2*x);

        ArrayList<Float> cellWidth = new ArrayList<>();
        for (Float f : widths) {
            cellWidth.add(tableWidth * f);
        }

        for (Map<String,String> map : data) {

            y = checkForNewPage(y);

            contentStream.drawLine(x, y, x + tableWidth, y);

            float nextx = x;
            int index = 0;
            for (String str : map.keySet()) {
                contentStream.drawLine(x, y, x, y - rowHeight);
                String text = map.get(str);
                if (map.get(str)==null) {
                    text = "Null";
                }

                contentStream.beginText();
                contentStream.moveTextPositionByAmount(nextx + cellMargin, y - 15);
                contentStream.setFont(PDType1Font.TIMES_ROMAN,12);
                contentStream.drawString(text);
                contentStream.endText();

                nextx += cellWidth.get(index);
                index++;
                contentStream.drawLine(nextx, y, nextx, y - rowHeight);
            }
            
            y -= rowHeight;
            contentStream.drawLine(x, y, x + tableWidth, y);
            
        }

        return y - 5;
    }
}
