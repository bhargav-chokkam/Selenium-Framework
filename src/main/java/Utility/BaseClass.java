package Utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BaseClass {
    public static ExtentReports report;
    public static Common com;
    public static Map<String, String> data;
    public ExtentTest spark;

    private static void deleteOldFiles() {
        Date oldestAllowedFileDate = DateUtils.addDays(new Date(), -5); //minus days from current date
        File targetDir = new File(System.getProperty("user.dir") + "//resultsBackup");
        Iterator<File> filesToDelete = FileUtils.iterateFiles(targetDir, new AgeFileFilter(oldestAllowedFileDate), null);
        //if deleting subdirs, replace null above with TrueFileFilter.INSTANCE
        while (filesToDelete.hasNext()) {
            FileUtils.deleteQuietly(filesToDelete.next());
        }  //I don't want an exception if a file is not deleted. Otherwise use filesToDelete.next().delete() in a try/catch
    }

    private static void deleteFolder() throws IOException {
        FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "//report"));
        System.out.println("Report Folder Deleted");
    }

    private static void zipFolder() throws IOException {
        String sourceFile = System.getProperty("user.dir") + "//report";
        String currentDateTime = getCurrentTime();
        FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "//resultsBackup//" + currentDateTime + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
        System.out.println("Old Test Result Report has Zipped and moved to resultBackUp Folder");

    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String currentDateTime = dtf.format(now);
        return currentDateTime;
    }

    private static void setSystemOutToFile() throws IOException {
        File newFolder = new File(System.getProperty("user.dir") + "//report");
        newFolder.mkdir();
        File newFile = new File(System.getProperty("user.dir") + "//report//console.txt");
        newFile.createNewFile();
        PrintStream file = new PrintStream(System.getProperty("user.dir") + "//report//console.txt");
        System.setOut(file);
    }

    public static void createSparkReport() {
        if (report == null) {
            report = getExtendReport();
        }
    }

    public static ExtentReports getExtendReport() {
        String reportPath = System.getProperty("user.dir") + "//report//SparkReport.html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Test Results");
        spark.config().setReportName("Bhargav Chokkam");
        spark.config().setTheme(Theme.DARK);
        ExtentReports extent2 = new ExtentReports();
        extent2.attachReporter(spark);
        return extent2;
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(ITestResult result) throws IOException {
        spark = report.createTest(result.getMethod().getMethodName());
        com = new Common(spark);
        com.browserInit();
    }

    @AfterMethod(alwaysRun = true)
    public void closeSetup() {
        com.quitBrowser();
    }

    @Parameters({"Project", "dataFile"})
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(String Project, String dataFile) throws IOException {
        zipFolder();//Will ZIP LastRun report and ZIP in to resultBackup Folder
        deleteFolder();//Deleting Report Folder
        deleteOldFiles();//Deleting 7 days before backup results
        //setSystemOutToFile();
        createSparkReport();
        Common suite = new Common(Project, dataFile);
        data = suite.getDataObject();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        report.flush();
        System.out.println("Extent Report Flushed");
        com.quitBrowser();
    }
}
