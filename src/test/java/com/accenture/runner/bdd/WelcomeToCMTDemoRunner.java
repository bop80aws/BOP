package com.accenture.runner.bdd;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber; 

import org.junit.AfterClass;
import org.junit.BeforeClass;
import com.accenture.aaft.logger.CTLogger;
import com.accenture.aaft.report.ExtentManager;
import com.accenture.aaft.report.ExtentTestManager;
import com.relevantcodes.extentreports.ExtentReports;


@RunWith(Cucumber.class) 
@CucumberOptions(features = "target/test-classes/cucumber/features/", tags = { "@TouchlessTestingDemo" }, format = { "html:target/cucumber-report/step" },
glue = { "cucumber.stepdefs", "cucumber.stepdefs.WelcomeToCMTDemo" })
public class WelcomeToCMTDemoRunner {
	static ExtentReports extent;
	static String scriptName = "WelcomeToCMTDemo";


@BeforeClass
public static void runOnceBeforeClass() {
	extent = ExtentManager.getExtentManager();
	ExtentTestManager.startTest("Cucumber Test : " + scriptName, "WelcomeToCMTDemo");
	CTLogger.writeToLog("@BeforeClass WelcomeToCMTDemo extent - "+extent);
 }


@AfterClass
public static void runOnceAfterClass() {
	CTLogger.writeToLog("@AfterClass WelcomeToCMTDemo extent - "+extent);
	ExtentManager.getReporter().endTest(ExtentTestManager.getTest());
	ExtentManager.getReporter().flush();
 }
}
