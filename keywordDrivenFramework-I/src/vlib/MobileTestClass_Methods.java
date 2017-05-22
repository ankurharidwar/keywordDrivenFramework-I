/**
 * Last Changes Done on Jan 23, 2015 3:45:57 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: Implemented logger, added support for rtb_win and rtb_bp trackers for hudson requests
 */

package vlib;

import projects.TestSuiteClass;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jxl.Workbook;
import jxl.biff.CellFinder;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;



public class MobileTestClass_Methods 
{

	static Logger logger = Logger.getLogger(MobileTestClass_Methods.class.getName());

	public static PropertiesConfiguration propertyConfigFile;
	public static String configFlag = "desktop";
	public static String isVast2vdo = "onlineplayertype";
	public static boolean isDeviceConnected = false;

	public static String hudsonFlag = "nonHudson";
	public static int expectedaiTracker_Hudson = 0;
	public static int expectedrtbbpTracker_Hudson = 0;

	public static String adFormat = "";


	/** Used in webservice code, migrated to bq
	 * 
	 * @param configFlag
	 */
	public MobileTestClass_Methods(String configFlag)
	{
		MobileTestClass_Methods.configFlag = configFlag;
		logger.debug(" : initializing constructor MobileTestClass_Methods: "+ " configFlag: "+configFlag);
	}

	/** Used in MobileAdServingTests, migrated to bq
	 * @param adFormat
	 */
	public MobileTestClass_Methods(Object adFormat)
	{
		MobileTestClass_Methods.adFormat = (String) adFormat;
		logger.debug(" : initializing constructor MobileTestClass_Methods: "+ " adFormat: "+adFormat);
	}

	/** Used in online serving, not migrated to bq yet
	 * 
	 * @param configFlag
	 * @param isVast2vdo
	 */
	public MobileTestClass_Methods(String configFlag, String isVast2vdo)
	{
		MobileTestClass_Methods.configFlag = configFlag;
		MobileTestClass_Methods.isVast2vdo = isVast2vdo;
		logger.debug(" : initializing constructor MobileTestClass_Methods: "+ " configFlag: "+configFlag + ", isVast2vdo: "+isVast2vdo);
	}

	/** Used in chocolate, not yet migrated to bq
	 * 
	 * @param hudsonFlag
	 * @param expectedaiTracker_Hudson
	 * @param expectedrtbbpTracker_Hudson
	 */
	public MobileTestClass_Methods(String hudsonFlag, int expectedaiTracker_Hudson, int expectedrtbbpTracker_Hudson)
	{
		MobileTestClass_Methods.hudsonFlag = hudsonFlag;
		MobileTestClass_Methods.expectedaiTracker_Hudson = expectedaiTracker_Hudson;
		MobileTestClass_Methods.expectedrtbbpTracker_Hudson = expectedrtbbpTracker_Hudson;
		logger.debug(" : initializing constructor MobileTestClass_Methods: "+ "hudsonFlag: "+hudsonFlag + ", expectedaiTracker_Hudson: "+expectedaiTracker_Hudson + ", expectedrtbbpTracker_Hudson: "+expectedrtbbpTracker_Hudson);
	}

	/** Used in MobileAdServingTests, migrated to bq
	 * 
	 * @param isDeviceConnected
	 */
	public MobileTestClass_Methods(boolean isDeviceConnected)
	{
		MobileTestClass_Methods.isDeviceConnected = isDeviceConnected;
		logger.debug(" : initializing constructor MobileTestClass_Methods: "+ " isDeviceConnected: "+isDeviceConnected);

	}


