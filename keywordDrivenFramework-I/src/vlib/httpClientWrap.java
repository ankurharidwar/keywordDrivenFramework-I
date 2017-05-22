/**
 * Last Changes Done on Jan 27, 2015 12:43:12 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;


public class httpClientWrap 
{

	static Logger logger = Logger.getLogger(httpClientWrap.class.getName()); 


	@SuppressWarnings("finally")
	public static String sendGetRequest(String ServerURL) 
	{
		// It may be more appropriate to use FileEntity class in this particular
		// instance but we are using a more generic InputStreamEntity to demonstrate
		// the capability to stream out data from any arbitrary source
		//
		// FileEntity entity = new FileEntity(file, "binary/octet-stream");


		// add request header
		//GetRequest.addHeader("User-Agent", USER_AGENT);

		ServerURL = ServerURL.replace("{", "");
		ServerURL = ServerURL.replace("}", "");
		ServerURL = ServerURL.trim();

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(ServerURL.isEmpty() || ServerURL.equalsIgnoreCase(""))
			{	
				logger.info(" : Provided server url is Either BLANK or EMPTY, Please check it again. "+ServerURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();

				ServerURL = ServerURL.replace("%%", "").trim();
				HttpGet GetRequest = new HttpGet(ServerURL);
				try{
					response = httpclient.execute(GetRequest);
				}catch(HttpHostConnectException h){
					logger.error(" : Couldn't connect to host: "+ServerURL);
				}

				logger.info(" : --------------------------RESPONSE ----------------------------");
				BufferedReader rd = null;
				try{
					logger.info(response.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(" : No RESPONSE RECEIVED.");
				}

				if(rd != null)
				{
					String line = "";
					while ((line = rd.readLine()) != null)
					{
						result.append(line);
					}

					logger.info(result.toString());
				}
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting response from url: "+ServerURL, e);
		}
		finally 
		{

			try{
				response.close();
				httpclient.close();
			}
			catch(NullPointerException n)
			{ logger.info(" : null pointer exception handled. ");}
			catch(Exception e){
				logger.error(" : Exception occurred while closing channel. ",e);
			}


			return result.toString();
		}
	}

	
	/** This method will return the status code and response.
	 * 
	 * @param ServerURL
	 * @return
	 */
	@SuppressWarnings("finally")
	public static HashMap<Object, Object> sendGetRequest_GetStatusLine(String ServerURL) 
	{
		HashMap<Object, Object> resultMap = new HashMap<>();
		
		ServerURL = ServerURL.replace("{", "");
		ServerURL = ServerURL.replace("}", "");
		ServerURL = ServerURL.trim();

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(ServerURL.isEmpty() || ServerURL.equalsIgnoreCase(""))
			{	
				logger.info(" : Provided server url is Either BLANK or EMPTY, Please check it again. "+ServerURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();

				ServerURL = ServerURL.replace("%%", "").trim();
				HttpGet GetRequest = new HttpGet(ServerURL);
				try{
					response = httpclient.execute(GetRequest);
				}catch(HttpHostConnectException h){
					logger.error(" : Couldn't connect to host: "+ServerURL);
				}

				logger.info(" : --------------------------RESPONSE ----------------------------");
				BufferedReader rd = null;
				try{
					logger.info(response.getStatusLine().getStatusCode() + "  ----  " + response.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(" : No RESPONSE RECEIVED.");
				}

				if(rd != null)
				{
					String line = "";
					while ((line = rd.readLine()) != null)
					{
						result.append(line);
					}

					logger.info(result.toString());
				}
				
				resultMap.put("response", result.toString());
				resultMap.put("statuscode", response.getStatusLine().getStatusCode());
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting response from url: "+ServerURL, e);
		}
		finally 
		{
			try{
				response.close();
				httpclient.close();
			}
			catch(Exception e){
				logger.error(" : Exception occurred while closing channel. ",e);
			}
			
			return resultMap;
		}
	}


	@SuppressWarnings("finally")
	public static String sendGetRequestPrintHeaders(String ServerURL) 
	{
		// It may be more appropriate to use FileEntity class in this particular
		// instance but we are using a more generic InputStreamEntity to demonstrate
		// the capability to stream out data from any arbitrary source
		//
		// FileEntity entity = new FileEntity(file, "binary/octet-stream");


		// add request header
		//GetRequest.addHeader("User-Agent", USER_AGENT);

		ServerURL = ServerURL.replace("{", "");
		ServerURL = ServerURL.replace("}", "");
		ServerURL = ServerURL.trim();

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(ServerURL.isEmpty() || ServerURL.equalsIgnoreCase(""))
			{	
				logger.info(" : Provided server url is Either BLANK or EMPTY, Please check it again. "+ServerURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();

				ServerURL = ServerURL.replace("%%", "").trim();
				HttpGet GetRequest = new HttpGet(ServerURL);
				try{
					response = httpclient.execute(GetRequest);
				}catch(HttpHostConnectException h){
					logger.error(" : Couldn't connect to host: "+ServerURL);
				}

				logger.info(" : --------------------------RESPONSE ----------------------------");
				BufferedReader rd = null;
				try{
					logger.info(response.getStatusLine());
					rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
				}catch(NullPointerException e){
					logger.error(" : No RESPONSE RECEIVED.");
				}

				if(rd != null)
				{
					String line = "";
					while ((line = rd.readLine()) != null)
					{
						result.append(line);
					}

					logger.info(result.toString());
				}
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting response from url: "+ServerURL, e);
		}
		finally 
		{

			try{

				for(int i=0; i< response.getAllHeaders().length; i++)
				{
					System.out.println(response.getAllHeaders()[i].getName() + " - "+ response.getAllHeaders()[i].getValue());
				}

				response.close();
				httpclient.close();
			}
			catch(NullPointerException n)
			{ logger.info(" : null pointer exception handled. ");}
			catch(Exception e){
				logger.error(" : Exception occurred while closing channel. ",e);
			}


			return result.toString();
		}
	}


	//Send Get Request with custom headers
	public static String sendGetRequest(String serverURL, HashMap<String, String> headers)  
	{
		serverURL = serverURL.replace("{", "");
		serverURL = serverURL.replace("}", "");
		serverURL = serverURL.trim();

		logger.info(" : Received URL: "+serverURL);

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.info(" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				logger.info(" : Adding custom headers... ");

				//Adding add custom headers in request
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					logger.error(name +" : "+value);
					GetRequest.addHeader(name, value);
				}

				response = httpclient.execute(GetRequest);

				logger.info(" : --------------------------RESPONSE ----------------------------");
				logger.debug(response.getStatusLine());

				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				logger.info(result.toString());

				response.close();
				httpclient.close();
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting response from url: "+serverURL, e);
		}
		//		finally 
		//		{
		return result.toString();
		//		}
	}


	/** Print headers also 
	 * 
	 * @param serverURL
	 * @param headers
	 * @return
	 */
	public static String sendGetRequestPrintHeaders(String serverURL, HashMap<String, String> headers)  
	{
		serverURL = serverURL.replace("{", "");
		serverURL = serverURL.replace("}", "");
		serverURL = serverURL.trim();

		logger.info(" : Received URL: "+serverURL);

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.info(" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				logger.info(" : Adding custom headers... ");

				//Adding add custom headers in request
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					GetRequest.addHeader(name, value);
				}

				response = httpclient.execute(GetRequest);

				logger.info(" : --------------------------RESPONSE ----------------------------");
				logger.debug(response.getStatusLine());

				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				logger.info(result.toString());

				response.close();
				httpclient.close();
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting response from url: "+serverURL, e);
		}

		for(int i=0; i< response.getAllHeaders().length; i++)
		{
			System.out.println(response.getAllHeaders()[i].getName() + " - "+ response.getAllHeaders()[i].getValue());
		}
		return result.toString();

	}


	/** Send Get Request with custom headers, return status code.
	 * 
	 * @param serverURL
	 * @param headers
	 * @return
	 */
	@SuppressWarnings("finally")
	public static int getStatusCodeOfGetRequest(String serverURL, HashMap<String, String> headers)  
	{
		serverURL = serverURL.replace("{", "");
		serverURL = serverURL.replace("}", "");
		serverURL = serverURL.trim();

		logger.info(" : Received URL: "+serverURL);

		StringBuffer result = new StringBuffer();
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;

		int statusCode = 0;
		try 
		{
			if(serverURL.isEmpty() || serverURL.equalsIgnoreCase(""))
			{	
				logger.info(" : Provided server url is Either BLANK or EMPTY, Please check it again. "+serverURL);
			}
			else
			{
				httpclient = HttpClients.createDefault();
				HttpGet GetRequest = new HttpGet(serverURL);

				//Adding add custom headers in request
				if(headers != null)
				{
					logger.info(" : Adding custom headers... ");
					for(Entry<String, String> map : headers.entrySet())
					{
						String name = map.getKey().trim();
						String value = map.getValue().trim();
						GetRequest.addHeader(name, value);
					}
				}

				response = httpclient.execute(GetRequest);

				logger.info(" : --------------------------RESPONSE ----------------------------");
				logger.debug(response.getStatusLine());

				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null)
				{
					result.append(line);
				}

				logger.info(result.toString());
			}
		} 
		catch(Exception e)
		{
			logger.error(" : Exception occurred while getting response from url: "+serverURL, e);
		}
		finally 
		{
			try
			{
				statusCode = response.getStatusLine().getStatusCode();
				response.close();
				httpclient.close();
			}
			catch(Exception e)
			{
				logger.error(e);
			}

			return statusCode;
		}
	}


	@SuppressWarnings("finally")
	public static String sendPostRequest(String ServerURL, String PostData)
	{
		//Comment added by Pankaj
		//Comment removed String buffer as it is initialized with null, instead using String initializing with "" 

		String result = "";

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(ServerURL);

			// It may be more appropriate to use FileEntity class in this particular
			// instance but we are using a more generic InputStreamEntity to demonstrate
			// the capability to stream out data from any arbitrary source
			//
			// FileEntity entity = new FileEntity(file, "binary/octet-stream");

			httppost.setEntity(new StringEntity(PostData));

			logger.info(" : Executing request: " + httppost.getRequestLine());

			CloseableHttpResponse response = httpclient.execute(httppost);
			try 
			{
				logger.info(" : --------------------------RESPONSE ----------------------------");
				logger.info(response.getStatusLine());
				if(response.getStatusLine().toString().toLowerCase().contains("204 no content"))
				{
					//assigning result
					result = result + "204 no content";

					throw new CustomException("Empty Response received from server.");

				}
				//Added else condition
				else
				{
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));

					String line = "";
					while ((line = rd.readLine()) != null) {

						result = result + line + "\n";
					}
				}

				logger.info(result.toString());
			} 
			finally 
			{
				response.close();
			}
		} 
		finally 
		{
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e);
			}
			return result.toString();
		}
	}


	/** This method will send the post request along with supplied headers.
	 * 
	 * @param ServerURL
	 * @param PostData
	 * @param headers
	 * @return
	 */
	public static String sendPostRequest(String ServerURL, String PostData, HashMap<String, String> headers)
	{
		String result = "";

		try 
		{
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(ServerURL);

			logger.info(" : Adding custom headers... ");

			/** Adding add custom headers in request */
			if(!headers.isEmpty() || headers !=null)
			{
				for(Entry<String, String> map : headers.entrySet())
				{
					String name = map.getKey().trim();
					String value = map.getValue().trim();
					httppost.addHeader(name, value);
				}
			}

			httppost.setEntity(new StringEntity(PostData));

			logger.info(" : Executing request: " + httppost.getRequestLine());

			CloseableHttpResponse response = httpclient.execute(httppost);

			logger.info(" : --------------------------RESPONSE ----------------------------");
			logger.info(result.toString());

			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {

				result = result + line + "\n";
			}

			response.close();
			httpclient.close();
		} 
		catch(HttpHostConnectException e)
		{
			logger.error(" : Error occurred while sending a post request. " + e.getMessage());
		}
		catch(Exception e)
		{
			logger.error(" : Error occurred while sending a post request. ", e);
		}
		return result.toString();
	}


	public static void main(String[] args) throws Exception 
	{

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try 
		{
			HttpPost httppost = new HttpPost("http://serve.qa.vdopia.com/adserver/rtb/adrequest/mopub");

			// It may be more appropriate to use FileEntity class in this particular
			// instance but we are using a more generic InputStreamEntity to demonstrate
			// the capability to stream out data from any arbitrary source
			//
			// FileEntity entity = new FileEntity(file, "binary/octet-stream");
			String data = "{	\"imp\": [    	{            \"h\": 48,            \"battr\": [                \"9\",                \"10\",                \"12\"        	],            \"api\": 3,            \"w\": 320,            \"instl\": 0,            \"impid\": \"5d6dedf3-17bb-11e2-b5c0-1040f38b83e0\"    	}	],	\"app\": {        \"name\": \"Test App\",        \"pid\": \"agltb3B1Yi1pbmNyAEsBS0GjZ291bnQY9Iv5FAw\",        \"pub\": \"Test Pub\",        \"cat\": [            \"Games\"    	],        \"paid\": 0,    	\"aid\": \"agltb3B1Yi1pdmNyBAsSA0FwcBjRmvkVDB\",        \"global_aid\": \"385763430\"	},	\"pf\": 0.05,	\"at\": 2,    \"restrictions\": {        \"badv\": [],        \"bcat\": [            \"IAB25\",            \"IAB7-39\",            \"IAB8-5\",        	\"IAB8-18\",            \"IAB9-9\",            \"IAB14-1\"    	]	},    \"device\": {        \"os\": \"iPhone OS\",        \"ip\": \"204.28.127.10\",        \"js\": 1,        \"dpid\": \"32b1c496b84ea549191cdc65d32ade0c8d74c91b\",        \"osv\": \"4.2.1\",        \"loc\": \"37.7,-122.4\",        \"country\": \"USA\",        \"make\": \"Apple\",        \"carrier\": \"Wi-Fi\",        \"model\": \"iPhone\",        \"ua\": \"Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7\"	},	\"tmax\": 200,	\"id\": \"31c64cf8-17bb-11e2-a4c5-1040f38b83e0\",	\"user\": {        \"uid\": \"32B1C496B84EA549191CDC65d32ADE0C8D74C91B\",        \"zip\": \"94110\",        \"gender\": \"F\",        \"yob\": 1990,    	\"country\": \"USA\",        \"keywords\": \"m_age:22,startups\"	}}";

			httppost.setEntity(new StringEntity(data));

			logger.info(" : Executing request: " + httppost.getRequestLine());

			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				logger.info(" : ----------------------------------------");
				logger.info(response.getStatusLine());
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				logger.info(result.toString());

			} 
			finally 
			{
				response.close();
			}
		} 
		finally 
		{
			httpclient.close();
		}
	}


}
