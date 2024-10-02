package main.java.it.l_soft.InforAutomator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.sikuli.basics.Settings;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;


public class Parameters {
	private static Logger log = Logger.getLogger(Parameters.class);
	
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
	public int secondsToWaitBeforeClose = 25;
	public boolean debug = false;
	public boolean postChanges = false;
	public boolean closeFunctionAtEnd = false;
	public int useScreen = 0;
	public int pauseAtStart = 0;
	public boolean highlightMainRegions = false;
	public String pathToTesseract = "/usr/bin/tesseract";
	public Locale appLocale = Locale.ITALIAN;
	
	public Region screen, menu, formHeader, appBody, toolBar;
	int xOffset, yOffset, screenH, screenW;
	public OCR.Options textOpt;


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
    	catch(Exception e) 
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
    	inforAutomatorHost = properties.getProperty("inforAutomatorHost");
    	inforAutomatorPort = Integer.valueOf(properties.getProperty("inforAutomatorPort"));
    	secondsToWaitBeforeClose = Integer.valueOf(properties.getProperty("secondsToWaitBeforeClose"));
    	
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
	}

	public String getInforAutomatorHost() {
		return inforAutomatorHost;
	}
	public int getInforAutomatorPort() {
		return inforAutomatorPort;
	}
	
	public void setScreenRegions(boolean highlight)
	{
		
		xOffset = (int) Screen.getBounds(useScreen).getX();
		yOffset = (int) Screen.getBounds(useScreen).getY();
		screenH  = (int) Screen.getBounds(useScreen).getHeight();
		screenW = (int) Screen.getBounds(useScreen).getWidth();
		log.debug(String.format("Screen details: # %d, X %d, Y %d, W %d, H%d",
								useScreen, xOffset, yOffset, screenW, screenH));

		screen = new Region(xOffset, yOffset, screenW, screenH);
		if (highlight)
		{
			screen.highlight(Parameters.HIGHLIGHT_DURATION);
		}
		
		Settings.ActionLogs = false; // messages from click, ...
		Settings.InfoLogs = false; //other information messages
		menu = new Region(xOffset, yOffset, 240, screenH);
		log.debug(String.format("Region menu at: X %d, Y %d, W %d, H%d",
				menu.getX(), menu.getY(), menu.getW() ,menu.getH()));
		if (highlight)
		{
			menu.highlight(Parameters.HIGHLIGHT_DURATION);
		}

		toolBar = new Region(xOffset + Parameters.TOOLBAR_OFFSET_X,
				yOffset + Parameters.TOOLBAR_OFFSET_Y, 
				screenW - Parameters.TOOLBAR_OFFSET_X,
				screenH - Parameters.TOOLBAR_OFFSET_Y);
		log.debug(String.format("Region toolBar at: X %d, Y %d, W %d, H%d",
				toolBar.getX(), toolBar.getY(), toolBar.getW() ,toolBar.getH()));	
		if (highlight)
		{
			toolBar.highlight(Parameters.HIGHLIGHT_DURATION);
		}
		
		formHeader = new Region(xOffset + Parameters.FORM_HEADER_OFFSET_X,
				yOffset + Parameters.FORM_HEADER_OFFSET_Y, 
				screenW - Parameters.FORM_HEADER_OFFSET_X,
				250);
		log.debug(String.format("Region formHeader at: X %d, Y %d, W %d, H%d",
				formHeader.getX(), formHeader.getY(), 
				formHeader.getW(), formHeader.getH()));		
		if (highlight)
		{
			formHeader.highlight(Parameters.HIGHLIGHT_DURATION);
		}
		
		appBody = new Region(xOffset + Parameters.APPBODY_OFFSET_X,
				yOffset + Parameters.APPBODY_OFFSET_Y, 
				screenW - Parameters.APPBODY_OFFSET_X,
				screenH - Parameters.APPBODY_OFFSET_Y);
		log.debug(String.format("Region appBody at: X %d, Y %d, W %d, H%d",
				appBody.getX(), appBody.getY(), appBody.getW() ,appBody.getH()));		
		if (highlight)
		{
			appBody.highlight(Parameters.HIGHLIGHT_DURATION);
		}
		
		textOpt = OCR.globalOptions().fontSize(9);
	}
}
