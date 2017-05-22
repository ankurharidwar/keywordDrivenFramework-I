/**
 * Last Changes Done on Jan 16, 2015 12:06:11 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */

package projects.portal;

import projects.TestSuiteClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import vlib.CaptureScreenShotLib;
import vlib.CustomException;


/**
 * This class is to get the webelements corresponding to objects received from test cases.
 */

public class GetObjects 
{

	String objectRepository;
	String objectNameColumnName;
	String identifierName;
	String identifierValue;
	String objectLabel;

	Logger logger = Logger.getLogger(GetObjects.class.getName());


	/**
	 * This constructor defines Object Repository location and various column name.
	 */
	GetObjects()
	{
		logger.info(" : Getting the object repository file with location");

		String objectRepoFileName = "";
		objectRepoFileName = "transformerPortal_ObjectRepository.xls";

		if(System.getProperty("os.name").matches("^Windows.*"))
		{
			this.objectRepository = TestSuiteClass.AUTOMATION_HOME.concat("\\object_repository\\portalObjectRepository\\"+objectRepoFileName);
		}
		else
		{
			this.objectRepository = TestSuiteClass.AUTOMATION_HOME.concat("/object_repository/portalObjectRepository/"+objectRepoFileName);
		}

		logger.debug(" : objectRepository location is : "+objectRepository);
		logger.info(" : Getting the columns name");

		this.objectNameColumnName = "objectName";
		this.identifierName="identifierName";
		this.identifierValue="identifierValue";
		this.objectLabel = "objectLabel";

		logger.debug(" : objectName column: "+objectNameColumnName+ ", identifierName column: "+identifierName+ ", identifierValue column: "+identifierValue + ", objectLabel column: "+objectLabel);
	}


