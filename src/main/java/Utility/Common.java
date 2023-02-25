package Utility;

import com.aventstack.extentreports.ExtentTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.*;

public class Common {
    private static final Properties prop; // Property File
    private static WebDriver driver;
    private static ExtentTest spark; // Spark Reporter
    private static WebDriverWait gWait; // Global Wait

    static {
        prop = new Properties();
        String propPath = "//src//main//java//Utility//propFile.properties";
        FileInputStream file = null;
        try {
            file = new FileInputStream(System.getProperty("user.dir") + propPath);
        } catch (FileNotFoundException e) {
            System.out.println("Property File Not Found : " + e);
        }
        try {
            prop.load(file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private Map<String, String> data;

    public Common(ExtentTest spark) {
        Common.spark = spark;
    }


    public Common(String project, String dataFile) {
        String path = System.getProperty("user.dir") + "//src//test//java//com//" + project + "//TestData//" + dataFile + ".json";
        System.out.println("Test Data File Path: " + path);
        try {
            Object temp = new JSONParser().parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) temp;
            data = toMap(jsonObject);
        } catch (ParseException e) {
            System.out.println(e);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Common() {

    }

    private static String getLocator(WebElement element) {
        //Returns Locator from WebElement
        String returnElement = null;
        try {
            String temp = String.valueOf(element);
            String[] splitData = temp.split(">");
            returnElement = splitData[1];
        } catch (Exception e) {
            System.out.println("Get Locator Method Failed: " + e);
        }
        return returnElement;
    }

    private static Map<String, String> toMap(JSONObject object) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            if (object != null) {
                Iterator<String> keysItr = object.keySet().iterator();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Object value = object.get(key);
                    // if (value instanceof JSONArray) {
                    // value = toList((JSONArray) value);
                    // } else if (value instanceof JSONObject) {
                    // value = toMap((JSONObject) value);
                    // }
                    map.put(key, (String) value);
                    System.out.println("Key: " + key + ", Value: " + value);
                }
                System.out.println("Stored JSON Data into Map");
            } else {
                map = null;
                System.out.println("JSON is empty, returning null");
            }
        } catch (Exception e) {
            System.out.println("Failed to process JSON data to Map: " + e);
        }
        return map;
    }

    private static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public Map<String, String> getDataObject() {
        System.out.println("Requested for Data Object");
        return data;
    }

    public String getScreenshot(String imageName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String destination = System.getProperty("user.dir") + "//report//" + imageName + ".png";
        try {
            FileUtils.copyFile(source, new File(destination));
            spark.info("Screenshot Captured at Path : " + destination);
        } catch (IOException e) {
            spark.fail(e);
        }
        return destination;
    }

    public void browserInit() {
        String browser = System.getProperty("browser") != null ? System.getProperty("browser") : prop.getProperty("browser");
        System.out.println("Launching Browser: " + browser);
        if (browser.contains("CHROME")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOptions = new ChromeOptions();
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("download.default_directory", System.getProperty("user.dir") + "//report//");
            chromeOptions.setExperimentalOption("prefs", prefs);
            driver = new ChromeDriver(chromeOptions);
        } else if (browser.contains("FIREFOX")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.dir", System.getProperty("user.dir") + "//report//");
            FirefoxOptions option = new FirefoxOptions();
            option.setProfile(profile);
            driver = new FirefoxDriver(option);
        } else {
            System.out.println("Unsupported Browser.");
            System.exit(0);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        gWait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(prop.getProperty("baseWaitTime"))));
        spark.info("Browser Opened Successfully");
    }

    public String readProperty(String Key) {
        String value = null;
        spark.info("Reading Data from Property File");
        try {
            value = prop.getProperty(Key);
            spark.info("Key: '" + Key + "' Value: '" + value + "'");
        } catch (Exception e) {
            spark.fail(e);
        }
        return value;
    }

    public void getUrl(String Url) {
        driver.get(Url);
        spark.info("Url Launched: " + Url);
    }

    public void closeBrowser() {
        try {
            driver.close();
            spark.info("Browser Closed");
        } catch (Exception e) {
            spark.fail(e);
        }
    }

