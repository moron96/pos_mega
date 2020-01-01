package print.print;

import com.google.gson.Gson;
import flexjson.JSONDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.print.PageFormat;
import java.io.*;

/**
 * Created by Mico Yohanes on 27/04/2017.
 */
public class PrinterSetting {

    public static final String SETTINGS_FOLDER_PATH = "POS/";
    public static final String SETTINGS_FILE_NAME = "PrinterSettings.json";
    public static final String SETTINGS_FILE_PATH = SETTINGS_FOLDER_PATH + SETTINGS_FILE_NAME;

    public static final String JSON_PAGE_FORMAT = "page_format";
    public static final String JSON_PRINTER_NAME = "printer_name";

    private static JSONParser parser = new JSONParser();

    private String printerName;
    private PageFormat pageFormat;

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public PageFormat getPageFormat() {
        return pageFormat;
    }

    public void setPageFormat(PageFormat pageFormat) {
        this.pageFormat = pageFormat;
    }

    /**
     * Buat settings baru ke file settings.json
     *
     * @param printerName
     * @param pageFormat
     */
    public PrinterSetting(String printerName, PageFormat pageFormat) {

        this.printerName = printerName;
        this.pageFormat = pageFormat;

        File file = new File(SETTINGS_FILE_PATH);
        //make directory if not exists
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
                try {
                    file.createNewFile();
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
        }
    }

    public static void createSettingsFolder()
    {
        File file = new File(SETTINGS_FILE_PATH);
        //make directory if not exists
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
                try {
                    file.createNewFile();
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
        }
    }

    public void writeSettingsFile() throws IOException {
        FileWriter fileWriter = new FileWriter(SETTINGS_FILE_PATH);

        JSONObject jsonObject = new JSONObject();
        Gson gsonPrinter = new Gson();
        Gson gsonPageFormat = new Gson();

        JSONObject pageObject = new JSONObject(gsonPageFormat.toJson(pageFormat));

        jsonObject.put(JSON_PRINTER_NAME, printerName);
        jsonObject.put(JSON_PAGE_FORMAT, pageObject);

        fileWriter.write(jsonObject.toString());
        fileWriter.flush();

        System.out.println(gsonPrinter.toJson(printerName));
        System.out.println(gsonPageFormat.toJson(pageFormat));
        System.out.println(SETTINGS_FILE_PATH + " written");
    }


    /**
     * Jadi kalau mau load settings dari file pake ini bikin classnya, kalau throw exception brarti setting file nya ga ada atau ga lengkap
     *
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static PrinterSetting loadSettingsFile() throws IOException, JSONException {
        FileReader fileReader = new FileReader(SETTINGS_FILE_PATH);
        Object obj = null;
        try {
            obj = parser.parse(fileReader);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (obj != null) {
            Gson gson = new Gson();

            JSONObject fileJson = new JSONObject(obj.toString());
            System.out.println(fileJson);

            JSONObject jsonObject = new JSONObject(fileJson.get(JSON_PAGE_FORMAT).toString());
            System.out.println(jsonObject.toString());

            //works
            JSONDeserializer<PageFormat> der = new JSONDeserializer<>();
            PageFormat pf = der.deserialize(jsonObject.toString(), PageFormat.class);
            System.out.println(fileJson.get(JSON_PRINTER_NAME));

            return new PrinterSetting(fileJson.get(JSON_PRINTER_NAME).toString(), pf);
        } else {
            throw new IOException("JSON FILE IS NULL");
        }
    }
}
