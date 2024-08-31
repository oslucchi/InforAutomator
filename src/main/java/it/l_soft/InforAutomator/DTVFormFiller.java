package main.java.it.l_soft.InforAutomator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Mouse;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class DTVFormFiller extends InforFunctions {	
	private final Logger log = Logger.getLogger(this.getClass());

	ArrayList<Picking> pickList = null;
	String orderRef = null;
	Parameters parms;
	Screen s = new Screen(1);
	Match mItem = null;
	Region screen, menu, formHeader, appBody, rItem, toolBar, r;
	String DTVName = null;
	OCR.Options textOpt;
	List<Match> resources;
	int xOffset, yOffset, screenH, screenW;

	
	public DTVFormFiller(ArrayList<Picking> pickList, String orderRef, Parameters parms)
	{
		this.pickList = pickList;
		this.orderRef = orderRef;
		this.parms = parms;
		if (parms.debug)
		{
			Debug.setLogFile("./InforAutomator.log");
			Debug.setDebugLevel(3);
		}
		else
		{
			Debug.off(); // any debugging messages
		}
		Settings.ActionLogs = false; // messages from click, ...
		Settings.InfoLogs = false; //other information messages
		xOffset = (int) Screen.getBounds(parms.useScreen).getX();
		yOffset = (int) Screen.getBounds(parms.useScreen).getY();
		screenH  = (int) Screen.getBounds(parms.useScreen).getHeight();
		screenW = (int) Screen.getBounds(parms.useScreen).getWidth();
		log.debug(String.format("Screen details: # %d, X %d, Y %d, W %d, H%d",
								parms.useScreen, xOffset, yOffset, screenW, screenH));

		screen = new Region(xOffset, yOffset, screenW, screenH);
		if (parms.highlightMainRegions)
		{
			Utils.highlightSelection(parms, screen, Parameters.HIGHLIGHT_DURATION);
		}
		
		Settings.ActionLogs = false; // messages from click, ...
		Settings.InfoLogs = false; //other information messages
		menu = new Region(xOffset, yOffset, 240, screenH);
		log.debug(String.format("Region menu at: X %d, Y %d, W %d, H%d",
				menu.getX(), menu.getY(), menu.getW() ,menu.getH()));
		if (parms.highlightMainRegions)
		{
			Utils.highlightSelection(parms, menu, Parameters.HIGHLIGHT_DURATION);
		}

		toolBar = new Region(xOffset + Parameters.TOOLBAR_OFFSET_X,
							 yOffset + Parameters.TOOLBAR_OFFSET_Y, 
							 screenW - Parameters.TOOLBAR_OFFSET_X,
							 screenH - Parameters.TOOLBAR_OFFSET_Y);
		log.debug(String.format("Region toolBar at: X %d, Y %d, W %d, H%d",
				toolBar.getX(), toolBar.getY(), toolBar.getW() ,toolBar.getH()));	
		if (parms.highlightMainRegions)
		{
			Utils.highlightSelection(parms, toolBar, Parameters.HIGHLIGHT_DURATION);
		}
		
		formHeader = new Region(xOffset + Parameters.FORM_HEADER_OFFSET_X,
								 yOffset + Parameters.FORM_HEADER_OFFSET_Y, 
								 screenW - Parameters.FORM_HEADER_OFFSET_X,
								 250);
		log.debug(String.format("Region formHeader at: X %d, Y %d, W %d, H%d",
				formHeader.getX(), formHeader.getY(), formHeader.getW() ,formHeader.getH()));		
		if (parms.highlightMainRegions)
		{
			Utils.highlightSelection(parms, formHeader, Parameters.HIGHLIGHT_DURATION);
		}
		
		appBody = new Region(xOffset + Parameters.APPBODY_OFFSET_X,
								 yOffset + Parameters.APPBODY_OFFSET_Y, 
								 screenW - Parameters.APPBODY_OFFSET_X,
								 screenH - Parameters.APPBODY_OFFSET_Y);
		log.debug(String.format("Region appBody at: X %d, Y %d, W %d, H%d",
				appBody.getX(), appBody.getY(), appBody.getW() ,appBody.getH()));		
		if (parms.highlightMainRegions)
		{
			Utils.highlightSelection(parms, appBody, Parameters.HIGHLIGHT_DURATION);
		}
		textOpt = OCR.globalOptions().fontSize(9);
	}

	private boolean getSalesIssueFeatureOn()
	{
		Match m;
		log.debug("Check if the Inventory menu is exposed already");
		try{
			menu.find("img/Inventory_Menu_Exposed.png");
		}
		catch(Exception e)
		{
			Utils.pauseExecution(1000);
			log.debug("It seems it is not opened, click on Inventory button");
			try {
				menu.click("img/InventoryButton.png");
			}
			catch(Exception e1)
			{
				Utils.pauseExecution(1000);
				log.error("Can't get Inventory menu opened. Aborting function", e1);
				return false;
			}
		}
		log.trace("The Inventory Menu should now is exposed");

		log.debug("Check if 'Sales issue' is already exposed on menu");
		
		if ((m = Utils.findTextInRegion("Sales issue", menu, textOpt)) == null)
		{
			log.debug("Sales issue option is not exposed, expand the GoodsIssue option");
			try {
				menu.click("img/Inventory_GoodsIssueButton.png");
				Utils.pauseExecution(500);
			}
			catch(Exception e)
			{
				log.error("Can't opened the Sales issue option. Aborting function");
				return false;
			}
		}
		log.trace("The Sales issue option is exposed");
		Mouse.move(50, 0);

		if ((m = Utils.findTextInRegion("Sales issue", menu, textOpt)) == null)
		{
			log.error("Can't see the Sales issue option. Aborting function");
			return false;
		}
		
		Location l = new Location(xOffset+m.getX() + 10, yOffset+m.getY()+5); 
		Mouse.move(l);
		log.debug("Click on Sales issue button at " + Mouse.at().toString());
		Mouse.click(l, "L", 1);
		
		return true;		
	}
	
	private boolean getSalesIssueData()
	{
		try{
			if (formHeader.exists("img/Inventory_GoodsIssue_SalesIssue_InputFormReady.png",20) == null)
			{
				log.debug("The header form to pick items did not show up, abort");
				return false;
			}

			formHeader.click("img/Inventory_GoodsIssue_SalesIssue_PickListCheck.png");
			Utils.pauseExecution(300);
			mItem = formHeader.exists("img/Inventory_GoodsIssue_SalesIssue_OrderNoCombo.png");
			if (mItem == null)
			{
				log.debug("The Order no. combo box wasn't found on screen");
				return false;
			}
			Location l = new Location(mItem.getX() + mItem.getW() - 10, mItem.getY() + 10);
			Mouse.click(l, "L", 1);
			screen.type(orderRef);
			screen.type(Key.ENTER);

			if (screen.exists("img/No_Data_Record_Found.png", 3) != null)
			{
				log.error("L'ordine '" + orderRef + "' non e' stato trovato");
				screen.type(Key.ENTER);
				return false;
			}
			
			if (appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png", 10) == null)
			{
				log.debug("The could not get the pick form out, abort");
				return false;
			}

		}
		catch(Exception e)
		{
			log.error("Exception " + e.getMessage() + " in enterSalesIssueCoordinates");
			return false;
		}
		return true;
	}
	

	private boolean enterSalesIssueCoordinates(Region resourcesRegion, ArrayList<String> articles)
	{
		try{
			mItem = appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTab.png");
			mItem.click();
			Utils.pauseExecution(250);
			
			Location l = new Location(resourcesRegion.aboveAt().getX(), resourcesRegion.getY() + 10);
			Mouse.click(l, "L", 1);

			for(int i = 0; i < articles.size(); i++)
			{
				screen.type("a", KeyModifier.CTRL);
				screen.type("c", KeyModifier.CTRL);
				// Move to the X-Coordinate column
				screen.type(Key.TAB);
				Utils.pauseExecution(100);
				String article = Utils.getClipboardContents();
				log.debug("Setting PID for article '" + article + "'");
				boolean artFound = false;
				for(Picking item : pickList)				
				{
					log.debug("Considering article '" + item.getArticle() + "' from pickList");
					if (item.getArticle().compareTo(article) != 0)
						continue;
					screen.type(item.getWh());
					screen.type(Key.TAB);
					screen.type(item.getX());
					screen.type(Key.TAB);
					Utils.pauseExecution(100);
					screen.type(item.getY());
					screen.type(Key.TAB);
					Utils.pauseExecution(100);
					screen.type(item.getZ());
					screen.type(Key.TAB);
					Utils.pauseExecution(100);
					for(int y = 0; y < 3; y++)
					{
						screen.type(Key.TAB);
						Utils.pauseExecution(100);
					}
					artFound = true;
					break;
				}
				if (!artFound)
				{
					log.debug("Can't find article '" + article + "' in PickList. Aborting");
					return false;
				}
			}
			
			appBody.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
		}
		catch(Exception e)
		{
			log.error("Exception " + e.getMessage() + " in enterSalesIssueCoordinates");
			return false;
		}
		return true;
	}

	private boolean enterSalesIssueInventory()
	{
		ArrayList<String> articles;
		
		Region resourcesRegion;
		Match m;
//		parms.highlight = true;
		try{
			appBody.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
			
			if ((m = appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_Resource.png")) == null)
			{
				log.debug("Non ho trovato la colonna Reources sul tab Inventory");
				return false;
			}
			log.debug("Colonna delle risorse a " + m.leftAt().toString());
			
			resourcesRegion = new Region(m.getX() - 2, m.getY() + 18, 120, 500);
			Utils.highlightSelection(parms, resourcesRegion, Parameters.HIGHLIGHT_DURATION);

			articles = Utils.runTesseractOnRegion(resourcesRegion, null, true);
			log.debug("Found " + articles.size() + " items in form (" + articles.toString());

			Location l = new Location(resourcesRegion.aboveAt().getX(), resourcesRegion.getY() + 10);

			for(int i = 0; i < articles.size(); i++)
			{
				Mouse.click(l, "L", 1);
				screen.type("a", KeyModifier.CTRL);
				screen.type("c", KeyModifier.CTRL);
				screen.type(Key.TAB);
				String article = Utils.getClipboardContents();
				log.debug("Setting PID for arti	cle '" + article + "'");
				boolean artFound = false;
				for(Picking item : pickList)				
				{
					log.debug("Considering article '" + item.getArticle() + "' from pickList");
					if (item.getArticle().compareTo(article) != 0)
						continue;
					screen.type("1");
					artFound = true;
					break;
				}
				if (!artFound)
				{
					log.debug("Can't find article '" + article + "' in PickList. Aborting");
					return false;
				}
				l = new Location(l.getX(), l.getY() + 20);
			}
		}
		catch(Exception e)
		{
			log.error("Exception " + e.getMessage() + " in enterSalesIssueInventory");
			return false;
		}
		finally 
		{
//			parms.highlight = false;
		}
		return enterSalesIssueCoordinates(resourcesRegion, articles);
	}

	private String getSalesIssueDTV()
	{
		DTVName = "";
		
		try{
			mItem = appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png");
			Location l = new Location(mItem.getX() + 350, mItem.getY() + 10);
			Mouse.click(l, "D", 1);
			screen.type("c", KeyModifier.CTRL);
			DTVName = Utils.getClipboardContents();
			log.debug("DTVName " + DTVName);
		}
		catch(Exception e)
		{
			log.error("Exception " + e.getMessage() + " in enterSalesIssueInventory");
		}
		return DTVName;
	}
	
	private void postData() 
	{
		Match match;
		try
		{
			match = menu.exists("img/SaveIcon.png");
			
			if (match == null)
			{
				log.debug("Can't find the Save icon, aborting post");
			}
			else
			{
				match.click();
				screen.exists("img/Inventory_GoodsIssue_SalesIssue_End.png", 30);
			}
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
		}
	}

	@Override
	public String enterData()
	{
		String imgPath = System.getProperty("user.dir");
		ImagePath.add(imgPath);
		
		try{
			if ((mItem = screen.exists("img/InforLogo.png")) == null)
			{
				log.debug("La finestra di INFOR non e' aperta, cerco l'icona");

				if ((mItem = toolBar.exists("img/InforIcon.png")) == null)
				{
					log.debug("Icona INFOR non trovata, non è in esecuzione. Esco");
					System.exit(-1);
				}
				mItem.click();
				menu.wait("img/InforLogo.png");
			}

			if (!getSalesIssueFeatureOn()) return "KO";
			if (!getSalesIssueData()) return "KO";
			if (!enterSalesIssueInventory()) return "KO";
						
			getSalesIssueDTV();
			log.debug("Shall I post changes? " + parms.postChanges);
			if (parms.postChanges)
			{
				postData();
			}
			else
			{
				System.out.println("NOT POSTING CHANGES since postChanges var is false!!!");
				Utils.pauseExecution(2000);
			}
			
			if (parms.closeFunctionAtEnd)
			{
				formHeader.click("img/Main_Window_Close.png");
				Utils.pauseExecution(2000);
				menu.click("img/Inventory_GoodsIssueButton.png");	
				Utils.pauseExecution(500);
				menu.click("img/Sales_Button.png");
			}		
			return DTVName;
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			return "KO";
		}
	}
}