    public WebDriver getDriverInstance() {
        spark.info("Requested for Driver Object");
        return driver;
    }

    public void writeToJson(String fileName, JSONArray object) {
        // Not tested
        String path = System.getProperty("user.dir") + "//report//" + fileName + ".json";
        try (FileWriter file = new FileWriter(path)) {
            file.write(object.toJSONString());
            file.flush();
        } catch (IOException e) {
            spark.fail(e);
        }
    }

    public void waitUrlToBe(Duration time, String actualUrl) {
        WebDriverWait wait = new WebDriverWait(driver, time);
        try {
            wait.until(ExpectedConditions.urlToBe(actualUrl));
            spark.info("Waited till Url Matched");
        } catch (TimeoutException e) {
            spark.fail(e);
        }
    }

//    public void waitForGivenTime(Duration time) {
//        WebDriverWait wait = new WebDriverWait(driver, time);
//        try {
//            wait.until(ExpectedConditions.)
//        } catch () {
//
//        }
//    }

    public String getCurrentUrl() {
        String currentUrl = driver.getCurrentUrl();
        spark.info("Current Url is: " + currentUrl);
        return currentUrl;
    }

    public String getTitle() {
        String title = driver.getTitle();
        spark.info("Current Title is: " + title);

        return title;

    }

    public void refreshBrowser() {
        driver.navigate().refresh();
        spark.info("Browser Refreshed");
    }

    public void navigateTo(String Url) {

        driver.navigate().to(Url);
        waitUrlToBe(Duration.ofSeconds(5), Url);
        spark.info("Navigated to: " + Url);

    }

    public void alertSendKeys(String value) {
        gWait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().sendKeys(value);
        spark.info("Sent Keys to Alert Box: " + value);
    }

    public String alertGetText() {
        gWait.until(ExpectedConditions.alertIsPresent());
        String Text = driver.switchTo().alert().getText();
        spark.info("Text from Alert: " + Text);
        return Text;
    }

    public void alertAccept() {
        gWait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        spark.info("Accepted Alert");
    }

    public void alertDismiss() {
        gWait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
        spark.info("Dismissed Alert");
    }

    public void switchToFrameByIndex(int index) {
        gWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
        driver.switchTo().frame(index);
        spark.info("Frame Switched By Index: " + index);
    }

    public void switchToFrameByNamOrID(String nameOrId) {
        try {
            gWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
            driver.switchTo().frame(nameOrId);
            spark.info("Frame Switched By NamOrID: " + nameOrId);
        } catch (NoSuchFrameException e) {
            spark.fail(e);
        } catch (TimeoutException k) {
            spark.fail(k);
        }
    }

