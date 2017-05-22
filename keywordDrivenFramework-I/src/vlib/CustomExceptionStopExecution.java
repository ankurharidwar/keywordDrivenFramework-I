/**
 * Last Changes Done on 5 Mar, 2015 12:07:43 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;

//This class defines the custom exception, which may be utilized in some other classes.


@SuppressWarnings("serial")
public class CustomExceptionStopExecution extends Exception 
{
	public CustomExceptionStopExecution()
	{
		super();
	}
	public CustomExceptionStopExecution(String message) 
	{
		super(message); 
	}
	public CustomExceptionStopExecution(String message, Throwable cause)
	{ 
		super(message, cause); 
	}
	public CustomExceptionStopExecution(Throwable cause)
	{ 
		super(cause); 
	}
}