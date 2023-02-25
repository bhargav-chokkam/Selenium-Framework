package com.TaxUI.PageObject;

import Utility.Common;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class DeletePage extends Common {
    @FindBy(xpath = "//button[text()='Delete']")
    WebElement deleteButton;
    @FindBy(xpath = "//button[text()='Confirm']")
    WebElement confirmButton;
    private final WebDriver driver;
    private final ExtentTest spark;

    public DeletePage(ExtentTest spark) {
        this.spark = spark;
        this.driver = getDriverInstance();
        PageFactory.initElements(driver, this);
    }

    public void clickOnDeleteButton() {
        clickOnElement(deleteButton);
        spark.addScreenCaptureFromPath(getScreenshot("deleteButton"), "deleteButton");
    }

    public void clickOnConfirmButton() {
        clickOnElement(confirmButton);
        waitTillInvisibilityOf(Duration.ofSeconds(2000), confirmButton);
        spark.addScreenCaptureFromPath(getScreenshot("confirmButton"), "confirmButton");
    }

    public boolean clickOnDisabledDeleteButton() {
        boolean flag = false;
        try {
            clickOnElement(deleteButton);
            flag = false;
        } catch (Exception e) {
            spark.info("DeleteButtonDisabled");
            flag = true;
        }
        return flag;
    }
}
