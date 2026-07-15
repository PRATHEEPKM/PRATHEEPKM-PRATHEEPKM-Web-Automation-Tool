package test;

import fi.iki.elonen.NanoHTTPD;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.net.httpserver.HttpServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;

public class ExcelStatusServer extends NanoHTTPD {
	HttpServer server = HttpServer.create(new InetSocketAddress("53.89.202.94",8000), 0);

    private static final String excelPath = "C:\\Users\\Public\\Documents\\WebCheckResults.xlsx";

    public ExcelStatusServer() throws IOException {
    	
        super(8080); // start server on port 8080
        
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("http://53.89.202.94:8080/");
    }

    @Override
   public Response serve(IHTTPSession session) {
        String html = buildHtmlFromExcel();
        return newFixedLengthResponse(Response.Status.OK, "text/html", html);
    }

    private String buildHtmlFromExcel() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
        .append("<title>Status Log</title>")
        // Auto-refresh every 30 seconds
        .append("<meta http-equiv='refresh' content='30'>")
        .append("<style>")
        
        .append("body { font-family: Arial, sans-serif; margin:20px; }")
        .append("table { border-collapse: collapse; width: 100%; }")
        .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
        .append("th { background-color: #f2f2f2; }")
        .append("tr:nth-child(even) { background-color: #f9f9f9; }")
        .append(".success { color: green; font-weight: bold; }")
        .append(".fail { color: red; font-weight: bold; }")
        .append("</style></head><body>")
        .append("<h2>Portal Login Status</h2>")
        .append("<table><tr><th>Timestamp</th><th>Status</th></tr>");


        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int lastRowNum = sheet.getLastRowNum();

         // calculate the starting row so we only pick last 10 (excluding header row)
         int startRow = Math.max(1, lastRowNum - 9);

         for (int i = lastRowNum; i >= startRow; i--) {   // go backwards
             Row row = sheet.getRow(i);
             if (row == null) continue;

             String time = row.getCell(0).getStringCellValue();
             String status = row.getCell(1).getStringCellValue();
             String cssClass = status.toLowerCase().contains("success") ? "success" : "fail";
          

                html.append("<tr>")
                    .append("<td>").append(time).append("</td>")
                    .append("<td class='").append(cssClass).append("'>").append(status).append("</td>")
                    .append("</tr>");
            }
        } catch (Exception e) {
            html.append("<tr><td colspan='2'>Error reading Excel: ")
                .append(e.getMessage()).append("</td></tr>");
        }

        html.append("</table></body></html>");
        return html.toString();
    }

    public static void main(String[] args) {
        try {
            new ExcelStatusServer();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}