	/***
	 * This method initialize the webdriver based on supplied browser type. New Way implemented for Chrome Driver:
	 * Now we'll start the chrome server and then wait until server is started and then create a remote driver.
	 * @param browser
	 * @param capabilities
	 * @return
	 */
	public static WebDriver WebDriverSetUp (String browser, String[] capabilities) 
	{

		WebDriver driver = null;
		try
		{
			logger.info(browser+" is being setup on " +System.getProperty("os.name"));

			if(browser.equalsIgnoreCase("FireFox"))
			{
				driver = new FirefoxDriver();
				logger.info(" : Firefox is being setup");
			}
			else if (browser.equalsIgnoreCase("Chrome")) 
			{
				String chromeDriver;
				if(System.getProperty("os.name").matches("^Windows.*"))
				{
					chromeDriver = TestSuiteClass.AUTOMATION_HOME.concat("/tpt/chromedriver.exe");
				}else
				{
					//ExecuteCommands.ExecuteMacCommand_ReturnsExitStatus("killall chromedriver");
					chromeDriver = TestSuiteClass.AUTOMATION_HOME.concat("/tpt/chromedriver");
				}

				/** create chrome driver service */
				ChromeDriverService service = retryChromeDriverService(chromeDriver);				

				if(service != null && service.isRunning())
				{
					DesiredCapabilities cap = DesiredCapabilities.chrome();

					try{
						driver = new RemoteWebDriver(service.getUrl(), cap);
					}catch (SessionNotCreatedException e) 
					{
						/** if session is not created successfully then re-try to create it. Calling recursion */
						logger.info(" : Chrome driver session not setup, retrying ... ");
						
						driver = WebDriverSetUp(browser, capabilities);
					}
					catch (WebDriverException e) 
					{
						/** if session is not created successfully then re-try to create it. Calling recursion */
						logger.info(" : Chrome driver session not setup coz of webdriver exception, retrying ... ");
						
						driver = WebDriverSetUp(browser, capabilities);
					}
				}
				else
				{
					logger.info(" : Chrome driver service seems not started while setting up driver ... ");
				}

				/** browsing google.com to check if driver is launched successfully */
				try{driver.get("http://www.google.com");}catch(NoSuchWindowException n)
				{
					logger.info(" : Chrome browser was closed coz of unknown reason, retrying ... ");
					
					driver = WebDriverSetUp(browser, capabilities);
				}
			}
			else 
			{	
				logger.info(" : No Support For: "+browser +" Browser. ");
			}			

			int driverImplicitDelay = Integer.parseInt(propertyConfigFile.getProperty("driverImplicitDelay").toString());
			driver.manage().window().maximize();
			driver.manage().deleteAllCookies();

			/** setting up implicit driver delay */
			driver.manage().timeouts().implicitlyWait(driverImplicitDelay, TimeUnit.SECONDS);
		}
		catch (Exception e)
		{
			logger.error(" : Exception occurred while setting up browser: " + browser, e);
		} 

		return driver;
	}


	/** This method will attempt to start chrome driver service, earler we were using recursion for retry that may result in
	 * infinite loops, now limiting max attempts to 10.
	 * 
	 * @param chromeDriver
	 * @return
	 */
	public static ChromeDriverService retryChromeDriverService(String chromeDriver) 
	{
		ChromeDriverService service = null;

		int i = 0;
		while(i <= 10)
		{
			if(service != null)
			{
				logger.info(" : Chrome driver service is started yet, attempt: "+i);
				break;
			}
			else
			{
				service = getChromeDriverService(chromeDriver);
				logger.info(" : Chrome driver service is not started yet, attempt: "+i);
			}
		}

		/** wait for chrome driver to start */ 
		if(service != null)
		{
			waitForChromeDriverToStart(service);
		}

		return service;
	}


	/** Get chromedriver service instance.
	 * 
	 * @param chromeDriver
	 * @return
	 */
	public static ChromeDriverService getChromeDriverService(String chromeDriver)
	{
		ChromeDriverService service = null;
		try
		{
			service = new ChromeDriverService.Builder()
					.usingDriverExecutable(new File(chromeDriver))
					.usingAnyFreePort()
					.build();
			service.start();

			Thread.sleep(1000);

		}catch(Exception io){
			logger.info(" : Exception occurred while starting the chrome driver service: "+ io);
		}

		return service;
	}


