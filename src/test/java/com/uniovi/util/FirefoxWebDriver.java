package com.uniovi.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxWebDriver extends EventFiringWebDriver {

    private static final WebDriver webdriver;
    private static final Logger logger = LoggerFactory.getLogger(FirefoxWebDriver.class);

    private static final Thread CLOSE_THREAD = new Thread() {
        @Override
        public void run() {
            webdriver.close();
        }
    };

    static {
        WebDriverManager.firefoxdriver().setup();
        if (System.getenv("headless") != null && System.getenv("headless").equals("true")) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            webdriver = new FirefoxDriver(options);
        } else {
            webdriver = new FirefoxDriver();
        }

        //Runtime.getRuntime().addShutdownHook(CLOSE_THREAD);
    }

    public FirefoxWebDriver() {
        super(webdriver);
    }

    @Override
    public void close() {
        if (Thread.currentThread() != CLOSE_THREAD) {
           logger.info("You shouldn't close this WebDriver. It's shared and will close when the JVM exits.");
        }
        super.close();
    }
}
