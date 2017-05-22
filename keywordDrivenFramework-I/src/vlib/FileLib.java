/**
 * Last Changes Done on Jan 27, 2015 12:39:54 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;


import projects.TestSuiteClass;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.CellFinder;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import vlib.MobileTestClass_Methods;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;


public class FileLib 
{		

	static Logger logger = Logger.getLogger(FileLib.class.getName());

	static PrintStream logFile;	//Declared variable to save Log file to be used in method SaveConsoleLogs
	static String onlinePlayerFolderLocation;


	//This constructor is used to set folder location in server for each type of online player. 
	//further the value set here will be used in method: FileLib.WritingTestURLInExcelSheet to construct Test URL for online ads.
	public FileLib(String onlinePlayerType) 
	{
		onlinePlayerFolderLocation = onlinePlayerType;	
	}


	//********** Save Console Logs In A Given File, In Logs Folder: *********************************************//
	public static void SaveConsoleLogs(String logFileLocation, String logFileName) throws FileNotFoundException 
	{

		//String logFileLocation = TestSuiteClass.AUTOMATION_HOME.concat("/logs").toString();	

		//Check if Log Folder exists, if not then create it.
		File checkLogFileLocation = new File(logFileLocation);

		if(!(checkLogFileLocation.exists()))
		{
			//logger.info(" : Log folder doesn't exist at " +logFileLocation);
			boolean b = checkLogFileLocation.mkdirs();

			if(b)
			{
				//logger.info(" : Refer logs stored in folder: "+logFileLocation); 
			}
			else
			{
				logger.error(" : Log folder wasn't created");
			}
		}

		//Create Log file and save all Console Log in it.
		logFile = new PrintStream(new FileOutputStream(logFileLocation.concat("/").concat(logFileName)), true);
		System.setOut(logFile);
		//System.setErr(logFile);

		//Printing Test Start Time.
		logger.info(" : ###################################################################");
		logger.info(" : ******* Test Suite Execution Started At Time ********: " +MobileTestClass_Methods.DateTimeStamp("MMddyyyy_hhmmss"));
		logger.info(" : ###################################################################");
	}



	//******************** Generic Method To Write String In File ***************************//
	public static boolean WriteFile(String strContent, String testFileLocation, String fileName)
	{	
		boolean flag = false;
		try
		{
			File testDataFolderLocation = new File(testFileLocation);

			if(!(testDataFolderLocation.exists()))
			{
				logger.info(" : Test Data folder doesn't exist at " +testFileLocation);
				boolean b = testDataFolderLocation.mkdirs();

				if(b)
				{
					logger.info(" : Test data folder was created successfully "); 
				}
				else
				{
					logger.info(" : Test data folder wasn't created");
				}
			}

			String testFile = testFileLocation+"/"+fileName;
			File file = new File(testFile);

			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(strContent);
			bw.close();
			fw.close();

			flag = true;
		}catch(Exception t)
		{
			flag = false;
			logger.error(" : Error occurred while writing file. ", t);
		}
		return flag;
	}


	//******************** Generic Method To Write / Append String In File ***************************//
	@SuppressWarnings("finally")
	public static boolean WriteFile(String strContent, String testFileLocation, String fileName, boolean append) 
	{	

		boolean flag = false;

		try{
			File testDataFolderLocation = new File(testFileLocation);

			if(!(testDataFolderLocation.exists()))
			{
				logger.info(" : Directory doesn't exist at " +testFileLocation);
				boolean b = testDataFolderLocation.mkdirs();

				if(b)
				{
					logger.info(" : Directory was created successfully "); 
				}
				else
				{
					logger.info(" : Directory was not created");
				}
			}

			String testFile = testFileLocation+"/"+fileName;
			File file = new File(testFile);

			FileWriter fw = new FileWriter(file, append);
			fw.write(strContent);
			fw.close();

			logger.info(" : File : " +testFile+ " is written successfully");

			flag = true;
		}catch(Exception e)
		{
			flag = false;
			logger.info(" : Exception occured while writing in file. ", e);
		}
		finally
		{
			return flag;
		}
	}


	//******************** Generic Method To Copy File From One Folder To Another Folder And Rename With Date Stamp  ***************************//
	public static File CopyExcelFile(String sourceFileNameWithLocation, String destinationFileLocationWithOutExtension) 
	{
		File testResultFile = null;

		try{
			File testDataFile = new File(sourceFileNameWithLocation);

			//Defining Test Result WorkBook and Appending DateTime Stamp On File Name 
			String dateTimeStamp = MobileTestClass_Methods.DateTimeStamp();

			String testResultFileNameWithLocation = destinationFileLocationWithOutExtension.concat("_").concat(dateTimeStamp).concat(".xls");
			testResultFile = new File(testResultFileNameWithLocation);

			//Copying Test Data File to Test Results Folder
			FileUtils.copyFile(testDataFile, testResultFile);

			if (testResultFile.exists())
			{
				logger.info(" : Test Result File Created At: " +testResultFile.getPath());
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while while copying excel file: "+sourceFileNameWithLocation, e);
		}

		return testResultFile;	
	}



	//******************** Generic Method To remove rows containing Yes in a particular column  ***************************//
	public static void RemoveRowsInExcelFile(String fileNameWithLocation) throws IOException, BiffException, WriteException 
	{
		try
		{
			logger.info(" : fileNameWithLocation: " +fileNameWithLocation);
			//Getting WorkBook
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			WritableWorkbook copiedBook = Workbook.createWorkbook(new File(fileNameWithLocation), book);	
			WritableSheet sheet = copiedBook.getSheet(0);	//.getSheet(sheetName);

			logger.info(" : total rows: " +sheet.getRows());

			for(int row=1;row<sheet.getRows();row++)
			{
				String flag = sheet.getCell(0, row).getContents().toString().toLowerCase().trim();
				logger.info(" : flag: " +flag + " at row: " +row);

				if (flag.equalsIgnoreCase("no"))
				{
					logger.info(flag + " - and - " +row);
					sheet.removeRow(row);
				}
			}
			copiedBook.write();
			copiedBook.close();
			book.close();
		}
		catch(NullPointerException e)
		{
			logger.error(" : Error occured while writing data.", e);
		}
	}



	//*********************** Generic Method To Write MYSQL Data Into Excel Sheet: *********************************************//
	public static void WritingMySQLRecordsInExcelSheet (String fileNameWithLocation, String[][] strRecord) throws IOException, RowsExceededException, WriteException
	{

		String directory = StringLib.splitDirectoryFromFileLocation(fileNameWithLocation);

		//Creating Test Data Folder if it doesn't exist.
		//File testDataFolderLocation = new File(TestSuiteClass.AUTOMATION_HOME.concat("/tc_data/adserve/mobileAdServe/DataToFormURL"));

		File testDataFolderLocation = new File(directory);

		if(!(testDataFolderLocation.exists()))
		{
			logger.info(" : Test Data folder doesn't exist at " + directory);
			boolean b = testDataFolderLocation.mkdirs();

			if(b)
			{
				logger.info(" : Test data folder was created successfully "); 
			}
			else
			{
				logger.info(" : Test data folder wasn't created");
			}
		}

		//Delete Existing Test Data File Before Creating It 
		File checkFile;
		checkFile = new File(fileNameWithLocation);

		if (checkFile.exists())
		{	
			checkFile.delete();
		}

		//Writing The Data Into Excel After Deleting Existing File
		FileOutputStream outputfile = new FileOutputStream(fileNameWithLocation,true);
		WritableWorkbook book = Workbook.createWorkbook(outputfile);
		WritableSheet sheet = book.createSheet("Test_Data", 0);

		try
		{
			for(int i=0; i<strRecord.length; i++)		// For Every Row
			{
				for(int j=0; j<strRecord[0].length; j++)	// For Every Column
				{
					Label lblRecordData = new Label(j, i, strRecord[i][j]);
					sheet.addCell(lblRecordData);
					//logger.info(" : Printing Elements : "+strRecord[i][j]);
				}
				// logger.info(" : ");
			}
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{
			logger.error(" : No Records Returned By The Query. Handled by Method : WritingMySQLRecordsInExcelSheet:", e);
		}
		catch (Exception e) 
		{
			logger.error(" : Exception Handled by Method : WritingMySQLRecordsInExcelSheet:", e);
		}
		finally
		{
			book.write();
			book.close();		
		}
		logger.error(" : Data Was Successfully Written In Excel Sheet By Method WritingExcelSheet: " + " Rows: " +strRecord.length + ", Total Columns: " +strRecord[0].length);
	}			    



	//******************** Generic Method To Write Test URLs For Mobile Adult Targeting in Test Data Spreadsheet *************************************************//
	@SuppressWarnings({ "finally", "unused" })
	public static boolean WritingTestURLForTargeting(String fileNameWithLocation, String targetingType) throws IOException, RowsExceededException, WriteException, BiffException
	{	
		boolean urlWritten = false;

		//This flag will decide if URLs needs to write or not.
		boolean flag;

		//reading Test URL information - Base URL from configuration.
		String onlineBaseTestURL = MobileTestClass_Methods.propertyConfigFile.getProperty("onlineBaseTestURL").toString();

		try
		{
			Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
			CellFinder cellFind = new CellFinder(book.getSheet(0));

			String labelDeviceType = cellFind.findLabelCell("Test_URLs").getContents().toString();
			logger.info(" : Column Test_URLs is found in file: "+fileNameWithLocation);

			book.close();

			flag = true;
		}
		catch(Exception e)
		{
			urlWritten = false;
			flag = false;
			logger.error(" : Column Test_URLs is not found in file: "+fileNameWithLocation, e);
		}

		try
		{
			//Write urls only if Device_Type coulmn is found.
			if(flag)
			{
				Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));
				WritableWorkbook copiedBook = Workbook.createWorkbook(new File(fileNameWithLocation), book);	
				WritableSheet sheet = copiedBook.getSheet(0);	//.getSheet(sheetName);

				CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

				int column = sheet.getColumns();

				logger.info(" : Test URLs to browse HTML Pages to check Adult Content Targeting are being written in file...");

				Label lblColumnName;
				if(targetingType.equalsIgnoreCase("adultcontent"))
				{
					lblColumnName = new Label(column, 0, "URL_AdultContentHTMLPage");	//Adding Column Name = URL_AdultContent at last
				}
				else if(targetingType.equalsIgnoreCase("keyword"))
				{
					lblColumnName = new Label(column, 0, "URL_KEYWORDHTMLPage");	//Adding Column Name = URL_KEYWORDHTMLPage at last
				}
				else
				{
					lblColumnName = new Label(column, 0, "URL_TARGETINGHTMLPage");	//Adding Column Name = URL_TARGETINGHTMLPage at last
				}
				sheet.addCell(lblColumnName);

				for(int row=1;row<sheet.getRows();row++)
				{
					String strDevice = sheet.getCell(cellFind.findLabelCell("Test_URLs").getColumn(), row).getContents().toString();

					if (strDevice.matches("^http.*"))
					{
						String strChannelAPIKEY = sheet.getCell(cellFind.findLabelCell("Channel_APIKEY").getColumn(), row).getContents().toString();
						//Add Folder Location In URL Depending On The Player Type						
						String url ="";
						if(targetingType.equalsIgnoreCase("adultcontent"))
						{
							url = onlineBaseTestURL + "/" + "AdultContentTargeting" + "/" + row + "_" + strChannelAPIKEY + "_" + MobileTestClass_Methods.DateTimeStamp() +".html";
						}
						else if(targetingType.equalsIgnoreCase("keyword"))
						{
							url = onlineBaseTestURL + "/" + "KeywordTargeting" + "/" + row + "_" + strChannelAPIKEY + "_" + MobileTestClass_Methods.DateTimeStamp() +".html";
						}
						else
						{
							url = onlineBaseTestURL + "/" + "Targeting" + "/" + row + "_" + strChannelAPIKEY + "_" + MobileTestClass_Methods.DateTimeStamp() +".html";
						}

						//URL - http://qa.vdopia.com/qa/QAAutomation/AdultContentTargeting/new.html

						Label lblTestURL = new Label(column, row, url);
						sheet.addCell(lblTestURL);
					}
					else
					{
						Label lblTestURL = new Label(column, row, "Not Valid URL");
						sheet.addCell(lblTestURL);
						logger.info(" : Contents of This Cell: " +strDevice);
					}	
				}
				//logger.info(" : Test URLs Were Successfully Written In Excel Sheet By Method WritingTestURLInExcelSheet: ");

				copiedBook.write();
				copiedBook.close();
				book.close();

				urlWritten = true;
				logger.info(" : Test URLs for adult content HTML pages are written in file.");
			}
			else
			{
				urlWritten = false;
				logger.info(" : No Test URL to browse Adult Content Targeting is written because column - Test_URLs is not present in file: "+fileNameWithLocation);
			}
		}
		catch(Exception e)
		{
			urlWritten = false;
			logger.error(" : Exception handled while writing test urls in excel sheet by method: WritingTestURLInExcelSheet. ", e);
		}
		finally
		{
			return urlWritten;
		}
	}



	/**
	 *  This method will fetch data from given file .
	 * @param fileNameWithLocation
	 * @param columns
	 * @return
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws BiffException
	 */
	public static String[][] FetchDataFromExcelSheet(String fileNameWithLocation, String ... columns)		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;
		Workbook book = null;
		try
		{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
		}catch( IOException | BiffException e)
		{
			logger.error(" : Error occurred while reading excel file: "+fileNameWithLocation, e);
		}

		String [][]testDataProvider = null;
		if(book != null)
		{
			Sheet sheet = book.getSheet(0);	//getSheet(sheetName);
			CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

			testDataProvider = new String[sheet.getRows()-1][columnNo];
			// This will work as Data Provider - returning columns as - Ad_Format, Test_URLs, Campaign_ID, Channel_ID

			for(int row=1;row<sheet.getRows();row++)
			{
				//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
				for (int c = 0; c < columnNo; c++)
				{
					try
					{
						String strAd_Format = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
						testDataProvider[row-1][c] = strAd_Format;
						//System.out.print(strAd_Format + "   :   ");
					}catch(NullPointerException n)
					{
						testDataProvider[row-1][c] = "";
						logger.info(" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+columnNo );
					}
				}
				//logger.info();
			}	   

			book.close();
		}
		return testDataProvider;				
	}


	/**
	 * This method will fetch the data from the specified sheet of the supplied excel file.
	 * @param fileNameWithLocation
	 * @param sheetName
	 * @param columns
	 * @return
	 */
	public static String[][] FetchDataFromExcel_SpecificSheet(String fileNameWithLocation, String sheetName, String ... columns)		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;
		Workbook book = null;
		try
		{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
		}catch( IOException | BiffException e)
		{
			logger.error(" : Error occurred while reading excel file: "+fileNameWithLocation, e);
		}

		String [][]testDataProvider = null;
		if(book != null)
		{
			Sheet sheet = book.getSheet(sheetName);
			CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

			testDataProvider = new String[sheet.getRows()-1][columnNo];
			// This will work as Data Provider - returning columns as - Ad_Format, Test_URLs, Campaign_ID, Channel_ID

			for(int row=1;row<sheet.getRows();row++)
			{
				//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
				for (int c = 0; c < columnNo; c++)
				{
					try
					{
						String strAd_Format = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
						testDataProvider[row-1][c] = strAd_Format;
						//System.out.print(strAd_Format + "   :   ");
					}catch(NullPointerException n)
					{
						testDataProvider[row-1][c] = "";
						logger.info(" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+columnNo );
					}
				}
			}	   

			book.close();
		}
		return testDataProvider;				
	}


	/** This method will get the test cases from portal test case spreadsheet for the supplied runnable test case ids,
	 * considering portal spreadsheet has fixed columnName though location of coulmns may change.
	 * 
	 * @param fileNameWithLocation
	 * @param sheetName
	 * @param runnableTC_ID
	 * @param columns
	 * @return
	 */
	public static String[][] FetchPortalTestCases(String fileNameWithLocation, String sheetName, List<String> runnableTC_ID, String ... columns)		
	{	
		/** Read file name from configuration file and forming the file path using the system environment variable */
		int columnNo = columns.length;
		Workbook book = null;
		try
		{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
		}catch( IOException | BiffException e)
		{
			logger.error(" : Error occurred while reading excel file: "+fileNameWithLocation, e);
		}

		/** this list will contain the 1D array of test step data */
		List<String []> tcStepDataList = new ArrayList<>();

		if(book != null)
		{
			Sheet sheet = book.getSheet(sheetName);

			/** Finding the Cell with a particular text and later on get the corresponding Row or Column; */
			CellFinder cellFind = new CellFinder(sheet);

			for(int row=1;row<sheet.getRows();row++)
			{
				/** getting the contents of TC_ID column for each row and checking if that is contained in
				 * supplied TC_ID list, if yes then add that whole row in array else not. */
				String tcID = sheet.getCell(cellFind.findLabelCell("TC_ID").getColumn(), row).getContents().trim();

				if(runnableTC_ID.contains(tcID))
				{
					/** setting up array */
					String []testStepData = new String[columnNo];

					for (int c = 0; c < columnNo; c++)
					{
						try
						{
							String strData = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
							testStepData[c] = strData;

						}catch(NullPointerException n)
						{
							testStepData[c] = "";
							logger.info(" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+c );
						}
					}

					/** add teststepdata into list */
					tcStepDataList.add(testStepData);
				}
			}	   

			book.close();
		}

		/** convert List of 1 D array to 2D array -- this is being done to avoid 2D array containing null because of selected data. */
		return StringLib.get2DArrayFrom1DArrayList(tcStepDataList);				
	}



	//******************** Generic Method To Fetch Data as Data Provider from any Test Data Spreadsheet *************************************************//
	public static String[][] FetchDataFromExcelSheetWithColumnNames(String fileNameWithLocation, String ... columns) throws BiffException, IOException 		
	{	
		//Read file name from configuration file and forming the file path using the system environment variable
		int columnNo = columns.length;

		Workbook book = Workbook.getWorkbook(new File(fileNameWithLocation));	
		Sheet sheet = book.getSheet(0);	//getSheet(sheetName);

		logger.info(" : Total Rows: "+sheet.getRows());

		CellFinder cellFind = new CellFinder(sheet);	//Finding the Cell with a particular text and later on get the corresponding Row or Column;

		//logger.info(" : Test Data Contains: Rows: " +sheet.getRows() + " And Columns: " +sheet.getColumns());

		String [][]testDataProvider = new String[sheet.getRows()][columnNo];
		// This will work as Data Provider - returning columns as - Ad_Format, Test_URLs, Campaign_ID, Channel_ID

		for(int row=0;row<sheet.getRows();row++)
		{
			//c=0 is fileLocation passed in method FetchDataFromExcelSheet, so counting from c=1
			for (int c = 0; c < columnNo; c++)
			{
				try
				{
					String strAd_Format = sheet.getCell(cellFind.findLabelCell(columns[c]).getColumn(), row).getContents().toString().trim();
					testDataProvider[row][c] = strAd_Format;

					//System.out.print(strAd_Format + "   :   ");
				}catch(NullPointerException n)
				{
					logger.info(" : There may be a Leading or Trailing SPACE in this cell: " + "row: "+row + "  and coulmn: "+columnNo );
					n.printStackTrace();
				}
			}
			//logger.info();
		}	   

		book.close();
		return testDataProvider;				
	}


	//******************** This Method Will Copy The Sikuli-IDE.app files to /Applications Folder In Mac Only *************************************************//
	public static void SetUpSikuli()  
	{
		try
		{
			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				logger.info(" : Environment Variable has been already setup for: "+System.getProperty("os.name"));
			}
			else
			{
				//Applications Folder In Mac:
				File sikuliIDE = new File("/Applications/Sikuli-IDE.app");

				String sikuliSourceLocation = TestSuiteClass.AUTOMATION_HOME.concat("/tpt/Sikuli-IDE.app");
				String sikuliDestination = "/Applications/";

				if(!(sikuliIDE.exists()))
				{
					//String command = "cp -r /Users/user/Desktop/Automation/qascripting/Vdopia_Automation/tpt/Sikuli-IDE.app /Applications/";

					String command = "cp -r " + sikuliSourceLocation + " " + sikuliDestination;
					ExecuteCommands.ExecuteCommand_ReturnsOutput(command);

					logger.info(" : Sikuli has been setup successfully in: "+ System.getProperty("os.name"));
				}
				else
				{
					logger.info(" : Sikuli already exists in: "+ System.getProperty("os.name"));
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while setting up sikuli. ", e);
		}
	}



	//******************** This Method Will open Result file at the end    *************************************************//
	public static void OpenResult(String HtmlFilename) 
	{
		try{
			Desktop.getDesktop().open(new File(HtmlFilename));
		}
		catch(Exception e)
		{
			logger.error(" : ", e);
		}
	}



	//******************** This Method Will Create the directory at the given location *************************************************//
	public static boolean CreateDirectory(String directoryLocation)
	{
		File checkLocation = new File(directoryLocation);
		boolean b = false;

		if(!(checkLocation.exists()))
		{
			//logger.info(" : Log folder doesn't exist at " +logFileLocation);
			b = checkLocation.mkdirs();

			if(b)
			{
				logger.info(" : Directory is created at: "+checkLocation);
				return b;
			}
			else
			{
				logger.info(" : Directory wasn't created at: "+checkLocation);
				return b;
			}
		}
		else
		{
			logger.info(" : Directory: " +checkLocation + " already exists.");
			return true;
		}
	}



	//******************** This Method Will Return All The Files Matching The Given Extension *************************************************//
	@SuppressWarnings("finally")
	public static String GetFilesWithSpecificExtensionFromDirectory(String directory, final String fileExtension)
	{
		String fileNames = "";
		String[] fileList = null;

		try
		{
			File file = new File(directory);

			fileList = file.list(new FilenameFilter()
			{	
				@Override
				public boolean accept(File dir, String name) 
				{
					return name.endsWith(fileExtension);
				}
			});

			if(fileList.length < 1)
			{
				logger.info(" : No File was found having extension: "+fileExtension);
			}
			else if(fileList.length == 1)
			{
				fileNames = fileList[0].toString();
			}
			else
			{
				//Print file list
				logger.info(" : Printing List Of Files Found Having Extension: "+fileExtension);

				for(int i=0; i<fileList.length; i++)
				{
					fileNames = fileNames + fileList[i].toString();

					if(i<fileList.length - 1)
					{
						fileNames = fileNames + ",";
					}

					logger.info(fileList[i].toString());
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception handled by method: GetFileWithSpecificExtensionInDirectory. ", e);
		}
		finally
		{
			return fileNames;
		}

	}



	//*********** This method will be used to find a text in a file. ******************
	@SuppressWarnings({ "finally" })
	public static boolean FindTextInFile(File desiredFile, String desiredText) throws IOException
	{
		boolean flag = false;
		try
		{
			logger.info(" : Text: "+desiredText + " is being searched in file: "+desiredFile);
			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			String line = "";

			while((line = reader.readLine()) != null)
			{
				if(line.contains(desiredText))
				{
					logger.info(" : desired text: "+ desiredText+ " is found in line: "+line);
					flag = true;
					break;
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception handled while searching text: "+desiredText+" in file: "+desiredFile.toString(), e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will be used to get the whole content of file into a string. ******************
	@SuppressWarnings({ "finally" })
	public static String ReadContentOfFile(String desiredFile) 
	{
		String content = "";
		String line = "";

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			while((line = reader.readLine()) != null)
			{
				content = content + line; 
			}
			reader.close();
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while reading file: "+desiredFile, e);
		}
		finally
		{
			return content;
		}
	}



	//*********** This method will be used to wait for a text to appear in file. ******************
	@SuppressWarnings("finally")
	public static boolean WaitForTextToAppearInFile(File desiredFile, String desiredText, int waitSeconds)
	{
		boolean flag = false;
		try
		{
			for(int i=0; i<waitSeconds; i++)
			{
				try
				{
					flag = FindTextInFile(desiredFile, desiredText);
				}
				catch(Exception e)
				{
					Thread.sleep(1000);
				}

				if(flag == true)
				{
					break;
				}
				else
				{
					Thread.sleep(1000);
					continue;
				}
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception handled while waiting for text: "+desiredText + " to appear in file. ", e);
			Thread.sleep(1000);
		}
		finally
		{
			return flag;	
		}

	}



	/*********** This method will be used to copy all (only) files (not sub directory) of source directory to destination dir
	 * 
	 * @param sourceFile
	 * @param destinationDir
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean CopyAllFilesToDirectory(String sourceFile, String destinationDir)
	{
		boolean flag = false;
		try
		{
			File sourceDir = new File(sourceFile); 
			File copyDir = new File(destinationDir);

			//Getting all files located in source directory
			String []requiredFiles = sourceDir.list();

			//Copying all sub files 
			for(int i=0; i<requiredFiles.length; i++)
			{

				File tempFile = new File(sourceDir+"/"+requiredFiles[i]);

				if(!tempFile.isDirectory())
				{
					FileUtils.copyFileToDirectory(tempFile, copyDir);
				}
				logger.info(" : File: "+tempFile.toString() + " is copied successfully to directory: "+copyDir.toString());
			}

			flag = true;
			logger.info(" : All files are copied. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception handled by method: CopyAllFilesToDirectory. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will be used to check a specific file in a directory
	@SuppressWarnings("finally")
	public static boolean CheckFileInDirectory(String directory, String checkFileName)
	{
		boolean flag = false;
		try
		{
			File sourceDir = new File(directory); 

			//Getting all files located in source directory
			String []requiredFiles = sourceDir.list();

			//Copying all sub files 
			for(int i=0; i<requiredFiles.length; i++)
			{
				File tempFile = new File(sourceDir+"/"+requiredFiles[i]);

				if(!tempFile.isDirectory())
				{
					//Checking if desired file exists in given directory
					if(requiredFiles[i].equalsIgnoreCase(checkFileName))
					{
						flag = true;
						logger.info(" : File: "+checkFileName + " exists in directory: "+directory);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exceptin handled by method: CheckFileInDirectory. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will replace unique and single line in file containing a particular text in that line ******************
	@SuppressWarnings({ "finally" })
	public static boolean ReplaceSingleLineInFile(String propertyFile, String desiredTextContained, String desiredLine) throws IOException
	{
		String line = "";
		String actualText = "";
		File desiredFile = new File(propertyFile);

		boolean flag = false;
		boolean occurence = true;
		try
		{	
			logger.info(" : File: "+desiredFile+" is being replaced by the expected lines...");

			BufferedReader reader = new BufferedReader(new FileReader(desiredFile));

			while((line = reader.readLine()) != null)
			{
				if(occurence)
				{
					if(line.contains(desiredTextContained))
					{	
						//get the file content appended in a string
						line = desiredLine;

						//This flag will make sure to replace only first occurrence of desired line
						occurence = false;
					}
				}

				actualText =  actualText + line  + "\n";
			}

			reader.close();	

			//Writing file 
			FileWriter writer = new FileWriter(desiredFile);
			writer.write(actualText);
			writer.close();

			flag = true;
			logger.info(" : File: " +desiredFile+ " has been replaced with desired lines successfully. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.info(" : ********** Exception Handled By Method: ReplaceSingleLineInFile. ", e);
		}
		finally
		{
			return flag;
		}

	}



	/** This method will write the supplied text in given file in overwrite mode. 
	 * 
	 * @param fileNameWithLocation
	 * @param strText
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean WriteTextInFile(String fileNameWithLocation, String strText)
	{
		boolean flag =  false;

		try
		{
			logger.debug(" : Received file location: "+fileNameWithLocation);
			String dir = StringLib.splitDirectoryFromFileLocation(fileNameWithLocation);
			logger.debug(" : Splitted directory location: "+dir);

			//Get the directory from file location 
			File directory = new File(dir);

			//and check if directory exists
			if(!(directory.exists()))
			{
				//If not then create directory
				if(directory.mkdirs())
				{
					logger.info(" : Directory: "+directory + " wasn't existed, its created now. ");
				}
			}

			File file = new File(fileNameWithLocation);

			//And then create file if it doesn't exist
			if(!(file.exists()))
			{
				if(file.createNewFile())
				{
					logger.info(" : File: " +fileNameWithLocation + " wasn't existed, its created now. ");
				}
			}


			//Write content in file
			FileWriter writer = new FileWriter(fileNameWithLocation);
			writer.write(strText);
			writer.close();

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception occured while writing text in file. ", e);
		}
		finally
		{
			return flag;
		}
	}


	//*********** This method will create a new file at given location ******************
	@SuppressWarnings("finally")
	public static boolean CreateNewFile(String fileNameWithLocation)
	{
		boolean flag =  false;

		try
		{
			//Get the directory from file location 
			File directory = new File(StringLib.splitDirectoryFromFileLocation(fileNameWithLocation));

			//and check if directory exists
			if(!(directory.exists()))
			{
				//If not then create directory
				if(directory.mkdirs())
				{
					logger.info(" : Directory: "+directory + " wasn't existed, its created now. ");
				}
			}

			File file = new File(fileNameWithLocation);

			//And then create file if it doesn't exist
			if(!(file.exists()))
			{
				if(file.createNewFile())
				{
					flag = true;
					logger.info(" : File: " +fileNameWithLocation + " wasn't existed, its created now. ");
				}
			}
			else
			{
				flag = true;
				logger.info(" : File: " +fileNameWithLocation + " already existed. ");
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception handled by method: CreateNewFile. ", e);
		}
		finally
		{
			return flag;
		}
	}



	//*********** This method will return the last modified file at the given directory ******************
	@SuppressWarnings("finally")
	public static String GetLastModifiedFile(String directory, String fileExtension)
	{
		String modifiedFile = "NO_FILE"; 
		try
		{
			logger.info(" : Searching the last modified file at location: "+directory);

			File dir = new File(directory);

			FileFilter fileFilter = new WildcardFileFilter("*."+fileExtension);
			File[] files = dir.listFiles(fileFilter);

			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

			modifiedFile = files[0].toString();

			logger.info(" : Found the last modified file: " +modifiedFile);
		}
		catch(Exception e)
		{
			modifiedFile = "NO_FILE";
			logger.error(" : Exception handled by method: GetLastModifiedFile. ", e);
		}
		finally
		{
			return modifiedFile;
		}
	}

	/** Get clipboard text
	 * 
	 * @return
	 */
	public static String getClipBoardText()
	{
		String text= "";

		try {
			text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}	
		return text;
	}
}