	/** This method waits for chrome driver to start, earlier we were putting infinite loop for wait, now limiting 10 attempts.
	 * 
	 * @param service
	 */
	public static void waitForChromeDriverToStart(ChromeDriverService service)
	{
		int i = 0;

		/** wait until chrome driver server is started -- maximum 10 attempts */
		while(i <= 10)
		{
			String output = httpClientWrap.sendGetRequest((service.getUrl().toString()));
			if(output.isEmpty())
			{
				logger.info(" : Chrome driver is not started yet, attempt: "+i);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
			else
			{
				logger.info(" : Chrome driver is started, exiting loop at attempt: "+i);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
				break;
			}
		}
	}


	//********** Initializing Configuration File: *********************************************//
	// AUTOMATION_HOME is defined in the RUN CONFIGURATION settings of eclipse.
	public static void InitializeConfiguration()  
	{	
		//logger.info(" : Automation_Home: " + TestSuiteClass.AUTOMATION_HOME);
		try
		{
			propertyConfigFile = new PropertiesConfiguration();

			String varAutomationHome = "";

			if(configFlag.equalsIgnoreCase("webservice"))
			{
				if (System.getProperty("os.name").toLowerCase().matches("^mac.*"))
				{
					varAutomationHome = "/Users/user/Documents/ProjectAdServingWebservice/VdopiaAdserving";
				}
				else
				{
					varAutomationHome = "C:\\WebService\\ProjectAdServingWebservice\\VdopiaAdserving";
				}
			}
			else
			{
				varAutomationHome = TestSuiteClass.AUTOMATION_HOME;
			}

			//logger.info(" : Environment Variable= " +varAutomationHome + " Has Been Set. ");

			// Now we will add path to conf folder and qaconf.properties is the file which will be needed to fetch the configurations.
			String config = varAutomationHome.concat("/conf/qaconf.properties");

			propertyConfigFile.load(config);
		}
		catch (Exception e) 
		{
			logger.error(" : Exception Error occurred While Reading Config File, Ensure that Config file is at the mentioned path. ", e);
		}
		//logger.info(" : Property File Is Successfully Loaded:" +System.getenv("AUTOMATION_CONF").toString());
	}


	//********** Establishing JDBC Connection to Mysql database: *********************************************//
	public static Connection CreateSQLConnection()  
	{
		Connection qaConnection = null;
		try
		{
			MobileTestClass_Methods.InitializeConfiguration();

			String dbClass = "com.mysql.jdbc.Driver";		
			Class.forName(dbClass);

			// Getting Values for dburl,dbUsername and dbPassword from configuration file
			String dburl = propertyConfigFile.getProperty("dbURL").toString();
			String dbuserName = propertyConfigFile.getProperty("dbUserName").toString();
			String dbpassword = propertyConfigFile.getProperty("dbPassword").toString();

			qaConnection = (Connection) DriverManager.getConnection (dburl,dbuserName,dbpassword);
		}
		catch(NullPointerException e)
		{
			logger.info(" : NullPointerException Handled By Method CreateSQLConnection, Plz check Config Values or Initialize Config by calling Method - InitializeConfiguration", e);
		}
		catch (Exception e) 
		{
			logger.error(" : Error occurred while creating sql connection. ", e);
		}
		//logger.info(" : SQL Connection Was Made Successfully By Method CreateSQLConnection: " +url + " ; " + userName + " ; " +password);
		return qaConnection;
	}


	//********** Establishing JDBC Connection: *********************************************//
	public static Connection CreateServeSQLConnection()  
	{
		Connection qaServeConnection = null;
		int i = 0;
		try
		{
			MobileTestClass_Methods.InitializeConfiguration();

			String dbClass = "com.mysql.jdbc.Driver";		
			Class.forName(dbClass);

			// Getting Values from configuration file
			String url = propertyConfigFile.getProperty("serveDBURL").toString();
			String userName = propertyConfigFile.getProperty("serveDBUserName").toString();
			String password = propertyConfigFile.getProperty("serveDBPassword").toString();

			qaServeConnection = (Connection) DriverManager.getConnection (url,userName,password);
			//logger.info(" : SQL Connection Was Made Successfully By Method CreateSQLConnection: " +url + " ; " + userName + " ; " +password);
		}
		catch(NullPointerException n)
		{
			logger.error(" : There is SQL Connection Problem: By Method - CreateServeSQLConnection: ", n);
			if(qaServeConnection == null && i < 5)
			{
				i++;
				/** retry to get session with serve db */
				qaServeConnection = MobileTestClass_Methods.CreateServeSQLConnection();	
			}

			if(qaServeConnection == null && i >= 5)
			{
				logger.error("DB connection wasn't made, exiting tests ..."); 
				Assert.fail("DB connection wasn't made, exiting tests ... ");
			}
		}
		catch (Exception e) {
			logger.error(" : Error occurred while creating serve sql connection. ", e);
			if(qaServeConnection == null && i < 5)
			{
				i++;
				/** retry to get session with serve db */
				qaServeConnection = MobileTestClass_Methods.CreateServeSQLConnection();	
			}

			if(qaServeConnection == null && i >= 5)
			{
				logger.error("DB connection wasn't made, exiting tests ..."); 
				Assert.fail("DB connection wasn't made, exiting tests ... ");
			}
		}
		return qaServeConnection;
	}


	//********** Executing MySQL Query and Returning Result Set: *********************************************//
	public static ResultSet ExecuteMySQLQueryReturnsResultSet(Connection con, String sqlQuery) throws SQLException 
	{		
		try{
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlQuery);
			return rs;
		}catch(MySQLSyntaxErrorException m){
			logger.error(m.getMessage());
			return null;
		}
	}



