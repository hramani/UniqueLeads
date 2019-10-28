package com.hiral.uniquejson;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    /**
     * @param args
     */
    public static void main( String[] args )
    {
        try {
        	if(args.length>0) {
        		EmployeeRecordParser jsonParser = new EmployeeRecordParser(args[0]);
				jsonParser.parseEmployeeFile();
        	} else {
        		System.err.println("No file name specified\n usage Main \"valid file path\"");
        	}
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("IO Exception: " + e);
		}
        
    }
}
