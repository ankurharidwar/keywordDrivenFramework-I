/**
 * Last Changes Done on Feb 20, 2015 3:32:54 PM
 * Last Changes Done by ${author}
 * Change made in Vdopia_Automation
 * Purpose of change: Changed Name of class, adding few methods
 * 
 * Sample javascript to check xpath in Browser Console:
var element = document.evaluate("//a[@id='clients' and @class='dropdown-toggle toggle-active']" ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
if (element != null) {
  element.click();
};
 *
 *
 * Sample javascript to check the top window of in webpage
function myFunction() { if (window.top != window.self)  { alert('No! Top Window');} 
else { alert('Yes! Top Window'); } }; 
myFunction(); 
 *  
 */



package projects.portal;

import projects.TestSuiteClass;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mysql.jdbc.Connection;

import vlib.CaptureScreenShotLib;
import vlib.CustomException;
import vlib.CustomExceptionStopExecution;
import vlib.IntegerLib;
import vlib.MobileTestClass_Methods;



public class HandlerLib 
{
	Logger logger = Logger.getLogger(HandlerLib.class.getName());


	/** This method is being used to switch between iframes.
	 * @param driver
	 * @param byLocator
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean moveToFrame(WebDriver driver, String frameName)
	{
		boolean flag = false;
		try{
			driver.switchTo().defaultContent();
			WebDriverWait wait = new WebDriverWait(driver, 120);
			try{
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
			}catch(TimeoutException t){driver.navigate().refresh();}
			logger.info(" : Switching to frame: "+frameName);			
			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while switching frame: ", e);
		}
		finally
		{
			return flag;
		}
	}


	/** This method will be called by keyword class while performing operations, almost all thrown selenium exception will be handled 
	 * by explicit webdriver wait. this method accepts by locator, and desired exception to be handled.
	 * 
	 *  
	 * @param driver
	 * @param webelement
	 * @param bylocator
	 * @param e
	 * @return
	 */
	public boolean applyExplicitWait(WebDriver driver, By bylocator, Exception e)
	{
		boolean flag = false;
		try
		{
			logger.info(" : Explicit Wait of 60 seconds is being applied .... ");

			WebDriverWait wait = new WebDriverWait(driver, 60);

			if(e instanceof ElementNotVisibleException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
			}
			else if(e instanceof WebDriverException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
				wait.until(ExpectedConditions.elementToBeClickable(bylocator));
			}
			else if(e instanceof NoSuchElementException)
			{
				wait.until(ExpectedConditions.presenceOfElementLocated(bylocator));
			}

			flag = true;
		}
		catch(TimeoutException t){
			logger.error( " : " + t.getMessage());
		}
		catch(Exception ex)
		{
			flag = false;
			logger.error(" : Error occurred while performing external wait. ", ex);
		}
		return flag;
	}


