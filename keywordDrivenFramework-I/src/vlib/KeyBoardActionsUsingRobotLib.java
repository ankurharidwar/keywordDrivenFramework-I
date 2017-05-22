/**
 * Last Changes Done on 26 Feb, 2015 11:45:15 AM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */

package vlib;

import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_3;
import static java.awt.event.KeyEvent.VK_4;
import static java.awt.event.KeyEvent.VK_5;
import static java.awt.event.KeyEvent.VK_6;
import static java.awt.event.KeyEvent.VK_7;
import static java.awt.event.KeyEvent.VK_8;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_AMPERSAND;
import static java.awt.event.KeyEvent.VK_ASTERISK;
import static java.awt.event.KeyEvent.VK_AT;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_BACK_QUOTE;
import static java.awt.event.KeyEvent.VK_BACK_SLASH;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CIRCUMFLEX;
import static java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
import static java.awt.event.KeyEvent.VK_COLON;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOLLAR;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_EXCLAMATION_MARK;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_GREATER;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_K;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS;
import static java.awt.event.KeyEvent.VK_LESS;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_NUMBER_SIGN;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_OPEN_BRACKET;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_PERIOD;
import static java.awt.event.KeyEvent.VK_PLUS;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_QUOTE;
import static java.awt.event.KeyEvent.VK_QUOTEDBL;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SEMICOLON;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_SLASH;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_U;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;



public class KeyBoardActionsUsingRobotLib
{

	static Logger logger = Logger.getLogger(KeyBoardActionsUsingRobotLib.class.getName());

	public static Robot rt = null;

	/** This constructor is being used to initialize the robot class instance.
	 * 
	 * @param rt
	 */
	public KeyBoardActionsUsingRobotLib(Robot rt)
	{
		KeyBoardActionsUsingRobotLib.rt= rt;
		rt.setAutoDelay(500);
	}


	/** This method sets the clip board and paste in upload file dialog.
	 * 
	 * Before calling this method, initialize the constructor in before test or atleast two or three line before you
	 * actual call this method.
	 * The basic purpose of keeping an explicit constructor is:
	 * whenever Robot class is called an external jar is opened and the focus goes outside of browser therefore 
	 * pasting the location in upload dialog is difficult. 
	 * 
	 * @param clipBoard
	 * @param driver
	 */
	public static void ChooseFileToUpload(String clipBoard, WebDriver driver) 
	{	
		StringSelection stringSelection;

		try
		{
			rt.setAutoDelay(1500);

			if(driver.toString().matches("^Chrome.*"))
			{
				if(System.getProperty("os.name").matches("^Windows.*"))
				{
					String clipBoardForWindows = clipBoard.replace("/", "\\");

					logger.info(" : File: "+clipBoardForWindows +" is being selected on: " +System.getProperty("os.name") + " and Browser: " +driver.toString());

					stringSelection = new StringSelection(clipBoardForWindows);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

					Thread.sleep(250);

					rt.keyPress(KeyEvent.VK_CONTROL);
					rt.keyPress(KeyEvent.VK_V);

					Thread.sleep(250);

					//Paste the file path
					rt.keyPress(KeyEvent.VK_CONTROL);
					rt.keyRelease(KeyEvent.VK_V);

					Thread.sleep(250);
				}
				else
				{
					logger.info(" : File: "+clipBoard +" is being selected on: " +System.getProperty("os.name") + " and Browser: " +driver.toString());

					Thread.sleep(250);

					//First Typing / to open the dialog box
					rt.keyPress(KeyEvent.VK_SLASH);
					rt.keyRelease(KeyEvent.VK_SLASH);

					Thread.sleep(250);

					//Now setting up the clip board after removing / from the full file path
					String strSubClipBoard = clipBoard.substring(1, clipBoard.length());

					stringSelection = new StringSelection(strSubClipBoard);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

					Thread.sleep(250);

					//Paste the file path using COMMAND + V
					rt.keyPress(KeyEvent.VK_META);
					rt.keyPress(KeyEvent.VK_V);

					Thread.sleep(250);

					rt.keyRelease(KeyEvent.VK_META);
					rt.keyRelease(KeyEvent.VK_V);

					Thread.sleep(250);

					rt.keyPress(KeyEvent.VK_ENTER);
					rt.keyRelease(KeyEvent.VK_ENTER);

					Thread.sleep(250);
				}

				rt.keyPress(KeyEvent.VK_ENTER);
				rt.keyRelease(KeyEvent.VK_ENTER);
			}
			else if(driver.toString().matches("^Firefox.*"))
			{
				if(System.getProperty("os.name").matches("^Windows.*"))
				{
					String clipBoardForWindows = clipBoard.replace("/", "\\");

					logger.info(" : File: "+clipBoardForWindows +" is being selected on: " +System.getProperty("os.name") + " and Browser: " +driver.toString());

					ChooseFileToUploadUsingKeyBoard(clipBoardForWindows);
				}
				else
				{
					logger.info(" : File: "+clipBoard +" is being selected on: " +System.getProperty("os.name") + " and Browser: " +driver.toString());
					ChooseFileToUploadUsingKeyBoard(clipBoard);
				}
			}
			Thread.sleep(250);	 
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while uploading file. ", e);
		}

	}


