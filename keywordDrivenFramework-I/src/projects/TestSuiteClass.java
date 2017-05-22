/**
 * Last Changes Done on Jan 20, 2015 12:41:21 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */

package projects;


import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;


import vlib.FileLib;
import vlib.MobileTestClass_Methods;
import vlib.XlsLib;


public class TestSuiteClass {

	public static String executionResult;
	public static String rerunExecutionResult;

	public static String resultFileLocation;
	public static PropertiesConfiguration propertyConfigFile;
	public static TreeMap<String, Integer> totalTC = new TreeMap<String, Integer>();
	public static boolean isFresh;
	public static boolean rerun;
	public static Map<String, ArrayList<String>> rerunClassNames=new HashMap<String, ArrayList<String>>();
	public static String local_logFileLocation;
	public static String local_logFileName;

	public static String suiteStartTime;
	public static String suiteEndTime;
	public static String executedOnMachine;
	public static String environment;

	public static String AUTOMATION_HOME;


	//Declaring logger
	Logger logger = Logger.getLogger(TestSuiteClass.class.getName());

	@BeforeSuite
	public void beforeSuite(String ReRun)
	{

		try
		{
			logger.info("###################################################################");
			logger.info("******* Test Started At Time ********: " +MobileTestClass_Methods.DateTimeStamp("MMddyyyy_hhmmss"));
			logger.info("###################################################################");

			/** setting up automation_home */
			AUTOMATION_HOME = TestSuiteClass.class.getProtectionDomain().getCodeSource().getLocation().getFile().toString().replace("/bin/", "");

			/** 1. Initialize configuration */
			MobileTestClass_Methods.InitializeConfiguration();

			/** Getting suite start time, this time will also be saved in id column of each module specific table at the time of saving results. */
			suiteStartTime = MobileTestClass_Methods.DateTimeStamp("yyyy-MM-dd HH:mm:ss");

			/** Loading log4j.properties file for logger and creating logs folder in advance */
			PropertyConfigurator.configure(TestSuiteClass.AUTOMATION_HOME.concat("/conf/log4j.properties"));
			FileLib.CreateDirectory(TestSuiteClass.AUTOMATION_HOME.concat("/logs"));


			/** 4.Check if result file exists or not. */
			String resultFileName =  null;

			String dateTimeStmap = MobileTestClass_Methods.DateTimeStamp("MMddyyyy_hhmmss");

			resultFileName = "Main_Result_".concat(dateTimeStmap);

			resultFileLocation = TestSuiteClass.AUTOMATION_HOME.concat("/results/").concat(resultFileName).toString();	
			File ResultFile = new File(resultFileLocation);

			if(!(ResultFile.exists()))
			{
				logger.info("Main Result Folder doesn't exist at " + resultFileLocation);

				boolean b = ResultFile.mkdirs();

				if(b)
				{
					logger.info("Main Result folder is created successfully ");
				}
				else
				{
					logger.info("Main Result folder can not be created");
				}
			}

			executionResult = resultFileLocation.concat("/").concat(resultFileName).concat(".xls");

			logger.info("Main Result file location : " + executionResult);

			XlsLib result = new XlsLib();

			/** 4. Create Empty Excel file */
			result.emptyExcel(executionResult);
		}
		catch(Exception e)
		{
			logger.error("Exception handled during execution of beforeTestSuite. ", e); 
		}

	}

	@AfterSuite
	public void afterSuite()  
	{
		try{

			/** Generate Summary of Results */ 
			XlsLib test = new XlsLib();
			test.generateFinalResult(executionResult);
		

			/** Printing Test End Time. */
			logger.info("###################################################################");
			logger.info("******* Test End Time ********: " +MobileTestClass_Methods.DateTimeStamp("MMddyyyy_hhmmss"));
			logger.info("###################################################################");

		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}

	}

}
