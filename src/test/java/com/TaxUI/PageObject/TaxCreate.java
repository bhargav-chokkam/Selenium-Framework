package com.TaxUI.PageObject;

import Utility.Common;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class TaxCreate extends Common {
    @FindBy(xpath = "//button[text()='Create']")
    WebElement createRule;
    @FindBy(xpath = "//input[@name='name']")
    WebElement rule;
    @FindBy(xpath = "//div[@name='country']//input")
    WebElement country;
    @FindBy(xpath = "//div[@name='currency']//input")
    WebElement currency;
    @FindBy(xpath = "//div[@name='sendPay']//input")
    WebElement sendPay;
    @FindBy(xpath = "//textarea[@name='comments']")
    WebElement comments;
    @FindBy(xpath = "(//button[text()='Create'])[2]")
    WebElement createRule2;
    @FindBy(xpath = "//label[text()='Start Date/time']//following::input")
    WebElement startDate;
    @FindBy(xpath = "//input[@class='rc-calendar-input ']")
    WebElement inputDate;
    @FindBy(xpath = "//a[text()='Ok']")
    WebElement Ok;
    @FindBy(xpath = "//label[text()='End Date/time']//following::input")
    WebElement endDate;
    @FindBy(xpath = "//div[@name='CalcOn']//input")
    WebElement CalcOn;
    @FindBy(xpath = "//div[@name='taxSource']//input")
    WebElement taxSource;
    @FindBy(xpath = "//tbody//div")
    WebElement Ceiling;
    @FindBy(xpath = "(//tbody//div)[2]")
    WebElement flatFee;
    @FindBy(xpath = "//button[text()='Submit']")
    WebElement taxSourceSubmit;
    @FindBy(xpath = "//div[@name='taxTag']//input")
    WebElement taxTag;
    @FindBy(xpath = "//button[text()='Confirm']")
    WebElement Confirm;
    @FindBy(xpath = "//button[text()='Create']")
    WebElement Create;
    @FindBy(xpath = "//*[text()='TAX DEFINITION']")
    WebElement confirmScreen;


    private final WebDriver driver;
    private final ExtentTest spark;

    public TaxCreate(ExtentTest spark) {
        this.spark = spark;
        this.driver = getDriverInstance();
        PageFactory.initElements(driver, this);
    }

    public void clickOnCreateRule() {
        waitTillElementToBeClickable(Duration.ofSeconds(5), createRule);
        clickOnElement(createRule);
    }

    public void enterRuleName(String ruleName) {
        waitTillElementToBeClickable(Duration.ofSeconds(2), rule);
        sendKeysToElement(rule, ruleName);
        pressKeyStroke("TAB");
    }

    public void enterCountry(String countryName) {
        sendKeysToElement(country, countryName);
        pressKeyStroke("TAB");
    }

    public void enterCurrency(String currencyName) {
        sendKeysToElement(currency, currencyName);
        pressKeyStroke("TAB");
    }

    public void enterSendPay(String sendPayName) {
        sendKeysToElement(sendPay, sendPayName);
        pressKeyStroke("TAB");
    }

    public void enterComments(String comment) {
        sendKeysToElement(comments, comment);
    }

    public void createBaseRule() {
        spark.addScreenCaptureFromPath(getScreenshot("createBaseRule"), "createBaseRule");
        clickOnElement(createRule2);
    }

    public void enterStartDate(String date) {
        clickOnElement(startDate);
        sendKeysToElement(inputDate, date);
        clickOnElement(Ok);
    }

    public void enterEndDate(String date) {
        clickOnElement(endDate);
        sendKeysToElement(inputDate, date);
        clickOnElement(Ok);
    }

    public void enterTaxCalculatedOn(String parameter) {
        sendKeysToElement(CalcOn, parameter);
        pressKeyStroke("ENTER");
    }

    public void enterSourceOfTax(String parameter) {
        sendKeysToElement(taxSource, parameter);
        pressKeyStroke("ENTER");
    }

    public void enterTaxBracket(String ceiling, String fee) {
        sendKeysToElement(Ceiling, ceiling);
        sendKeysToElement(flatFee, fee);
        clickOnElement(taxSourceSubmit);
    }

    public void enterTaxTag(String tag) {
        sendKeysToElement(taxTag, tag);
        pressKeyStroke("ENTER");
    }

    public void confirmRule() {
        spark.addScreenCaptureFromPath(getScreenshot("confirmRuleBefore"), "confirmRuleBefore");
        clickOnElement(Confirm);
        spark.addScreenCaptureFromPath(getScreenshot("confirmRuleAfter"), "confirmRuleAfter");
    }

    public void createRule() {
        clickOnElement(Create);
        waitTillInvisibilityOf(Duration.ofSeconds(3000), confirmScreen);
        spark.addScreenCaptureFromPath(getScreenshot("final"), "RuleCreated");
    }


}