	/**
	 * This method will be used to scroll down the page.
	 */
	public static void PageScrollDown() 
	{
		try
		{
			rt = new Robot();
			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				rt.keyPress(KeyEvent.VK_CONTROL);
				rt.keyPress(KeyEvent.VK_END);
				logger.info(" : Scroll Down To The Bottom Of The Web Page in " + System.getProperty("os.name").toString());
				Thread.sleep(1000);

				rt.keyRelease(KeyEvent.VK_CONTROL);
				rt.keyRelease(KeyEvent.VK_END);
				Thread.sleep(1000);
			}
			else
			{
				rt.keyPress(KeyEvent.VK_META);
				rt.keyPress(KeyEvent.VK_DOWN);
				logger.info(" : Scroll Down To The Bottom Of The Web Page in " + System.getProperty("os.name").toString());
				Thread.sleep(1000);

				rt.keyRelease(KeyEvent.VK_META);
				rt.keyRelease(KeyEvent.VK_DOWN);
				Thread.sleep(1000);
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured while scrolling page. ", e);
		}
	}


	/** This method will be used to move page to right
	 * 
	 */
	public static void PageMoveRight()
	{
		try
		{
			rt = new Robot();
			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				for(int i = 0;i<5;i++)
				{
					rt.keyPress(KeyEvent.VK_RIGHT);
					Thread.sleep(1000);
					rt.keyRelease(KeyEvent.VK_RIGHT);
				}
				Thread.sleep(1000);
				logger.info(" : Move Right to the Web Page in " + System.getProperty("os.name").toString());
			}
			else
			{
				for(int i = 0;i<5;i++)
				{
					rt.keyPress(KeyEvent.VK_RIGHT);
					Thread.sleep(1000);
					rt.keyRelease(KeyEvent.VK_RIGHT);
				}
				Thread.sleep(1000);
				logger.info(" : Move Right to the Web Page in " + System.getProperty("os.name").toString());
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured. ", e);
		}
	}


	/** This method will be used to minimize all the windows. Doesn't work well on mac. */
	public static void MinimizeAllWindows() 
	{
		try{
			rt = new Robot();
			rt.setAutoDelay(1000);

			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				logger.info(" : Minimize All Windows On Your Desktop: " + System.getProperty("os.name").toString());

				rt.keyPress(KeyEvent.VK_WINDOWS);
				rt.keyPress(KeyEvent.VK_M);

				Thread.sleep(500);

				rt.keyRelease(KeyEvent.VK_WINDOWS);
				rt.keyRelease(KeyEvent.VK_M);
				Thread.sleep(500);
			}
			else
			{
				logger.info(" : Minimize All Windows On Your Desktop: "  + System.getProperty("os.name").toString());

				for(int i=0; i<2; i++)
				{
					rt.keyPress(KeyEvent.VK_META);
					rt.keyPress(KeyEvent.VK_H);

					Thread.sleep(500);

					rt.keyRelease(KeyEvent.VK_META);
					rt.keyRelease(KeyEvent.VK_H);
				}
				Thread.sleep(1000);
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured. ", e);
		}
	}


