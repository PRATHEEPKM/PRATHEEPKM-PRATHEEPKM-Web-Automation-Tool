package test;

import fi.iki.elonen.NanoHTTPD;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.Iterator;

public class ScreenShotButton extends NanoHTTPD {

    private static final String excelPathProd = "C:\\Users\\Public\\Documents\\WebCheckResults.xlsx";
    private static final String excelPathDev = "C:\\Users\\Public\\Documents\\ProscanDevWebCheckResults.xlsx";
    private static final String prodScreenshotPath = "\\M365TBWOFW30611\\ScreenShot";
    private static final String devScreenshotPath = "\\M365TBWOFW30611\\ScreenShot";

    public ScreenShotButton() throws IOException {
        super(8080);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started → http://53.89.202.94:8080/");
    }

    @Override
    public Response serve(IHTTPSession session) {

        // Serve production screenshot
        if (session.getUri().equals("/prodshot")) {
            return serveScreenshot(prodScreenshotPath, "Production Screenshot");
        }

        // Serve development screenshot
        if (session.getUri().equals("/devshot")) {
            return serveScreenshot(devScreenshotPath, "Development Screenshot");
        }

        // Default page
        String html = buildHtmlFromExcel();
        return newFixedLengthResponse(Response.Status.OK, "text/html", html);
    }

    private Response serveScreenshot(String path, String label) {
        try {
            File screenshot = new File(path);
            if (screenshot.exists()) {
                return newChunkedResponse(Response.Status.OK, "image/jpeg", new FileInputStream(screenshot));
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain",
                        label + " not found at: " + path);
            }
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain",
                    "Error loading " + label + ": " + e.getMessage());
        }
    }

    private String buildHtmlFromExcel() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
            .append("<title>Status Log</title>")
            .append("<meta http-equiv='refresh' content='30'>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin:20px; }")
            .append("h2 { color: #007bff; }")
            .append("table { border-collapse: collapse; width: 100%; margin-bottom:40px; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
            .append("th { background-color: #f2f2f2; }")
            .append("tr:nth-child(even) { background-color: #f9f9f9; }")
            .append(".success { color: green; font-weight: bold; }")
            .append(".fail { color: red; font-weight: bold; }")
            .append("button { background-color:#007bff;color:white;border:none;padding:10px 20px;")
            .append("border-radius:8px;cursor:pointer;margin:5px; }")
            .append("button:hover { background-color:#0056b3; }")
            .append("</style></head><body>")
            .append("<h2>Portal Login Status</h2>")
            .append("<div>")
            .append("<button onclick=\"window.open('/prodshot', '_blank')\">📷 View Production Screenshot</button>")
            .append("<button onclick=\"window.open('/devshot', '_blank')\">🧑‍💻 View Development Screenshot</button>")
            .append("</div><br><br>");

        // Production Excel
        html.append("<h3>Production Excel - WebCheckResults.xlsx</h3>");
        html.append(readExcelToHtml(excelPathProd));

        // Development Excel
        html.append("<h3>Development Excel - ProscanDevWebCheckResults.xlsx</h3>");
        html.append(readExcelToHtml(excelPathDev));

        html.append("</body></html>");
        return html.toString();
    }

    private String readExcelToHtml(String path) {
        StringBuilder html = new StringBuilder();
        html.append("<table><tr><th>Timestamp</th><th>Status</th></tr>");

        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            int startRow = Math.max(1, lastRowNum - 9);

            for (int i = lastRowNum; i >= startRow; i--) {
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

        html.append("</table>");
        return html.toString();
    }

    public static void main(String[] args) {
        try {
            new ScreenShotButton();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}

