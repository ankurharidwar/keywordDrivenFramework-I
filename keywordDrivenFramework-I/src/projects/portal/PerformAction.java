/**
 * Last Changes Done on Jan 16, 2015 12:04:40 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */

package projects.portal;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.mysql.jdbc.Connection;


public class PerformAction {


	Logger logger = Logger.getLogger(PerformAction.class.getName());


	/** This method will call the method corresponding to supplied keyword dynamically and perform action.
	 * 
	 * @param keyword 
	 * @param object 
	 * @param data 
	 * @return 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String performAction(WebDriver driver, String keyword, String object, String data, Connection connection, JSONObject jsonObjectRepo)
	{
		String result = "";
		try
		{
			/** adding a logic to parse the data - for example if a test step has to be executed based on certain test condition 
			 * then use expression like: expression ([Connection_Type]=VAST/VPAID/XYZ?DEAL12435) -->
			 * in this case first the macro [Connection_Type] will be replaced and then its value will be compared with VAST and if matched then 
			 * use DEAL12345 for that step else skipThisTest. */
			if(data.trim().startsWith("expression"))
			{
				data = new HandlerLib().parseTestDataExpression(data);
			}

			/** added a new check for Concurrent Framework --> if the supplied data is skipTestStep then don't execute this step. */
			if(data.trim().equalsIgnoreCase("skipTestStep") || data.trim().toLowerCase().contains("skipteststep"))
			{
				result = "Skip: This Test Step Wasn't Required To Be Excecuted. ";
			}
			else
			{
				Class keywordClass = Class.forName(Keywords.class.getName());

				Method keywordMethod = null;
				boolean flag = true;

				//converting supplied keywords to lower case making it case insensitive
				keyword = keyword.toLowerCase();
				try
				{
					keywordMethod = keywordClass.getMethod(keyword, WebDriver.class, String.class, String.class);
				}
				catch(NoSuchMethodException n)
				{
					flag = false;
					logger.error(" : This keyword = "+keyword + " is not supported", n);
				}

				if(flag)
				{
					logger.debug( " : " + keywordMethod.getName() + " is being executed for keyword: "+ keyword);

					//Creating object of class Keywords class.
					Keywords keywordObj = new Keywords(connection, jsonObjectRepo);

					/** Get keywords results and check if there is any execution needs to be halted   
					 */
					result = (String) keywordMethod.invoke(keywordObj, driver, object, data);
				}

				//Do not perform in case of wrong/unsupported keyword
				else
				{
					result = "Fail: This keyword = "+keyword + " is not supported, please check.";
				}	
			}
		}
		catch(Exception e)
		{
			result = "Fail: Error occurred while performing action.";
			logger.error(" : Exception occurred while trying to perform action for keyword: "+keyword, e);
		}
		return result;
	}


}