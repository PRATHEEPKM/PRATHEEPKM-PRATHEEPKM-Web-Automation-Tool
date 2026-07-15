package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Proscan {
	static WebDriver driver;
    static String excelPath = "C:\\Users\\Public\\Documents\\WebCheckResults.xlsx";
    static String resultprint;
    static String emailprint;

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            String formattedDateTime = now.format(formatter);

            String testurl = "https://diagnostics-proscan.bharatbenz.com/flashserver_dicv/admin-view";
            String url = "https://diagnostics-proscan.bharatbenz.com/flashserver_dicv/login";

         
            try {
         //	WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                driver.manage().window().maximize();
                driver.get(url);
              
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement userId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input)[1]")));
                userId.sendKeys("Superadmin");

                WebElement pass = driver.findElement(By.xpath("(//input)[2]"));
                pass.sendKeys("Proscan@12345", Keys.ENTER);

                wait.until(ExpectedConditions.urlToBe(testurl));
                String currentUrl = driver.getCurrentUrl();

                if (testurl.equals(currentUrl)) {
                    resultprint = "Login successful";
                    Thread.sleep(2000);
                    WebElement userprofile = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button)[11]")));
                    userprofile.click();
                    WebElement userlogout = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button)[15]")));
                    Thread.sleep(5000);
                    userlogout.click();
                    Thread.sleep(3000);
                } else {             	
                	                  	                 
        	            resultprint = "Login failed";
                }


            } catch (Exception e) {
            	
            	 LocalDateTime nows = LocalDateTime.now();
                 DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
                 String timestamp = nows.format(formatters);
            	TakesScreenshot scrShot = ((TakesScreenshot) driver);
                
                //Create image file
                File source = scrShot.getScreenshotAs(OutputType.FILE);
         
                //Set new destination & File Name
                String fileWithPath = "C:\\Users\\Public\\Documents\\ScreenShot\\ScreenShot_" + timestamp +".png";
                File destination = new File(fileWithPath);
                try {
					FileUtils.copyFile(source, destination);
					
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                

                resultprint = "Login failed";
               try {
            	   File soundFile = new File("C:\\Users\\pid906c\\eclipse-workspace\\test\\src\\test\\resources\\Sound\\freesound.wav");

   	            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
   	            Clip clip = AudioSystem.getClip();
   	            clip.open(audioStream);
   	            clip.start();
   	      // Verify attachment file exists
   	            File attachmentFile = new File(fileWithPath);
   	            if (!attachmentFile.exists()) {
   	                System.out.println("Attachment file not found: " + fileWithPath);
   	                return;
   	            }
   	         final String username = "TAPPID-106212";
   	        final String password = "ifpPzlTFPhRdsCYfsHdpdvceWDU";


   	        Properties props = new Properties();
   	        props.put("mail.smtp.host", "apac-smtp.che.dc.tbintra.net");
   	        props.put("mail.smtp.port", "25");
   	        props.put("mail.smtp.auth", "true");
   	        props.put("mail.smtp.starttls.enable", "true");

   	        Session session = Session.getInstance(props,
   	                new Authenticator() {
   	                    @Override
   	                    protected PasswordAuthentication getPasswordAuthentication() {
   	                        return new PasswordAuthentication(username, password);
   	                    }
   	                });

   	        session.setDebug(true);
   	            MimeMessage message = new MimeMessage(session);

   	            message.setFrom(new InternetAddress("dw_365_dicv-flashwaresupport@daimlertruck.com"));

   	            message.setRecipients(
   	                    Message.RecipientType.TO,
   	                    InternetAddress.parse("mathusuthanan.marimuthu@daimlertruck.com,"
             		              +"vikram.k@daimlertruck.com," 
             		              + "alten.jeyaganesh@daimlertruck.com,"
            		              + "alten.paul_nilavan@daimlertruck.com,"
            		              + "alten.a.pratheep@daimlertruck.com,"
             		              + "alten.jackson_sahayaraj@daimlertruck.com,"
             		              + "alten.aravind@daimlertruck.com,"
             		              + "alten.udayakumar@daimlertruck.com," 
             		              + "alten.sadheeshkumar@daimlertruck.com," 
             		              + "alten.saiprakhash@daimlertruck.com," 
             		              + "alten.balaji@daimlertruck.com"      	                		                   	                		              
   	                    		              ));

   	           

   	            message.setSubject("Portal Login Status Failure - " + timestamp);

   	            // Text part
   	            MimeBodyPart textPart = new MimeBodyPart();
   	            textPart.setText(
   	                    "Webportal login failure detected.\n\n" +
   	                    "Environment: Production\n" +
   	                    "Timestamp: " + timestamp +"\n"+  	                  
   	            		"ScreenShotPath: \\\\M365TBWOFW30611\\ScreenShot" 
   	                    );

   	            // Attachment part
   	            MimeBodyPart attachmentPart = new MimeBodyPart();
   	            attachmentPart.attachFile(attachmentFile);

   	            // Multipart container
   	            MimeMultipart multipart = new MimeMultipart();
   	            multipart.addBodyPart(textPart);
   	            multipart.addBodyPart(attachmentPart);

   	            // Set content
   	            message.setContent(multipart);

   	            // Send email
   	         Transport.send(message);

   	        //   emailprint ="Email sent successfully with attachment!";
   	            System.out.println("Attached file: " + attachmentFile.getAbsolutePath());
   	        }
               
   	        catch (MessagingException e1) {
   	            e1.printStackTrace();
				
			} catch (Exception e2) {
				// TODO: handle exception
			} 
                
                
                
            } finally {
            	
                if (driver != null) {
                	
                    driver.quit();
                    
                }
            }

            logResultToExcel(formattedDateTime, resultprint);
        };

      scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.MINUTES); 
    }
        

    private static void logResultToExcel(String time, String resultprint) {
        File file = new File(excelPath);
        XSSFWorkbook workbook;
        XSSFSheet sheet;

        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Login Results");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Timestamp");
                header.createCell(1).setCellValue("Status");
                header.createCell(3).setCellValue("Email Status");
            }

            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(time);
            newRow.createCell(1).setCellValue(resultprint);
            newRow.createCell(2).setCellValue(emailprint);
            FileOutputStream fos = new FileOutputStream(excelPath);
            workbook.write(fos);
            fos.close();
            workbook.close();

        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }
	

}


