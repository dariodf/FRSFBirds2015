package tori.utils;

import java.io.FileWriter;
import java.io.IOException;

public final class Logger {

	public static void Print(String Message) {
		
		try {
			FileWriter fw = new FileWriter("Output/Log.txt", true);
			fw.write(System.getProperty( "line.separator" ));
			
			fw.write(Message);
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
	}

}
