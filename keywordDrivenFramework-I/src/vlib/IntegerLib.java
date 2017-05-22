/**
 * Last Changes Done on 5 Mar, 2015 12:07:43 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;

import java.util.Random;

public class IntegerLib 
{	
	
	public static void main(String []args)
	{
		System.out.println(GetRandomNumber(1, 1));
	}
	
	
	
	public static int GetRandomNumber(int upperLimit, int lowerLimit)
	{
		Random random = new Random();
		int range = upperLimit - lowerLimit;
		
		int randomNumber = random.nextInt(range) + lowerLimit; 
				
				//(int )(Math.random() * (upperLimit-lowerLimit + 1));
		return randomNumber;
	}
	
	public static String GetRandomNumberByLength(long len) {
		if (len > 18)
		{
			throw new IllegalStateException("To many digits");
		}

		long upperLen = (long) Math.pow(10, len - 1) * 9;
		long lowerLen = (long) Math.pow(10, len - 1) * 1;

		long number = (long) (Math.random() * upperLen) + lowerLen;

		String tVal = number + "";
		if (tVal.length() != len) {
			throw new IllegalStateException("The random number '" + tVal + "' is not '" + len + "' digits");
		}
		return tVal;
	}
}
