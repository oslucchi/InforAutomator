package main.java.it.l_soft.InforAutomator;

import java.util.Locale;

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
	}

	public String getInforAutomatorHost() {
		return inforAutomatorHost;
	}
	public int getInforAutomatorPort() {
		return inforAutomatorPort;
	}
}
