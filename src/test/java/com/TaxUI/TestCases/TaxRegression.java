package com.TaxUI.TestCases;


import Utility.BaseClass;
import com.TaxUI.PageObject.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TaxRegression extends BaseClass {

    @Test(description = "Create Tax Rule", groups = {"TaxRule"})

    public void createRule() {
        com.getUrl(com.readProperty("OktaUrl"));
        SandBox sandBox = new SandBox(spark);
        sandBox.oktaSandBox();
        com.switchWindowByTitle(data.get("sandBoxTitle"));
        sandBox.taxUI();
        com.switchWindowByTitle(data.get("taxUITitle"));
        TaxHome taxHome = new TaxHome(spark);
        taxHome.clickTaxDefinition();
        TaxCreate taxCreate = new TaxCreate(spark);
        taxCreate.clickOnCreateRule();
        taxCreate.enterRuleName(data.get("RuleName"));
        taxCreate.enterCountry(data.get("Country"));
        taxCreate.enterCurrency(data.get("Currency"));
        taxCreate.enterSendPay(data.get("SendPay"));
        taxCreate.enterComments(data.get("Comment"));
        taxCreate.createBaseRule();
        taxCreate.enterStartDate(data.get("StartDate"));
        taxCreate.enterEndDate(data.get("EndDate"));
        taxCreate.enterTaxCalculatedOn(data.get("TaxCalculatedOn"));
        taxCreate.enterSourceOfTax(data.get("SourceOfTax"));
        taxCreate.enterTaxBracket(data.get("Ceiling"), data.get("FlatFee"));
        taxCreate.enterTaxTag(data.get("TaxTag"));
        taxCreate.confirmRule();
        taxCreate.createRule();
    }

    @Test(groups = {"TaxRuleView"})
    public void viewTax() {
        com.getUrl(com.readProperty("OktaUrl"));
        SandBox sandBox = new SandBox(spark);
        sandBox.oktaSandBox();
        com.switchWindowByTitle(data.get("sandBoxTitle"));
        sandBox.taxUI();
        com.switchWindowByTitle(data.get("taxUITitle"));
        TaxHome taxHome = new TaxHome(spark);
        taxHome.clickTaxDefinition();
        taxHome.filterName(data.get("viewTaxName"));
        taxHome.viewRule();
        taxHome.viewDone();
    }

    @Test(groups = {"TaxRuleUpdate"})
    public void updateTax() {
        com.getUrl(com.readProperty("OktaUrl"));
        SandBox sandBox = new SandBox(spark);
        sandBox.oktaSandBox();
        com.switchWindowByTitle(data.get("sandBoxTitle"));
        sandBox.taxUI();
        com.switchWindowByTitle(data.get("taxUITitle"));
        TaxHome taxHome = new TaxHome(spark);
        taxHome.clickTaxDefinition();
        taxHome.filterName(data.get("viewTaxName"));
        taxHome.editRule();
        UpdateTax updateTax = new UpdateTax(spark);
        updateTax.setComment(data.get("Comment"));
        updateTax.clickOnConfirmButton();
        updateTax.clickOnUpdateButton();
    }

    @Test(groups = {"TaxRuleDeleteActive"})
    public void deleteInActive() {
        com.getUrl(com.readProperty("OktaUrl"));
        SandBox sandBox = new SandBox(spark);
        sandBox.oktaSandBox();
        com.switchWindowByTitle(data.get("sandBoxTitle"));
        sandBox.taxUI();
        com.switchWindowByTitle(data.get("taxUITitle"));
        TaxHome taxHome = new TaxHome(spark);
        taxHome.clickTaxDefinition();
        taxHome.filterName(data.get("viewTaxName"));
        taxHome.deleteRule();
        DeletePage deletePage = new DeletePage(spark);
        deletePage.clickOnDeleteButton();
        deletePage.clickOnConfirmButton();
    }

    @Test(groups = {"TaxRuleDeleteInActive"})
    public void deleteActive() {
        com.getUrl(com.readProperty("OktaUrl"));
        SandBox sandBox = new SandBox(spark);
        sandBox.oktaSandBox();
        com.switchWindowByTitle(data.get("sandBoxTitle"));
        sandBox.taxUI();
        com.switchWindowByTitle(data.get("taxUITitle"));
        TaxHome taxHome = new TaxHome(spark);
        taxHome.clickTaxDefinition();
        taxHome.filterName(data.get("viewTaxName"));
        taxHome.deleteRule();
        DeletePage deletePage = new DeletePage(spark);
        Assert.assertTrue(deletePage.clickOnDisabledDeleteButton());
    }
}
