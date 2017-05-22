/**
 * Last Changes Done on Jan 27, 2015 12:44:43 PM
 * Last Changes Done by Pankaj Katiyar
 * Change made in Vdopia_Automation
 * Purpose of change: 
 */
package vlib;


import org.apache.log4j.Logger;
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;


public class DBLib 
{
	static Logger logger = Logger.getLogger(DBLib.class.getName());

	Connection connection; 

	/** Need to have this
	 * 
	 */
	public DBLib()
	{

	}

	/** Getting db connection 
	 * 
	 * @param connection
	 */
	public DBLib(Connection connection)
	{
		this.connection = connection;
	}


	/** This method will execute the update / insert query.
	 * 
	 * @return
	 */
	public boolean executeUpdateInsertQuery(Connection connection, String sql)
	{
		boolean flag;
		try
		{
			logger.info(" : Running Query in DB : " + sql);
			Statement statement = (Statement) connection.createStatement();
			statement.executeUpdate(sql);

			flag = true;
		}catch(CommunicationsException | MySQLNonTransientConnectionException w)
		{
			flag = false;
			logger.error(" : SQL connection was closed while executing query. ");
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(" : Exception occurred while executing query: "+sql, e);
		}
		return flag;
	}



}
