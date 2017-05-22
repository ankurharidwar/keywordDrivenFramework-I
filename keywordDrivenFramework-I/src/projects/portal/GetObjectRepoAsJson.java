package projects.portal;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import jxl.Sheet;
import jxl.Workbook;


public class GetObjectRepoAsJson {

	Logger logger = Logger.getLogger(GetObjectRepoAsJson.class.getName());

	public static void main(String[] args) 
	{
		new GetObjectRepoAsJson().getObjectRepoAsJson("/Users/Pankaj/Desktop/qaAutomation/qascripting/Vdopia_Automation/object_repository/portalObjectRepository/transformerPortal_ObjectRepository.xls");
	}


	/** This method will convert the received excel sheet in json object, we need to find the primary key first and then this will handle everything.  
	 * Sample: 
	 * 
	 * 
	 * @param filename
	 * @return
	 */
	public JSONObject getObjectRepoAsJson(String filename)
	{
		JSONObject jsonObj = new JSONObject();	

		try
		{
			Workbook book = Workbook.getWorkbook(new File(filename));
			Sheet sheet = book.getSheet(0);

			int objectName_column = sheet.findCell("objectName".trim(), 0, 0, sheet.getColumns(), 0, false).getColumn();

			for(int row =1; row<sheet.getRows(); row++)
			{
				String objectName = sheet.getCell(objectName_column, row).getContents().trim();

				JSONObject json = new JSONObject();
				for(int column=0; column<sheet.getColumns(); column++)
				{
					/** don't add the primary key again in json object */
					if(column != objectName_column)
					{
						String key = sheet.getCell(column, 0).getContents().trim();
						String value = sheet.getCell(column, row).getContents().trim();

						/** put values in json only if there is no empty values. */
						if(!key.isEmpty())
						{
							json.put(key, value);
						}
					}
				}

				jsonObj.put(objectName, json);
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}		

		return jsonObj;
	}


	/** This method will parse the supplied json object and retrieve the objectName corresponding to supplied object label. 
	 * 
	 * @param objectRepo
	 * @param objectLabel
	 * @return
	 * @throws JSONException
	 */
	public String getObjectName(JSONObject objectRepo, String objectLabel)
	{
		String objectName = "";

		try{
			Iterator<?> objectNames = objectRepo.keys();

			while(objectNames.hasNext())
			{
				String key = (String) objectNames.next();

				if(objectRepo.getJSONObject(key).getString("objectLabel").equalsIgnoreCase(objectLabel))
				{
					objectName = key;

					break;
				}
			}
		}catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return objectName;
	}

	
	public void writeExcelFromJosn()
	{
		
	}
}
