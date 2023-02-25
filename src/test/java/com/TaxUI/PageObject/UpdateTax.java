package com.TaxUI.PageObject;

import Utility.Common;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class UpdateTax extends Common {
    @FindBy(xpath = "//input[@name='comment']")
    WebElement commentField;
    @FindBy(xpath = "//button[text()='Confirm']")
    WebElement confirmButton;
    @FindBy(xpath = "//button[text()='Update']")
    WebElement updateButton;
    private final WebDriver driver;
    private final ExtentTest spark;


    public UpdateTax(ExtentTest spark) {
        this.spark = spark;
        this.driver = getDriverInstance();
        PageFactory.initElements(driver, this);
    }

    public void setComment(String comment) {
        clearElementField(commentField);
        sendKeysToElement(commentField, comment);
        spark.addScreenCaptureFromPath(getScreenshot("UpdateScreen"), "UpdateScreen");
    }

    public void clickOnConfirmButton() {
        clickOnElement(confirmButton);
        spark.addScreenCaptureFromPath(getScreenshot("ConfirmButton"), "ConfirmButton");
    }

    public void clickOnUpdateButton() {
        clickOnElement(updateButton);
        waitTillInvisibilityOf(Duration.ofSeconds(2000), updateButton);
        spark.addScreenCaptureFromPath(getScreenshot("UpdateButton"), "UpdateButton");
    }
}
