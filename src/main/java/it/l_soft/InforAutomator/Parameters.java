package main.java.it.l_soft.InforAutomator;

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
	
	
	public String getInforAutomatorHost() {
		return inforAutomatorHost;
	}
	public int getInforAutomatorPort() {
		return inforAutomatorPort;
	}
}
