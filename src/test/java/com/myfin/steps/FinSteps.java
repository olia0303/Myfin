package com.myfin.steps;

import com.codeborne.selenide.Configuration;
import com.myfin.api.RatesAdapter;
import com.myfin.model.Currency;
import com.myfin.model.Rate;
import com.myfin.pageobjects.FinPage;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.CapabilitiesGenerator;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@Log4j2
public class FinSteps {
    private FinPage finPage;
    private WebDriver driver;

    public FinSteps(){
        finPage = new FinPage();
    }

    public void startBrowser() {
        ChromeOptions options = CapabilitiesGenerator.getChromeOptions();
        driver = new ChromeDriver(options);
        Configuration.timeout = 30000;
        setWebDriver(driver);
        getWebDriver().manage().window().maximize();
    }

    @Step("Open My Fin page")
    public void openPage(String url){
        startBrowser();
        finPage.openPage(url)
                .isPageOpened();
    }
    
    @Step("Validate Weighted Average Exchange Rates")
    public FinSteps validateAverageExRatesForTodayTrading(Currency currency, double value) {
        double actualValue = finPage.getWeightedAverageCourses(currency.getName());
        assertThat(actualValue, (greaterThan(value)));
        return this;
    }

    @Step("Validate compare exchange rates")
    public FinSteps validateCompareExchangeRates(Currency currency, String id, String url) {
        double weightedAverageRates = finPage.getWeightedAverageCourses(currency.getName());
        Rate obj = new RatesAdapter().get(id, url);
        assertThat(weightedAverageRates, (equalTo(obj.getOfficialRate())));
        return this;
    }

    public void closeBrowser() {
        log.debug("Closing Chrome instance " + driver);
        if (null != driver) {
            driver.quit();
        }
    }
}