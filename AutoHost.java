package test;

import fi.iki.elonen.NanoHTTPD;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class AutoHost extends NanoHTTPD {

    private static final String productionPath = "C:\\Users\\Public\\Documents\\WebCheckResults.xlsx";
    private static final String developmentPath = "C:\\Users\\Public\\Documents\\ProscanDevWebCheckResults.xlsx";
    private static final String screenshotPath = "\\\\M365TBWOFW30611\\ScreenShot";  // shared folder
    private static final String logoPath = "C:\\Users\\Public\\Documents\\OIP.jpg";  // BharatBenz logo

    public AutoHost() throws IOException {
        super(8080); // Start server on port 8080
        start(SOCKET_READ_TIMEOUT, false);

        // Auto-detect system hostname and IP
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        String hostAddress = localHost.getHostAddress();

        System.out.println("✅ Server started successfully!");
        System.out.println("🌐 Access via Hostname: http://" + hostName + ":8080/");
        System.out.println("💻 Access via IP:        http://" + hostAddress + ":8080/");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (uri.startsWith("/screenshots/view/")) {
            String fileName = uri.substring("/screenshots/view/".length());
            return serveScreenshotFile(fileName);
        }

        if (uri.startsWith("/screenshots")) {
            return serveScreenshots();
        }

        String html = buildHtmlFromExcels();
        return newFixedLengthResponse(Response.Status.OK, "text/html", html);
    }

    private Response serveScreenshots() {
        try {
            Path folderPath = Paths.get(screenshotPath);
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Screenshot folder not found");
            }

            StringBuilder html = new StringBuilder("<html><head><title>Screenshots</title></head><body>");
            html.append("<h2>Screenshots</h2><ul>");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                for (Path file : stream) {
                    String fileName = file.getFileName().toString();
                    html.append("<li><a href='/screenshots/view/").append(fileName)
                        .append("' target='_blank'>").append(fileName).append("</a></li>");
                }
            }
            html.append("</ul></body></html>");
            return newFixedLengthResponse(Response.Status.OK, "text/html", html.toString());

        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error reading folder: " + e.getMessage());
        }
    }

    private Response serveScreenshotFile(String fileName) {
        try {
            Path filePath = Paths.get(screenshotPath, fileName);
            if (!Files.exists(filePath)) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File not found");
            }

            String mime = Files.probeContentType(filePath);
            byte[] data = Files.readAllBytes(filePath);
            return newFixedLengthResponse(Response.Status.OK, mime != null ? mime : "application/octet-stream",
                    new java.io.ByteArrayInputStream(data), data.length);

        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error reading file: " + e.getMessage());
        }
    }

    private String buildHtmlFromExcels() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
            .append("<title>Portal Login Status</title>")
            .append("<meta http-equiv='refresh' content='20'>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("h2 { text-align: center; position: relative; }")
            .append(".screenshot-btn { position: absolute; right: 20px; top: 0; background-color: #007bff; color: white; ")
            .append("border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: bold; }")
            .append(".screenshot-btn:hover { background-color: #0056b3; }")
            .append("table { border-collapse: collapse; width: 45%; margin: 10px; float: left; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }")
            .append("th { background-color: #f2f2f2; }")
            .append("tr:nth-child(even) { background-color: #f9f9f9; }")
            .append(".success { color: green; font-weight: bold; }")
            .append(".fail { color: red; font-weight: bold; }")
            .append(".header { background-color: #ffff66; font-weight: bold; }")
            .append("img { display: block; margin: 0 auto 20px auto; height: 80px; }")
            .append("</style></head><body>");

        // Logo
        try {
            byte[] logoBytes = Files.readAllBytes(Paths.get(logoPath));
            String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);
            html.append("<img src='data:image/jpeg;base64,").append(logoBase64).append("' alt='BharatBenz Logo'>");
        } catch (IOException e) {
            html.append("<h3 style='color:red;text-align:center;'>Logo not found</h3>");
        }

        // Title + Screenshot button
        html.append("<div style='position: relative; text-align: center;'>")
            .append("<h2>Portal Login Status</h2>")
            .append("<button class='screenshot-btn' onclick=\"window.open('file://///M365TBWOFW30611/ScreenShot')\">ScreenShot</button>")
            .append("</div>");

        // Production table
        html.append("<table><tr class='header'><th colspan='2'>Production</th></tr>")
            .append("<tr><th>Timestamp</th><th>Status</th></tr>")
            .append(readExcel(productionPath))
            .append("</table>");

        // Development table
        html.append("<table><tr class='header'><th colspan='2'>Development</th></tr>")
            .append("<tr><th>Timestamp</th><th>Status</th></tr>")
            .append(readExcel(developmentPath))
            .append("</table>");

        html.append("</body></html>");
        return html.toString();
    }

    private String readExcel(String path) {
        StringBuilder html = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            int startRow = Math.max(1, lastRowNum - 9);

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
            new AutoHost();
        } catch (IOException e) {
            System.err.println("❌ Failed to start server: " + e.getMessage());
        }
    }
}
