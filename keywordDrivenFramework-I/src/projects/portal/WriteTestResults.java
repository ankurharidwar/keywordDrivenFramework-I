/**
 * Last Changes Done on 5 Mar, 2015 12:07:46 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package projects.portal;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class WriteTestResults {

	/**
	 * @param args
	 */

	Logger logger = Logger.getLogger(WriteTestResults.class.getName());

	public boolean writeTestStepResult(String fileName, HashMap<String, String> hashmap)
	{
		boolean flag = false;
		try
		{
			ReadTestCases readTestCase = new ReadTestCases();
			String separator = readTestCase.separator;
			String teststepsSheet = readTestCase.testStepSheet;

			logger.info(" : Received separator: "+separator + " to split column, row and values from hashmap values.");
			logger.info(" : Writing test step result in sheet: "+teststepsSheet);

			//Get the existing workbook
			Workbook book = Workbook.getWorkbook(new File(fileName));
			WritableWorkbook copiedBook = Workbook.createWorkbook(new File(fileName), book);	
			WritableSheet sheet = copiedBook.getSheet(teststepsSheet);

			for(Entry<String, String> map: hashmap.entrySet())
			{
				String value = map.getValue();

				int column = Integer.parseInt(value.split(separator)[0]);
				int row = Integer.parseInt(value.split(separator)[1]);
				String result = "";

				try{
					result = value.split(separator)[2];
				}catch(ArrayIndexOutOfBoundsException a){
					logger.debug(" : No Result received for column = "+column + " and row = "+row + " reassging a space. ");
				}

				//logger.debug(" :  writing test step result: "+ result + " in column " +column + " and row: "+row);

				Label lblRecordData = new Label(column, row, result);
				sheet.addCell(lblRecordData);
			}

			copiedBook.write();
			copiedBook.close();
			book.close();

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while writing test steps results. ", e);
		}
		return flag;

	}



	public void addResultColumn(File testResultFile, String sheetName, String resultLabel)
	{
		try{
			logger.info(" : Adding label: "+resultLabel +" column in file: "+testResultFile + " in sheet: "+sheetName);

			Workbook book = Workbook.getWorkbook(testResultFile);
			WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);
			WritableSheet sheet = copiedBook.getSheet(sheetName);

			Label lblColumnName = new Label(sheet.getColumns(), 0, resultLabel);
			sheet.addCell(lblColumnName);

			copiedBook.write();
			copiedBook.close();
			book.close();
		}catch(Exception e)
		{
			logger.error(" : Error occurred while adding Test Result column in file: "+testResultFile, e);
		}
	}

	public boolean writesTestCaseResult(File testResultFile, HashMap<String, Boolean> hashmap)
	{
		boolean flag;
		try{
			logger.info(" : Writting test results in test summary file "+testResultFile);
			Workbook book = Workbook.getWorkbook(testResultFile);
			WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);

			ReadTestCases readTest = new ReadTestCases();
			String summarySheet = readTest.testCaseSummarySheet;
			WritableSheet sheet = copiedBook.getSheet(summarySheet);

			int tcIDcolumn = sheet.findCell(readTest.tcSummaryTCIdColumn, 0, 0, sheet.getColumns(),0, false).getColumn();
			int tcResultscolumn = sheet.findCell(readTest.tcSummaryResultColumn, 0, 0, sheet.getColumns(),0, false).getColumn();

			logger.debug(" : Test Id column: "+tcIDcolumn + " and test result column: "+tcResultscolumn + " in test summary file.");

			for(Entry<String, Boolean> map: hashmap.entrySet())
			{
				String tcID = map.getKey();
				String tcstatus;

				if(map.getValue())
				{
					tcstatus = "Pass";					
				}
				else
				{
					tcstatus = "Fail";
				}

				for(int i=1; i<sheet.getRows(); i++)
				{
					String testCaseID = sheet.getCell(tcIDcolumn, i).getContents().toString();

					if(testCaseID.equalsIgnoreCase(tcID))
					{
						Label lblColumnName = new Label(tcResultscolumn, i, tcstatus);
						sheet.addCell(lblColumnName);

						logger.debug(" : Writitng Result: "+tcstatus + " For Test Case Id: "+testCaseID);
						break;
					}
				}
			}
			copiedBook.write();
			copiedBook.close();
			book.close();
			flag = true; 
		}catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while writting resutls in test summary file: " + testResultFile, e);
		}
		return flag;
	}
}
