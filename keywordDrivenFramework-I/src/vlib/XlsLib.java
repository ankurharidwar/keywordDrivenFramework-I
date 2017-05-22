/**
 * Last Changes Done on Jan 16, 2015 6:24:43 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import projects.TestSuiteClass;
import projects.portal.ReadTestCases;
import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.CellFinder;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class XlsLib 
{

	static Logger logger = Logger.getLogger(XlsLib.class.getName());

	int TS_count = 0;
	TreeMap <Integer, List<String>> Result_map = new TreeMap<Integer, List<String>>();

	public String[][] dataFromExcel(String fileNameWithLocation) 		
	{	
		String [][]testDataProvider = null;
		try
		{
			//Read file name from configuration file and forming the file path using the system environment variable
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			Sheet sheet = book.getSheet(0);	//getSheet(sheetName);

			//CellFinder cellFind = new CellFinder(sheet);
			testDataProvider = new String[sheet.getRows()][sheet.getColumns()];

			for(int row=0;row<sheet.getRows();row++)
			{
				//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
				for (int col = 0; col < sheet.getColumns(); col++)
				{
					String data = sheet.getCell(col, row).getContents().toString();
					testDataProvider[row][col] = data;		
				}
			}
			book.close();
		}
		catch(Exception e)
		{
			logger.error(" : Error: ", e);
		}
		return testDataProvider;				
	}


	public String[][] dataFromExcel(String fileNameWithLocation, String sheetName) 		
	{	
		String [][]testDataProvider = null;
		try
		{
			//Read file name from configuration file and forming the file path using the system environment variable
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			Sheet sheet = book.getSheet(sheetName);

			//CellFinder cellFind = new CellFinder(sheet);
			testDataProvider = new String[sheet.getRows()][sheet.getColumns()];

			for(int row=0;row<sheet.getRows();row++)
			{
				//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
				for (int col = 0; col < sheet.getColumns(); col++)
				{
					String data = sheet.getCell(col, row).getContents().toString();
					testDataProvider[row][col] = data;		
				}
			}
			book.close();
			return testDataProvider;
		}
		catch(Exception e)
		{
			return testDataProvider;
		}
	}


	public String[][] dataFromExcel(String fileNameWithLocation, int sheetNumber) throws IOException, RowsExceededException, WriteException, BiffException		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
		Sheet sheet = book.getSheet(sheetNumber);

		String [][]testDataProvider = new String[sheet.getRows()][sheet.getColumns()];

		for(int row=0;row<sheet.getRows();row++)
		{
			//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
			for (int col = 0; col < sheet.getColumns(); col++)
			{
				String data = sheet.getCell(col, row).getContents().toString();
				testDataProvider[row][col] = data;

			}
		}
		book.close();
		return testDataProvider;				
	}


	public String[][] dataFromExcel(String fileNameWithLocation, int sheetNumber, String ... columns) throws IOException, RowsExceededException, WriteException, BiffException		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;

		Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
		Sheet sheet = book.getSheet(sheetNumber);	//getSheet(sheetName);

		CellFinder cellFind = new CellFinder(sheet);

		//System.out.println("Rows in the sheet are : " + sheet.getRows());
		String [][]testDataProvider = new String[sheet.getRows()][columnNo];

		for(int row=0;row<sheet.getRows();row++)
		{
			//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
			for (int col = 0; col < columnNo; col++)
			{
				String data = sheet.getCell(cellFind.findLabelCell(columns[col]).getColumn(), row).getContents().toString();
				//System.out.println("strAdFormat: " +data);	
				//System.out.println("Adding data in ROW " + row);
				testDataProvider[row][col] = data;
			}
		}
		book.close();
		return testDataProvider;				
	}


	public String[][] dataFromExcel(String fileNameWithLocation, String sheetName, String ... columns) throws IOException, RowsExceededException, WriteException, BiffException		
	{	

		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;
		Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation.toString()));
		Sheet sheet = book.getSheet(sheetName);
		CellFinder cellFind = new CellFinder(sheet);
		//System.out.println("Rows in the sheet are : " + sheet.getRows());
		String [][]testDataProvider = new String[sheet.getRows()][columnNo];
		for(int row=0;row<sheet.getRows();row++)
		{
			//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
			for (int col = 0; col < columnNo; col++)
			{
				String data = sheet.getCell(cellFind.findLabelCell(columns[col]).getColumn(), row).getContents().toString();
				//System.out.println("strAdFormat: " +data);	
				//System.out.println("Adding data in ROW " + row);
				testDataProvider[row][col] = data;
			}
		}
		book.close();
		return testDataProvider;				
	}


	public void updateResultInNewSheet (String fileNameWithLocation,String sheetName, String[][] strRecord)
	{
		Workbook book = null;
		WritableWorkbook copiedBook  = null;

		try
		{
			File f = new File(fileNameWithLocation);
			book = Workbook.getWorkbook(f);	
			copiedBook = Workbook.createWorkbook(f, book);
			copiedBook.getSheet(0);
			int sheetCount = book.getNumberOfSheets();
			WritableSheet sheet = copiedBook.createSheet(sheetName, sheetCount);	//book.createSheet(sheetName, sheetCount);

			for(int i=0; i<strRecord.length; i++)		// For Every Row
			{
				for(int j=0; j<strRecord[0].length; j++)	// For Every Column
				{
					Label lblRecordData = new Label(j, i, strRecord[i][j]);
					sheet.addCell(lblRecordData);
					//System.out.println("Printing Elements : "+strRecord[i][j]);
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally
		{
			//System.out.println("Writing Data in Result file.");
			try {
				copiedBook.write();
				copiedBook.close();
			} catch (IOException | WriteException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			}

			book.close();		
		}
		//System.out.println(" UpdateResultInNewSheet " + " Rows: " +strRecord.length + ", Total Columns: " +strRecord[0].length);
	}


	public void emptyExcel(String fileNameWithLocation)
	{
		//Creating Test Data Folder if it doesn't exist.
		//File testDataFolderLocation = new File(TestSuiteClass.AUTOMATION_HOME.concat("/tc_data/adserve/mobileAdServe/DataToFormURL"));
		String directory = StringLib.splitDirectoryFromFileLocation(fileNameWithLocation);

		//Creating Test Data Folder if it doesn't exist.
		//File testDataFolderLocation = new File(TestSuiteClass.AUTOMATION_HOME.concat("/tc_data/adserve/mobileAdServe/DataToFormURL"));

		File testDataFolderLocation = new File(directory);

		if(!(testDataFolderLocation.exists()))
		{
			//UnComment for debug
			//System.out.println("Test Data folder doesn't exist at " + directory);
			boolean b = testDataFolderLocation.mkdirs();

			if(b)
			{
				//System.out.println("Test data folder was created successfully "); 
			}
			else
			{
				//System.out.println("Test data folder wasn't created");
			}
		}
		File testFile = new File(fileNameWithLocation);

		if(!(testFile.exists()))
		{
			//System.out.println("file doesn't exist:");
		}
		else
		{
			//System.out.println("file exists: " + testFile.getPath());
		}


		try 
		{

			WritableWorkbook book = Workbook.createWorkbook(new File(fileNameWithLocation));
			book.createSheet("Summary_Result", 0);

			book.write();
			book.close();
		}
		catch(Exception e)
		{
			logger.info(" : Exception Handled by Method: emptyExcel. ", e);

		}
	}


	public static void deleteSheetFromExcelWorkBook(String fileNameWithLocation) throws BiffException, IOException, WriteException
	{

		Workbook workbook = Workbook.getWorkbook(new File(fileNameWithLocation));

		WritableWorkbook book = Workbook.createWorkbook(new File(fileNameWithLocation), workbook);
		book.removeSheet(0);

		book.write();
		workbook.close();
		book.close();

	}


	//************* This method will be used to write records in existing excel sheet *******************************
	public static void writeDataInExistingExcelWorkbook(File existingExcelSheet, String [][]strRecord) throws IOException, BiffException, RowsExceededException, WriteException
	{
		//Get the existing workbook
		Workbook book = Workbook.getWorkbook(existingExcelSheet);
		WritableWorkbook copiedBook = Workbook.createWorkbook(existingExcelSheet, book);	
		WritableSheet sheet = copiedBook.getSheet(0);	//.getSheet(sheetName);

		int startColumn = sheet.getColumns();		//Get the total number of columns
		//System.out.println("no of columns: "+startColumn);

		//Writing the array of records
		try
		{
			for(int i=0; i<strRecord.length; i++)		// For Every Row
			{
				for(int j=0; j<strRecord[0].length; j++)	// For Every Column
				{
					try
					{	
						int column = startColumn + j;
						//System.out.println("Print column: "+column);

						//Adding label starting from existing column + 1 (Last Column)
						Label lblRecordData = new Label(column, i, strRecord[i][j]);
						sheet.addCell(lblRecordData);

						//System.out.print("Column: " + column + " Row: " + i + " Element: " + strRecord[i][j] + " lblRecordData: " + lblRecordData.toString() + " ");
					}
					catch(Exception e)
					{
						System.out.println("Exception Handled by Method : WriteRecordsInExistingExcelWorkbook. "+e.getMessage());
						System.out.println(e.getStackTrace());
					}
				}

				System.out.println("");
			}
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{
			System.out.println("No Records To Be Written, Handled by Method : WriteRecordsInExistingExcel. "+e.getMessage());
			System.out.println(e.getStackTrace());
		}
		catch (Exception e) 
		{
			System.out.println("Exception Handled by Method : WriteRecordsInExistingExcel:");
			System.out.println(e.getStackTrace());
		}
		finally
		{
			copiedBook.write();
			copiedBook.close();
			book.close();
		}
	}


	//************* This method will be used to write records in existing excel sheet *******************************
	public static void writeSpecificRowInExistingExcelWorkbook(File existingExcelSheet, String []strRecord, int startColumn, int row) throws IOException, BiffException, RowsExceededException, WriteException
	{

		if(strRecord != null)
		{
			//Get the existing workbook
			Workbook book = Workbook.getWorkbook(existingExcelSheet);
			WritableWorkbook copiedBook = Workbook.createWorkbook(existingExcelSheet, book);	
			WritableSheet sheet = copiedBook.getSheet(0);

			//Writing the array of records
			try
			{
				for(int i=0; i<strRecord.length; i++)		// For Every Row
				{
					try
					{	
						//System.out.println("Print column: "+startColumn);

						//Adding label starting from existing column + 1 (Last Column)
						Label lblRecordData = new Label(startColumn, row, strRecord[i]);
						sheet.addCell(lblRecordData);

						//System.out.print("Column: " + startColumn + " Row: " + row + " Element: " + strRecord[i] + " ");
					}
					catch(Exception e)
					{
						System.out.println("Exception Handled by Method : WriteSpecificRowInExistingExcelWorkbook. "+e.getMessage());
						System.out.println(e.getStackTrace());
					}
					finally
					{
						//incrementing column
						startColumn ++;
					}

					System.out.println("");
				}
			}
			catch (ArrayIndexOutOfBoundsException e) 
			{
				System.out.println("No Records To Be Written, Handled by Method : WriteSpecificRowInExistingExcelWorkbook. "+e.getMessage());
				System.out.println(e.getStackTrace());
			}
			catch (Exception e) 
			{
				System.out.println("Exception Handled by Method : WriteSpecificRowInExistingExcelWorkbook. " +e.getMessage());
				System.out.println(e.getStackTrace());
			}
			finally
			{
				copiedBook.write();
				copiedBook.close();
				book.close();

				//System.out.println("Data is successfully written in file: " +existingExcelSheet.toString());
			}
		}
		else
		{
			System.out.println("Excel File: "+existingExcelSheet.toString() + " is not written because null data is received. ");
		}
	}


	//************* This method will be used to return total rows from an existing excel sheet *******************************
	public static int getTotalRowOfExcelWorkbook(File existingExcelSheet) 
	{
		int row = 0;
		try
		{
			//Get the existing workbook
			Workbook book = Workbook.getWorkbook(existingExcelSheet);
			row = book.getSheet(0).getRows();
			//System.out.println(existingExcelSheet.toString() + " has total rows: "+row);
		} 
		catch(Exception e)
		{
			System.out.println("Exception handled by method: GetTotalRowOfExcelWorkbook. " +e.getMessage());
		}
		return row;
	}


	//************* This method will be used to return total columns from an existing excel sheet *******************************
	public static int getTotalColumnOfExcelWorkbook(File existingExcelSheet) throws BiffException, IOException
	{
		//System.out.println(existingExcelSheet.toString());

		int column = 0;
		try
		{
			//Get the existing workbook
			Workbook book = Workbook.getWorkbook(existingExcelSheet);
			column = book.getSheet(0).getColumns();
			//System.out.println(existingExcelSheet.toString() + " has total columns: "+column);
		} 
		catch(Exception e)
		{
			System.out.println("Exception handled by method: GetTotalColumnOfExcelWorkbook. " +e.getMessage());
		}
		return column;
	}


	//Function to Generate Final Summary Report
	public void generateFinalResult(String fileNameWithLocation) throws BiffException, IOException, RowsExceededException, WriteException
	{
		List<String> resultsList = new ArrayList<String>();
		XlsLib test = new XlsLib();
		File f = new File(fileNameWithLocation);
		Workbook book = Workbook.getWorkbook(f);
		//Get all Sheet Name
		String[] SheetNames =  book.getSheetNames();
		String mainResultFileName = SheetNames[0];

		//get transformer portal test steps result sheet name
		String transformerPortalTestStepsResult = new ReadTestCases().gettestStepSheet();

		//Creating Heading and Fill in HashMap
		resultsList.clear();
		resultsList.add("S.No");
		resultsList.add("TEST SUITE NAME");
		resultsList.add("TOTAL TC");
		resultsList.add("TOTAL PASSED TC");
		resultsList.add("TOTAL FAILED TC");
		resultsList.add("TOTAL SKIPPED TC");
		Result_map.put(TS_count, resultsList);
		//System.out.println("Main Heading Value is added to hash map: "+Result_map.get(TS_count));

		for(String SheetName: SheetNames)
		{
			if(!(SheetName.equalsIgnoreCase(mainResultFileName)) && !(SheetName.equalsIgnoreCase(transformerPortalTestStepsResult))	)
			{
				resultsList = new ArrayList<String>();
				resultsList.clear();
				TS_count = TS_count + 1;
				String TestSuitName = SheetName;

				String DataSheet1[][] = test.dataFromExcel(book, fileNameWithLocation, SheetName, "Test_Results");
				//System.out.println("#########################################################");
				//System.out.println("######   DATA    OF    "+ SheetName + "    ######");
				//System.out.println("#########################################################");
				int total_tc_count = 0;
				int total_tc_passed = 0;
				int total_tc_failed = 0;
				int total_tc_skipped = 0;
				int total_tc_count_inputFile = 0;

				for(int i=0; i<DataSheet1.length; i++)		// For Every Row
				{
					for(int j=0; j<DataSheet1[0].length; j++)	// For Every Column
					{
						total_tc_count = total_tc_count + 1;
						//System.out.println("RESULT of TC No : " + total_tc_count);

						//System.out.println("DATA IN SHEET NAME : " + SheetName);
						//System.out.println("Printing Elements : "+DataSheet1[i][j]);
						if(StringLib.Strexist(DataSheet1[i][j], "FAIL") || StringLib.Strexist(DataSheet1[i][j], "Fail"))
						{
							total_tc_failed = total_tc_failed + 1;
							//System.out.println("RESULT of TC No : " + total_tc_count + " is => FAILED");
						}
						else if(StringLib.Strexist(DataSheet1[i][j], "PASS") || StringLib.Strexist(DataSheet1[i][j], "Pass"))
						{
							total_tc_passed = total_tc_passed + 1;
							//System.out.println("RESULT of TC No : " + total_tc_count + " is => PASSED");
						}
						else
						{
							total_tc_skipped = total_tc_skipped + 1;
							//System.out.println("RESULT of TC No : " + total_tc_count + " is => SKIPPED");
						}
					}
				}

				total_tc_count_inputFile = TestSuiteClass.totalTC.get(SheetName);

				//System.out.println("FINAL VALUES OF TEST");
				//System.out.println("TOTAL TC COUNT : " + total_tc_count);
				//System.out.println("TOTAL PASSED COUNT : " + total_tc_passed);
				//System.out.println("TOTAL FAILED COUNT : " + total_tc_failed);
				//System.out.println("TOTAL SKIPPED COUNT : " + total_tc_skipped);
				//System.out.println("TOTAL TC COUNT FROM INPUT TC_DATA: " + total_tc_count_inputFile);

				resultsList.add(String.valueOf(TS_count));
				resultsList.add(TestSuitName);
				resultsList.add(String.valueOf(total_tc_count_inputFile));
				resultsList.add(String.valueOf(total_tc_passed));
				resultsList.add(String.valueOf(total_tc_failed));
				resultsList.add(String.valueOf(total_tc_skipped));

				Result_map.put(TS_count, resultsList);
				//System.out.println("Value is added to hash map: " + Result_map.get(TS_count));
			}

		}

		WritableWorkbook copiedBook = Workbook.createWorkbook(f, book);
		WritableSheet sheet = copiedBook.getSheet(0);

		for(int row = 0;row<=TS_count;row++)
		{
			List<String> finalList = new ArrayList<String>();
			finalList = Result_map.get(row);
			for(int column = 0;column<finalList.size();column++)
			{
				try
				{
					Label lblTestResult = new Label(column, row, finalList.get(column).toString());
					//System.out.println("column: " + column + " list value: " + finalList.get(column));
					sheet.addCell(lblTestResult);
				}
				catch(Exception e)
				{
					logger.error(" : Exception: ", e);
				}
			}
		}
		copiedBook.write();
		copiedBook.close();
		book.close();
	}


	public String[][] dataFromExcel(Workbook book, String fileNameWithLocation, String sheetName, String ... columns) throws IOException, RowsExceededException, WriteException, BiffException		
	{	

		//Read file name from configuration file and forming the file path using the system environment variable
		//int columnNo = columns.length;
		int columnNo = columns.length;

		//Sheet sheet = book.getSheet(sheetNumber);	//getSheet(sheetName);

		Sheet sheet = book.getSheet(sheetName);


		CellFinder cellFind = new CellFinder(sheet);


		//System.out.println("Rows in the sheet are : " + ((sheet.getRows())-1));

		String [][]testDataProvider = new String[sheet.getRows()-1][columnNo];


		for(int row=1;row<sheet.getRows();row++)
		{
			//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
			for (int col = 0; col < columnNo; col++)
			{
				try
				{
					//String data = sheet.getCell(cellFind.findLabelCell("Test Case Execution Result").getColumn(), row).getContents().toString();
					String data = sheet.getCell(cellFind.findLabelCell(columns[col]).getColumn(), row).getContents().toString();

					//System.out.println("Adding data in ROW " + row);
					//System.out.println("Value of Test_Result: " +data);
					testDataProvider[row-1][col] = data;
					//System.out.println("strAdFormat: " +strAd_Format);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		}

		//book.close();
		return testDataProvider;				
	}


	public List<String> getFinalResults(@SuppressWarnings("unchecked") List<String> ... columns)		
	{	
		List<String> returnList = new ArrayList<String>();
		int columnNo = columns.length;
		if(columnNo ==1)
		{
			List<String> firstList = columns[0];
			for (String value : firstList) 
			{
				//System.out.println(value);
				if(value.toLowerCase().contains("fail"))
				{
					returnList.add("FAIL");
				}
				else if(value.toLowerCase().contains("pass"))
				{
					returnList.add("PASS");
				}
				else
				{
					returnList.add("SKIP");
				}
			}
		}
		else if(columnNo == 2)
		{
			List<String> firstList = columns[0];
			List<String> secondList = columns[1];
			Iterator<String> it1 = firstList.iterator();
			Iterator<String> it2 = secondList.iterator();
			while(it1.hasNext() || it2.hasNext()) 
			{
				String firstvalue = "";
				String secondValue = "";
				if(it1.hasNext())
				{
					firstvalue = it1.next();
				}
				if(it2.hasNext())
				{
					secondValue = it2.next();
				}
				if(firstvalue.equalsIgnoreCase("") || secondValue.equalsIgnoreCase(""))
				{
					//System.out.println("===============================================================================");
					//System.out.println("===============================================================================");
					//System.out.println(" Result Return: FAIL");
					returnList.add("FAIL");
				}
				else if(firstvalue.toLowerCase().trim().startsWith("fail") || secondValue.toLowerCase().trim().startsWith("fail"))
				{
					//System.out.println("===============================================================================");
					//System.out.println("===============================================================================");
					//System.out.println(" Result Return: FAIL");
					returnList.add("FAIL");
				}


				else if(firstvalue.toLowerCase().trim().startsWith("pass") && secondValue.toLowerCase().trim().startsWith("pass"))
				{
					//System.out.println("===============================================================================");
					//System.out.println("===============================================================================");
					//System.out.println(" Result Return: PASS");
					returnList.add("PASS");
				}
				else if(firstvalue.toLowerCase().trim().startsWith("pass") && secondValue.toLowerCase().trim().startsWith("fail"))
				{
					System.out.println("===============================================================================");
					//System.out.println("===============================================================================");
					//System.out.println(" Result Return: FAIL");
					returnList.add("FAIL");
				}
				else if(firstvalue.toLowerCase().trim().startsWith("fail") && secondValue.toLowerCase().trim().startsWith("pass"))
				{
					//System.out.println("===============================================================================");
					//System.out.println("===============================================================================");
					//System.out.println(" Result Return: FAIL");
					returnList.add("FAIL");
				}
				else
				{
					//System.out.println("===============================================================================");
					//System.out.println("===============================================================================");
					//System.out.println(" Result Return: SKIP");
					returnList.add("SKIP");
				}
			}
		}
		else
		{
			returnList = null;
		}
		return returnList;				
	}


	//******************** Write the given list in Test Results excel sheet, here test result column name can be given *************************************************//
	public void writeExcelResultInHiddenColumn(File testResultFile, List<String> resultsList, String testResultsColumnName) throws IOException, RowsExceededException, WriteException, BiffException, InterruptedException
	{       
		Workbook book = Workbook.getWorkbook(testResultFile);
		WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);
		WritableSheet sheet = copiedBook.getSheet(0);
		int column = sheet.getColumns();
		Label lblColumnName = new Label(column, 0, testResultsColumnName);     //Adding Column Name = Test_Results in last Column and first row
		sheet.addCell(lblColumnName);
		CellFinder cellFind = new CellFinder(sheet);    //Finding the Cell with a particular text and later on, get the corresponding Row or Column;
		try 
		{
			for(int row=1;row<sheet.getRows();row++)
			{
				String testResult = resultsList.get(row-1).trim().toString();
				int testResultColumnNo = cellFind.findLabelCell(testResultsColumnName).getColumn();
				Label lblTestResult = new Label(testResultColumnNo, row, testResult);
				sheet.addCell(lblTestResult);
			}
			CellView cellView = new CellView();
			cellView.setHidden(true); //set hidden
			//sheet.setColumnView(0, cellView);
			sheet.setColumnView(column, cellView);

		} 
		catch(Exception e)
		{
			System.out.println("Exception Handled by Method : WritingTestResultsInExcelSheet. "+e.getMessage());
			System.out.println(e.getMessage());
		}
		finally 
		{
			try
			{
				copiedBook.write();
				copiedBook.close();
				book.close();
				System.out.println("Test Results was written successfully");
			}
			catch(NullPointerException n)
			{
				System.out.println("NullPointerException Handled while writing test results, file format may have some issues. " +n.getMessage());
			}
			catch(Exception n)
			{
				System.out.println("Exception Handled while writing test results. " +n.getMessage());
			}
		} 
	}


	public void copyResultInNewSheet (String outputFileLocation,String sheetName, String[][] strRecord) throws IOException, RowsExceededException, WriteException, BiffException
	{
		File f = new File(outputFileLocation);
		Workbook book = Workbook.getWorkbook(f);	
		WritableWorkbook copiedBook = Workbook.createWorkbook(f, book);
		copiedBook.getSheet(0);
		int sheetCount = book.getNumberOfSheets();
		WritableSheet sheet = copiedBook.createSheet(sheetName, sheetCount);	//book.createSheet(sheetName, sheetCount);

		try
		{
			for(int i=0; i<strRecord.length; i++)		// For Every Row
			{
				for(int j=0; j<strRecord[0].length; j++)	// For Every Column
				{
					Label lblRecordData = new Label(j, i, strRecord[i][j]);
					sheet.addCell(lblRecordData);
					//System.out.println("Printing Elements : "+strRecord[i][j]);
				}
			}
			int column = (sheet.getColumns())-1;
			CellView cellView = new CellView();
			cellView.setHidden(true); //set hidden
			//sheet.setColumnView(0, cellView);
			sheet.setColumnView(column, cellView);
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{
			System.out.println("Nothing is available to copy");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("Exception Handled by Method : UpdateResultInNewSheet");
		}
		finally
		{
			System.out.println("Writing Data in Result file.");
			copiedBook.write();
			copiedBook.close();
			book.close();		
		}
		System.out.println(" UpdateResultInNewSheet " + " Rows: " +strRecord.length + ", Total Columns: " +strRecord[0].length);
	}


	public void createReRunTestCase(String testDataFile_ReRun,String SheetName, String[][] strRecord)
	{		
		//
		int lastColumnIndex = (strRecord[0].length)-1;
		try
		{
			WritableWorkbook createbook = Workbook.createWorkbook(new File(testDataFile_ReRun));
			WritableSheet sheet = createbook.createSheet(SheetName, 0);

			for(int i=0; i<1; i++)		// For Every Row
			{
				for(int j=0; j<lastColumnIndex; j++)	// For Every Column
				{
					Label lblRecordData = new Label(j, i, strRecord[i][j]);
					sheet.addCell(lblRecordData);
				}
			}
			int row = 1;
			for(int i=1; i<strRecord.length; i++)		// For Every Row
			{
				if(StringLib.Strexist(strRecord[i][lastColumnIndex], "FAIL"))
				{
					for(int j=0; j<lastColumnIndex; j++)	// For Every Column
					{
						Label lblRecordData = new Label(j, row, strRecord[i][j]);
						sheet.addCell(lblRecordData);
					}
					row = row+1;
				}
			}
			createbook.write();
			createbook.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception handled by method CreateReRunTestCase: " + e.getMessage());
		}
	}


	public void createReRunTestCaseSpecial(String testDataFile_ReRun,String SheetName, String[][] strRecord, String serveSanityCampaignFlag)
	{		
		int lastColumnForIteration;
		int lastColumnIndex = (strRecord[0].length)-1;
		if(serveSanityCampaignFlag.equalsIgnoreCase("Yes"))
		{
			lastColumnForIteration =(strRecord[0].length)-3;
		}
		else
		{
			lastColumnForIteration =(strRecord[0].length)-2;
		}
		try
		{
			WritableWorkbook createbook = Workbook.createWorkbook(new File(testDataFile_ReRun));
			WritableSheet sheet = createbook.createSheet(SheetName, 0);

			for(int i=0; i<1; i++)		// For Every Row
			{
				for(int j=0; j<lastColumnForIteration; j++)	// For Every Column
				{
					Label lblRecordData = new Label(j, i, strRecord[i][j]);
					sheet.addCell(lblRecordData);
				}
			}
			int row = 1;
			for(int i=1; i<strRecord.length; i++)		// For Every Row
			{
				if(StringLib.Strexist(strRecord[i][lastColumnIndex], "FAIL"))
				{
					for(int j=0; j<lastColumnForIteration; j++)	// For Every Column
					{
						Label lblRecordData = new Label(j, row, strRecord[i][j]);
						sheet.addCell(lblRecordData);
					}
					row = row+1;
				}
			}
			createbook.write();
			createbook.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception handled by method CreateReRunTestCaseSpecial: " + e.getMessage());
		}
	}


	public boolean getFailStatusForTestCase(String[][] strRecord)
	{
		boolean flag = false;
		int lastColumnIndex = (strRecord[0].length)-1;
		try
		{
			for(int i=0; i<strRecord.length; i++)		// For Every Row
			{
				if(StringLib.Strexist(strRecord[i][lastColumnIndex], "FAIL"))
				{
					flag = true;	
					break;
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception handled by method GetFailStatusForTestCase: " + e.getMessage());
			flag = false;
		}
		return flag;
	}


	//This method will write the test description in excel sheet based on ad format
	@SuppressWarnings("finally")
	public static boolean writeTestDescription(String fileNameWithLocation)
	{
		boolean flag = false;
		try
		{
			System.out.println("Writing Test Description In File: "+fileNameWithLocation + " .... ");
			//String fileNameWithLocation = "/Users/user/Documents/ProjectAdServingWebservice/VdopiaAdserving/TestData/ea96868feb39654_TestData.xls";

			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			WritableWorkbook copiedBook = Workbook.createWorkbook(new File(fileNameWithLocation), book);	
			WritableSheet sheet = copiedBook.getSheet(0);	//.getSheet(sheetName);

			CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

			int column = sheet.getColumns();

			Label lblColumnName = new Label(column, 0, "Test_Description");	//Adding Column Name = Test_URLs at last 
			sheet.addCell(lblColumnName);

			for(int row=1;row<sheet.getRows();row++)
			{
				String strCampaign = sheet.getCell(cellFind.findLabelCell("Campaign_Name").getColumn(), row).getContents().toString();

				String strAdFormat = sheet.getCell(cellFind.findLabelCell("Ad_Format").getColumn(), row).getContents().toString();

				if(strAdFormat.equalsIgnoreCase("appinterstitial"))
				{
					strAdFormat = "Interstitial";
				}
				else if(strAdFormat.equalsIgnoreCase("vdobanner"))
				{
					strAdFormat = "Mini Video";
				}
				else if(strAdFormat.equalsIgnoreCase("leadervdo"))
				{
					strAdFormat = "Leader Video";
				}
				else if(strAdFormat.equalsIgnoreCase("banner"))
				{
					strAdFormat = "Banner";
				}
				else if(strAdFormat.equalsIgnoreCase("html"))
				{
					strAdFormat = "Animated Banner";
				}
				else if(strAdFormat.equalsIgnoreCase("jsbanner"))
				{
					strAdFormat = "Js Banner";
				}
				else if(strAdFormat.equalsIgnoreCase("htmlinter"))
				{
					strAdFormat = "HTML Interstitial";
				}
				else if(strAdFormat.equalsIgnoreCase("video"))
				{
					strAdFormat = "Video";
				}
				else if(strAdFormat.equalsIgnoreCase("vastfeed"))
				{
					strAdFormat = "Vast";
				}
				else if(strAdFormat.contains("prerol"))
				{
					strAdFormat = "Preroll";
				}

				String description = "Campaign Name: "+strCampaign+" and Ad Format: "+strAdFormat;

				Label lblTestURL = new Label(column, row, description);
				sheet.addCell(lblTestURL);
			}

			copiedBook.write();
			copiedBook.close();
			book.close();

			flag = true;

			System.out.println("Test Description is written successfully .... ");

		}catch(Exception e)
		{
			flag = false;
			System.out.println("Exception occured while writing test description in file: " + fileNameWithLocation);
			System.out.println(e.getMessage());
		}
		finally
		{
			return flag;
		}
	}


	//This method the get the string at the specified location from the supplied excel sheet 
	@SuppressWarnings("finally")
	public static String getSpecificStringFromExcel(String fileNameWithLocation, int column, int row)
	{
		String content = "";
		try
		{
			System.out.println("Getting string at col: "+column + " row: "+row + " from the supplied file: "+fileNameWithLocation);

			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			Sheet sheet = book.getSheet(0);	//get default sheet - first sheet

			try{
				content = sheet.getCell(column, row).getContents().trim();
				System.out.println("Found string: "+ content + " at column: "+column + ", row: "+row);	}
			catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Supplied Location Doesn't Exist In File: "+fileNameWithLocation);
			}

			book.close();
		}
		catch(Exception e)
		{
			content = "";
			System.out.println("Exception occured while getting the string from the file: " + fileNameWithLocation);
			System.out.println(e);
		}
		finally
		{
			return content;
		}
	}


	//This method will return a hash map containing the given columns as key and value 
	@SuppressWarnings("finally")
	public static HashMap<String, String> getHashMapFromExcelsheet(String fileNameWithLocation, String sheetName, String columnForKey, String columnForValue)
	{
		HashMap<String, String> hashmap = new HashMap<String, String>();
		try
		{
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			Sheet sheet = book.getSheet(sheetName);
			CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

			int keyColumn = cellFind.findLabelCell(columnForKey).getColumn();
			int valueColumn = cellFind.findLabelCell(columnForValue).getColumn();

			//start reading the data from excel sheet
			for(int row=1; row<sheet.getRows(); row++)
			{
				String key = sheet.getCell(keyColumn, row).getContents().toLowerCase().trim().toString();
				String value = sheet.getCell(valueColumn, row).getContents().toLowerCase().trim().toString();

				//put data into a hash map if key is not empty
				if(!key.isEmpty())
				{
					//System.out.println("Adding Key: "+key + " and value: "+value);
					hashmap.put(key, value);
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while inserting excel data into a hash map. ", e);
		}
		finally
		{
			return hashmap;
		}
	}


	//************* This method will be used to write a record in specified row, column in existing excel sheet *******************************
	public static void writeDataInExistingExcelWorkbook(String existingExcelSheet, String strRecord, int column, int row) 
	{
		//Get the existing workbook

		Workbook book = null;
		WritableWorkbook copiedBook = null;
		WritableSheet sheet = null;

		try{
			book = Workbook.getWorkbook(new File(existingExcelSheet));
			copiedBook = Workbook.createWorkbook(new File(existingExcelSheet), book);	
			sheet = copiedBook.getSheet(0);
		}catch(Exception e)
		{
			System.out.println(e);
		}

		try
		{
			if(!(strRecord != null))
			{
				strRecord = "NULL";
			}

			Label lblRecordData = new Label(column, row, strRecord);
			sheet.addCell(lblRecordData);
		}
		catch(Exception e)
		{
			System.out.println("Exception Handled while writing in excel sheet, writing string: "+strRecord);
			System.out.println(e + "\n" + e.getMessage());
		}
		finally
		{
			try{
				copiedBook.write();
				copiedBook.close();
				book.close();

				System.out.println("Data: "+strRecord + " is written in excelsheet located at: "+existingExcelSheet);
			}catch(Exception e)
			{
				System.out.println("Exception occured while finally writing data in excel sheet. "+e);
			}
		}
	}


	//************* This method will be used to find specified string from a specified row and column in existing excel sheet *******************************
	@SuppressWarnings("finally")
	public static boolean checkStringExistsInExcelWorkbook(String existingExcelSheet, String strRecord, int column, int row) 
	{
		boolean flag = false;

		try{

			//Get the existing workbook
			Workbook book = Workbook.getWorkbook(new File(existingExcelSheet));
			Sheet sheet = book.getSheet(0);

			try{
				Cell i = sheet.findCell(strRecord, column, row, column, row, false);
				System.out.println("Found Row: "+i.getRow() + " containing the supplied string: "+strRecord);

				flag = true;
			}catch(NullPointerException e)
			{
				flag = false;
				System.out.println("String: "+strRecord + " was not found in excelsheet located at: "+existingExcelSheet);
			}

			book.close();
		}
		catch(Exception e)
		{
			flag = false;
			System.out.println("Exception Handled while writing string: "+strRecord + " in excelsheet: "+existingExcelSheet);
			System.out.println(e + "\n" + e.getMessage());
		}
		finally
		{
			return flag;
		}


	}



	//************* This method will be used to return row of specified string in existing excel sheet *******************************
	@SuppressWarnings("finally")
	public static Object getLocationOfStringFromExcelWorkbook(String existingExcelSheet, String strRecord, String rowColumnFlag) 
	{
		Object location = null;

		try{

			//Get the existing workbook
			Workbook book = Workbook.getWorkbook(new File(existingExcelSheet));
			Sheet sheet = book.getSheet(0);

			try{
				Cell i = sheet.findCell(strRecord);	

				if(rowColumnFlag.equalsIgnoreCase("row"))
				{
					location = i.getRow();
					System.out.println("Found Row: "+i.getRow() + " containing the supplied string: "+strRecord);
				}
				else if(rowColumnFlag.equalsIgnoreCase("column"))
				{
					location = i.getColumn();
					System.out.println("Found Column: "+i.getColumn() + " containing the supplied string: "+strRecord);
				}
				else
				{
					location = null;
					System.out.println("Please supply the correct flag: row or coulmn." );
				}
			}catch(NullPointerException e)
			{
				location = null;
				System.out.println("String: "+strRecord + " was not found in excelsheet located at: "+existingExcelSheet);
			}

			book.close();
		}
		catch(Exception e)
		{
			location = null;
			System.out.println("Exception Handled while writing string: "+strRecord + " in excelsheet: "+existingExcelSheet);
			System.out.println(e + "\n" + e.getMessage());
		}
		finally
		{
			return location;
		}

	}

}
