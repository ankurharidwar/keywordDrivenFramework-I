/**
 * Last Changes Done on 24 Feb, 2015 11:19:44 AM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: This class contains the code to pick the desired date from calendar
 */
package projects.portal;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;


public class DateParser 
{

	Logger logger = Logger.getLogger(DateParser.class.getName());


	/** This method will be used to convert the supplied date format to dd/MM/yy
	 * 
	 * @param suppliedDate
	 * @param suppliedDateFormat
	 * @return
	 * @throws ParseException
	 */
	public String formatDate(String suppliedDate, String suppliedDateFormat) throws ParseException
	{
		/** String dMMMMyy = "February 4, 2015" == "MMMM dd, yyyy"
		 */

		String convertedDate = null;

		try
		{
			SimpleDateFormat suppliedFormat = new SimpleDateFormat(suppliedDateFormat);
			Date date = suppliedFormat.parse(suppliedDate);

			SimpleDateFormat convertedFormat = new SimpleDateFormat("dd/MM/yy");
			convertedDate = convertedFormat.format(date);
			logger.debug(" : Converted Date: "+convertedDate);
		}
		catch(Exception e)
		{
			logger.error(" : Couldn't convert the supplied date: "+suppliedDate);
		}
		return convertedDate;
	}


	/** This is a generic method to parse the given string and return the desired date value.
	 * 
	 * @param strDate
	 * @param strDesiredValue
	 * @param returnMonthName
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getDateValues(String strDate, String strDesiredValue, boolean returnMonthName)
	{
		String desiredValue = "";
		try
		{
			/** Sample Date Format:
			 * dd/MM/yy = 29/03/15
			 * MMMM dd, yyyy = February 4, 2015
			 */

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

			Date date = format.parse(strDate);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			if(strDesiredValue.equalsIgnoreCase("month"))
			{
				if(returnMonthName)
				{
					desiredValue = calendar.getDisplayName(calendar.MONTH, calendar.SHORT, Locale.getDefault());
				}
				else
				{
					desiredValue = String.valueOf(calendar.get(Calendar.MONTH));
				}
			}
			else if(strDesiredValue.equalsIgnoreCase("year"))
			{
				desiredValue =  String.valueOf(calendar.get(Calendar.YEAR));
			}
			else if(strDesiredValue.equalsIgnoreCase("date"))
			{
				desiredValue = String.valueOf(calendar.get(Calendar.DATE));
			}

			logger.info(strDesiredValue+" value: "+desiredValue);
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while parsing supplied date: "+strDate, e);
		}

		return desiredValue;
	}
}
