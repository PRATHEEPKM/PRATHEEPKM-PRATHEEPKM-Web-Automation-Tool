package test;

import fi.iki.elonen.NanoHTTPD;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class Excel2View extends NanoHTTPD {

    private static final String productionPath = "C:\\Users\\Public\\Documents\\WebCheckResults.xlsx";
    private static final String developmentPath = "C:\\Users\\Public\\Documents\\ProscanDevWebCheckResults.xlsx";
    private static final String prodScreenshotPath = "\\M365TBWOFW30611\\ScreenShot";
    private static final String logoPath = "C:\\Users\\Public\\Documents\\OIP.jpg"; // BharatBenz logo

    public Excel2View() throws IOException {
        super(8080); // start server on port 8080
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server running at: http://53.89.202.94:8080/");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String html = buildHtmlFromExcels();
        return newFixedLengthResponse(Response.Status.OK, "text/html", html);
    }

    private String buildHtmlFromExcels() {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
            .append("<title>Portal Login Status</title>")
            .append("<meta http-equiv='refresh' content='30'>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("h2 { text-align: center; }")
            .append("table { border-collapse: collapse; width: 45%; margin: 10px; float: left; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }")
            .append("th { background-color: #f2f2f2; }")
            .append("tr:nth-child(even) { background-color: #f9f9f9; }")
            .append(".success { color: green; font-weight: bold; }")
            .append(".fail { color: red; font-weight: bold; }")
            .append(".header { background-color: #ffff66; font-weight: bold; }")
            .append("img { display: block; margin: 0 auto 20px auto; height: 80px; }")
            .append("</style></head><body>");

        // --- Logo ---
        try {
            byte[] logoBytes = Files.readAllBytes(Paths.get(logoPath));
            String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);
            html.append("<img src='data:image/jpeg;base64,").append(logoBase64)
                .append("' alt='BharatBenz Logo'>");
        } catch (IOException e) {
            html.append("<h3 style='color:red;text-align:center;'>Logo not found</h3>");
        }

        html.append("<h2>Portal Login Status</h2>");

        // --- Production Table ---
        html.append("<table><tr class='header'><th colspan='2'>Production</th></tr>")
            .append("<tr><th>Timestamp</th><th>Status</th></tr>");
        html.append(readExcel(productionPath));
        html.append("</table>");

        // --- Development Table ---
        html.append("<table><tr class='header'><th colspan='2'>Development</th></tr>")
            .append("<tr><th>Timestamp</th><th>Status</th></tr>");
        html.append(readExcel(developmentPath));
        html.append("</table>");

        html.append("</body></html>");
        return html.toString();
    }

    private String readExcel(String path) {
        StringBuilder html = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            int startRow = Math.max(1, lastRowNum - 9); // show last 10 entries

            for (int i = lastRowNum; i >= startRow; i--) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String time = getCellValue(row.getCell(0));
                String status = getCellValue(row.getCell(1));
                String css = status.toLowerCase().contains("success") ? "success" : "fail";

                html.append("<tr><td>").append(time).append("</td>")
                    .append("<td class='").append(css).append("'>").append(status).append("</td></tr>");
            }
        } catch (Exception e) {
            html.append("<tr><td colspan='2'>Error reading file: ").append(e.getMessage()).append("</td></tr>");
        }
        return html.toString();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
                else
                    return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }

    public static void main(String[] args) {
        try {
            new Excel2View();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
