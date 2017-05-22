/**
 * Last Changes Done on Feb 2, 2015 3:07:02 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import org.apache.log4j.Logger;


public class StringLib 
{

	static Logger logger = Logger.getLogger(StringLib.class.getName());

	public static String[] StrSplit(String MainString, String Limiter)
	{

		//String targetingDetail = "A_4.0,A_4.1,A_5.0,I_3.0";
		//String targetingDetail = "A_4.0";
		//String Limiter = ",";


		if (Strexist(MainString, Limiter))
		{
			String[] OSlists;
			//logger.info(" : Sub String exist");
			OSlists = MainString.split(Limiter);
			//logger.info(" : splits.size: " + OSlists.length);
			//for(String OS: OSlists)
			//{
			//Uncomment for debug 
			//logger.info(OS);
			//}
			return OSlists;
		}
		else
		{
			logger.info(" : Received String: " + MainString);
			//String[] OSlist = new String[1];
			String[] OSlist = { MainString };
			//OSlist[] = targetingDetail.split(Limiter).toString();
			logger.info(" : Value Returned: " + OSlist[0]);
			return OSlist;
		}


	}


	public static boolean Strexist(String Str, String subStr)
	{
		boolean c = Str.contains(subStr);
		return c;	
	}


	//************ This Method Will BUILD STRING CONTENT FOR Adult Content Targeting  ***********
	public static String BuildStringForAdultContentTargeting(String serveURL)
	{
		//Replacing html with js
		serveURL = serveURL.replace("output=html", "output=js");

		String str1 = "<html>";
		String str2 = "<script language='javascript' src='"+ serveURL +"'></script>";
		String str3 = "</html>";

		String finalHtmlString = str1 + str2 + str3;

		return finalHtmlString;
	}


	//************ This Method Will BUILD STRING CONTENT FOR Keyword Targeting  ***********
	public static String BuildStringForKeywordTargeting(String serveURL,String content)
	{
		serveURL = serveURL.replace("output=html;", "");

		String line1 = "<html>";
		String line2 = "<head>";
		String line3 = "<meta name=\\\"author\\\" content=\\\"" + content + "\\\">";
		String line4 = "<meta charset=\\\"UTF-8\\\">";
		String line5 = "</head>";
		String line6 = "<body>";
		String line7 = "<h1>KEYWORD TARGETING</h1>";
		String line8 = "<p>Trying to serv Test url: <a href = '" + serveURL + "'>" + serveURL + "</a></p>";
		String line9 = "<p>Keywords are: " + content + "</p>";
		String line10 = "<script language='javascript' src='" + serveURL + "'></script><noscript></body>";
		String line11 = "</html>";


		String finalHtmlString = line1 + line2 + line3 + line4 + line5 + line6 + line7 + line8 + line9 + line10 + line11;

		return finalHtmlString;
	}


	public static String BuildStringForInViewAdFormat(String apiKey, String strActionType,String channelID)
	{
		MobileTestClass_Methods.InitializeConfiguration();
		String finalHtmlString = "";
		String str = "";
		String strBaseTestURL = MobileTestClass_Methods.propertyConfigFile.getProperty("mobileBaseTestURL").toString();

		for(int i =0;i<100;i++)
		{
			str = str + "My first paragraph.<br>" + "\n";
		}

		String initialUrl = strBaseTestURL.split("html5")[0];
		String url1 = initialUrl + "tracker.php?m=ti;ci=3708;ai=11010;chid=" + channelID + ";ou=rd;rand=[timestamp]";
		String url2 = strBaseTestURL + "responds_to_scroll=1;target_div_id=rand_tar_id_1408448099;sleepAfter=0;adFormat=inview;ak=" + apiKey + ";version=1.0;showClose=1;cb=[timestamp]";
		String url3 = initialUrl + "tracker.php?m=nji;ci=3708;ai=11011;chid=" + channelID + ";ou=rd;rand=[timestamp]";

		String channelTag = "<img id=\\\"rand_tar_id_1408448099\\\" src=\\\"" + url1 + "\\\" style=\\\"height:1px;width:1px;position:" +
				"absolute;visibility:hidden;\\\" /><script language='javascript' src='" + url2
				+ "'></script><noscript><img src=\\\"" + url3 + "\\\" style=\\\"height:1px;width:1px;position:absolute;visibility:hidden;\\\" /></noscript>";


		finalHtmlString = "<html>" + "\n" + "<body>" + "\n" + str + "<div>" + "\n" + channelTag + "\n</div>\n</body>\n</html>";
		return finalHtmlString;
	}


	//************ This Method Will BUILD STRING CONTENT FOR V4 PLAYER  ***********
	public static String BulidStringForV4PlayerForOnlineTestFile(String apiKey)
	{
		MobileTestClass_Methods.InitializeConfiguration();

		String enter = System.getProperty("line.separator");

		String str1 = "<!DOCTYPE html PUBLIC \"" +"-//W3C" +"//DTD " +"XHTML 1.0 Transitional//EN\" " +"\"http:" +"//www.w3.org" +"/TR/xhtml1" +"/DTD/xhtml1-transitional.dtd\">";
		String str2 = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
		String str3 = "<title>vpaid sample player</title><script language=\"javascript\" type=\"text/javascript\" src=\"";

		//Get Serve JS url from config
		String serveURL = MobileTestClass_Methods.propertyConfigFile.getProperty("serveJsURLForOnlineAdServing").toString();	//"http://serve.qa.vdopia.com/js/vdo.js";	//"http://cdni.vdopia.com/js/vdo.js";

		String str4 = "\"></script><script language=\"javascript\" type=\"text/javascript\">";
		String str5 = "function loadPlayer(_streamURL,_videoImage,_autoplay){Vdopia({\"api_key\":";
		String strApiKey = "\"" + apiKey + "\"";
		String str6 = ",\"videoFileURL\":\"vdopiavod://0|\"+_streamURL,\"api_test\":\"false\",\"player_width\":\"700\",\"player_height\":\"400\",\"videoImageURL\":_videoImage,";
		String str7 = "\"userParams\":\"networkError=\"+escape(\"Sorry! We are experiencing technical difficulties. Please stay tuned, we will be back shortly.\"),";
		String str8 = "\"autoplay\":_autoplay,\"AdMsg\":\"Time [ADV_TIME]\"},\"player\", \"v4\");}</script></head><body><div id=\"player\"><script language=\"javascript\">";
		String str9 = "loadPlayer(\"" ;
		String playerURL = "http://qa.vdopia.com/qa/QAAutomation/samplevideo/video.flv" ;
		String str10 = "\",\"";
		String imageURL = "http://qa.vdopia.com/qa/QAAutomation/sampleimage/sampleimage.jpg";
		String str11 = "\",\"true\");</script></div></body></html>";

		String strContent = str1 + enter + str2 + enter + str3 + serveURL + str4 + enter + str5 + enter + strApiKey + enter 
				+str6 + enter + str7 +  enter + str8 + enter + str9 + playerURL + str10 +imageURL + str11;

		return strContent;
	}


	//************ This Method Will BUILD STRING CONTENT FOR SWC PLAYER  ***********	
	public static String BulidStringForSWCPlayerForOnlineTestFile(String apiKey)
	{
		MobileTestClass_Methods.InitializeConfiguration();

		String enter = System.getProperty("line.separator");

		String str1 = "<!DOCTYPE html PUBLIC \"" +"-//W3C" +"//DTD " +"XHTML 1.0 Transitional//EN\" " +"\"http:" +"//www.w3.org" +"/TR/xhtml1" +"/DTD/xhtml1-transitional.dtd\">";
		String str2 = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
		String str3 = "<title>vpaid sample player</title><script language=\"javascript\" type=\"text/javascript\" src=\"";

		//Get Serve JS url from config
		String serveURL = MobileTestClass_Methods.propertyConfigFile.getProperty("serveJsURLForOnlineAdServing").toString();	//"http://serve.qa.vdopia.com/js/vdo.js";	//"http://cdni.vdopia.com/js/vdo.js";

		String str4 = "\"></script><script language=\"javascript\" type=\"text/javascript\">";
		String str5 = "function loadPlayer(_streamURL,_videoImage,_autoplay){Vdopia({\"api_key\":";
		String strApiKey = "\"" + apiKey + "\"";
		String str6 = ",\"videoFileURL\":\"vdopiavod://0|\"+_streamURL,\"api_test\":\"false\",\"player_width\":\"700\",\"player_height\":\"400\",\"videoImageURL\":_videoImage,";
		String str7 = "\"userParams\":\"networkError=\"+escape(\"Sorry! We are experiencing technical difficulties. Please stay tuned, we will be back shortly.\"),";

		//Change Player V4 to SWC
		String str8 = "\"autoplay\":_autoplay,\"AdMsg\":\"Time [ADV_TIME]\"},\"player\", \"swc\");}</script></head><body><div id=\"player\"><script language=\"javascript\">";
		String str9 = "loadPlayer(\"" ;

		String playerURL = "http://qa.vdopia.com/qa/QAAutomation/samplevideo/video.flv" ;

		String str10 = "\",\"";

		String imageURL = "http://qa.vdopia.com/qa/QAAutomation/sampleimage/sampleimage.jpg";

		String str11 = "\",\"true\");</script></div></body></html>";

		String strContent = str1 + enter + str2 + enter + str3 + serveURL + str4 + enter + str5 + enter + strApiKey + enter 
				+str6 + enter + str7 +  enter + str8 + enter + str9 + playerURL + str10 +imageURL + str11;

		return strContent;	
	}


	//************ This Method Will BUILD STRING CONTENT FOR PREWIDGET PLAYER  ***********
	public static String BulidStringForPrewidgetPlayerForOnlineTestFile(String apiKey)
	{
		MobileTestClass_Methods.InitializeConfiguration();

		String str1 = 		"<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><head><meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=UTF-8\\\" /><title>Sample</title>";
		String str2 = 		"<script language=\\\"javascript\\\" type=\\\"text/javascript\\\" src=\\\"http://cdn.sb.vdopia.com/js/VDOPIA_preWidget_API_1_2.js\\\"></script>";
		String str3 = 		"</head><body><h1>Sample code</h1><div id =\\\"myVDOplayer\\\"></div><script language=\\\"javascript\\\">VDOPIAPreWidget({api_key:";
		String strApiKey = 	"'"+ apiKey +"'"; 
		String str4 = 		", api_test:false, width:450, height:300, playerDiv:\\\"myVDOplayer\\\",callback:\\\"executeApp\\\",tout:10000,server:\\\"";

		//Get Serve url from config
		String serveURL = MobileTestClass_Methods.propertyConfigFile.getProperty("serveURLForOnlineAdServing_Prewidget").toString();	//"serve.qa.vdopia.com";

		String cdnURL = 	"\\\",cdn:\\\"http://cdn.sb.vdopia.com";
		String str5 = 		"\\\"});function executeApp(id,status)" +
				" {var str = '<iframe width=\\\"450\\\" height=\\\"300\\\"" +
				" src=\\\"http://www.youtube.com/embed/uWC8N1p11SA?rel=0\\&autoplay=1\\\" " +
				"frameborder=\\\"0\\\" allowfullscreen></iframe>';";
		String str6 =		"var d = document.getElementById(id);d.innerHTML = str;}</script></body></html>";

		String strContent = str1 + str2 + str3 + strApiKey + str4 + serveURL + cdnURL + str5 + str6; 

		return strContent;
	}


	//************ This Method Will BUILD STRING CONTENT FOR VAST2VDO AD  ***********
	public static String BulidStringForVast2VdoForOnlineTestFile(String apiKey)
	{
		MobileTestClass_Methods.InitializeConfiguration();

		//Get Serve url from config
		String serveURL = MobileTestClass_Methods.propertyConfigFile.getProperty("serveURLForOnlineAdServing_Prewidget").toString();	//"serve.qa.vdopia.com";

		String str1 = 	"<html><body><div id=\\\"Vast2VDO\\\" align=\\\"center\\\"><script language=\\\"javascript\\\" type=\\\"text/javascript\\\" ";
		String str2 = 	"src=\\\"http://"+ serveURL +"/adserver/vast2vdo/js/"+ apiKey +"/size:300x250;" ;
		String str3 = 	"api_test:false;autoplay:true;autoplayAfter:0;playIcon:;category:AU|TR|SC|SP|CR|MU|HC|PA|NE|NA|BU|HF|SH|LF|PO|FI|EN|GA|SN|PH|PR|BO|UT|ED|ME|RE|WE|WO|MN;mute:false;";
		String str4 = 	"contentAutoPlay:true;contentMute:false;callback:adCallback\\\"></script>";
		String str5 = 	"<script language=\\\"javascript\\\" type=\\\"text/javascript\\\">";
		String str6 =	"function adCallback() {";
		String str7 = 	"  this.success = function(msg) {";
		String str8 = 	"    switch(msg) {";
		String str9 = 	"      case 'Ad.Start':";
		String str10 = 	"      break;";
		String str11 =	"      case 'Ad.Complete':";
		String str12 = 	"      break;} }";
		String str13 = 	"  this.error = function(msg) {}}";
		String str14 = 	"</script></div></body></html>";

		String strContent = str1 + "\n" + str2 + "\n" + str3 + "\n" + str4 + "\n" + str5 + "\n" + str6 + "\n" + str7 + "\n" + str8 + "\n" + str9 + "\n" + str10;
		strContent = strContent + "\n" + str11 + "\n" + str12 + "\n" + str13 + "\n" + str14;

		return strContent;
	}


	//************ This Method Will Split The File Name From The Given HTTP URL ***********
	public static String splitFileNameFromURL(String url)
	{	
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		return fileName;
	}


	//************ This Method Will Split The File Name From The Given DIRECTORY LOCATION ***********
	public static String splitDirectoryFromFileLocation(String fileNameWithLocation)
	{
		String directory = "";
		if(System.getProperty("os.name").matches("^Windows.*"))
		{
			fileNameWithLocation = fileNameWithLocation.replace("/", "\\");
			directory = fileNameWithLocation.substring(0, fileNameWithLocation.lastIndexOf("\\"));
		}
		else
		{
			directory = fileNameWithLocation.substring(0, fileNameWithLocation.lastIndexOf("/"));
		}

		return directory;
	}


	//*********** This method will be used to get URL out of generated tag. ******************
	public static String GetURLFromChannelTag(String channelTag)
	{
		//String t = "<img src=\"http://serve.qa.vdopia.com/adserver/tracker.php?m=ti;ci=3708;ai=11010;chid=111400;ou=rd;rand=[timestamp]\" style=\"height:1px;width:1px;position:absolute;visibility:hidden;\" /><script language='javascript' src='http://serve.qa.vdopia.com/adserver/html5/inwapads/?sleepAfter=0;adFormat=banner;ak=960e42b72b82ea66b00d2cfea8a16795;version=1.0;cb=[timestamp]'></script><noscript><img src=\"http://serve.qa.vdopia.com/adserver/tracker.php?m=nji;ci=3708;ai=11011;chid=111400;ou=rd;rand=[timestamp]\" style=\"height:1px;width:1px;position:absolute;visibility:hidden;\" /></noscript>";

		String testURL = null;
		logger.info(" : Received Channel Tag: "+channelTag);

		List<String> list = Arrays.asList(channelTag.split(" "));

		for(int i=0; i<list.size(); i++)
		{
			if(list.get(i).contains("inwapads"))
			{
				List<String> list01 = Arrays.asList(list.get(i).split("'"));

				for(int j=0; j<list01.size(); j++)
				{
					if(list01.get(j).contains("inwapads"))
					{
						testURL = list01.get(j);
						logger.info(" : Found Test URL: " +list01.get(j));
					}
				}
			}
		}

		return testURL;
	}


	//*****************************************************************************************
	// Method to get the current Method name
	//*****************************************************************************************
	public static String trace(StackTraceElement e[]) 
	{
		boolean doNext = false;
		String methodName = "";
		for (StackTraceElement s : e) 
		{
			if (doNext) 
			{
				methodName = s.getMethodName(); 
				break;
			}
			doNext = s.getMethodName().equals("getStackTrace");
		}
		return methodName;
	}


	//This method will return the unique parameter (di parameter) from Test URL
	@SuppressWarnings("finally")
	public static String GetUniqueParamFromURL(String testURL) 
	{
		String diParam = null;
		try
		{
			List<String> di = Arrays.asList(testURL.split(";"));

			for(int i=0; i<di.size(); i++)
			{
				if(di.get(i).startsWith("di="))
				{
					diParam = Arrays.asList(di.get(i).split("=")).get(1);
					logger.info(" : di parameter in test url is: "+diParam);
				}
			}
		}
		catch(Exception e)
		{	
			diParam = null;
			logger.info(" : Exception handled by method: GetUniqueParamFromURL. "+e.getMessage());
		}
		finally
		{
			return diParam;
		} 
	}


	public static String compareLists_Generic(List <String> SuperSet, List <String> Subset)
	{

		boolean loopresult = false;
		String loopresultString = "";

		String compareListsResult = "";


		for(int i=0; i<Subset.size(); i++)
		{
			loopresult = false;
			for(int j=0; j<SuperSet.size(); j++)
			{

				if(loopresult == false)
				{
					if(SuperSet.get(j).equalsIgnoreCase(Subset.get(i)))
					{

						loopresult = true;
						logger.info(" : PASS : " + Subset.get(i) + " exist");
						loopresultString = loopresultString + "PASS : " +  Subset.get(i) + " exist \n";
					}	
				}


			}
			if(loopresult == false)
			{
				logger.info(" : FAIL : " + Subset.get(i) + " does not exist");
				loopresultString = loopresultString + "FAIL : " +  Subset.get(i) + " does not exist\n";
			}

		}

		if(loopresultString.contains("FAIL :"))
		{
			compareListsResult = "false," + loopresultString;
		}
		else
		{
			compareListsResult = "true," + loopresultString;
		}

		return compareListsResult;

	}



	public static String compareLists_impression(List <String> SuperSet, List <String> Subset)
	{

		boolean loopresult = false;
		String loopresultString = "";

		String compareListsResult = "";


		for(int i=0; i<Subset.size(); i++)
		{
			loopresult = false;
			for(int j=0; j<SuperSet.size(); j++)
			{

				if(loopresult == false)
				{
					if(SuperSet.get(j).equalsIgnoreCase(Subset.get(i)))
					{

						loopresult = true;
						logger.info(" : PASS : Impression URL : " + Subset.get(i) + " exist in Adserver XML.");
						loopresultString = loopresultString + "PASS : Impression URL : " +  Subset.get(i) + " exist in Adserver XML.\n";
					}	
				}


			}
			if(loopresult == false)
			{
				logger.info(" : FAIL : Impression URL : " + Subset.get(i) + " does not exist in Adserver XML.");
				loopresultString = loopresultString + "FAIL : Impression URL : " +  Subset.get(i) + " does not exist in Adserver XML.\n";
			}

		}

		if(loopresultString.contains("FAIL :"))
		{
			compareListsResult = "false," + loopresultString;
		}
		else
		{
			compareListsResult = "true," + loopresultString;
		}

		return compareListsResult;

	}



	public static String compareLists_trackers(TreeMap<String, ArrayList<String>> SuperSetMap, TreeMap<String, ArrayList<String>> SubsetMap)
	{

		for (Entry<String, ArrayList<String>>  map : SuperSetMap.entrySet())
		{	
			logger.info(" : SuperSetMap : Key: "+map.getKey() + " Value: "+ map.getValue());
		}


		for (Entry<String, ArrayList<String>>  map : SubsetMap.entrySet())
		{	
			logger.info(" : SubSetMap : Key: "+map.getKey() + " Value: "+ map.getValue());
		}


		ArrayList <String > Mainresult = new ArrayList<String>();
		String MainresultString = "";

		for( String SubsetKey : SubsetMap.keySet())
		{
			boolean SubsetKetExist = false;
			for( String SuperSetKey : SuperSetMap.keySet())
			{
				if(SubsetKetExist == false)
				{
					if(SubsetKey.equalsIgnoreCase(SuperSetKey))
					{
						SubsetKetExist = true;
						logger.info(" : Key : " + SubsetKey + " exist in main hashmap.");

						logger.info(SuperSetMap.get(SubsetKey) + ", "+  SubsetMap.get(SuperSetKey));


						Mainresult.add(compareLists_Generic(SuperSetMap.get(SubsetKey), SubsetMap.get(SuperSetKey)).toString());
					}
				}


			}
			if( SubsetKetExist == false)
			{
				logger.info(" : Key : " + SubsetKey + " does not exist in main hashmap");
				Mainresult.add("false, "  + SubsetKey + " does not exist in adserve vast xml");

			}

		}


		String MainResultstatus = "true";
		for(String resultRow : Mainresult)
		{
			List<String> KeyResultList = Arrays.asList(resultRow.split(",", 2));
			if(KeyResultList.get(0) == "false")
			{
				MainResultstatus = "false";

			}
			MainresultString = MainresultString + KeyResultList.get(1);

		}

		MainresultString = MainResultstatus + "," + MainresultString;


		return MainresultString;

	}


	//Compare Two Lists and Return the unmatched data from Child List
	public static String CompareTwoList_GetUnMatched(List<String> serveList, List<String> expectedList)
	{
		boolean flag = false;

		String unmatchedData = "";
		String finalString = "";

		for(int i=0; i<expectedList.size(); i++)
		{
			for(int j=0; j<serveList.size(); j++)
			{

				//Exit if match found
				if(serveList.get(j).contains(expectedList.get(i)))
				{
					flag = false;

					break;
				}
				else
				{
					//Set a flag in case of unmatch, to be set after matching one item with the whole second list   
					flag = true;
					unmatchedData = expectedList.get(i);

					//logger.info(" : Not Found Item: "+unmatchedData);
				}
			}

			//Appending unmatched strings
			if(flag)
			{
				finalString = finalString + unmatchedData + ", ";
			}
		}

		return finalString;

	}


	public static String[][] MergeTwoArrayWithRow(String[][] recordOutput_WithRon,String[][] recordOutput_WithoutRon)
	{
		String[][] recordOutput = new String[recordOutput_WithRon.length + recordOutput_WithoutRon.length -1][recordOutput_WithRon[0].length];
		int x=0;
		try
		{
			for(int i=0; i<recordOutput_WithRon.length; i++,x++)		// For Every Row
			{
				for(int j=0; j<recordOutput_WithRon[0].length; j++)	// For Every Column
				{
					recordOutput[i][j] = recordOutput_WithRon[i][j];
				}
			}
			for(int i=1; i<recordOutput_WithoutRon.length; i++,x++)		// For Every Row
			{
				for(int j=0; j<recordOutput_WithoutRon[0].length; j++)	// For Every Column
				{
					recordOutput[x][j] = recordOutput_WithoutRon[i][j];
				}
			}
		}
		catch(Exception e)
		{
			logger.info(" : Exception occurred: " + e.getMessage());
		} 
		return recordOutput;
	}


	public static String[][] MergeTwoArrayWithColumn(String[][] recordOutputFirst,String[][] recordOutputSecond)
	{
		String[][] recordOutput = new String[recordOutputFirst.length][];
		try
		{
			for (int index = 0; index < recordOutput.length; index++) 
			{
				recordOutput[index] = join(recordOutputFirst[index], recordOutputSecond[index]);
			}
		}
		catch(Exception e)
		{
			logger.info(" : Exception occurred: " + e.getMessage());
		}
		return recordOutput;
	}


	public static String[] join(String[] array1, String[] array2) {
		String[] array1and2 = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}


	//This method will return the unmatched values of list1 after comparing with list2.
	@SuppressWarnings("finally")
	public static List<String> getUnmatchedValuesFromTwoLists(List<String> list1, List<String> list2) 
	{
		List<String> unMatchedValues = new ArrayList<String>();

		try
		{

			//Checking if the there is any unexpected format is found in server.
			for(int i=0; i<list1.size(); i++)
			{
				String str1 = list1.get(i).trim();
				boolean b = false;

				for(int j=0; j<list2.size(); j++)
				{
					String str2 = list2.get(j).trim().trim();

					//logger.info(" : For index : " + j + ": value is :" + str2 + ":");

					if(str1.equalsIgnoreCase(str2))
					{
						//logger.info(" : Found item: "+list1.get(i));
						break;
					}
					else
					{
						//Checking is list1.get(i) has been compared to till last element of array
						if(j==list2.size()-1)
						{
							b = true;
						}
					}
				}

				//logger.info(" : Loop2 is over. ");

				if(b)
				{
					//logger.info(" : Adding in unmatched list: "+str1);

					//Collecting result result after comparing the whole list with array
					unMatchedValues.add(str1);
				}
			}
		}
		catch(Exception e)
		{
			logger.info(" : Exception occurred while comparing two lists. "+e);
		}
		finally
		{
			return unMatchedValues;
		}
	}


	/** Copy values of one hashmap to another hashmap, it will replace those keys in source which has 
	 * no value or space in value
	 * 
	 * @param sourceMap
	 * @param destinationMap
	 * @return
	 */
	public static HashMap<String, String> copyOneDistinctHashmapToAnother(HashMap<String, String> sourceMap, HashMap<String, String> destinationMap)
	{
		/** if source key doesn't exist in destination map or key exists will null value,  then add else not.
		 */
		for(Entry<String, String> sourceEntry: sourceMap.entrySet())
		{
			if(destinationMap.get(sourceEntry.getKey()) == null || destinationMap.get(sourceEntry.getKey()).isEmpty())
			{
				destinationMap.put(sourceEntry.getKey(), sourceEntry.getValue());
			}			
		}

		return destinationMap;
	}


	/** This method will return the printable string from the supplied hashmap in a format like:
	 * key: value, key: value etc..
	 * 
	 * @param queryParam
	 * @return
	 */
	public static String getStringFromMap(HashMap<String, String> queryParam)
	{
		String strQueryParam = ""; 

		for(Entry<String, String> entry : queryParam.entrySet())
		{
			strQueryParam = strQueryParam + entry.getKey() + ": " + entry.getValue() + ", ";
		}

		return strQueryParam;
	}


	/** This method converts a comma separated string in to a list after trimming any space.
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> getListFromCommaSeparatedString(String str)
	{
		List<String> list = new ArrayList<>();

		String [] strArray = str.split(",");

		for(int i=0; i<strArray.length; i++)
		{
			list.add(strArray[i].trim());
		}

		return list;
	}


	/** Convert List of 1 d array to 2 d array.
	 * 
	 * @param x
	 * @return
	 */
	public static String[][] get2DArrayFrom1DArrayList(List<String[]> x)
	{
		String [][] array = new String[x.size()][x.get(0).length];

		for (int i=0; i<x.size(); i++)
		{
			String[] xe = x.get(i);

			for(int j=0; j<xe.length; j++)
			{
				array[i][j] = xe[j];
			}
		}

		//		for(int i=0; i<array.length; i++)
		//		{
		//			for(int j=0; j<array[0].length; j++)
		//			{
		//				System.out.print(array[i][j] + "     ");
		//			}
		//		}

		return array;
	}


	/**
	 *  This method will apply the received the regular expression on the received string and 
	 *  return the list of match found.
	 *  
	 * @param str
	 * @param regx
	 * @return
	 */
	public List<String> getRegxMatches(String str, String regx)
	{
		List<String> matches = new ArrayList<>();

		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(str);

		while(matcher.find())
		{
			matches.add(matcher.group());
		}
		
		return matches;
	}


}




