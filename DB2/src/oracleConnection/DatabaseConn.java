package oracleConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleTypes;
/**
 * Database connection methods: Modified from the DBDocuments from cprg251
 * to fit the assignment specifications for Database assignment 2.
 * @author 672749
 *
 */
public class DatabaseConn
{
	private Connection conn;
	private Statement stmt;
	private ResultSet rs;
	CallableStatement cstmt; 
	
	public DatabaseConn()
	{
		
	}
	
	public void setConnection(String user, String password) throws ClassNotFoundException, SQLException
	{

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:XE",
					user, password);

	}
	
	public void closeConnection()
	{
		try
		{
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
	
	public String checkPayroll()
	{
		String checkPayroll = "";
		try
		{
			CallableStatement cstmt = conn.prepareCall("{? = call payroll_process_check}");
			cstmt.registerOutParameter(1, OracleTypes.VARCHAR); 
			cstmt.executeQuery();
			checkPayroll = String.valueOf(cstmt.getString(1).charAt(0));

			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
		return checkPayroll;
	}

	public void setPayrollFlagN() 
	{
		try
		{
			CallableStatement cstmt = conn.prepareCall("{call update_processing_flag_n}");
			cstmt.executeUpdate();
			
			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
	
	public void setPayrollFlagY() 
	{
		try
		{
			CallableStatement cstmt = conn.prepareCall("{call update_processing_flag_y}");
			cstmt.executeUpdate();
			
			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
	
	
	public String checkMonthEnd()
	{
		String checkMonthEnd = "";
		try
		{
			CallableStatement cstmt = conn.prepareCall("{? = call month_end_process_check}");
			cstmt.registerOutParameter(1, OracleTypes.VARCHAR); 
			cstmt.executeQuery();
			checkMonthEnd = String.valueOf(cstmt.getString(1).charAt(0));

			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
		return checkMonthEnd;
	}
	
	public void setMonthEndN()
	{
		try
		{
			CallableStatement cstmt = conn.prepareCall("{call update_month_end_flag_N}");
			cstmt.executeUpdate();
			
			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
	
	public void setMonthEndY()
	{
		try
		{
			CallableStatement cstmt = conn.prepareCall("{call update_month_end_flag_Y}");
			cstmt.executeUpdate();
			
			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
	
	public void zeroAccounts()
	{
		try
		{
			CallableStatement cstmt = conn.prepareCall("{call zero_accounts}");
			cstmt.executeUpdate();
			
			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		} 
	}

	public void createExport(String alias, String path ) 
	{
		try
		{
			String sql = "CREATE OR REPLACE DIRECTORY "+alias +" AS '" + path + "'";
			Statement st = conn.createStatement();
			st.executeUpdate(sql);
			
			st.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
	
	public void writeExport(String filename, String alias)
	{
		try
		{
			CallableStatement cstmt = conn.prepareCall("{call populate_file(?,?)}");
			
			cstmt.setString(1, alias); 
			cstmt.setString(2, filename);
			
			cstmt.execute();
			
			cstmt.close();
			conn.close();
		}
		catch(SQLException e)
		{
		}
	}
}
