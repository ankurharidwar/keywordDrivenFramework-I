/**
 * Last Changes Done on Jan 16, 2015 12:13:22 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */


package projects.portal;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.mysql.jdbc.Connection;

import vlib.MobileTestClass_Methods;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ReadTestCases 
{
	public String tcSummaryRunColumn;
	public String tcSummaryLabelColumn;
	public String tcSummaryTCIdColumn;
	public String tcStepTCIdColumn;
	public String tcStepTCStepIDColumn;
	public String tcStepKeywordColumn;
	public String tcStepObjectColumn;
	public String tcStepDataColumn;

	String keyword;
	String objectName;
	String data;
	public String tcSummaryResultColumn;
	public String tcStepResultColumn;
	String separator;
	public String testCaseSummarySheet;
	public String testStepSheet;
	String haltedTestStepResult;
	String haltFlag;


	static Logger logger = Logger.getLogger(ReadTestCases.class.getName());

	public ReadTestCases()
	{	
		this.tcSummaryRunColumn = "Run" ;
		this.tcSummaryTCIdColumn = "TC_ID" ;
		this.tcSummaryResultColumn = "Test_Results";

		this.tcStepTCIdColumn = "TC_ID";
		this.tcStepResultColumn = "Test_Results";
		this.tcStepTCStepIDColumn = "Step_ID";
		this.tcStepKeywordColumn = "Keyword";
		this.tcStepObjectColumn = "objectName";

		this.tcSummaryLabelColumn = "Label";

		/** Choosing input data column based on current test environment */
		String currentTestEnv = MobileTestClass_Methods.propertyConfigFile.getProperty("currentTestEnvironment").toString().trim();
		if(currentTestEnv.equalsIgnoreCase("qa"))
		{
			this.tcStepDataColumn = "inputData_QA";
		}
		else
		{
			this.tcStepDataColumn = "inputData_Production";
		}

		this.separator = "####";

		/** define sheets for transformer portal, if ssp wasn't selected then default to transfromerPortal */
		String suiteName = "TransformerPortal";

		if(suiteName.equalsIgnoreCase("TransformerPortal"))
		{
			this.testCaseSummarySheet = "executionControl";
			this.testStepSheet = "testCaseSteps";	
		}

		/** define sheets for ssp portal */
		else if(suiteName.equalsIgnoreCase("SSP"))
		{
			this.testCaseSummarySheet = "sspExecutionControl";
			this.testStepSheet = "sspTestCaseSteps";	
		}

		this.haltedTestStepResult = "Not Executed.";
		this.haltFlag = "must pass";
	}


	/** getter to return testStepSheet name
	 * @return
	 */
	public String gettestStepSheet()
	{
		return testStepSheet;
	}

	/** getter to return testCaseSummarySheet name
	 * @return
	 */
	public String gettestCaseSummarySheet()
	{
		return testCaseSummarySheet;
	}

	/** This method reads the executionControl sheet and finds all the runnable test case id and corresponding runnable test steps
	 * in sheet testCaseSteps and return as a list.
	 * 
	 * @param fileNameWithLocation 
	 * @return 
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public List<String> getRunnableTestCases(String fileNameWithLocation)		
	{	
		logger.info(" : Test Case Summary File is : "+fileNameWithLocation);
		List<String> tc_id = new ArrayList<String>();
		Sheet sheet = null;
		Workbook book = null;

		try{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
			sheet = book.getSheet(testCaseSummarySheet);
		}catch(Exception e)
		{
			logger.error(" : Please check the file location, Error occurred while loading file: "+fileNameWithLocation, e);
		}

		try
		{
			//Finding "Run" and "TC_ID" column in the Test Case Summary excel sheet
			int run_column = sheet.findCell(tcSummaryRunColumn, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int id_column = sheet.findCell(tcSummaryTCIdColumn, 0, 0, sheet.getColumns(),0, false).getColumn();

			//logger.debug(" : Test Case Summary File has RUN column is: "+run_column + " TC_ID column is: "+id_column);

			for(int row=1;row<sheet.getRows();row++)
			{
				String runMode = sheet.getCell(run_column, row).getContents().trim();
				if (runMode.equalsIgnoreCase("yes")) 
				{
					String testcaseID = sheet.getCell(id_column, row).getContents().trim();
					tc_id.add(testcaseID);
					logger.debug(" : Getting runnable test case id: " + testcaseID);
				}
			}
			book.close();
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while reading Test Case Summary File :" +fileNameWithLocation, e);
		}

		return tc_id; 				
	}


	/** This method reads the test steps sheet, perform action, get action results and write action result of each step into test steps sheet.
	 *  finally return the result of all runnable test case id.
	 *
	 * @param tc_id 
	 * @param fileNameWithLocation 
	 * @return 
	 */
	public HashMap<String, Boolean> getRunnableTestStepsID(List<String> tc_id, String fileNameWithLocation, Connection connection, JSONObject jsonObjectRepo)
	{
		logger.info(" : Test case summary file is : " + fileNameWithLocation);
		HashMap<String, String> testStepResults = new HashMap<String, String>();
		HashMap<String, Boolean> testCaseResults = new HashMap<String, Boolean>();
		String result;

		boolean resultFlag;
		try
		{
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			Sheet sheet = book.getSheet(testStepSheet);

			int tc_id_column = sheet.findCell(tcStepTCIdColumn, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int tc_step_id_column = sheet.findCell(tcStepTCStepIDColumn, 0, 0, sheet.getColumns(),0, false).getColumn();
			int keyword_column = sheet.findCell(tcStepKeywordColumn, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int object_column = sheet.findCell(tcStepObjectColumn, 0, 0, sheet.getColumns(),0, false).getColumn();
			int data_column = sheet.findCell(tcStepDataColumn, 0, 0, sheet.getColumns(),0, false).getColumn();
			int tcsteps_result_column = sheet.findCell(tcStepResultColumn, 0, 0, sheet.getColumns(),0, false).getColumn();

			//logger.debug(" : Test Case Summary File has TC_ID column is: "+tc_id_column + " TC Step ID column is: "+tc_step_id_column);

			/** This hashmap contains the test step id and corresponding test step data, will not reset for each test case id.
			 * this will be used when user wants to give same (dynamic) data in multiple test steps for one test case.
			 */
			HashMap<String, String> testStepID_InputData = new HashMap<String, String>();

			/** Iterating the received runnable test case id list
			 */
			for(int i=0;i<tc_id.size();i++)
			{
				/** Launching a driver here -- for every test case, it will be launched in every test case id
				 * --> Making this change to make it work --> End to End Test - Threads Framework */

				WebDriver driver = MobileTestClass_Methods.WebDriverSetUp("chrome", null);

				//********** Just for Debugging purpose to see the events *********** 
				//WebDriver eventHandler = MobileTestClass_Methods.WebDriverSetUp("chrome", null);
				//EventFiringWebDriver driver = new EventFiringWebDriver(eventHandler);
				//driver.register(new WebdriverEvents());
				//*********** *********** *********** ***********

				String runnableTestCaseID = tc_id.get(i);

				/** This resultFlag is set to false if any of test step of a test case is failed. 
				 * This flag sets the result either PASS or FAIL for the test case id in executionControl sheet.
				 */
				resultFlag = true;

				/** This haltExecution is set to false by default, in case any result is fail and corresponding input data has 
				 * must pass flag, then subsequent steps, for that test case id, will not be executed and default result will set = "Not Executed"  
				 */
				boolean haltExecution = false;

				/** Setting up new feature --> on_error_resume_next, if this flag is found no in the first step of Test Case, then in case of any test step 
				 * failure, subsequent steps won't be executed, similar to must pass flag, but difference is --> must pass flag can be used only with 
				 * verify keywords like verifyText, verifyTitle etc. not with other keywords like typeValue or clickButton etc. 
				 */
				boolean on_error_resume_next = false;

				/** iterating the sheet containing test steps, finding test steps corresponding to supplied runnable test case id.
				 */
				for(int row =1; row<sheet.getRows(); row++)
				{
					String testCaseID = sheet.getCell(tc_id_column , row).getContents().trim();

					result = "";

					/** Getting test steps corresponding to supplied runnable test case id
					 */
					if(testCaseID.equalsIgnoreCase(runnableTestCaseID))
					{
						String testStepID = sheet.getCell(tc_step_id_column, row).getContents().trim();
						logger.info(" : Executing Test Step: " +testStepID + "  For Test Case ID: "+testCaseID);

						keyword = sheet.getCell(keyword_column, row).getContents().trim();
						objectName = sheet.getCell(object_column, row).getContents().trim();
						data = sheet.getCell(data_column, row).getContents().trim();

						/** set on_error_resume_next flag to true, if found in the first step as no --> that means upon first failure,
						 *  subsequent steps won't be executed. */
						if(row == 1 && keyword.equalsIgnoreCase("on_error_resume_next") && data.equalsIgnoreCase("no"))
						{
							on_error_resume_next = true;
						}

						/** If the supplied data has #time# then replace #time# with the time stamp. */
						/** First of all store each step step id and corresponding data in a hashmap for each test step id. */
						/** 1. If user supplies the input data like #TC_01_03# in test step id TC_01_06 then this means the input data for 
						 * step 06 is the same as data given in step 03, in this case hashmap stores value like (TC_01_06,#TC_01_03#),
						 * Now first of all get the input data from testStepID_InputData hashmap for id(key) = TC_01_03 after removing # from it.
						 * If hashmap has this value then update hashmap as (TC_01_06,Value) and pass this value for further processing, else data = ""
						 */

						data = new HandlerLib().dataParser(data, keyword, testStepID_InputData, connection);
						objectName = new HandlerLib().dataParser(objectName, keyword, testStepID_InputData, connection);
						testStepID_InputData.put(testStepID.toLowerCase().trim(), data);

						logger.debug(" : Received Keyword: "+keyword + ", ObjectName: "+objectName + ", Input Data: "+data);


						/** Check if execution needs to be halted, if yes then set Result = "Not Executed."
						 * for keyword = closebrowser, execution will not be halted. 
						 */
						if(!haltExecution || keyword.equalsIgnoreCase("closebrowser"))
						{
							/** Performing action based on received keyword, object and input data
							 */
							PerformAction action = new PerformAction();
							result = action.performAction(driver, keyword, objectName, data, connection, jsonObjectRepo);

							/** Removing string "; must pass" from results
							 */
							if(result.contains(haltFlag))
							{
								result = new HandlerLib().resultParser(result, haltFlag);
							}

							logger.debug(" : Received Test Result is: " +result);

							/** Check if execution needs to be halted based on received above test step result.
							 */
							haltExecution = new HandlerLib().haltTestExecution(data, result);
						}
						else
						{
							result = haltedTestStepResult;
							logger.debug(" : halting steps now: ");
						}

						/** Setting up haltExecution flag to true if on_error_resume_next is found to be true and any fail result. 
						 * If on_error_resume_next is found in first row as no and if there is any Fail result found then set haltExecution = true.
						 */
						if(on_error_resume_next && (result.toLowerCase().startsWith("fail: ") || result.toLowerCase().contains("fail: ")))
						{
							haltExecution = true;
						}

						/** Setting result of each test case id using flag resultFlag, test case id result will be Fail 
						 * if any of the test step is failed or if any test step result = Not Executed
						 */
						if(resultFlag)
						{
							if(result.toLowerCase().trim().matches("^fail.*") || result.equalsIgnoreCase(haltedTestStepResult)
									|| result.toLowerCase().trim().startsWith("fail:") || result.toLowerCase().contains("fail:"))
							{
								resultFlag = false;
								logger.debug(" : Found Test Step: "+testStepID +" = Failed:");
							}
						}

						/** Appending Test_Result column and test step number respectively in each test step result
						 */
						result = String.valueOf(tcsteps_result_column) + this.separator + row + this.separator + result;

						/** Putting results of each test step in hash map
						 */
						testStepResults.put(testStepID, result);

						logger.debug(" : " +testStepID + " has result: " +result);
					}
				}

				/** writing each test step result in testSteps sheet after completion of each test case.
				 */
				WriteTestResults writeResult = new WriteTestResults();
				boolean flag = writeResult.writeTestStepResult(fileNameWithLocation, testStepResults);
				logger.info(" : Test Step result writing status: "+flag);

				/** Getting results of each runnable test case id in hash map based on flag resultFlag, if this flag is False then test case id
				 * result will be fail else pass.
				 */
				testCaseResults.put(runnableTestCaseID, resultFlag);
			}
			book.close();
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred: " +fileNameWithLocation, e);
		}
		return testCaseResults;
	}

}
