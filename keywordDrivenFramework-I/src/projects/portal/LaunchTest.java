/**
 * Last Changes Done on Jan 16, 2015 12:04:40 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */

package projects.portal;

import java.awt.Robot;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import projects.TestSuiteClass;

import vlib.CaptureScreenShotLib;
import vlib.FileLib;
import vlib.KeyBoardActionsUsingRobotLib;
import vlib.MobileTestClass_Methods;
import vlib.XlsLib;


public class LaunchTest {

	static String suiteName = ""; 
	String testCaseFile;
	String testResultFile;
	File resultFile;
	HashMap<String, Boolean> map = new HashMap<>();
	Logger logger = Logger.getLogger(LaunchTest.class.getName());
	
	static Connection connectionServe;
	static JSONObject jsonObjectRepo = new JSONObject();


	/** setting up configuration before test */
	@SuppressWarnings("unused")
	@BeforeClass
	public void beforeClass() 
	{
		try
		{
			logger.info(" : ################### Portal Test Started. ########################");
			suiteName = "TransformerPortal";
			MobileTestClass_Methods.InitializeConfiguration();
			
			/** Initializing constructor of KeyBoardActionsUsingRobotLib and CaptureScreenShotLib here, 
			 * so that focus on chrome browser is not disturbed. 
			 */
			Robot rt = new Robot();
			KeyBoardActionsUsingRobotLib keyBoard = new KeyBoardActionsUsingRobotLib(rt);
			CaptureScreenShotLib captureScreenshot = new CaptureScreenShotLib(rt);
			
			connectionServe = MobileTestClass_Methods.CreateServeSQLConnection();
			
			testCaseFile = TestSuiteClass.AUTOMATION_HOME.toString().concat("/tc_cases/transformerportal/Test_Cases_Transformer.xls");
			logger.debug(" : Test Cases File Located at: "+testCaseFile);
			testResultFile = TestSuiteClass.resultFileLocation.concat("/transformerPortal/TestResults");

			resultFile = FileLib.CopyExcelFile(testCaseFile, testResultFile);
			logger.debug(" : Test Cases Result File Located at: "+resultFile);

			/** get object repository as json object */
			String objectRepo = TestSuiteClass.AUTOMATION_HOME.concat("/object_repository/portalObjectRepository/transformerPortal_ObjectRepository.xls");
			jsonObjectRepo = new GetObjectRepoAsJson().getObjectRepoAsJson(objectRepo);
			
			ReadTestCases readTest = new ReadTestCases();
			String testStepResultColumnLabel = readTest.tcStepResultColumn;
			String testStepSheetName = readTest.testStepSheet;

			WriteTestResults writeResult = new WriteTestResults();
			writeResult.addResultColumn(resultFile, testStepSheetName, testStepResultColumnLabel);

			String testSummaryResultColumnLabel = readTest.tcSummaryResultColumn;
			String testSummarySheetName = readTest.testCaseSummarySheet;
			writeResult.addResultColumn(resultFile, testSummarySheetName, testSummaryResultColumnLabel);
		}
		catch (Exception e)
		{
			logger.error(" : Error occurred before starting the portal test", e);
		}
	}


	/** running tests */
	@Test
	public void runTests()
	{
		ReadTestCases getrunnabletestcases = new ReadTestCases();

		/**
		 * Get all runnable test case id which has RUN = Yes from execution control sheet 
		 */
		List <String> tc_id = getrunnabletestcases.getRunnableTestCases(resultFile.toString());

		/**
		 * Get results of runnable test case id into a hashmap
		 */
		map = getrunnabletestcases.getRunnableTestStepsID(tc_id, resultFile.toString(), connectionServe, jsonObjectRepo);

		//Write test results
		WriteTestResults writeResults = new WriteTestResults();
		writeResults.writesTestCaseResult(resultFile, map);
	}


	/** finishing tests, writing results and saving in db */
	@AfterClass
	public void afterClass()  
	{
		
		try {
			connectionServe.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/** Get Total number of test cases executed */
		File f = new File(resultFile.toString());
		int totalTestCase = (XlsLib.getTotalRowOfExcelWorkbook(f))-1;

		totalTestCase = map.size();
		TestSuiteClass.totalTC.put(new ReadTestCases().gettestCaseSummarySheet(), totalTestCase);

		/** Updating portal execution summary and test steps results to main results sheet */
		String summaryData[][] = new XlsLib().dataFromExcel(resultFile.toString(), new ReadTestCases().gettestCaseSummarySheet());
		new XlsLib().updateResultInNewSheet(TestSuiteClass.executionResult, new ReadTestCases().gettestCaseSummarySheet(), summaryData);

		String stepsResultData[][] = new XlsLib().dataFromExcel(resultFile.toString(), new ReadTestCases().gettestStepSheet());
		new XlsLib().updateResultInNewSheet(TestSuiteClass.executionResult, new ReadTestCases().gettestStepSheet(), stepsResultData);

		logger.info(" : ################### Test Ended. ########################");
	}


	/**
	 * Setting up SSP Suite Name to decide which OR needs to be loaded.
	 * @return
	 */
	public static String getSuiteName()
	{
		return suiteName; 
	}
}