    public void switchToFrameByWebElement(WebElement element) {
        gWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
        driver.switchTo().frame(element);
        spark.info("Frame Switched By element: " + getLocator(element));
    }

    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
        spark.info("Frame Switched To Parent");
    }

    public void switchToDefaultFrame() {
        driver.switchTo().defaultContent();
        spark.info("Frame Switched To Default Content");
    }

    public void quitBrowser() {
        driver.quit();
        System.out.println("Quit Browser Method Executed");
    }

    public void sendKeysToElement(WebElement element, String value) {
        waitTillVisibilityOfElement(Duration.ofSeconds(5), element);
        element.sendKeys(value);
        spark.info("Entered: " + value + " Successfully to: " + getLocator(element));
    }

    public void clickOnElement(WebElement element) {
        waitTillElementToBeClickable(Duration.ofSeconds(5), element);
        element.click();
        spark.info("Clicked Successfully: " + getLocator(element));
    }

    public void submitForm(WebElement element) {
        gWait.until(ExpectedConditions.elementToBeClickable(element));
        element.submit();
        spark.info("Submitted Successfully: " + getLocator(element));
    }

    public String getTextFromElement(WebElement element) {
        String extractedText = element.getText();
        spark.info("Text extracted: " + extractedText + " from element: " + getLocator(element));
        return extractedText;
    }

    public void clearElementField(WebElement element) {
        spark.info("Text before clearing field: " + element.getText());
        element.clear();
        spark.info("Cleared Successfully");
    }

    public boolean isElementDisplayed(WebElement element) {
        boolean flag = element.isDisplayed();
        if (flag = true) {
            spark.info("Element Displayed: " + getLocator(element));
        } else {
            spark.info("Element not Displayed: " + getLocator(element));
        }
        return flag;
    }

    public boolean isElementEnabled(WebElement element) {
        boolean flag = element.isEnabled();
        if (flag = true) {
            spark.info("Element Enabled: " + getLocator(element));
        } else {
            spark.info("Element not Enabled: " + getLocator(element));
        }
        return flag;
    }

    public boolean isElementSelected(WebElement element) {
        boolean flag = element.isSelected();
        if (flag = true) {
            spark.info("Element Selected: " + getLocator(element));
        } else {
            spark.info("Element not Selected: " + getLocator(element));
        }
        return flag;
    }

    public void waitTillTitleIs(Duration waitTime, String title) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        wait.until(ExpectedConditions.titleIs(title));
        spark.info("Waited till title is Matched. Title: " + title);

    }

    public void waitTillVisibilityOfElement(Duration waitTime, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            spark.info("Waited till visibilityOf Element: " + getLocator(element));
        } catch (TimeoutException e) {
            spark.info("Failed waitTillVisibilityOfElement Method");
        }

    }

    public void waitTillElementToBeClickable(Duration waitTime, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            spark.info("Waited till elementToBeClickable: " + getLocator(element));
        } catch (TimeoutException e) {
            spark.info("Failed waitTillElementToBeClickable Method");
        }
    }

    public void waitTillTextToBePresentInElementValue(Duration waitTime, WebElement element, String value) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        wait.until(ExpectedConditions.textToBePresentInElementValue(element, value));
        spark.info("Waited till textToBePresentInElementValue: " + value);
    }

    public void waitTillInvisibilityOf(Duration waitTime, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        wait.until(ExpectedConditions.invisibilityOf(element));
        spark.info("Waited till invisibilityOf: " + getLocator(element));
    }

    public void clickCheckBox(WebElement element) {
        spark.info("Before Clicking on CheckBox: " + element.isSelected());
        if (!element.isSelected()) {
            element.click();
            spark.info("After Clicking on CheckBox: " + element.isSelected());
        } else {
            spark.info("CheckBox already Checked");
        }
    }

    public void UnClickCheckBox(WebElement element) {
        spark.info("Before Clicking on CheckBox: " + element.isSelected());
        if (element.isSelected()) {
            element.click();
            spark.info("After Clicking on CheckBox: " + element.isSelected());
        } else {
            spark.info("CheckBox already UnChecked");
        }
    }

    public void rightClickOnElement(WebElement element) {
        Actions action = new Actions(driver);
        action.contextClick(element);
        action.build().perform();
        spark.info("Right Clicked on given Element: " + getLocator(element));
    }

    public void doubleClickOnElement(WebElement element) {
        Actions action = new Actions(driver);
        action.doubleClick(element);
        action.build().perform();
        spark.info("Double Clicked on given Element: " + getLocator(element));
    }

    public void clickAndHoldOnElement(WebElement element) {
        Actions action = new Actions(driver);
        action.clickAndHold(element);
        action.build().perform();
        spark.info("Clicked and Held on given Element: " + getLocator(element));
    }

    public void dragAndDropByElements(WebElement source, WebElement target) {
        Actions action = new Actions(driver);
        action.dragAndDrop(source, target);
        action.build().perform();
        spark.info("Dragged and Dropped. Source: " + getLocator(source) + " Target: " + getLocator(target));
    }

    public void moveMouseToElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element);
        action.build().perform();
        spark.info("Mouse moved to given element: " + getLocator(element));
    }

    public void scrollToElement(WebElement element) {
        Actions action = new Actions(driver);
        action.scrollToElement(element);
        action.build().perform();
        spark.info("Mouse moved to given element: " + getLocator(element));
    }

    public void mouseRelease() {
        Actions action = new Actions(driver);
        action.release();
        action.build().perform();
        spark.info("Mouse Released");
    }

    public void KeyDownOnKeyBoard(String Key) {
        Actions action = new Actions(driver);
        switch (Key) {
            case "ALT":
                action.keyDown(Keys.ALT);
                break;
            case "CTRL":
                action.keyDown(Keys.CONTROL);
                break;
            case "SHIFT":
                action.keyDown(Keys.SHIFT);
                break;
        }
        action.build().perform();
        spark.info("Key Pressed Down: " + Key);
    }

    public void KeyUpOnKeyBoard(String Key) {
        Actions action = new Actions(driver);
        switch (Key) {
            case "ALT":
                action.keyUp(Keys.ALT);
                break;
            case "CTRL":
                action.keyUp(Keys.CONTROL);
                break;
            case "SHIFT":
                action.keyUp(Keys.SHIFT);
                break;
        }
        action.build().perform();
        spark.info("Key Released: " + Key);
    }

    public void pressKeyStroke(String Key) {
        Actions action = new Actions(driver);
        switch (Key) {
            case "DELETE":
                action.sendKeys(Keys.DELETE);
                break;
            case "SPACE":
                action.sendKeys(Keys.SPACE);
                break;
            case "ESCAPE":
                action.sendKeys(Keys.ESCAPE);
                break;
            case "F5":
                action.sendKeys(Keys.F5);
                break;
            case "ENTER":
                action.sendKeys(Keys.ENTER);
                break;
            case "TAB":
                action.sendKeys(Keys.TAB);
                break;
        }
        action.build().perform();
        spark.info("Key Pressed: " + Key);
    }

    public void switchWindowByTitle(String title) {
        Map<String, String> map = new HashMap<>();
        try {
            Set<String> windows = driver.getWindowHandles();
            Iterator<String> it = windows.iterator();
            while (it.hasNext()) {
                String window = it.next();
                driver.switchTo().window(window);
                String windowTitle = driver.getTitle();
                map.put(windowTitle, window);
            }
            driver.switchTo().window(map.get(title));
            spark.info("Window Switched to: " + title);
        } catch (Exception e) {
            spark.fail(e);
        }
    }

    public void uploadFile(String exePath) {
        try {
            Runtime.getRuntime().exec(exePath);
            spark.info("Upload File Executed: " + exePath);
        } catch (IOException e) {
            spark.fail(e);
        }
    }

    public void robotKeyPress(String Key) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        switch (Key) {
            case "ESCAPE":
                robot.keyPress(KeyEvent.VK_ESCAPE);
                break;
            case "CAPS_LOCK":
                robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                break;
            case "ENTER":
                robot.keyPress(KeyEvent.VK_ENTER);
                break;
            case "SHIFT":
                robot.keyPress(KeyEvent.VK_SHIFT);
                break;
            case "TAB":
                robot.keyPress(KeyEvent.VK_TAB);
                break;
            case "CONTROL":
                robot.keyPress(KeyEvent.VK_CONTROL);
                break;
            case "ALT":
                robot.keyPress(KeyEvent.VK_ALT);
                break;
            case "BACK_SPACE":
                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                break;
            case "A":
                robot.keyPress(KeyEvent.VK_A);
                break;
            case "B":
                robot.keyPress(KeyEvent.VK_B);
                break;
            case "C":
                robot.keyPress(KeyEvent.VK_C);
                break;
            case "D":
                robot.keyPress(KeyEvent.VK_D);
                break;
            case "E":
                robot.keyPress(KeyEvent.VK_E);
                break;
            case "F":
                robot.keyPress(KeyEvent.VK_F);
                break;
            case "G":
                robot.keyPress(KeyEvent.VK_G);
                break;
            case "H":
                robot.keyPress(KeyEvent.VK_H);
                break;
            case "I":
                robot.keyPress(KeyEvent.VK_I);
                break;
            case "J":
                robot.keyPress(KeyEvent.VK_J);
                break;
            case "K":
                robot.keyPress(KeyEvent.VK_K);
                break;
            case "L":
                robot.keyPress(KeyEvent.VK_L);
                break;
            case "M":
                robot.keyPress(KeyEvent.VK_M);
                break;
            case "N":
                robot.keyPress(KeyEvent.VK_N);
                break;
            case "O":
                robot.keyPress(KeyEvent.VK_O);
                break;
            case "P":
                robot.keyPress(KeyEvent.VK_P);
                break;
            case "Q":
                robot.keyPress(KeyEvent.VK_Q);
                break;
            case "R":
                robot.keyPress(KeyEvent.VK_R);
                break;
            case "S":
                robot.keyPress(KeyEvent.VK_S);
                break;
            case "T":
                robot.keyPress(KeyEvent.VK_T);
                break;
            case "U":
                robot.keyPress(KeyEvent.VK_U);
                break;
            case "V":
                robot.keyPress(KeyEvent.VK_V);
                break;
            case "W":
                robot.keyPress(KeyEvent.VK_W);
                break;
            case "X":
                robot.keyPress(KeyEvent.VK_X);
                break;
            case "Y":
                robot.keyPress(KeyEvent.VK_Y);
                break;
            case "Z":
                robot.keyPress(KeyEvent.VK_Z);
                break;
            case "0":
                robot.keyPress(KeyEvent.VK_0);
                break;
            case "1":
                robot.keyPress(KeyEvent.VK_1);
                break;
            case "2":
                robot.keyPress(KeyEvent.VK_2);
                break;
            case "3":
                robot.keyPress(KeyEvent.VK_3);
                break;
            case "4":
                robot.keyPress(KeyEvent.VK_4);
                break;
            case "5":
                robot.keyPress(KeyEvent.VK_5);
                break;
            case "6":
                robot.keyPress(KeyEvent.VK_6);
                break;
            case "7":
                robot.keyPress(KeyEvent.VK_7);
                break;
            case "8":
                robot.keyPress(KeyEvent.VK_8);
                break;
            case "9":
                robot.keyPress(KeyEvent.VK_9);
                break;
            case ";":
                robot.keyPress(KeyEvent.VK_SEMICOLON);
                break;
            case ":":
                robot.keyPress(KeyEvent.VK_COLON);
                break;
            case "/":
                robot.keyPress(KeyEvent.VK_SLASH);
                break;
            case "-":
                robot.keyPress(KeyEvent.VK_MINUS);
                break;
            case "F5":
                robot.keyPress(KeyEvent.VK_F5);
                break;
        }
        spark.info("Robot Key Pressed: " + Key);
    }

    public void robotKeyRelease(String Key) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        switch (Key) {
            case "ESCAPE":
                robot.keyRelease(KeyEvent.VK_ESCAPE);
                break;
            case "CAPS_LOCK":
                robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
                break;
            case "ENTER":
                robot.keyRelease(KeyEvent.VK_ENTER);
                break;
            case "SHIFT":
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case "TAB":
                robot.keyPress(KeyEvent.VK_TAB);
                break;
            case "CONTROL":
                robot.keyRelease(KeyEvent.VK_CONTROL);
                break;
            case "ALT":
                robot.keyRelease(KeyEvent.VK_ALT);
                break;
            case "BACK_SPACE":
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                break;
        }
    }

    public void robotMouseMove(int x, int y) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        robot.mouseMove(x, y);
        spark.info("Moved Mouse: " + x + " : " + y);
    }

    public void robotMousePress(String mouseKey) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        switch (mouseKey) {
            case "LEFTCLICK":
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case "RIGHTCLICK":
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                break;
            case "MIDDLECLICK":
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                break;
        }
        spark.info("Robot Key Pressed: " + mouseKey);
    }

    public void robotMouseRelease(String mouseKey) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        switch (mouseKey) {
            case "LEFTCLICK":
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case "RIGHTCLICK":
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                break;
            case "MIDDLECLICK":
                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                break;
        }
        spark.info("Robot Key Released: " + mouseKey);
    }
}