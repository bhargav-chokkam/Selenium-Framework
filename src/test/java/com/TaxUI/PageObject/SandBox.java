package com.TaxUI.PageObject;

import Utility.Common;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SandBox extends Common {
    @FindBy(xpath = "//a[contains(@aria-label,'Sandbox')]")
    WebElement Sandbox;
    @FindBy(xpath = "//a[contains(@aria-label,'TaxUI QA')]")
    WebElement TaxUI;

    private final WebDriver driver;
    private final ExtentTest spark;

    public SandBox(ExtentTest spark) {
        this.spark = spark;
        this.driver = getDriverInstance();
        PageFactory.initElements(driver, this);
    }

    public void oktaSandBox() {
        clickOnElement(Sandbox);
    }

    public void taxUI() {
        clickOnElement(TaxUI);
    }
}