	/** This method will be used to perform -- Select All + Copy Selected Text
	 * 
	 * @return
	 */
	public static String SelectAllAndCopyText() 
	{
		String data = "";
		try{
			rt = new Robot();
			rt.setAutoDelay(1000);

			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				rt.keyPress(KeyEvent.VK_CONTROL);
				rt.keyPress(KeyEvent.VK_A);

				Thread.sleep(100);

				rt.keyRelease(KeyEvent.VK_CONTROL);
				rt.keyRelease(KeyEvent.VK_A);

				Thread.sleep(100);

				rt.keyPress(KeyEvent.VK_CONTROL);
				rt.keyPress(KeyEvent.VK_C);

				logger.info(" : Selected Text is copied on desktop: " + System.getProperty("os.name").toString());

				Thread.sleep(100);

				rt.keyRelease(KeyEvent.VK_CONTROL);
				rt.keyRelease(KeyEvent.VK_C);

				Thread.sleep(100);
			}
			else
			{
				rt.keyPress(KeyEvent.VK_META);
				rt.keyPress(KeyEvent.VK_A);

				Thread.sleep(100);

				rt.keyRelease(KeyEvent.VK_META);
				rt.keyRelease(KeyEvent.VK_A);

				Thread.sleep(100);

				rt.keyPress(KeyEvent.VK_META);
				rt.keyPress(KeyEvent.VK_C);

				logger.info(" : Selected Text is copied on desktop: " + System.getProperty("os.name").toString());

				Thread.sleep(100);

				rt.keyRelease(KeyEvent.VK_META);
				rt.keyRelease(KeyEvent.VK_C);

				Thread.sleep(100);
			}

			data = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();

		}
		catch(Exception e)
		{
			logger.error(" : Exception occured. ", e);
		}
		return data;
	}


	/** This method will type the path of file to be uploaded
	 * 
	 * @param filePath
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	public static void ChooseFileToUploadUsingKeyBoard(String filePath)
	{
		try
		{
			//Robot rt = new Robot();
			rt.setAutoDelay(1000);

			if(System.getProperty("os.name").matches("^Windows.*"))
			{
				filePath = filePath.replace("/", "\\");
				logger.info(" : File: " +filePath + " is being browsed on os: " +System.getProperty("os.name"));
			}
			else
			{
				Thread.sleep(150);

				//First Typing / to open the dialog box
				rt.keyPress(KeyEvent.VK_SLASH);
				rt.keyRelease(KeyEvent.VK_SLASH);

				Thread.sleep(250);

				filePath = filePath.substring(1, filePath.length());	
				logger.info(" : File: " +filePath + " is being browsed on os: " +System.getProperty("os.name"));
			}

			logger.info(" : Each character from the supplied file path is being typed here using Keyboard...");

			type(filePath);
			Thread.sleep(250);

			rt.keyPress(KeyEvent.VK_ENTER);
			rt.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(250);

			rt.keyPress(KeyEvent.VK_ENTER);
			rt.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(250);
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured. ", e);
		}
	} 


	/** This method is used to click in the center of the screen using robot class. This method is being used in 
	 * portal to bring the browser in focus.  
	 * 
	 * @param characters
	 */
	public static boolean MouseClick()
	{
		boolean flag = false;
		try{
			logger.info(" : External Mouse Click Using Robot Class.");
			rt = new Robot();

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

			//Move mouse to the center of screen and click 
			rt.mouseMove(screensize.width/2, screensize.height/2);

			rt.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			rt.delay(500);
			rt.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

			flag = true; 
		}catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception occured: ", e);
		}
		return flag;
	}


	public static void type(CharSequence characters)
	{
		try{
			int length = characters.length();
			for (int i = 0; i < length; i++) {
				char character = characters.charAt(i);
				type(character);
			}
		}
		catch(Exception e)
		{
			logger.error(" : Exception occured: ", e);
		}
	}


	public static void type(char character) throws AWTException
	{
		Robot rt = new Robot();
		switch (character) {
		case 'a': doType(VK_A); break;
		case 'b': doType(VK_B); break;
		case 'c': doType(VK_C); break;
		case 'd': doType(VK_D); break;
		case 'e': doType(VK_E); break;
		case 'f': doType(VK_F); break;
		case 'g': doType(VK_G); break;
		case 'h': doType(VK_H); break;
		case 'i': doType(VK_I); break;
		case 'j': doType(VK_J); break;
		case 'k': doType(VK_K); break;
		case 'l': doType(VK_L); break;
		case 'm': doType(VK_M); break;
		case 'n': doType(VK_N); break;
		case 'o': doType(VK_O); break;
		case 'p': doType(VK_P); break;
		case 'q': doType(VK_Q); break;
		case 'r': doType(VK_R); break;
		case 's': doType(VK_S); break;
		case 't': doType(VK_T); break;
		case 'u': doType(VK_U); break;
		case 'v': doType(VK_V); break;
		case 'w': doType(VK_W); break;
		case 'x': doType(VK_X); break;
		case 'y': doType(VK_Y); break;
		case 'z': doType(VK_Z); break;
		case 'A': doType(VK_SHIFT, VK_A); break;
		case 'B': doType(VK_SHIFT, VK_B); break;
		case 'C': doType(VK_SHIFT, VK_C); break;
		case 'D': doType(VK_SHIFT, VK_D); break;
		case 'E': doType(VK_SHIFT, VK_E); break;
		case 'F': doType(VK_SHIFT, VK_F); break;
		case 'G': doType(VK_SHIFT, VK_G); break;
		case 'H': doType(VK_SHIFT, VK_H); break;
		case 'I': doType(VK_SHIFT, VK_I); break;
		case 'J': doType(VK_SHIFT, VK_J); break;
		case 'K': doType(VK_SHIFT, VK_K); break;
		case 'L': doType(VK_SHIFT, VK_L); break;
		case 'M': doType(VK_SHIFT, VK_M); break;
		case 'N': doType(VK_SHIFT, VK_N); break;
		case 'O': doType(VK_SHIFT, VK_O); break;
		case 'P': doType(VK_SHIFT, VK_P); break;
		case 'Q': doType(VK_SHIFT, VK_Q); break;
		case 'R': doType(VK_SHIFT, VK_R); break;
		case 'S': doType(VK_SHIFT, VK_S); break;
		case 'T': doType(VK_SHIFT, VK_T); break;
		case 'U': doType(VK_SHIFT, VK_U); break;
		case 'V': doType(VK_SHIFT, VK_V); break;
		case 'W': doType(VK_SHIFT, VK_W); break;
		case 'X': doType(VK_SHIFT, VK_X); break;
		case 'Y': doType(VK_SHIFT, VK_Y); break;
		case 'Z': doType(VK_SHIFT, VK_Z); break;
		case '`': doType(VK_BACK_QUOTE); break;
		case '0': doType(VK_0); break;
		case '1': doType(VK_1); break;
		case '2': doType(VK_2); break;
		case '3': doType(VK_3); break;
		case '4': doType(VK_4); break;
		case '5': doType(VK_5); break;
		case '6': doType(VK_6); break;
		case '7': doType(VK_7); break;
		case '8': doType(VK_8); break;
		case '9': doType(VK_9); break;
		case '-': doType(VK_MINUS); break;
		case '=': doType(VK_EQUALS); break;
		case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
		case '!': doType(VK_EXCLAMATION_MARK); break;
		case '@': doType(VK_AT); break;
		case '#': doType(VK_NUMBER_SIGN); break;
		case '$': doType(VK_DOLLAR); break;
		case '%': doType(VK_SHIFT, VK_5); break;
		case '^': doType(VK_CIRCUMFLEX); break;
		case '&': doType(VK_AMPERSAND); break;
		case '*': doType(VK_ASTERISK); break;
		case '(': doType(VK_LEFT_PARENTHESIS); break;
		case ')': doType(VK_RIGHT_PARENTHESIS); break;
		case '_':
			rt.keyPress(KeyEvent.VK_SHIFT);
			rt.keyPress(KeyEvent.VK_MINUS);
			rt.keyRelease(KeyEvent.VK_SHIFT); 
			break;
		case '+': doType(VK_PLUS); break;
		case '\t': doType(VK_TAB); break;
		case '\n': doType(VK_ENTER); break;
		case '[': doType(VK_OPEN_BRACKET); break;
		case ']': doType(VK_CLOSE_BRACKET); break;
		case '\\': doType(VK_BACK_SLASH); break;
		case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
		case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
		case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
		case ';': doType(VK_SEMICOLON); break;
		case ':': doType(VK_COLON); break;
		case '\'': doType(VK_QUOTE); break;
		case '"': doType(VK_QUOTEDBL); break;
		case ',': doType(VK_COMMA); break;
		case '<': doType(VK_LESS); break;
		case '.': doType(VK_PERIOD); break;
		case '>': doType(VK_GREATER); break;
		case '/': doType(VK_SLASH); break;
		case '?': doType(VK_SHIFT, VK_SLASH); break;
		case ' ': doType(VK_SPACE); break;
		default:
			throw new IllegalArgumentException("Cannot type character " + character);
		}
	}


	private static void doType(int... keyCodes) throws AWTException 
	{
		doType(keyCodes, 0, keyCodes.length);
	}


	private static void doType(int[] keyCodes, int offset, int length) throws AWTException 
	{
		Robot robot = new Robot();
		if (length == 0) {
			return;
		}
		robot.keyPress(keyCodes[offset]);
		doType(keyCodes, offset + 1, length - 1);
		robot.keyRelease(keyCodes[offset]);
	}

}