package oracleConnection;

public class ConnectionSettings
{
	String username,password; 
	public ConnectionSettings()
	{
		
	}
	
	public ConnectionSettings(String user, String pass)
	{
		this.username = user;
		this.password = pass;
	}

	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}
	
	

}
