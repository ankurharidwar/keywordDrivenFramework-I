/**
 * Last Changes Done on Feb 3, 2015 2:41:00 PM
 * Last Changes Done by ${author}
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;


import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import projects.portal.HandlerLib;



public class CaptureScreenShotLib 
{

	static Logger logger = Logger.getLogger(CaptureScreenShotLib.class.getName());

	static Robot robot;


	/** Constructor is being used to initialize the robot instance, because whenever Robot object is created,   
	 * the test browser specially chrome goes into background and thus looses focus. Therefore before using the
	 * method: captureScreenShot(String locationToSaveSceenShot), call this constructor: CaptureScreenShotLib(Robot robot)
	 * in annotation @beforeTest in the actual test before setting up the browser.   
	 * 
	 * @param robot
	 */
	public CaptureScreenShotLib(Robot robot)
	{
		CaptureScreenShotLib.robot = robot;
	}


	/** This method will be used to take the browser screenshot using selenium. In the event of exception, whole screen
	 * is captured.
	 * 
	 * @param driver
	 * @param locationToSaveSceenShot
	 */
	public static void captureScreenShot(WebDriver driver, String locationToSaveSceenShot)
	{		
		try
		{
			String directoryForScreenShot = StringLib.splitDirectoryFromFileLocation(locationToSaveSceenShot);

			if(FileLib.CreateDirectory(directoryForScreenShot))
			{
				((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);				
				FileUtils.copyFile(scrFile, new File(locationToSaveSceenShot));

				logger.info(" : Screenshot captured and saved at: " +locationToSaveSceenShot);
			}		
		} 
		catch (Exception e) 
		{
			new HandlerLib().checkIfAlertPresent(driver);
			logger.error(" : Exception occured while capturing browser screenshot, now capturing whole system screen.");
			
			captureScreenShot(locationToSaveSceenShot);
		}
	}



	/** This method will be used to take the system screen screenshot using java.robot.*
	 * 
	 * @param driver
	 * @param locationToSaveSceenShot
	 */
	public static void captureScreenShot(String locationToSaveSceenShot)
	{		
		try
		{
			String directoryForScreenShot = StringLib.splitDirectoryFromFileLocation(locationToSaveSceenShot);

			if(FileLib.CreateDirectory(directoryForScreenShot))
			{
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Rectangle screenRectangle = new Rectangle(screenSize);

				BufferedImage image = robot.createScreenCapture(screenRectangle);
				ImageIO.write(image, "png", new File(locationToSaveSceenShot));

				logger.info(" : Screenshot captured and saved at: " +locationToSaveSceenShot);
			}		
		} 
		catch (Exception e) 
		{
			logger.error(" : Exception occured while capturing screenshot. ");
		}
	}

}
