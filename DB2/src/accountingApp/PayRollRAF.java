package accountingApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * random access:
 * Implementation modified from CPRG 251
 * to match specifications for CPRG 300 
 * @author 672749
 *
 */
public class PayRollRAF
{
	private static final String MODE= "rwd";
	static String binaryPath = "res/payroll.bin";
	static String txtPath = "res/payroll.txt";
 	private PayRollRAF praf;
	private File file;

	private static RandomAccessFile raf;
	
	
	public PayRollRAF()
	{
		
	}
	
	void getPraf(String binaryPath) throws IOException
	{

		raf = new RandomAccessFile(new File(binaryPath), MODE);
	}
	
	void writeTxt(String txtPath)
	{	
		FileWriter fw = null;
		BufferedWriter out=null;
		try	
		{
			file = new File(txtPath);
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			fw = new FileWriter(file);
			out = new BufferedWriter(fw);
			out.write(readRAF());
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}


			try {
				out.close();
				fw.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}


	}
	
	String readRAF()
	{
		String fileToWrite = "";
		try 
		{
			raf.seek(0);
			while(raf.getFilePointer() < raf.length())
			{
				fileToWrite = fileToWrite + raf.readUTF().trim()+";"+ 
						raf.readUTF()+";"+
						raf.readDouble()+";"+
						raf.readUTF() + ";\n";
			}
		} catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		return fileToWrite;	
	}
}
