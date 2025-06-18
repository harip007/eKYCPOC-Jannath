package com.example.ekyc_service.constants;

import org.openqa.selenium.By;

public class LocatorConstants {

    public static final By MODAL_LOCATOR = By.xpath("//div[@id='myModalVerify' and contains(@class, 'in')]");
    public static final By RESULT_TABLE_LOCATOR  = By.id("tableResult");
    public static final By SWEET_ALERT_LOCATOR  = By.cssSelector("div.sweet-alert.showSweetAlert.visible");
    public static final By SWEET_ALERT_MSG_LOCATOR  = By.tagName("p");
    public static final By SWEET_ALERT_OK_LOCATOR  = By.cssSelector("button.confirm");
    public static final By MODAL_CLOSE_BUTTON_LOCATOR  = By.xpath(".//button[contains(text(),'CLOSE')]");

    public static final By MODAL_SCAN_LOCATOR = By.id("myModalVerifySCAN");
    public static final By TABLE_RESULT_LOCATOR = By.id("tableResultScan");
    public static final By CLOSE_BUTTON_LOCATOR = By.id("closeModalScan");
}
