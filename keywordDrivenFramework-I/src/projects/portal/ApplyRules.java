/**
 * Last Changes Done on 8 May, 2015 1:17:44 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: This class will be used to handle special condition based on specific element.
 */

package projects.portal;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


import vlib.CustomException;


public class ApplyRules 
{

	Logger logger = Logger.getLogger(ApplyRules.class.getName());

	HandlerLib elementHandler = new HandlerLib();
	GetObjects getObject = new GetObjects();


	/** This method will be used to handle conditions for specific web elements.  
	 * 
	 * @param driver
	 * @param objectName
	 * @param webelement
	 * @return
	 */
	public boolean applyRule(WebDriver driver, String objectName, WebElement webelement, String data, JSONObject jsonObjectRepo)
	{
		boolean flag;

		try
		{
			objectName = objectName.trim();

			/**1. Scrolling to left to see column labels in Market Place Screen.
			 * 
			 */
			if(objectName.equalsIgnoreCase("MarketplaceConnections_DisplayedColumn_ConnectionName_Label") || 
					objectName.equalsIgnoreCase("MarketplaceConnections_DisplayedColumn_Status_Label") ||
					objectName.equalsIgnoreCase("MarketplaceConnections_DisplayedColumn_RowSetting_Icon"))
			{

				/**
				 * Executing java script to scroll to left to see the supplied elements
				 */
				String javaScript = "document.getElementsByClassName('icon-cog')[5].scrollIntoView();";
				elementHandler.executeJavaScript(driver, javaScript, null);
				flag = true;
			}


			/**2. Handling the clicks of specific objects
			 */
			else if(objectName.equalsIgnoreCase("Create_Sub_placement_VastFeed_Save&Continue_button")
					|| objectName.equalsIgnoreCase("Create_Sub_placement_Submit_button")
					|| objectName.equalsIgnoreCase("Create_Sub_placement_Video_Save_button")
					|| objectName.equalsIgnoreCase("SubPlacements_CreateNewSubPlacement_Button")
					|| objectName.equalsIgnoreCase("Vadmin_Button_Signin")
					|| objectName.equalsIgnoreCase("MarketplaceConnections_NewConnection_Button")
					|| objectName.equalsIgnoreCase("Publisher_SearchedEmail_Link") 
					|| objectName.equalsIgnoreCase("viewapplications_setting_link")
					|| objectName.equalsIgnoreCase("ManagePackagesandDeals_ManageDeals_SearchedDeal_RowSetting_Label"))
			{
				elementHandler.executeJavaScript(driver, "arguments[0].click()", webelement);
				flag = true;
			}

			/**3. Handling the clicks of specific objects - By Action Object 
			 * 
			 */
			else if( objectName.equalsIgnoreCase("Tools_Menu_Link"))
			{
				Thread.sleep(1000);
				Actions action = new Actions(driver);
				action.moveToElement(webelement)
				.click()
				.build()
				.perform();
				Thread.sleep(1000);

				flag = true;

			}

			/**4. Handling the clicks of specific objects - By Action Object 
			 */
			else if(objectName.equalsIgnoreCase("Create_Sub_placement_Save&Continue_button")
					|| objectName.equalsIgnoreCase("Create_Sub_placement_Submit&Continue_button")
					||  objectName.equalsIgnoreCase("Create_Sub_placement_Preroll_Save&Continue_button"))
			{
				/** Bring the save and continue button in focus.
				 */
				Thread.sleep(1000);
				String javaScript = "arguments[0].scrollIntoView(false);";
				elementHandler.executeJavaScript(driver, javaScript, null);

				/**
				 * Click button using mouse
				 */
				Thread.sleep(250);
				elementHandler.performMouseAction(driver, webelement);
				flag = true;
			}


			/**3. Applying explicit wait  
			 */
			else if(objectName.equalsIgnoreCase("Create_Sub_placement_channel_selectcheckBox")
					|| objectName.equalsIgnoreCase("ConfigureAdUnits_NativeInview_Mopub_Button"))
			{
				By bylocator = null;

				try{
					bylocator = new HandlerLib().createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
				}catch(CustomException c){
					bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
				}

				elementHandler.applyExplicitWait(driver, bylocator, new NoSuchElementException(""));
				flag = true;
			}
			else
			{
				flag = false;
			}
		}
		catch(WebDriverException w)
		{
			flag = false;
			logger.error(" : Error occurred while applying rule for object: "+objectName + " Error is: "+w.getMessage());
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception occured while applying rules. ", e);
		}
		return flag;
	}



}
