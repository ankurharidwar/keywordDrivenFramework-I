package projects.portal;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");//dd/MM/yyyy
	    Date date = new Date();
	    String strDate = sdfDate.format(date);
		System.out.println(strDate);
	}





}
