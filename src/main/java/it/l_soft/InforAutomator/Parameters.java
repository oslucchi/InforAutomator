package main.java.it.l_soft.InforAutomator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;


public class Parameters {
	static final int FORM_HEADER_OFFSET_X = 220;
	static final int FORM_HEADER_OFFSET_Y = 0;
	static final int TOOLBAR_OFFSET_X = 0;
	static final int TOOLBAR_OFFSET_Y = 980;
	static final int APPBODY_OFFSET_X = 220;
	static final int APPBODY_OFFSET_Y = 220;
	static final int HIGHLIGHT_DURATION = 1;

	public boolean highlight = false;
	public boolean simulator = false;
	public boolean testRun = false;
	public String inforAutomatorHost = "192.168.60.110";
	public int inforAutomatorPort = 9000;
	public boolean debug = false;
	public boolean postChanges = false;
	public boolean closeFunctionAtEnd = false;
	public int useScreen = 0;
	public int pauseAtStart = 0;
	public boolean highlightMainRegions = false;
	public String pathToTesseract = "/usr/bin/tesseract";
	public Locale appLocale = Locale.ITALIAN;
	
	private static Parameters instance = null;
	public static Parameters getInstance()
	{
		if (instance == null)
		{
			instance = new Parameters();
		}
		return(instance);
	}
	
	private Parameters()
	{
		Properties properties = new Properties();
    	try 
    	{
    		File confFile = new File("./conf/log4j.properties");
        	InputStream in = new FileInputStream(confFile);
        	properties.load(in);
	    	in.close();
	    	PropertyConfigurator.configure(properties);
	    }
    	catch(IOException e) 
    	{
			System.out.println("Exception getting log4j properties " + e.getMessage());
			e.printStackTrace();
		}


    	try 
    	{
    		File confFile = new File("./conf/package.properties");
        	InputStream in = new FileInputStream(confFile);
        	properties.load(in);
	    	in.close();
		}
    	catch(IOException e) 
    	{
			System.out.println("Exception getting package properties " + e.getMessage());
    		return;
		}

    	pathToTesseract = properties.getProperty("pathToTesseract");
    	switch(properties.getProperty("appLocale"))
    	{
    	case "ENGLISH":
        	appLocale = Locale.ENGLISH;
        	break;
    	case "ITALIAN":
        	appLocale = Locale.ITALIAN;
        	break;
    	case "GERMAN":
        	appLocale = Locale.GERMAN;
        	break;
    	}
    	inforAutomatorHost = properties.getProperty("inforAutomatorHost");
    	inforAutomatorPort = Integer.valueOf(properties.getProperty("inforAutomatorPort"));
	}

	public String getInforAutomatorHost() {
		return inforAutomatorHost;
	}
	public int getInforAutomatorPort() {
		return inforAutomatorPort;
	}
}
