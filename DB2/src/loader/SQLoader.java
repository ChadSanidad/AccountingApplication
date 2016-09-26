package loader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SQLoader 
{
	String cmd;
	char quotes = '"';
	int exitValue;
	private File file;
	public SQLoader()
	{
	}
	
	public void executeLoader(String username, String pass, String loader)
	{	
		cmd = "sqlldr userid="+username+"/"+pass+" " // the user id, for the sql login
				+ "control=" + loader; //path of the loader
				//+ "log=res/loadlog.log";
		try 
		{
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			exitValue = proc.waitFor();
			
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}	
	}
	
	public void createLoader(String loaderTxt, String txtPath) throws IOException
	{
		FileWriter fw = null;
		BufferedWriter out=null;
		String write = "LOAD DATA" + "\n" + 
				"INFILE " + "'" + txtPath + "'" + "\n"
				+ "REPLACE \n"
				+ "INTO TABLE payroll_load \n" 
				+ "FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '" + quotes   
				+ "' \n TRAILING NULLCOLS \n"
				+ "(payroll_date DATE "+ quotes+ "Month dd, yyyy"+quotes + ",  employee_id,  amount,  status)";	

			file = new File(loaderTxt);
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			fw = new FileWriter(file);
			out = new BufferedWriter(fw);
			out.write(write);
			
			out.close();
			fw.close();
		

	}
}