	/** This method will be called by keyword class while performing operations, almost all thrown selenium exception will be handled 
	 * by explicit webdriver wait. this method accepts by locator, and desired exception to be handled.
	 * 
	 * @param driver
	 * @param bylocator
	 * @param e
	 * @param timeout
	 * @return
	 */
	public boolean applyExplicitWait(WebDriver driver, By bylocator, Exception e, long timeoutSeconds)
	{
		boolean flag = false;
		try
		{
			logger.info(" : Explicit Wait of "+timeoutSeconds +" seconds is being applied .... ");

			WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);

			if(e instanceof ElementNotVisibleException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
			}
			else if(e instanceof WebDriverException)
			{
				wait.until(ExpectedConditions.visibilityOfElementLocated(bylocator));
				wait.until(ExpectedConditions.elementToBeClickable(bylocator));
			}
			else if(e instanceof NoSuchElementException)
			{
				wait.until(ExpectedConditions.presenceOfElementLocated(bylocator));
			}

			flag = true;
		}
		catch(WebDriverException w)
		{
			if(w instanceof UnhandledAlertException)
			{
				org.openqa.selenium.Alert alert = driver.switchTo().alert();
				String text = alert.getText();
				alert.accept();

				logger.error(" : Unhandled alert found having text: "+text+ ", re-applying explicit wait ...",w);
				applyExplicitWait(driver, bylocator, e, timeoutSeconds);
			}
		}
		catch(Exception ex)
		{
			flag = false;
			logger.error(" : Error occurred while performing external wait. ", ex);
		}
		return flag;
	}


	/** This method will be used to select date from calendar
	 * 
	 * @param calendar
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean selectDateFromCalendar(WebDriver driver, WebElement webElementCalendarIcon, String strSuppliedDate, JSONObject jsonObjectRepo)
	{
		boolean flag = false;

		try
		{
			/** get currently selected date, selected date is retrieved as  */
			String currentSelectedValue = webElementCalendarIcon.getAttribute("value").trim();

			/** click calendar icon only if there is any difference between 
			 * date to be selected and already selected date, formatting the supplied date and currently selected date according their format */
			long dateDiff = getDateDiff(new SimpleDateFormat("dd/MM/yyyy").parse(strSuppliedDate), new SimpleDateFormat("yyyy-MM-dd").parse(currentSelectedValue));

			if(dateDiff != 0)
			{
				/** Click calendar icon */
				webElementCalendarIcon.click();

				/**Earlier:  Convert the received selected value to dd/MM/yy format as the received date has format = MMMM dd, yyyy 
				 * Current: now current selected date is taken from the actual calendar not from the selected value; */
				//currentSelectedValue = new DateParser().formatDate(currentSelectedValue, "yyyy-MM-dd");

				currentSelectedValue = getCurrentSelectedCalendarDate(driver, jsonObjectRepo);
				currentSelectedValue = new DateParser().formatDate(currentSelectedValue, "dd MMM yyyy");

				String day = new DateParser().getDateValues(strSuppliedDate, "date", false);
				logger.info(" : Selecting Date: "+day);

				//Select desired month
				selectDesiredMonth(driver, strSuppliedDate, currentSelectedValue, jsonObjectRepo);

				//Click desired day in calendar
				String calendarObjectName = "Calender_element";		//Object name to be used to get detail from repository
				new HandlerLib().clickDayInCalendar(driver, calendarObjectName, day, jsonObjectRepo);
			}

			flag = true;
		}
		catch(ParseException p)
		{
			flag = false;
			logger.error(" : Prase exception: ", p);
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while selecting date from calendar. ", e);
		}
		finally
		{
			return flag;
		}

	}


	/** This method will get the difference in supplied dates like date1 - date2
	 * and return the difference in days
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException 
	 */
	public long getDateDiff(Date dateFirst, Date dateSecond) throws ParseException
	{
		long duration = 0;

		//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		//		Date dateFirst = format.parse(date1);
		//		Date dateSecond = format.parse(date2);

		duration = TimeUnit.MILLISECONDS.toDays((dateFirst.getTime() - dateSecond.getTime()));

		logger.info(" : date difference in days: "+duration);

		return duration;
	}


	/** This method will be used to select date range from calendar on placement and sub-placement screens.
	 * 
	 * @param calendar
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean selectDateRangeFromCalendar(WebDriver driver, String strDateLeftCalendar, String strDateRightCalendar, JSONObject jsonObjectRepo)
	{
		boolean flag = false;

		try
		{
			//getting values for left calendar
			String dayLeftCalendar = new DateParser().getDateValues(strDateLeftCalendar, "date", false);
			String monthLeftCalendar = new DateParser().getDateValues(strDateLeftCalendar, "month", false);
			String yearLeftCalendar = new DateParser().getDateValues(strDateLeftCalendar, "year", false);
			logger.info(" : Selecting Values In Left Calendar, day: "+dayLeftCalendar + ", month: "+monthLeftCalendar + ", year: "+yearLeftCalendar);

			//Select month first in left calendar and then year
			boolean monthSelection = new HandlerLib().selectMonthYearFromCalendar(driver, "Placement_DateRange_Select_LeftCalendar_MonthSelect", "month", monthLeftCalendar, "", jsonObjectRepo);
			boolean yearSelection = new HandlerLib().selectMonthYearFromCalendar(driver, "Placement_DateRange_Select_LeftCalendar_YearSelect", "year", monthLeftCalendar, yearLeftCalendar, jsonObjectRepo);

			logger.info(" : Left calendar, month selection status: "+monthSelection + " and " + yearSelection);

			//Click desired day in left calendar
			String leftcalendarObjectName = "Placement_DateRange_Select_LeftCalendar";		//Object name to be used to get detail from repository
			boolean dayClicked = new HandlerLib().clickDayInCalendar(driver, leftcalendarObjectName, dayLeftCalendar, jsonObjectRepo);
			logger.info(" : Left calendar, day selection status: "+dayClicked);

			//getting values for right calendar
			String dayRightCalendar = new DateParser().getDateValues(strDateRightCalendar, "date", false);
			String monthRightCalendar = new DateParser().getDateValues(strDateRightCalendar, "month", false);
			String yearRightCalendar = new DateParser().getDateValues(strDateRightCalendar, "year", false);
			logger.info(" : Selecting Values In Right Calendar, day: "+dayRightCalendar + ", month: "+monthRightCalendar + ", year: "+yearRightCalendar);

			//Select month first in right calendar and then year
			monthSelection = new HandlerLib().selectMonthYearFromCalendar(driver, "Placement_DateRange_Select_RightCalendar_MonthSelect", "month", monthRightCalendar, "", jsonObjectRepo);
			yearSelection = new HandlerLib().selectMonthYearFromCalendar(driver, "Placement_DateRange_Select_RightCalendar_YearSelect", "year", monthRightCalendar, yearRightCalendar, jsonObjectRepo);

			logger.info(" : Right calendar, month selection status: "+monthSelection + " and " + yearSelection);

			//Click desired day in right calendar
			String rightcalendarObjectName = "Placement_DateRange_Select_RightCalendar";		//Object name to be used to get detail from repository
			dayClicked = new HandlerLib().clickDayInCalendar(driver, rightcalendarObjectName, dayRightCalendar, jsonObjectRepo);
			logger.info(" : right calendar, day selection status: "+dayClicked);

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while selecting date range from calendar. ", e);
		}
		finally
		{
			return flag;
		}

	}


	/** This method will be used to select month and year from drop down from calendars in placement and subplacement page.
	 * 
	 * @param calendar
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean selectMonthYearFromCalendar(WebDriver driver, String monthYearObjectName, String monthYearFlag, String monthNumber, String yearNumber, JSONObject jsonObjectRepo)
	{
		boolean flag = false;

		try
		{
			//Select month first in left calendar
			WebElement leftmonthSelect = new GetObjects().getWebElementFromRepository(monthYearObjectName, "name", driver, jsonObjectRepo);
			Select select = new Select(leftmonthSelect);

			if(monthYearFlag.equalsIgnoreCase("month"))
			{
				select.selectByValue(monthNumber);
				flag = true;
			}
			else
			{
				select.selectByValue(yearNumber);
				flag = true;
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while selecting month/year in calendar. ", e);
		}
		finally
		{
			return flag;
		}

	}


	/** This method will be used to click on desired day in calendar.
	 * 
	 * @param calendar
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean clickDayInCalendar(WebDriver driver, String calendarObjectName, String day, JSONObject jsonObjectRepo)
	{
		boolean flag = false;

		try
		{
			//Collect all cells of actual calendar in a list
			WebElement webelementCalendar = new GetObjects().getWebElementFromRepository(calendarObjectName, "name", driver, jsonObjectRepo);
			List<WebElement> listCellsStartCalendar = webelementCalendar.findElements(By.tagName("td"));		

			for (WebElement cell: listCellsStartCalendar)
			{ 
				/** Get text of each cell by parsing as int at first to remove any zero like (02, 03 etc..) and then converting back to string. */
				String cellText = String.valueOf(Integer.parseInt(cell.getText()));

				/** Get the class name of cell and this class should not contain text = available off, 
				 * to make sure date of current month is being selected.
				 */
				String cellClassName = cell.getAttribute("class").trim();

				if( cellText.equalsIgnoreCase(day) && (!cellClassName.contains("off") && (!cellClassName.equalsIgnoreCase("week")))
						//(cellClassName.contains("available in-range") || cellClassName.contains("available"))
						)
				{
					flag = true;

					//cell.click();
					Thread.sleep(1000);

					Actions action = new Actions(driver);
					action.click(cell)
					.build()
					.perform();

					Thread.sleep(1000);

					break;
				}
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Error occurred while clicking day on calendar. ", e);
		}
		finally
		{
			return flag;
		}

	}


	/**
	 * Get current selected date from calendar
	 * @param driver
	 * @return
	 */
	public String getCurrentSelectedCalendarDate(WebDriver driver, JSONObject jsonObjectRepo)
	{
		String date = null;
		WebElement webElementCalendar;

		try {
			webElementCalendar = new GetObjects().getWebElementFromRepository("Calender_element", "name", driver, jsonObjectRepo);
			By byElement = new GetObjects().getByLocatorFromRepository("calendar_month_title", driver, jsonObjectRepo);

			String monthTitle = webElementCalendar.findElement(byElement).getText().trim();

			//By bySelectedDate = new GetObjects().getByLocatorFromRepository("Calendar_CurrentSelected_Date", driver);
			//String currentSelectedDate = webElementCalendar.findElement(bySelectedDate).getText().trim();

			String currentSelectedDate ="";
			List<WebElement> td = webElementCalendar.findElements(By.tagName("td"));
			for(WebElement w : td)
			{
				//logger.info(" : text: "+w.getText());

				if(w.getAttribute("class").contains("available active"))
				{
					currentSelectedDate = w.getText();
					break;
				}
			}

			date = currentSelectedDate + " " + monthTitle;
			logger.info(" : Current selected date: "+date);
		} catch (CustomException e) 
		{
			logger.error(" : Error occurred: "+e);
		}

		return date;
	}


	/** This method will be used to move on next or previous month
	 * @param driver
	 * @param strDate
	 */
	public boolean selectDesiredMonth(WebDriver driver, String strSuppliedDate, String currentSelectedValue, JSONObject jsonObjectRepo)
	{

		boolean flag = false;
		try
		{
			//Get the month and year of supplied date
			String suppliedMonthName = new DateParser().getDateValues(strSuppliedDate, "month", true);
			String suppliedYear = new DateParser().getDateValues(strSuppliedDate, "year", false);

			String expectedMonthTitle = suppliedMonthName + " " + suppliedYear;
			logger.info(" : Desired Text To Be Searched: "+expectedMonthTitle);	

			/** Get month as integer */
			int suppliedMonth = Integer.parseInt(new DateParser().getDateValues(strSuppliedDate, "month", false));			

			/** Get the difference in the supplied and desired dates
			 */

			/** Commenting this code earlier difference was taken between current and desired dates,
			 * now difference will be taken between currently selected date and desired date
			 */
			/*
			Calendar calendar = Calendar.getInstance();
			int yearDiff = Integer.parseInt(suppliedYear) - calendar.get(Calendar.YEAR);
			int monthDiff = suppliedMonth - calendar.get(Calendar.MONTH);
			 */

			//Get month and year from the supplied currently selected date
			int currentSelectedMonth = Integer.parseInt(new DateParser().getDateValues(currentSelectedValue, "month", false));
			int currentSelectedYear = Integer.parseInt(new DateParser().getDateValues(currentSelectedValue, "year", false));

			int yearDiff = Integer.parseInt(suppliedYear) - currentSelectedYear;
			int monthDiff = suppliedMonth - currentSelectedMonth;


			boolean moveBackward = false;
			boolean moveForward = false;

			if(yearDiff < 0)
			{
				//move backward
				moveBackward = true;
			}
			else if(yearDiff > 0)
			{
				//move forward
				moveForward = true;
			}
			else
			{
				//check month diff

				//Move forward
				if(monthDiff > 0)
				{
					moveForward = true;
				}
				//move backward
				else if(monthDiff < 0)
				{
					moveBackward = true;
				}
				else
				{
					logger.info(" : No need to click any where. ");
				}

			}

			boolean clickFlag = true;

			while(clickFlag)
			{
				//Clicking on actual calendar
				WebElement webElementCalendar = new GetObjects().getWebElementFromRepository("Calender_element", "name", driver, jsonObjectRepo);
				WebElement monthNavigator = null;


				/** Adding a check on staleExceptionHandleFlag exception, in case this occurs then find the element again until the max attempt = 5.
				 */
				boolean staleExceptionHandleFlag = true;
				int staleExceptionAttempt=0;

				while(staleExceptionHandleFlag)
				{
					try
					{
						if(moveForward)
						{
							By byElement = new GetObjects().getByLocatorFromRepository("calender_next_icon", driver, jsonObjectRepo);
							monthNavigator = webElementCalendar.findElement(byElement);
						}
						else if(moveBackward)
						{
							By byElement = new GetObjects().getByLocatorFromRepository("calender_prev_icon", driver, jsonObjectRepo);
							monthNavigator = webElementCalendar.findElement(byElement);
						}
						else
						{
							clickFlag = false;
						}

						if(clickFlag)
						{
							//getting calendar's month title name like Feb 2015
							By byElement = new GetObjects().getByLocatorFromRepository("calendar_month_title", driver, jsonObjectRepo);

							String monthTitle = webElementCalendar.findElement(byElement).getText().trim();
							logger.info(" : Text month name: "+monthTitle);

							if(expectedMonthTitle.equalsIgnoreCase(monthTitle))
							{
								clickFlag = false;
								logger.info(" : Found Desired Text: "+monthTitle);
							}
							else
							{
								monthNavigator.click();
							}
						}


						staleExceptionHandleFlag = false;
					}
					catch(StaleElementReferenceException e)
					{
						staleExceptionAttempt++;
					}

					if(staleExceptionAttempt ==5){
						break;
					}

				}

			}

			flag = true;
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while choosing the desired month. ", e);
		}

		return flag;
	}


	/** This method will wait until a particular text is displayed in video upload screen, to make sure  video is 
	 * successfully uploaded.
	 * 
	 * @param elementXPathLocator
	 * @param desiredText
	 */
	public String waitForUploadVideoText(WebDriver driver, By byElement, String desiredText) 
	{
		String result = "";
		boolean flag = false;
		WebElement desiredWebElement = null;

		try
		{	
			logger.info(" : Desired Text: "+desiredText );

			//Adding explicit wait for webelement having upload text to appear
			WebDriverWait wait = new WebDriverWait(driver, 600);
			wait.until(ExpectedConditions.presenceOfElementLocated(byElement));			

			for(int i=0; i<600; i ++)
			{
				logger.info(" : Time Stamp: " +MobileTestClass_Methods.DateTimeStamp());

				desiredWebElement = driver.findElement(byElement); 

				String dynamicText = desiredWebElement.getText().trim();

				logger.info(" : Print Text: " +dynamicText);

				if(dynamicText.toLowerCase().contains(desiredText.toLowerCase().trim()))
				{
					logger.info(" : Desired text is found: " +dynamicText);
					flag = true;
					break;
				}

				Thread.sleep(1000);
			}
			if(flag)
			{
				result = "Pass: Video was uploaded successfully. ";
				logger.info(" : Video is uploaded and notified for Video Ad: ");
			}
			else
			{
				result = "Fail: Video wasn't uploaded. ";
				logger.error(" : Video uploaded message was not found. ");
			}	
		}
		catch (Exception e) 
		{
			logger.error(" : Exception occurred. ", e);
		}
		return result; 
	}


	/** This method is a customized work around to bring browser on top of all windows and keep in focus.
	 * 
	 * @param driver
	 */
	public void getBrowserInFocus(WebDriver driver)
	{
		try
		{
			//Store window state
			String currentWindowHandle = driver.getWindowHandle();

			//run javascript and alert code
			((JavascriptExecutor)driver).executeScript("alert('Test')"); 

			//Accept alert
			try{driver.switchTo().alert().accept();}
			catch(NoAlertPresentException n){}

			//Switch back to to the window using the handle saved earlier
			driver.switchTo().window(currentWindowHandle);
		}
		catch(Exception e)
		{
			logger.error(" : Exception: ", e);
		}
	}


	/** This method will check if there is any alert is present.
	 * 
	 * @param driver
	 * @return
	 */
	public boolean checkIfAlertPresent(WebDriver driver)
	{
		try
		{
			driver.switchTo().alert();
			logger.warn(" : Alert is displayed having text: " +driver.switchTo().alert().getText());

			driver.switchTo().alert().accept();
			return true;
		}
		catch(NoAlertPresentException ex)
		{
			return false;
		}
		catch (Exception e) {
			return false; 
		}
	}


	/** This method will execute the javascript to perform the operation.
	 * This method returns the object --> which can store boolean, string and integer.
	 * As it is returning false in case of exception, so at the time of using this method, always use condition like:
	 * if((Boolean) objJsOutput){ } 
	 * 
	 * @param driver
	 * @param javaScript
	 * @param webelement
	 * @return
	 */
	public Object executeJavaScript(WebDriver driver, String javaScript, WebElement webelement)
	{
		/**
		 * Sample code, if javascript has to be executed on a webelement:
		 * js.executeScript("arguments[0].click()", webelement);
		 */

		Object objJsOutput = null;
		try
		{
			JavascriptExecutor js = (JavascriptExecutor) driver;

			if(webelement != null)
			{
				objJsOutput = js.executeScript(javaScript, webelement);
			}
			else
			{
				objJsOutput = js.executeScript(javaScript);
			}
		}
		catch(WebDriverException w)
		{
			logger.error(" : Error occurred while executing java script: "+javaScript +" for supplied element: "+webelement);
		}
		catch(Exception ex)
		{
			logger.error(" : Exception occurred while executing java script: "+javaScript +" for supplied element: "+webelement, ex);
		}
		return objJsOutput;
	}


	/** This method will return the xpath formed for elements for the searched placement
	 * 
	 * @param driver
	 * @param placementName
	 * @param whichXpath
	 * @return
	 */
	public WebElement getSearchedPlacementElements(WebDriver driver, String placementName, String whichXpath)
	{

		WebElement webelement = null;
		try
		{
			String xpath = "";

			if(whichXpath.equalsIgnoreCase("placement"))
			{
				xpath = "//table[contains(@class,'display _dataTables table table-striped table-hover')]/tbody/tr/td/a[@title='"+ placementName +"']";

			}
			else
			{
				/** Sample Xpath of using parent and sibling element 
				 * //tr[contains(@id,'rowplacementsummary')]/..//td/a[@title='Mukesh_12March_AnimatedBanner']/../following-sibling::td/div/a 
				 */

				String xpathForPlacementRow = "//tr[contains(@id,'rowplacementsummary')]/..//td/a[@title='"+ placementName +"']";

				String xpathForPlacementRowSiblings = xpathForPlacementRow + "/../following-sibling::"; 

				if(whichXpath.equalsIgnoreCase("rowsetting"))
				{	
					xpath = xpathForPlacementRowSiblings + "td/div";
				}
				else if(whichXpath.equalsIgnoreCase("editPlacement"))
				{
					xpath = xpathForPlacementRowSiblings + "td/div/ul/li/a/span[contains(@class,'icon-edit')]";
				}
				else if(whichXpath.equalsIgnoreCase("viewsubplacements"))
				{
					xpath = xpathForPlacementRowSiblings + "td/div/ul/li/a[contains(text(),'Sub-Placements')]";
				}
				else if(whichXpath.equalsIgnoreCase("createsubplacement"))
				{
					xpath = xpathForPlacementRowSiblings + "td/div/ul/li/a[contains(text(),'Create')]";
				}
				else if(whichXpath.equalsIgnoreCase("viewreport"))
				{
					xpath = xpathForPlacementRowSiblings + "td/div/ul/li/a[contains(text(),'Report')]";
				}
			}
			logger.info(" : xpath: "+xpath + " for "+ whichXpath);

			webelement = driver.findElement(By.xpath(xpath));
		}
		catch(Exception e)
		{
			logger.error(" : Exception: ", e);
		}

		return webelement;
	}


	/** This method will return the xpath formed for elements for the searched sub-placement
	 * 
	 * @param driver
	 * @param placementName
	 * @param whichXpath
	 * @return
	 */
	public String getSearchedSubPlacementElements(WebDriver driver, String subplacementName, String whichXpath)
	{
		String xpath = "";
		try
		{
			subplacementName = subplacementName.toLowerCase();

			if(whichXpath.equalsIgnoreCase("subplacement"))
			{
				xpath = "//table[@class='display _dataTables table table-striped table-hover no-footer dataTable DTFC_Cloned']/tbody/tr/td/a[translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='"+ subplacementName +"']";

			}else if(whichXpath.equalsIgnoreCase("checkbox"))
			{
				xpath = "//table[@class='display _dataTables table table-striped table-hover no-footer dataTable DTFC_Cloned']/tbody/tr/td/a[translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='"+ subplacementName +"']/../preceding-sibling::td/input[@type='checkbox']";
			}
			else
			{
				/** Sample Xpath of using parent and sibling element 
				 * //tr[contains(@id,'row')]/..//td/a[@title='Mukesh_25march_video_max']/../following-sibling::td/div[@class='dropdown'] 
				 */

				//table[@class='display _dataTables table table-striped table-hover no-footer dataTable DTFC_Cloned']/tbody/tr/td/a[@title='Mukesh_VastFeed_31march']
				//tr[contains(@id,'row')]/..//td/a[translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='mukesh_maxvdo_29april']

				String xpathForSubPlacementRow = "//tr[contains(@id,'row')]/..//td/a[translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='"+ subplacementName +"']";				

				String xpathForSubPlacementRowSiblings = xpathForSubPlacementRow + "/../following-sibling::"; 

				if(whichXpath.equalsIgnoreCase("rowsetting"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/div[@class='dropdown']/a/span";
				}
				else if(whichXpath.equalsIgnoreCase("editsubplacement"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/div[@class='dropdown open']/ul/li/a/span[@class='icon-edit']";
				}
				else if(whichXpath.equalsIgnoreCase("clonesubplacement"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/div[@class='dropdown open']/ul/li/a[contains(text(),'Clone')]/span[@class='icon-picture']";
				}
				else if(whichXpath.equalsIgnoreCase("viewtags"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/div[@class='dropdown open']/ul/li/a[contains(text(),'View Tags')]/span[@class='icon-picture']";
				}
				else if(whichXpath.equalsIgnoreCase("pricingmodel"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/input[@class='inline-input'][contains(@name,'update_cpm')]";
				}
				else if(whichXpath.equalsIgnoreCase("budget"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/input[@class='inline-input'][contains(@name,'update_budget')]";
				}
				else if(whichXpath.equalsIgnoreCase("dailylimit"))
				{
					xpath = xpathForSubPlacementRowSiblings + "td/input[@class='inline-input'][contains(@name,'update_daily_limit')]";
				}
			}
			logger.info(" : xpath: "+xpath + " for "+ whichXpath);
		}
		catch(Exception e)
		{
			logger.error(" : Exception: ", e);
		}

		return xpath;
	}


	/** This method will return the xpath formed for elements for the searched connections
	 * 
	 * @param driver
	 * @param placementName
	 * @param whichXpath
	 * @return
	 */
	public WebElement getSearchedConnectionElements(WebDriver driver, String connectionName, String whichXpath)
	{
		WebElement webelement = null;
		try
		{
			String xpath = "";

			/** Sample Xpath of using parent element: 
			 * //tr[@id]/td[text()='test9']/..//span[@class='icon-cog'] 
			 */			
			connectionName = connectionName.toLowerCase();

			String xpathForConnectionRow = "//tr[@id]/td[translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='"+ connectionName +"']";

			if(whichXpath.equalsIgnoreCase("rowsetting"))
			{	
				xpath = xpathForConnectionRow + "/..//a[@data-toggle='dropdown']";
			}
			else if(whichXpath.equalsIgnoreCase("editconnection"))
			{
				xpath = xpathForConnectionRow + "/..//a[contains(text(),'Edit')]";
			}
			else if(whichXpath.equalsIgnoreCase("enableconnection"))
			{
				xpath = xpathForConnectionRow + "/..//a[contains(text(),'Enable')]";
			}
			else if(whichXpath.equalsIgnoreCase("disableconnection"))
			{
				xpath = xpathForConnectionRow + "/..//a[contains(text(),'Disable')]";
			}

			logger.info(" : xpath: "+xpath + " for "+ whichXpath);

			webelement = driver.findElement(By.xpath(xpath));
		}
		catch(Exception e)
		{
			logger.error(" : Exception: ", e);
		}

		return webelement;
	}


	/** This method will search the campaign with the supplied campaign name and click the check box located in front of campaign.
	 * 
	 * @param driver
	 * @param campaignName
	 * @return
	 */
	public WebElement getSearchedCampaignElement(WebDriver driver, String campaignName)
	{
		/**
		 * Sample xpath containing preceding-sibling: 
		 * //table[@class='display _dataTables table table-striped table-hover no-footer dataTable DTFC_Cloned']/tbody/tr/td[text()='vast http']/preceding-sibling::td/input 
		 */

		WebElement searchedCampaign = null;
		campaignName = campaignName.toLowerCase();

		try{
			String xpath = "//div[@class='DTFC_LeftBodyLiner']//td[translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='"+ campaignName +"']/..//input";
			searchedCampaign = driver.findElement(By.xpath(xpath));
		}
		catch(NullPointerException e)
		{
			logger.error(" : There was no check box found corresponding to campaign: "+campaignName);
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while searching checkbox corresponding to campaign: "+campaignName, e);
		}

		return searchedCampaign;
	}


	/**
	 * This method will get the element of desired ad in Campaign Creative
	 * Screen and click the respective checkbox and set date options.
	 * 
	 * @param driver
	 * @param adName
	 * @param option
	 * @return
	 */
	public By getSearchedAdElement(WebDriver driver, String adName, String option)
	{
		/** Sample xpath containing normalize-space / trim  
		 * //td[normalize-space(text())='ss']
		 * 
		 * Checkbox:  //td[normalize-space(text())='ss']/..//input[@type='checkbox'] 
		 * Set Date: //td[normalize-space(text())='ss']/input[@type='button']
		 */

		By searchedAd = null;
		adName = adName.toLowerCase();
		String xpath = "";

		try{
			String baseXpath = "//td[normalize-space(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))='"+ adName +"']";

			if(option.equalsIgnoreCase("searchedAd"))
			{
				xpath = baseXpath;
			}
			else if(option.equalsIgnoreCase("searchedAdCheckBox"))
			{
				xpath = baseXpath + "/..//input[@type='checkbox']";
			}
			else if(option.equalsIgnoreCase("searchedAdSetDate"))
			{
				xpath = baseXpath + "/input[@type='button']";
			}

			searchedAd = By.xpath(xpath);
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while searching option: "+option+ " corresponding to ad: "+adName + " using xpath: "+xpath, e);
		}

		return searchedAd;
	}


	/** This method will get the element of desired campaign in Create New Account screen. 
	 * 
	 * @param driver
	 * @param adName
	 * @param option
	 * @return
	 */
	public WebElement getSearchedCampaignForClient(WebDriver driver, String campaignName)
	{
		/** Sample xpath contains case insensitive text, examples:    
		 * //option[contains(translate(@itext,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mukesh_vast_26feb (active)')]
		 * //option[contains(translate(@itext,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mukesh_vast_26feb (')]
		 * //option[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mukesh_23dec_placement')]
		 * //option[translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='mukesh_23dec_placement']
		 */

		WebElement searchedCampaign = null;
		try{
			//Converting campaign name to lower case after trimming it.
			campaignName = campaignName.trim().toLowerCase();

			String baseXpath = "//option[contains(translate(@itext,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"+ campaignName +" (')]";
			searchedCampaign = driver.findElement(By.xpath(baseXpath));
		}
		catch(NullPointerException e)
		{
			logger.error(" : There was no campaign found: "+campaignName );
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while searching campaign: "+campaignName, e);
		}

		return searchedCampaign;
	}


	/** This method will get the element of desired searched client details in Client Details screen. 
	 * 
	 * @param driver
	 * @param adName
	 * @param option
	 * @return
	 */
	public WebElement getSearchedClientElements(WebDriver driver, String clientName, String whichXpath)
	{
		/** Sample xpath contains case insensitive text, examples:    
		 * //table[@id='clientsummarytable']//td[text()='yogesh kumar']/following-sibling::td//span[@class='icon-cog']
		 * //option[translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='mukesh_23dec_placement']
		 * //option[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'Rizvan Rizvan')]
		 */

		WebElement searchedClient = null;
		try{

			//Converting client name to lower case after trimming it.
			clientName = clientName.trim().toLowerCase();

			String baseXpath = "//table[@id='clientsummarytable']//td[translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='"+ clientName +"']";

			String xpath = "";

			if(whichXpath.equalsIgnoreCase("rowsetting"))
			{
				xpath = baseXpath + "/following-sibling::td//span[@class='icon-cog']";
			}
			else if(whichXpath.equalsIgnoreCase("edit"))
			{
				xpath = baseXpath + "/following-sibling::td//ul//a[contains(text(),'Edit')]";
			}
			else if(whichXpath.equalsIgnoreCase("delete"))
			{
				xpath = baseXpath + "/following-sibling::td//ul//a[contains(text(),'Delete')]";
			}
			else if(whichXpath.equalsIgnoreCase("generatetag"))
			{
				xpath = baseXpath + "/following-sibling::td//ul//a[contains(text(),'Generate')]";
			}
			else if(whichXpath.equalsIgnoreCase("assignplacement"))
			{
				xpath = baseXpath + "/following-sibling::td//ul//a[contains(text(),'Placement')]";
			}

			searchedClient = driver.findElement(By.xpath(xpath));
		}
		catch(NullPointerException e)
		{
			logger.error(" : There was no client found: "+clientName );
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while searching client: "+clientName, e);
		}

		return searchedClient;
	}


	/** Note# For all elements which needs to be created by this method, always use case in-sensitive xpath.
	 *  
	 * This method will be used to create the web element dynamically, will be
	 * used to find records in search functionality: first get the locator name
	 * and locator value from repository and then replace ~~ in locator value
	 * with the supplied data from test case and then create the web element.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 * @throws CustomException 
	 */
	public WebElement createDynamicWebElement(WebDriver driver, String objectName, String data, JSONObject jsonObjectRepo) throws CustomException
	{
		WebElement webElement = null;
		boolean throwException = false;
		String exceptionMessage = "";
		String locatorName = "";
		String locatorValue = "";
		By byLocator = null;

		try
		{
			//Proceed only supplied data is not empty
			if(data.isEmpty())
			{
				throwException = true;
				exceptionMessage = "Supplied data should not be empty.";
			}
			else
			{
				//Splitting the received string to get object locator and locator value
				String [] objectLocatorValue = new GetObjects().getObjectLocatorNameValueFromRepository(objectName, jsonObjectRepo).split("##");
				locatorName = objectLocatorValue[0];
				locatorValue = objectLocatorValue[1];

				/** Object locator value may contain ~~ which needs to be replaced by the data supplied from test cases sheet to create locator value 
				 * dynamically using the supplied data and then create element and then do further processing.
				 */
				if(locatorValue.contains("~~"))
				{
					data = data.toLowerCase().trim();
					locatorValue = locatorValue.replace("~~", data).trim();

					//Create a By Locator to be used in finding elements.
					byLocator = new GetObjects().createByLocator(objectName, locatorName, locatorValue);

					/** apply explicit wait of 45 sec. for every element to be present. */
					boolean flag = new HandlerLib().applyExplicitWait(driver, byLocator, new NoSuchElementException(""), 45);
					if(!flag)
					{
						logger.error("Timeout Exception While Applying Wait - Creating Dynamic Element. ");
					}
					webElement = driver.findElement(byLocator);
				}
				else
				{
					/**
					 * In case if the supplied object is not dynamic then throw custom exception
					 */
					throwException = true;
					exceptionMessage = "Object: "+objectName +" is not a dynamic element. ";
				}
			}
		}
		catch(NoSuchElementException n)
		{	
			logger.error(" : Element wasn't found having locator name = "+locatorName + " and value = "+locatorValue + "\n" +n.getMessage());
		}
		catch(CustomException e)
		{
			logger.error(e.getMessage());
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting webelement for object: "+objectName, e);
		}

		if(throwException)
		{
			throw new CustomException(exceptionMessage);
		}

		return webElement;
	}


	/** Note# For all elements which needs to be created by this method, always use case in-sensitive xpath.
	 *  
	 * This method will be used to create the web element dynamically, will be
	 * used to find records in search functionality: first get the locator name
	 * and locator value from repository and then replace ~~ in locator value
	 * with the supplied data from test case and then create the web element.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 * @throws CustomException 
	 */
	public By createDynamicByLocator(WebDriver driver, String objectName, String data, JSONObject jsonObjectRepo) throws CustomException
	{
		boolean throwException = false;
		String exceptionMessage = "";
		String locatorName = "";
		String locatorValue = "";
		By byLocator = null;

		try
		{
			//Proceed only supplied data is not empty
			if(data.isEmpty())
			{
				throwException = true;
				exceptionMessage = "Supplied data should not be empty.";
			}
			else
			{
				//Splitting the received string to get object locator and locator value
				String [] objectLocatorValue = new GetObjects().getObjectLocatorNameValueFromRepository(objectName, jsonObjectRepo).split("##");
				locatorName = objectLocatorValue[0];
				locatorValue = objectLocatorValue[1];

				/** Object locator value may contain ~~ which needs to be replaced by the data supplied from test cases sheet to create locator value 
				 * dynamically using the supplied data and then create element and then do further processing.
				 */
				if(locatorValue.contains("~~"))
				{
					data = data.toLowerCase().trim();
					locatorValue = locatorValue.replace("~~", data).trim();

					//Create a By Locator to be used in finding elements.
					byLocator = new GetObjects().createByLocator(objectName, locatorName, locatorValue);
				}
				else
				{
					/**
					 * In case if the supplied object is not dynamic then throw custom exception
					 */
					throwException = true;
					exceptionMessage = "Object: "+objectName +" is not a dynamic element. ";
				}
			}
		}
		catch(CustomException e)
		{
			logger.error(e.getMessage());
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting webelement for object: "+objectName, e);
		}

		if(throwException)
		{
			throw new CustomException(exceptionMessage);
		}

		return byLocator;
	}


	/** This method returns the result = "Not Executed" if a flag = "must pass" is supplied in input data for any of verify*** keywords.
	 * it will halt the execution of subsequent steps in case of failure of must pass step and sets subsequent step's result = "Not Executed"
	 *  
	 * 
	 * @param data
	 * @param result
	 * @throws CustomExceptionStopExecution
	 */
	public boolean haltTestExecution(String data, String result) throws CustomExceptionStopExecution
	{
		boolean haltExecution = false;
		try
		{
			/** First check if the result is a failure.
			 */
			if(result.toLowerCase().startsWith("fail"))
			{
				List<String> dataList = new ArrayList<String>();
				boolean flag = false;			
				String strMustPass = "";

				/** Secondly check if there is any must pass flag is supplied in input data separated by comma or semi colon,
				 * if yes, convert the supplied data into list and get the last item by default and check if this string contains must pass flag or not.  
				 */
				if(data.trim().equalsIgnoreCase("must pass")){
					flag = true;
					strMustPass = data.toLowerCase().trim();
				}
				else
				{
					if(data.contains(",")){
						flag = true;
						dataList = Arrays.asList(data.split(","));
						strMustPass = dataList.get(dataList.size() - 1).trim().toLowerCase();
					}
					else if(data.contains(";")){
						flag = true;
						dataList = Arrays.asList(data.split(";"));
						strMustPass = dataList.get(dataList.size() - 1).trim().toLowerCase();
					}
				}

				if(flag){
					/** if there is a "must pass" flag then set haltExecution = true
					 */
					if(strMustPass.equalsIgnoreCase("mustpass") || strMustPass.equalsIgnoreCase("1") || 
							strMustPass.startsWith("must") || strMustPass.contains("must pass"))
					{
						haltExecution = true;
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Error occurred while checking must pass condition. ", e);
		}

		return haltExecution;
	}


	/** This method will be used to click on supplied web element using Mouse Action.
	 * 
	 * @param driver
	 * @param webelement
	 * @return
	 */
	public boolean performMouseAction(WebDriver driver, WebElement webelement)
	{
		try{
			Actions action = new Actions(driver);
			action.moveToElement(webelement)
			.click()
			.build()
			.perform();

			return true;
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while performing mouse action. ", e);
			return false;
		}
	}


	/**
	 * This method will be used to replace the dynamic parameters from the
	 * supplied objects and data. Any dynamic input will be like 
	 * #time# -- current time stamp
	 * #TC_02_03# -- data used in step TC_02_03
	 * #random# -- random value having range 1-100
	 * #nonzero_random# -- alphanumeric random string excluding zero 
	 * 
	 * @param data
	 * @param testStepID_InputData
	 * @return
	 */
	public String dataParser(String data, String keyword, HashMap<String, String> testStepID_InputData, Connection connection)
	{
		try{

			Pattern pattern = Pattern.compile("#([a-zA-Z0-9_-]+)#");
			Matcher match = pattern.matcher(data);

			while(match.find())
			{	
				String matchString = match.group();

				String key = matchString.replace("#", "").toLowerCase().trim();
				String value = "";

				/** in case time stamp is passed */
				if(key.equalsIgnoreCase("time"))
				{
					value = MobileTestClass_Methods.DateTimeStamp("MMddyy_hhmmss");
				}
				/** random parameter #random# is passed */
				else if(key.equalsIgnoreCase("random"))
				{
					value = String.valueOf(IntegerLib.GetRandomNumber(100, 1));
				}
				/** random parameter #nonzero_random# is passed */
				else if(key.equalsIgnoreCase("nonzero_random"))
				{
					value = MobileTestClass_Methods.DateTimeStamp("MMM").concat("_"+MobileTestClass_Methods.DateTimeStamp("hhmmss").replace("0", "z"));
				}
				else
				{
					value = testStepID_InputData.get(key);

					if(value == null)
					{
						value = "";
						logger.debug(" : Received step id: "+key + " wasn't found in map, reassigning to space. ");
					}
				}
				data = data.replace(matchString, value);
			}

			/** now check if the received data is a sql query, if yes then execute it and then return the output as final data
			 * but don't execute query for verifydbdetails keyword, coz this keyword expects a query */
			if(data.toLowerCase().trim().startsWith("select") && data.contains("from") && !keyword.equalsIgnoreCase("verifydbdetails") )
			{
				String queryResult = MobileTestClass_Methods.ExecuteMySQLQueryReturns1DArray(connection, data)[0];

				if(queryResult == null){
					data = "";
					logger.error("Received null output of query: "+data + ", re-assigning it as space. ");
				}else{
					data = queryResult;
				}
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occurred while parsing supplied string: ", e);
		}
		return data;
	}


	/** This method will parse the supplied result and remove the must pass flag and return the result;
	 * 
	 * @param result
	 * @param haltFlag
	 * @return
	 */
	public String resultParser(String result, String haltFlag)
	{
		result = result.substring(0, result.indexOf(haltFlag));

		if(result.endsWith(",")){
			result = result.substring(0, result.indexOf(",")).trim();
		}
		else if(result.endsWith(";")){
			result = result.substring(0, result.indexOf(";")).trim();
		}

		return result;
	}


	/** This method will parse the expected data -- to be used in verifydb keyword, with this change, user can 
	 * supply query even for expected data also, this method will return the final expected data.
	 * 
	 * @param data
	 * @param connection
	 * @return
	 */
	public String dataParser(String data, Connection connection)
	{
		String [] records = null;
		try {
			records = MobileTestClass_Methods.ExecuteMySQLQueryReturns1DArray(connection, data);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		if(records != null)
		{
			return records[0];
		}
		else
		{
			return data;
		}
	}

	/** This method will type values slowly in the supplied element.
	 * 
	 * @param webelement
	 * @param data
	 * @return
	 */
	public boolean typeSlowly(WebElement webelement, String data)
	{
		boolean flag = true;
		try
		{
			for(int i=0;i<data.length();i++)
			{
				String strChannelName = data.substring(i, i+1);
				webelement.sendKeys(strChannelName);
				Thread.sleep(250);
			}
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception occurred while typing data: "+data, e);
		}
		return flag;
	}


	/** This method will return the video location to be uploaded based on supplied video file name
	 * 
	 * @param data
	 * @return
	 */
	public String getUploadVideoLocation(String data)
	{
		/**
		 * Get extension of supplied file
		 */
		String extension = data.substring(data.indexOf(".")+1, data.length()).toLowerCase().trim();

		String videoLocation = TestSuiteClass.AUTOMATION_HOME.toString().concat("\\tc_data\\sample_videos");

		switch (extension)
		{
		case "mp4":

			data = videoLocation.concat("\\mp4\\"+data);
			break;

		case "avi":

			data = videoLocation.concat("\\avi\\"+data);
			break;

		case "m4v":

			data = videoLocation.concat("\\m4v\\"+data);
			break;

		case "mov":

			data = videoLocation.concat("\\mov\\"+data);
			break;

		case "mpeg":

			data = videoLocation.concat("\\mpeg\\"+data);
			break;

		case "rv":

			data = videoLocation.concat("\\rv\\"+data);
			break;

		case "rm":

			data = videoLocation.concat("\\rm\\"+data);
			break;

		default:

			data = "";
			break;
		}


		if(!System.getProperty("os.name").matches("^Windows.*"))
		{
			data = data.replace("\\", "/");
		}

		logger.info(" : Video location is being returned = "+data);
		return data;

	}


	/** This method will return the image location to be uploaded based on supplied image file name
	 * 
	 * @param data
	 * @return
	 */
	public String getUploadImageLocation(String data)
	{
		/**
		 * Get extension of supplied file
		 */
		String extension = data.substring(data.indexOf(".")+1, data.length()).toLowerCase().trim();
		String imageLocation = TestSuiteClass.AUTOMATION_HOME.toString().concat("\\tc_data\\sample_images");

		switch (extension)
		{
		case "jpg":

			data = imageLocation.concat("\\jpg\\"+data);
			break;

		case "png":

			data = imageLocation.concat("\\png\\"+data);
			break;

		default:

			data = imageLocation.concat("\\jpg\\"+data);
			break;
		}

		if(!System.getProperty("os.name").matches("^Windows.*"))
		{
			data = data.replace("\\", "/");
		}

		logger.info(" : Image location is being returned = "+data);
		return data;

	}


	/** This method will wait for an alert to be present.
	 * 
	 * @param driver
	 * @return
	 */
	public boolean waitForAlert(WebDriver driver)
	{
		boolean flag;

		try
		{
			WebDriverWait wait = new WebDriverWait(driver, 10);		
			wait.until(ExpectedConditions.alertIsPresent());

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
		}

		return flag;
	}


	/** Getting data after removing any flag like "must pass"
	 * 
	 * @param data
	 * @return
	 */
	public String parseMustPassFlag(String data)
	{
		List<String> dataList = new ArrayList<String>();

		if(data.contains(";")){
			dataList = new ArrayList<>(Arrays.asList(data.split(";")));
		}
		else if(data.contains(",")){
			dataList = new ArrayList<>(Arrays.asList(data.split(",")));
		}

		/** Check the last item in the dataList */
		if(dataList.get(dataList.size()-1).trim().equalsIgnoreCase("must pass"))
		{
			int mustPassFlag = dataList.size()-1;
			dataList.remove(mustPassFlag);
		}

		return dataList.toString().replace("[", "").replace("]", "").trim();
	}


	/** This method parses the supplied data and checks if it has to return an element after using data or not.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public WebElement parseObject_GetWebElement(WebDriver driver, String objectName, String data, JSONObject jsonObjectRepo)
	{
		WebElement webelement = null;

		try
		{
			if(data.contains(";"))
			{
				data = data.split(";")[0].trim();
				webelement = new HandlerLib().createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
			}
			else if(data.contains(","))
			{
				data = data.split(",")[0].trim();
				webelement = new HandlerLib().createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
			}
			//in case supplied data is just a value to create element dynamically 
			else if(!data.equalsIgnoreCase("must pass") && !data.isEmpty())
			{
				webelement = new HandlerLib().createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
			}
			//in case supplied data is just a flag "must pass" then element definition will be taken from repository
			else if(data.equalsIgnoreCase("must pass"))
			{
				webelement = new GetObjects().getWebElementFromRepository(objectName, "name", driver, jsonObjectRepo);
			}
			//in case no data is supplied
			else
			{
				webelement = new GetObjects().getWebElementFromRepository(objectName, "name", driver, jsonObjectRepo);
			}
		}catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return webelement;
	}


	/**
	 * This keyword will verify the existence of supplied web element(s), multiple elements 
	 * can be supplied separated by comma(,) or semicolon(;).
	 * 
	 * @param objectName
	 * @param data
	 * @param driver
	 * @param webelement
	 * @param getObject
	 * @param handler
	 * @param applyRules
	 * @param passed_status
	 * @param failed_status
	 * @param locationToSaveSceenShot
	 * @return
	 */
	public String verifyElementPresent(String objectName, String data, WebDriver driver, WebElement webelement, GetObjects getObject, 
			HandlerLib handler, ApplyRules applyRules, String passed_status, String failed_status, String locationToSaveSceenShot, JSONObject jsonObjectRepo)
	{
		String result = "";
		List<String> notPresentObjectList = new ArrayList<String>();

		try{
			logger.info(" : Checking presence of supplied element: "+objectName);

			Thread.sleep(1000);

			boolean listFlag = false;
			List<String> suppliedObjectList = new ArrayList<String>();

			/** if comma separated objects are supplied then splitting them into a list. */
			if(objectName.contains(","))
			{
				listFlag = true;
				suppliedObjectList = new ArrayList<String>(Arrays.asList(objectName.split(",")));
			}
			else if(objectName.contains(";"))
			{
				listFlag = true;
				suppliedObjectList = new ArrayList<String>(Arrays.asList(objectName.split(";")));
			}
			else
			{
				listFlag = false;

				boolean iselementDisplayed = false;

				/** if data is supplied with or without comma / semi colon then convert the data into list and get the first string 
				 * as data input to create the dynamic element, multiple data can be supplied like: xyz ; must pass 
				 * then last value needs to be separated out, other than this data can't be supplied.
				 */
				webelement = handler.parseObject_GetWebElement(driver, objectName, data, jsonObjectRepo);

				logger.info(" : Element is: " + webelement);

				if(webelement == null)
				{
					iselementDisplayed = false;

					/** Taking screenshot during exception */
					CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(MobileTestClass_Methods.DateTimeStamp("MMdd_hhmmss") + ".png"));
					logger.info(" : Screenshot is captured at: "+locationToSaveSceenShot);
				}
				else
				{
					//Checking if element is displayed
					Thread.sleep(1000);
					try{
						iselementDisplayed = webelement.isDisplayed();
					}catch(StaleElementReferenceException e){
						webelement = handler.parseObject_GetWebElement(driver, objectName, data, jsonObjectRepo);
						iselementDisplayed = webelement.isDisplayed();
					}
				}

				if(iselementDisplayed)
				{
					result = passed_status + "Element is present. ";
				}
				else
				{
					webelement = handler.parseObject_GetWebElement(driver, objectName, data, jsonObjectRepo);
					iselementDisplayed = webelement.isDisplayed();
					if(iselementDisplayed)
					{
						result = passed_status + "Element is present. ";
					}
					else
					{
						result = failed_status + "Element is not present. ";
						CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(MobileTestClass_Methods.DateTimeStamp("MMdd_hhmmss") + ".png"));
					}
				}

			}

			/** Iterating list and collecting not present objects into notPresentObjectList list, in case of InvocationTargetException exception
			 * also, adding object into  notPresentObjectList list
			 */
			if(listFlag)
			{
				for(int i=0; i<suppliedObjectList.size(); i++)
				{
					/** Catching InvocationTargetException exception in case webelement is not found on webpage.
					 */
					try{
						objectName = suppliedObjectList.get(i);
						webelement = getObject.getWebElementFromRepository(objectName, "name", driver, jsonObjectRepo);

						/** Applying rules on certain elements before checking if element is displayed
						 */
						applyRules.applyRule(driver, objectName, webelement, data, jsonObjectRepo);

						if(!webelement.isDisplayed())
						{
							notPresentObjectList.add(suppliedObjectList.get(i));
						}
					}catch(NullPointerException e)
					{		
						notPresentObjectList.add(suppliedObjectList.get(i));
						logger.info(suppliedObjectList.get(i) + " wasn't found on web page. ");

						/** Taking screenshot during exception */
						CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(MobileTestClass_Methods.DateTimeStamp("MMdd_hhmmss") + ".png"));
						logger.info(" : Screenshot is captured at: "+locationToSaveSceenShot);
					}
				}

				/** checking if notPresentObjectList's size, if this is empty then its pass 
				 * else fail it.
				 */
				if(notPresentObjectList.toString().replace("[", "").replace("]", "").trim().isEmpty())
				{
					result = passed_status + "All supplied elements were present. ";
				}
				else
				{
					/** writing not present objects in results. */
					result = failed_status + "Element(s): "+notPresentObjectList +" was(were) not present. ";
				}
			}
		}
		catch(Exception e)
		{
			if(e instanceof CustomException)
			{
				result = e.getMessage();
			}
			else
			{
				result = failed_status + "Couldn't check the presence of element. ";
				logger.error(" : Exception occurred while checking the presence of element. ", e);

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(MobileTestClass_Methods.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(" : Screenshot is captured at: "+locationToSaveSceenShot);
			}
		}
		//logger.info(" : Current url is : "+driver.getCurrentUrl());
		return result;

	}	


	public String selectCheckbox(WebDriver driver, String objectName, WebElement webelement, String data, String locationToSaveSceenShot, 
			String passed_status, String failed_status, JSONObject jsonObjectRepo)
	{
		String result; 

		By byLocator = null;

		/** apply rule for web element: Create_Sub_placement_channel_selectcheckBox */
		new ApplyRules().applyRule(driver, objectName, webelement, data, jsonObjectRepo);

		try{
			/** create dynamic element */
			byLocator = new HandlerLib().createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
		}catch(CustomException e)
		{
			/** create element normally */
			try {
				byLocator = new GetObjects().getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			} catch (CustomException e1) { logger.error(e.getMessage()); }
		}

		try{
			/** apply explicit wait to handle exceptions */
			new HandlerLib().applyExplicitWait(driver, byLocator, new WebDriverException(""));

			webelement = driver.findElement(byLocator);
			if(!webelement.isSelected()){
				webelement.click();
			}

			logger.info(" : Clicked checkbox is: " +webelement);
			result = passed_status+ "Clicked checkbox successfully";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't click checkbox";
			logger.error(" : Exception occurred while clicking checkbox : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(MobileTestClass_Methods.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** adding a logic to parse the data - for example if a test step has to be executed based on certain test condition 
		then use expression like: expression ([Connection_Type]=VAST/VPAID/XYZ?DEAL12435) -->
		in this case first the macro [Connection_Type] will be replaced and then its value will be compared with VAST and if matched then 
		use DEAL12345 for that step else skipThisTest.
	 * @param data
	 * @return
	 */
	public String parseTestDataExpression (String data)
	{
		data = data.replace("expression", "").replace("(", "").replace(")", "").trim();

		String defaultValue = "";
		try{defaultValue = data.split("\\?")[1].trim();}
		catch(ArrayIndexOutOfBoundsException a){}

		/** get the whole condition */
		String condition = data.split("\\?")[0].trim();

		/** further split condition by && and then evaluate this */
		String [] conditions = condition.split("&&");

		boolean expressionFlag;
		for(String expression : conditions)
		{
			expressionFlag = getExpressionFlag(expression);

			if(!expressionFlag)
			{
				data = "skipTestStep";
				break;
			}else{
				data = defaultValue;
			}
		}

		return data;
	}


	/** This method parses the test data expression flag and return the true or false
	 * 
	 * @param condition
	 * @return
	 */
	public boolean getExpressionFlag(String condition)
	{
		boolean flag = false;

		String actualValue = condition.split("=")[0].trim();
		String expectedValue = condition.split("=")[1].trim();		

		/** in case multiple values are given as a list of expected values */
		List<String> expectedValuesList = new ArrayList<>();

		String [] expectedValueArray = expectedValue.split("/");

		for(int i=0; i<expectedValueArray.length; i++)
		{
			expectedValuesList.add(expectedValueArray[i].trim().toLowerCase());
		}

		if(expectedValuesList.contains(actualValue.toLowerCase()))
		{
			flag = true;
		}

		return flag;
	}
}