	//********** Get Current Time: *********************************************//
	public static String GetCurrentDBTime()  
	{	
		String currentTime = "";

		try
		{
			String sqlQuery = "Select NOW() as CurrentDateTime;";
			logger.debug(" : Getting current database time by executing query: "+sqlQuery);

			Connection con = CreateServeSQLConnection();
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlQuery);

			while(rs.next())
			{
				currentTime = rs.getString("CurrentDateTime");
			}
			logger.info(" : Current DB Time Is: " +currentTime);
		}
		catch(Exception e)
		{
			logger.error(" : Error occurred while getting current database time: ", e);
		}
		return currentTime;
	}






	//********** Get Input Data From MySQL DB Using A QUERY And Returning Two D Array: ***********************************//
	public static String [][] GetInputDataFromMySQL(Connection con, String deviceType) 
	{               
		String vast2Vdo = "";
		String online_Channel_Supported_Ad_Formats = "";

		if(isVast2vdo.equalsIgnoreCase("vast2vdo"))
		{
			vast2Vdo = " = ";
		}
		else
		{
			vast2Vdo = " <> ";
		}

		if(deviceType.equalsIgnoreCase("pc"))
		{
			online_Channel_Supported_Ad_Formats = "IFNULL(chset.ad_format,0) AS Online_Channel_Supported_Ad_Formats, ";
		}

		String publisherEmail = propertyConfigFile.getProperty("publisherEmail").toString();
		//logger.info(" : PUBLISHER EMAIL: " +publisherEmail.toString());

		String [][]arrayRecords = null;

		//replacing [] - which is coming from config file on giving multiple values
		publisherEmail = publisherEmail.replace("[", "");
		publisherEmail = publisherEmail.replace("]", "");


		try
		{
			//Added cam.review_status = 'Approved' to get only started campaigns
			String sqlSelectQuery = "SELECT pub.email AS Publisher_Email, cam.name AS Campaign_Name, ch.apikey AS Channel_APIKEY, ch.publisher_id AS Publisher_ID, " +
					" ch.id AS Channel_ID, cam.id AS Campaign_ID, ad.id AS ADS_ID, IFNULL(cam.video_choice,0) AS Video_Choice, IFNULL(cam.custom_details,'') AS Custom_Details," +
					" IFNULL(chset.additional_settings, '') AS Channel_Settings, " +
					" IFNULL(ad.ad_format, 'NoAdFormatSaved') AS Ad_Format, CEIL(IFNULL(ad.duration,0)) AS Ads_Duration, IFNULL(ad.dimension,0) AS Ads_Dimension, IFNULL(ad.tracker_url,0) AS Tracker_URL, " +
					" IFNULL(ad.destination_url,0) AS Destination_URL, cam.device As Device_Type, " +
					" IFNULL(ad.action_type,0) AS Action_Type, "+ online_Channel_Supported_Ad_Formats +" IFNULL(ad.ad_details, 0) AS Ad_Details, " +
					" IFNULL(ad.branded_img_bot_ref_txbody, '') AS CompanionBanner " +
					" FROM channels ch INNER JOIN channel_settings chset ON ch.id = chset.channel_id INNER JOIN publisher pub ON ch.publisher_id = pub.id " +
					" INNER JOIN campaign cam ON ch.id = cam.channel_choice " +
					" INNER JOIN campaign_members camb ON cam.id = camb.cid " + "INNER JOIN ads ad ON ad.id = camb.ad_id " +
					" AND cam.status = 'active' AND camb.status = 'enabled' AND cam.review_status = 'Approved' AND cam.validto > CURDATE() AND pub.email in ("+ publisherEmail +") AND cam.device = " + "'" + deviceType + "'" +
					" where cam.id NOT IN (SELECT cid from campaign_target) AND ad.ad_format <> 'tracker' " +
					" AND ad.ad_format "+ vast2Vdo +" 'vast2vdo' " +
					" ORDER BY cam.id ASC;";


			logger.info(" : Print SQL Query To Get Test DATA : " +sqlSelectQuery);

			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlSelectQuery);

			rs.last();      // Setting the cursor at last
			int rows = rs.getRow();
			logger.info(" : Method - GetInputDataFromMySQL: rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			logger.info(" : Method - GetInputDataFromMySQL: columns in result set: "+columns);

			arrayRecords = new String[rows+1][columns];

			rs.beforeFirst();       // Setting the cursor at first line

			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					if(rs.getRow()==1)
					{
						String strRecord = rs.getMetaData().getColumnLabel(i).toString();
						arrayRecords[rs.getRow()-1][i-1] = strRecord;

						String strRecord_1 = rs.getString(i).toString();
						arrayRecords[rs.getRow()][i-1] = strRecord_1;
						//logger.info(" : Content Getting Stored: " +strRecord);
					}
					else
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[rs.getRow()][i-1] = strRecord;
						//logger.info(" : Content Getting Stored: " +strRecord);
					}
				}
				//logger.info(" : ");
			}                            
			logger.info(" : MySQL Data with Column Names Was Successfully Exported By Method GetInputData. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);
		}
		catch(NullPointerException n)
		{
			logger.error(" : A NULL Record is returned in some column, Check The Query Result, returning a NULL array by Method : GetInputDataFromMySQL:", n);
		}
		catch (ArrayIndexOutOfBoundsException a) {
			logger.error(" : There was no record found in the Result Set for the given publisher, returning a NULL array by Method : GetInputDataFromMySQL:", a);
		}
		catch (Exception e) 
		{
			logger.error(" : Exception handled by Method : GetInputDataFromMySQL: ",e);
		}
		return arrayRecords;
	}


	//********** For Preroll - Get Input Data From MySQL DB Using A QUERY And Returning Two D Array: ***********************************//
	public static String [][] GetInputDataFromMySQLForSDK(Connection con, String deviceType, String sdkType) 
	{               
		String publisherEmail = propertyConfigFile.getProperty("publisherEmail").toString();
		//logger.info(" : PUBLISHER EMAIL: " +publisherEmail.toString());

		String sdkSubQuery;
		String [][]arrayRecords = null;

		//replacing [] - which is coming from config file 
		publisherEmail = publisherEmail.replace("[", "");
		publisherEmail = publisherEmail.replace("]", "");

		if(sdkType.equalsIgnoreCase("MediaPlayer"))
		{
			sdkSubQuery = " and ad.ad_format = 'preroll' " ;
		}
		else
		{
			sdkSubQuery = " and ad.ad_format not like '%preroll%' ";
		}

		try
		{
			//Added cam.review_status = 'Approved' to get only started campaigns
			String sqlSelectQuery = "SELECT pub.email AS Publisher_Email, cam.name AS Campaign_Name, ch.apikey AS Channel_APIKEY, ch.publisher_id AS Publisher_ID, " +
					" ch.id AS Channel_ID, cam.id AS Campaign_ID, ad.id AS ADS_ID, IFNULL(cam.video_choice,0) AS Video_Choice, IFNULL(cam.custom_details,0) AS Custom_Details," +
					" IFNULL(ad.ad_format, 'NoAdFormatSaved') AS Ad_Format, CEIL(IFNULL(ad.duration,0)) AS Ads_Duration, IFNULL(ad.dimension,0) AS Ads_Dimension, cam.device As Device_Type, " +
					" IFNULL(ad.action_type,0) AS Action_Type, IFNULL(cam.expandable,0) AS Expandable_Video, IFNULL(ad.template_layout,0) AS Ad_Template_Layout, " +
					" IFNULL(ad.branded_img_bot_ref_txbody, '') AS CompanionBanner " +
					" FROM channels ch INNER JOIN channel_settings chset ON ch.id = chset.channel_id INNER JOIN publisher pub ON ch.publisher_id = pub.id " +
					" INNER JOIN campaign cam ON ch.id = cam.channel_choice " +
					" INNER JOIN campaign_members camb ON cam.id = camb.cid " + "INNER JOIN ads ad ON ad.id = camb.ad_id " +
					" AND cam.status = 'active' AND cam.review_status = 'Approved' AND cam.validto > CURDATE() AND pub.email in ("+ publisherEmail +") AND cam.device = " + "'" + deviceType + "'" +
					" where cam.id NOT IN (SELECT cid from campaign_target) AND ad.ad_format <> 'tracker' " + sdkSubQuery +
					" ORDER BY cam.id ASC ";


			logger.info(" : Print SQL Query To Get Test DATA : " +sqlSelectQuery);

			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlSelectQuery);

			rs.last();      // Setting the cursor at last
			int rows = rs.getRow();
			logger.info(" : Method - GetInputDataFromMySQL: rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			logger.info(" : Method - GetInputDataFromMySQL: columns in result set: "+columns);

			arrayRecords = new String[rows+1][columns];

			rs.beforeFirst();       // Setting the cursor at first line

			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					if(rs.getRow()==1)
					{
						String strRecord = rs.getMetaData().getColumnLabel(i).toString();
						arrayRecords[rs.getRow()-1][i-1] = strRecord;

						String strRecord_1 = rs.getString(i).toString();
						arrayRecords[rs.getRow()][i-1] = strRecord_1;
						//logger.info(" : Content Getting Stored: " +strRecord);
					}
					else
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[rs.getRow()][i-1] = strRecord;
						//logger.info(" : Content Getting Stored: " +strRecord);
					}
				}
				//logger.info(" : ");
			}                                
			logger.info(" : MySQL Data with Column Names Was Successfully Exported By Method GetInputData. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);
		}
		catch(NullPointerException n)
		{
			logger.error(" : A NULL Record is returned in some column, Check The Query Result, returning a NULL array by Method : GetInputDataFromMySQL:", n);
		}
		catch (ArrayIndexOutOfBoundsException a) {
			logger.error(" : There was no record found in the Result Set for the given publisher, returning a NULL array by Method : GetInputDataFromMySQL:", a);
		}
		catch (Exception e) 
		{
			logger.error(" : Exception handled by Method : GetInputDataFromMySQL: ", e);
		}
		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set without Column Name) *********************************************//
	public static String [][] ExecuteMySQLQueryReturnsArray(Connection con, String sqlQuery) 
	{		

		String [][]arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(" :  Column Count: "+columns);

			arrayRecords = new String[rows][columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString();
					arrayRecords[rs.getRow()-1][i-1] = strRecord;
					//logger.info(" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsArray: " +strRecord);
					//}
				}
				//logger.info(" : ");
			}			
			//logger.info(" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsArray", e);
			logger.error(" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(" : Expection Handled By: ExecuteMySQLQueryReturnsArray", e);
		}

		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name *********************************************//
	public static String [] ExecuteMySQLQueryReturns1DArray(Connection con, String sqlQuery) 
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			if(rs !=null)
			{
				int columns = rs.getMetaData().getColumnCount();
				//logger.info(" :  Column Count: "+columns);

				arrayRecords = new String[columns];

				rs.beforeFirst();	// Setting the cursor at first line	
				while (rs.next())
				{
					for(int i=1;i<=columns;i++)
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[i-1] = strRecord;
						//logger.info(" : Writing Rows BY METHOD - ExecuteMySQLQueryReturns1DArray: " +arrayRecords[i-1]);
					}
				}	
			}
			else
			{
				logger.error(" : Received NULL record set for the supplied query: "+sqlQuery);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(" : Exception Handled By: ExecuteMySQLQueryReturns1DArray. ", e);
		}

		return arrayRecords;
	}


	/** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name 
	 * 
	 * @param con
	 * @param sqlQuery
	 * @return
	 */
	@SuppressWarnings("finally")
	public static List<String> ExecuteMySQLQueryReturnsList(Connection con, String sqlQuery)
	{		
		List<String> recordList = new ArrayList<String>();

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString().trim();
					recordList.add(strRecord);
				}
			}		
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(" : NullPointerExpection Handled By: ExecuteMySQLQueryReturns1DArray", e);
			logger.error(" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(" : Expection Handled By: ExecuteMySQLQueryReturnsList. " ,e);
		}
		finally
		{
			return recordList;
		}
	}


	//********** Executing MySQL Query and Returning 1 D Array containing the Only Column Name Of Result Set *********************************************//
	public static String [] ExecuteMySQLQueryReturnsOnlyColumnNames(Connection con, String sqlQuery) throws SQLException
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(" :  Column Count: "+columns);

			arrayRecords = new String[columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getMetaData().getColumnLabel(i).toString();
					arrayRecords[i-1] = strRecord;
					//logger.info(" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsOnlyColumnNames: " +strRecord);
				}
			}		
			con.close();			
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsOnlyColumnNames:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames", e);
			logger.error(" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.",e);
		}
		catch (Exception e) 
		{
			logger.error(" : Expection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames. ", e);
		}

		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set with Column Name) *********************************************//
	public static String [][] ExecuteMySQLQueryReturnsArrayWithColumnName(Connection con, String sqlQuery) 
	{		
		String [][]arrayRecords = null;
		logger.info(" : Running this query: "+sqlQuery);

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);
			/*
			//Un-comment this for debugging
			while (rs.next())
			{
				for(int i=1;i<=rs.getMetaData().getColumnCount();i++)
				{
					String strRecord = rs.getString(i).toString();
					System.out.print(" : "+strRecord);
				}
				logger.info();
			}
			 */
			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(" :  Column Count: "+columns);

			arrayRecords = new String[rows+1][columns];

			rs.beforeFirst();	// Setting the cursor at first line

			while (rs.next())
			{
				int currentRow = rs.getRow();

				for(int i=1;i<=columns;i++)
				{
					if(currentRow == 1)
					{
						String strRecord = rs.getMetaData().getColumnLabel(i).toString();
						arrayRecords[currentRow-1][i-1] = strRecord;
						//logger.info(" : Column Label: " +strRecord);

						String strRecord_1 = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord_1;
						//logger.info(" : Record: " +strRecord_1);
					}
					else
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord;
						//logger.info(" : record in result set: " +strRecord);
					}
				}

			}					
			//logger.info(" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);

		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(" : Exception Handled by Method : ExecuteMySQLQueryReturnsArray: ",e);
		}

		/*
		// Only for debugging
		for(int i=0; i<arrayRecords.length; i++)
		{
			for(int j=0; j<arrayRecords[0].length; j++)
			{
				System.out.print(" : " +arrayRecords[i][j]);
			}
			logger.info();
		}
		 */

		return arrayRecords;
	}



	//******************** Write the given list in Test Results excel sheet *************************************************//
	public static void WritingTestResultsInExcelSheet(File testResultFile, List<String> resultsList) throws IOException, RowsExceededException, WriteException, BiffException 
	{      

		logger.info(" : writting results .... ");

		Workbook book = Workbook.getWorkbook(testResultFile);
		WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);
		WritableSheet sheet = copiedBook.getSheet(0);
		/*    
                    //Setting Cell Format - Right Alignment and Wrap Text
                    WritableCellFormat cellFormat = new WritableCellFormat();
                    cellFormat.setAlignment(Alignment.RIGHT);
                    cellFormat.setWrap(true);
		 */

		int column = sheet.getColumns();
		Label lblColumnName = new Label(column, 0, "Test_Results");     //Adding Column Name = Test_Results in last Column and first row
		sheet.addCell(lblColumnName);

		CellFinder cellFind = new CellFinder(sheet);    //Finding the Cell with a particular text and later on, get the corresponding Row or Column;

		try {
			for(int row=1;row<sheet.getRows();row++)
			{
				String testResult = resultsList.get(row-1).trim().toString();

				//un-comment all syso for debugging.
				//logger.info(" : print received result string from tests: " +testResult);

				int testResultColumnNo = cellFind.findLabelCell("Test_Results").getColumn();

				//logger.info(" : Print Test Results Column No: " +testResultColumnNo);

				Label lblTestResult = new Label(testResultColumnNo, row, testResult);

				//logger.info(" : Print Label: " +lblTestResult.toString());

				sheet.addCell(lblTestResult);
				//logger.info(" : Test Results was written Successfully :" +testResult);
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while writing test results. ", e);
		}
		finally 
		{
			try
			{
				copiedBook.write();
				copiedBook.close();
				book.close();
				logger.info(" : Test Results was written successfully");
			}
			catch(NullPointerException n)
			{
				logger.error(" : NullPointerException Handled while writing test results, file format may have some issues. ", n);
			}
			catch(Exception n)
			{
				logger.error(" : Exception Handled while writing test results. ", n);
			}
		}  
	}



	//******************** Write the given list in Test Results excel sheet, here test result column name can be given *************************************************//
	public static void WritingTestResultsInExcelSheet(File testResultFile, List<String> resultsList, String testResultsColumnName) throws IOException, RowsExceededException, WriteException, BiffException, InterruptedException
	{       
		Workbook book = Workbook.getWorkbook(testResultFile);
		WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);
		WritableSheet sheet = copiedBook.getSheet(0);
		/*    
	                    //Setting Cell Format - Right Alignment and Wrap Text
	                    WritableCellFormat cellFormat = new WritableCellFormat();
	                    cellFormat.setAlignment(Alignment.RIGHT);
	                    cellFormat.setWrap(true);
		 */

		int column = sheet.getColumns();
		Label lblColumnName = new Label(column, 0, testResultsColumnName);     //Adding Column Name = Test_Results in last Column and first row
		sheet.addCell(lblColumnName);

		CellFinder cellFind = new CellFinder(sheet);    //Finding the Cell with a particular text and later on, get the corresponding Row or Column;

		try 
		{
			for(int row=1;row<sheet.getRows();row++)
			{
				String testResult = resultsList.get(row-1).trim().toString();

				//un-comment all syso for debugging.
				//logger.info(" : print received result string from tests: " +testResult);

				int testResultColumnNo = cellFind.findLabelCell(testResultsColumnName).getColumn();

				//logger.info(" : Print Test Results Column No: " +testResultColumnNo);

				Label lblTestResult = new Label(testResultColumnNo, row, testResult);

				//logger.info(" : Print Label: " +lblTestResult.toString());

				sheet.addCell(lblTestResult);
				//logger.info(" : Test Results was written Successfully :" +testResult);
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception Handled by Method : WritingTestResultsInExcelSheet. ",e);
		}
		finally 
		{
			try
			{
				copiedBook.write();
				copiedBook.close();
				book.close();
				logger.info(" : Test Results was written successfully");
			}
			catch(NullPointerException n)
			{
				logger.error(" : NullPointerException Handled while writing test results, file format may have some issues. ", n);
			}
			catch(Exception n)
			{
				logger.error(" : Exception Handled while writing test results. ", n);
			}
		} 
	}


	//******************** Get Current Date Time Stamp *************************************************//
	public static String DateTimeStamp()
	{
		try
		{
			//Reading Date Format From Configuration File. 
			String dateStampFormat = propertyConfigFile.getProperty("dateStampFormatForFileName").toString();
			//logger.info(" : Date Time Stamp Format will be:" +dateStampFormat);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}catch(Exception n)
		{
			logger.error(" : Please check variable - dateStampFormatForFileName in config file.", n);
			return null;
		}
	}



	//******************** Get Current Date Time Stamp *************************************************//
	public static String DateTimeStamp(String dateStampFormat)
	{
		try
		{
			//Sample: MMddyyyy_hhmmss
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(" : Please check the supplied date format. " , n);
			return null;
		}
	}



	//******************** Writing The Date Time Stamp *************************************************//
	public static String DateTimeStampWithMiliSecond()
	{
		try
		{
			String dateStampFormat = "MMddyyyy_hhmmss_ms";
			//logger.info(" : Date Time Stamp Format will be:" +dateStampFormat);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(" : Exception handled by method: DateTimeStampWithMiliSecond. ", n);
			return null;
		}
	}



}