	/**
	 * This function is to get the object definitions from repository based on
	 * the supplied objectName or objectLabel.
	 * This method throws a custom exception: CustomExceptionsLib in case there is no object found based on supplied objectName.
	 * 
	 * @param objectName
	 * @param objectNameOrLabel
	 * @param driver
	 * @return
	 */
	public  WebElement getWebElementFromRepository(String objectName, String objectNameOrLabel, WebDriver driver, JSONObject jsonObjectRepo) throws CustomException
	{
		logger.debug(" : Got the object name : "+objectName);

		String identifierName = "";
		String identifierValue = "";

		WebElement webelement = null;
		boolean objectNotFound = false;
		objectName = objectName.trim();

		try
		{
			/** Commenting this code, new code will get objects from json object */
			/*
			Workbook book = Workbook.getWorkbook(new File(objectRepository));
			Sheet sheet = book.getSheet(0);
			int objectName_column = sheet.findCell(this.objectNameColumnName, 0, 0, sheet.getColumns(), 0, false).getColumn();
			int identifierName_column = sheet.findCell(this.identifierName, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int identifierValue_column = sheet.findCell(this.identifierValue, 0, 0, sheet.getColumns(),0, false).getColumn();
			int objectLabel_column = sheet.findCell(this.objectLabel, 0, 0, sheet.getColumns(),0, false).getColumn();

			logger.debug(" : objectName column number: " +objectName_column +" identifierName column number: " +identifierName_column +" identifierValue column number: " +identifierValue_column + " objectLabel column number: "+objectLabel_column);


			for(int row =1; row<sheet.getRows(); row++)
			{

				//get object definition based on supplied value = name or label
				String objectFromRepository;

				if(objectNameOrLabel.equalsIgnoreCase("Label") || objectNameOrLabel.equalsIgnoreCase("Object Label") )
				{
					objectFromRepository = sheet.getCell(objectLabel_column, row).getContents().trim();
				}
				else
				{
					objectFromRepository = sheet.getCell(objectName_column , row).getContents().trim();
				}

				if(objectFromRepository.equalsIgnoreCase(objectName))
				{
					identifierName = sheet.getCell(identifierName_column , row).getContents().trim();
					identifierValue = sheet.getCell(identifierValue_column , row).getContents().trim();
					break;
				}
			}
			 */

			/************ new code to lookup object from json ************/

			/** if user supplied object label then get the respective object name - for further processing */
			if(objectNameOrLabel.equalsIgnoreCase("Label") || objectNameOrLabel.equalsIgnoreCase("Object Label") )
			{
				objectName = new GetObjectRepoAsJson().getObjectName(jsonObjectRepo, objectName);
			}

			/** get identifier name and value of received object name */
			try{identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");}catch(JSONException e){}; 
			try{identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");}catch(JSONException e){};

			logger.debug(" : identifierName: "+identifierName+ " identifierValue: "+identifierValue + " for object: "+objectName);

			if(objectName.isEmpty())
			{
				objectNotFound = true;
				logger.error(" : Supplied object was empty or this object isn't present in OR, object name: "+objectName);
			}
			else if(identifierName.isEmpty() || identifierValue.isEmpty())
			{
				objectNotFound = true;
				logger.error(" : There was no webelement definition found in Object Repository for object name: "+objectName);
			}
			else
			{
				/** Get webelement for the supplied definition from object repository */
				webelement = createWebElement(driver, objectName, identifierName, identifierValue);
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting webelement from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(objectNotFound)
		{
			logger.info(" : Either object: "+objectName +" was not found in repository or its definition is blank. ");
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}
		return webelement;
	}


	/** This method will retrieve the locator and locator value of supplied the object from Object Repository Sheet and create the By locator 
	 * for this object and return that. 
	 * 
	 * @param objectName
	 * @param driver
	 * @return
	 * @throws CustomException 
	 */
	public By getByLocatorFromRepository(String objectName, WebDriver driver, JSONObject jsonObjectRepo) throws CustomException
	{
		String identifierName = "";
		String identifierValue = "";

		By by = null;
		boolean objectNotFound = false;
		try
		{
			/** Commenting this code, new code will get objects from json object */
			/* 
			Workbook book = Workbook.getWorkbook(new File(objectRepository));
			Sheet sheet = book.getSheet(0);
			int objectName_column = sheet.findCell(this.objectNameColumnName, 0, 0, sheet.getColumns(), 0, false).getColumn();
			int identifierName_column = sheet.findCell(this.identifierName, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int identifierValue_column = sheet.findCell(this.identifierValue, 0, 0, sheet.getColumns(),0, false).getColumn();

			logger.debug(" : objectName column number: " +objectName_column +" identifierName column number: " +identifierName_column +" identifierValue column number: " +identifierValue_column);

			for(int row =1; row<sheet.getRows(); row++)
			{
				String objectName_value = sheet.getCell(objectName_column , row).getContents().trim();

				if(objectName_value.equalsIgnoreCase(objectName))
				{
					identifierName = sheet.getCell(identifierName_column , row).getContents().trim();
					identifierValue = sheet.getCell(identifierValue_column , row).getContents().trim();
					break;
				}
			}
			 */

			/************ new code to lookup object from json ************/

			/** get identifier name and value of received object name */
			try{identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");}catch(JSONException e){}; 
			try{identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");}catch(JSONException e){};

			logger.debug(" : identifierName: "+identifierName+ " identifierValue: "+identifierValue + " for object: "+objectName);

			if(identifierName.isEmpty() || identifierValue.isEmpty())
			{
				objectNotFound = true;
				logger.error(" : There was no webelement definition found in Object Repository for object name: "+objectName);
			}
			else
			{
				/** Get webelement for the supplied definition from object repository */
				by = createByLocator(objectName, identifierName, identifierValue);
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting by locator from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(objectNotFound)
		{
			logger.info(" : Object: "+objectName +" was not found in repository. ");
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}
		return by;

	}


	/** This method will retrieve the locator and locator value of supplied the object from Object Repository Sheet and create the By locator 
	 * for this object and return that. This method will be used when the element is dynamic and the supplied data is needed to create 
	 * webelement finally.
	 * 
	 * @param objectName
	 * @param driver
	 * @return
	 * @throws CustomException 
	 */
	public By getByLocatorFromRepository(String objectName, String data, WebDriver driver, JSONObject jsonObjectRepo) throws CustomException
	{
		logger.debug(" : Got the object name : "+objectName);

		String identifierName = "";
		String identifierValue = "";

		By by = null;
		boolean objectNotFound = false;
		boolean throwCustomException = false;
		try
		{
			/** Commenting this code, new code will get objects from json object */

			/*
			Workbook book = Workbook.getWorkbook(new File(objectRepository));
			Sheet sheet = book.getSheet(0);
			int objectName_column = sheet.findCell(this.objectNameColumnName, 0, 0, sheet.getColumns(), 0, false).getColumn();
			int identifierName_column = sheet.findCell(this.identifierName, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int identifierValue_column = sheet.findCell(this.identifierValue, 0, 0, sheet.getColumns(),0, false).getColumn();

			logger.debug(" : objectName column number: " +objectName_column +" identifierName column number: " +identifierName_column +" identifierValue column number: " +identifierValue_column);

			for(int row =1; row<sheet.getRows(); row++)
			{
				String objectName_value = sheet.getCell(objectName_column , row).getContents().trim();

				if(objectName_value.equalsIgnoreCase(objectName))
				{
					identifierName = sheet.getCell(identifierName_column , row).getContents().trim();
					identifierValue = sheet.getCell(identifierValue_column , row).getContents().trim();
					break;
				}
			}
			 */

			/************ new code to lookup object from json ************/

			/** get identifier name and value of received object name */
			try{identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");}catch(JSONException e){}; 
			try{identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");}catch(JSONException e){};

			logger.debug(" : identifierName: "+identifierName+ " identifierValue: "+identifierValue + " for object: "+objectName);

			if(identifierName.isEmpty() || identifierValue.isEmpty())
			{
				objectNotFound = true;
				logger.error(" : There was no webelement definition found in Object Repository for object name: "+objectName);
			}
			else
			{
				/** Get webelement for the supplied definition from object repository */
				if(identifierValue.contains("~~"))
				{
					data = data.toLowerCase();
					identifierValue = identifierValue.replace("~~", data).trim();
					by = createByLocator(objectName, identifierName, identifierValue);
				}
				else
				{
					throwCustomException = true;
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting by locator from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(objectNotFound)
		{
			logger.info(" : Object: "+objectName +" was not found in repository. ");
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}

		/** Throw a Customexception if supplied object is not generic -- means doesn't contain ~~ */
		if(throwCustomException)
		{
			logger.info(" : Supplied object: "+objectName +" is not generic. ");
			throw new CustomException("Supplied object: "+objectName +" is not generic. ");
		}
		return by;
	}


	/** This method will find the element based on the supplied identifier name and value.
	 *
	 * @param driver 
	 * @param objectName 
	 * @param identifierName 
	 * @param identifierValue 
	 * @return 
	 */
	@SuppressWarnings({ "finally", "unchecked", "rawtypes" })
	public  WebElement createWebElement(WebDriver driver, String objectName, String identifierName, String identifierValue)
	{
		By byObjectToInvokeMethod = null;
		By byObjectCollectWebElement = null;
		WebElement webElementFound = null;

		String locationToSaveSceenShot = TestSuiteClass.AUTOMATION_HOME.concat("/screenshots/").concat("ErrorElement").concat("/").concat(objectName + ".png");

		try
		{
			/** Get By Class Dynamically */
			Class byClass = Class.forName(By.class.getName());

			/** Get By Method - to be used while finding web elements */
			Method byMethod = byClass.getMethod(identifierName, String.class);

			/** Invoke By Method like By.cssSelector("v") and collect value */ 
			byObjectCollectWebElement = (By) byMethod.invoke(byObjectToInvokeMethod, identifierValue); 

			/** wait for max 60 sec until the element is not present */
			new HandlerLib().applyExplicitWait(driver, byObjectCollectWebElement, new NoSuchElementException(""), 60);

			/** Get Webdriver class dynamically */
			Class webDriverClass = Class.forName(WebDriver.class.getName());

			/** Get Webdriver Method - to be used while finding web elements */
			Method webDriverMethod = webDriverClass.getMethod("findElement", new Class[]{By.class});

			/** Invoke WebDriver method like w.findElement(By.cssSelector("v")) and collect Web Element */ 
			webElementFound = (WebElement) webDriverMethod.invoke(driver, byObjectCollectWebElement);

			/** execute java script to bring element into foucs */
			new HandlerLib().executeJavaScript(driver, "arguments[0].scrollIntoView(false);", webElementFound);
		}
		catch(InvocationTargetException e)
		{
			logger.error(" : No Such Element Found, Check Repository: "+objectName + " identifier: "+identifierName + " identifier value: "+identifierValue, e);
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot);
			logger.info(" : Screenshot is saved for element: "+objectName + " at location: "+locationToSaveSceenShot);
		}
		catch (Exception e) 
		{
			/** Capture Screenshot */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot);
			logger.info(" : Screenshot is saved for element: "+objectName + " at location: "+locationToSaveSceenShot);

			logger.error(" : Error occurred while finding element: "+objectName + " identifier: "+identifierName + " identifier value: "+identifierValue, e);
		}
		finally
		{
			return webElementFound;
		}
	}


	/** This method will find the by type of web element based on the supplied identifier name and value.
	 *
	 * @param driver 
	 * @param objectName 
	 * @param identifierName 
	 * @param identifierValue 
	 * @return 
	 */
	@SuppressWarnings({ "finally", "unchecked", "rawtypes" })
	public By createByLocator(String objectName, String identifierName, String identifierValue)
	{
		By byObjectToInvokeMethod = null;
		By byObjectCollectWebElement = null;

		try
		{
			/** Get By Class Dynamically */
			Class byClass = Class.forName(By.class.getName());

			/** Get By Method - to be used while finding web elements */
			Method byMethod = byClass.getMethod(identifierName, String.class);

			/** Invoke By Method like By.cssSelector("v") and collect value */
			byObjectCollectWebElement = (By) byMethod.invoke(byObjectToInvokeMethod, identifierValue); 
		}
		catch (Exception e) 
		{
			logger.error(" : Error occurred while finding element: "+objectName, e);
		}
		finally
		{
			return byObjectCollectWebElement;
		}
	}


	/** This method will retrieve the supplied the object locator and its value from Object Repository Sheet  
	 * and return the identifier name and identifier value, appended by ##
	 * 
	 * @param objectName
	 * @param driver
	 * @return
	 * @throws CustomException 
	 */
	public String getObjectLocatorNameValueFromRepository(String objectName, JSONObject jsonObjectRepo) throws CustomException
	{
		logger.debug(" : Got the object name : "+objectName);

		String identifierName = "";
		String identifierValue = "";

		try
		{
			/** Commenting this code, new code will get objects from json object */

			/*
			Workbook book = Workbook.getWorkbook(new File(objectRepository));
			Sheet sheet = book.getSheet(0);
			int objectName_column = sheet.findCell(this.objectNameColumnName, 0, 0, sheet.getColumns(), 0, false).getColumn();
			int identifierName_column = sheet.findCell(this.identifierName, 0, 0,sheet.getColumns(), 0 , false).getColumn();
			int identifierValue_column = sheet.findCell(this.identifierValue, 0, 0, sheet.getColumns(),0, false).getColumn();

			logger.debug(" : objectName column number: " +objectName_column +" identifierName column number: " +identifierName_column +" identifierValue column number: " +identifierValue_column);

			for(int row =1; row<sheet.getRows(); row++)
			{
				String objectName_value = sheet.getCell(objectName_column , row).getContents().trim();

				if(objectName_value.equalsIgnoreCase(objectName))
				{
					identifierName = sheet.getCell(identifierName_column , row).getContents().trim();
					identifierValue = sheet.getCell(identifierValue_column , row).getContents().trim();
					break;
				}
			}
			 */
			/************ new code to lookup object from json ************/

			/** if user supplied object label then get the respective object name - for further processing */

			/** get identifier name and value of received object name */
			try{identifierName = jsonObjectRepo.getJSONObject(objectName).getString("identifierName");}catch(JSONException e){}; 
			try{identifierValue = jsonObjectRepo.getJSONObject(objectName).getString("identifierValue");}catch(JSONException e){};

			logger.debug(" : identifierName: "+identifierName+ " identifierValue: "+identifierValue + " for object: "+objectName);
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting by locator from repository:" +objectRepository, e);
		}

		/** Throw a Customexception if supplied object not found in repository */
		if(identifierName.isEmpty() || identifierValue.isEmpty())
		{
			logger.error(" : There was no webelement definition found in Object Repository for object name: "+objectName);
			throw new CustomException("Object: "+objectName +" was not found in repository. ");
		}			

		/** returning identifier name and identifier value, appended by ## */
		return identifierName+"##"+identifierValue;
	}

}
