package org.springsandbox.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springsandbox.util.AppProperties;
import org.springsandbox.enums.WaitCondition;
import org.springsandbox.waits.Waits;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IndexPage {
    private final WebDriver driver;
    private final Map<String, String> envVars = AppProperties.getProperties();
    private final String pageUrl = envVars.get("APP_URL");
    private final Waits waits;

    public IndexPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.waits = new Waits(driver);
    }

    @FindBy(xpath = "//span[contains(text(), 'Loading')]")
    private WebElement loadingSpinner;

    @FindAll(@FindBy(xpath = "//li[contains(@class, 'chakra-wrap__listitem')]"))
    private List<WebElement> customerCards;

    @FindBy(xpath = "//button[contains(text(), 'Create customer')]")
    private WebElement createCustomerButton;

    @FindBy(xpath = "//footer[contains(@class, 'chakra-modal__footer')]/button[text()='Delete']")
    private WebElement confirmDeleteButton;

    public String getPageUrl() {
        return pageUrl;
    }

    public void goTo() {
        driver.get(pageUrl);
    }

    public List<WebElement> getCustomerCards() {
        try {
            waits.waitForElements(WaitCondition.VISIBLE, customerCards);
        } catch (StaleElementReferenceException e) {
            PageFactory.initElements(driver, this);
            waits.waitForElements(WaitCondition.VISIBLE, customerCards);
        }
        return customerCards;
    }

    public WebElement getCustomerCardWithEmail(String email) {
        return getCustomerCards().stream()
                .filter(webEl -> webEl
                        .findElement(By.xpath(".//p[contains(text(), '@')]"))
                        .getText()
                        .equals(email))
                .findFirst()
                .orElse(null);
    }

    public String getCustomerNameFromCard(WebElement customerCard) {
        try {
            waits.waitForElement(WaitCondition.VISIBLE, customerCard);
        } catch (StaleElementReferenceException e) {
            PageFactory.initElements(driver, this);
            waits.waitForElement(WaitCondition.VISIBLE, customerCard);
        }
        return customerCard
                .findElement(By.xpath(".//h2"))
                .getText();
    }

    public Integer getCustomerAgeFromCard(WebElement customerCard) {
        try {
            waits.waitForElement(WaitCondition.VISIBLE, customerCard);
        } catch (StaleElementReferenceException e) {
            PageFactory.initElements(driver, this);
            waits.waitForElement(WaitCondition.VISIBLE, customerCard);
        }
        return Integer
                .parseInt(customerCard
                        .findElement(By.xpath(".//p/span"))
                        .getText()
                        .split(" ")[1]
                );
    }

    public String getCustomerGenderFromCard(WebElement customerCard) {
        try {
            waits.waitForElement(WaitCondition.VISIBLE, customerCard);
        } catch (StaleElementReferenceException e) {
            PageFactory.initElements(driver, this);
            waits.waitForElement(WaitCondition.VISIBLE, customerCard);
        }
        return customerCard
                .findElement(By.xpath(".//span[contains(@class, 'chakra-badge')]"))
                .getText();
    }

    public WebElement getCreateCustomerButton() {
        try {
            waits.waitForElement(WaitCondition.VISIBLE, createCustomerButton);
        } catch (StaleElementReferenceException e) {
            PageFactory.initElements(driver, this);
            waits.waitForElement(WaitCondition.VISIBLE, createCustomerButton);
        }
        return createCustomerButton;
    }

    public void clickCreateCustomerButton() {
        try {
            waits.waitForElement(WaitCondition.CLICKABLE, createCustomerButton);
        } catch (StaleElementReferenceException e) {
            PageFactory.initElements(driver, this);
            waits.waitForElement(WaitCondition.CLICKABLE, createCustomerButton);
        }
        createCustomerButton.click();
    }

    public void clickDeleteCustomer(WebElement customerCard) {
        WebElement deleteButton = customerCard.findElement(By.xpath(".//button[text()='Delete']"));
        waits.waitForElement(WaitCondition.CLICKABLE, deleteButton);
        // TODO: this is done with duct tape because success toast in displayed on top of delete button
        //  and prevents from clicking it until it disappears
        //  Solution 0 (shitty): scroll down a bit
        //  Solution 1 (ok): close toast
        //  CBA doing any of them atm so...
        Unreliables.retryUntilSuccess(10, TimeUnit.SECONDS, () -> {
            deleteButton.click();
            return null;
        });
    }

    public void clickEditCustomer(WebElement customerCard) {
        WebElement editButton = customerCard.findElement(By.xpath(".//button[text()='Edit']"));
        waits.waitForElement(WaitCondition.CLICKABLE, editButton);
        Unreliables.retryUntilSuccess(10, TimeUnit.SECONDS, () -> {
            editButton.click();
            return null;
        });

    }

    public void confirmDeleteCustomer() {
        waits.waitForElement(WaitCondition.CLICKABLE, confirmDeleteButton);
        confirmDeleteButton.click();
    }

}
