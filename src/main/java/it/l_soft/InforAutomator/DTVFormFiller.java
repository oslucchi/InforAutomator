package main.java.it.l_soft.InforAutomator;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Mouse;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;

public class DTVFormFiller extends InforFunctions {	
	private final Logger log = Logger.getLogger(this.getClass());

	ArrayList<Picking> pickList = null;
	String orderRef = null;
	Parameters parms;
	Match mItem = null;
	Region rItem, r;
	String DTVName = null;
	OCR.Options textOpt;
	List<Match> resources;

	
	public DTVFormFiller(ArrayList<Picking> pickList, String orderRef, Parameters parms)
	{
		this.pickList = pickList;
		this.orderRef = orderRef;
		Settings.ActionLogs = false; // messages from click, ...
		Settings.InfoLogs = false; //other information messages
		this.parms = parms;
	}

	private boolean getSalesIssueFeatureOn()
	{
		Match m;
		log.debug("Check if the Inventory parms.menu is exposed already");
		try{
			parms.menu.find("img/Inventory_Menu_Exposed.png");
		}
		catch(Exception e)
		{
			Utils.pauseExecution(1000);
			log.debug("It seems it is not opened, click on Inventory button");
			try {
				parms.menu.click("img/InventoryButton.png");
			}
			catch(Exception e1)
			{
				Utils.pauseExecution(1000);
				log.error("Can't get Inventory parms.menu opened. Aborting function", e1);
				return false;
			}
		}
		log.trace("The Inventory Menu should now is exposed");

		log.debug("Check if 'Sales issue' is already exposed on parms.menu");
		
		if ((m = Utils.findTextInRegion("Sales issue", parms.menu, textOpt)) == null)
		{
			log.debug("Sales issue option is not exposed, expand the GoodsIssue option");
			try {
				parms.menu.click("img/Inventory_GoodsIssueButton.png");
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

		if ((m = Utils.findTextInRegion("Sales issue", parms.menu, textOpt)) == null)
		{
			log.error("Can't see the Sales issue option. Aborting function");
			return false;
		}
		
		Location l = new Location(parms.xOffset+m.getX() + 10, parms.yOffset+m.getY()+5); 
		Mouse.move(l);
		log.debug("Click on Sales issue button at " + Mouse.at().toString());
		Mouse.click(l, "L", 1);
		
		return true;		
	}
	
	private boolean getSalesIssueData()
	{
		try{
			if (parms.formHeader.exists("img/Inventory_GoodsIssue_SalesIssue_InputFormReady.png",20) == null)
			{
				log.debug("The header form to pick items did not show up, abort");
				return false;
			}
			log.trace("Sales data form exposed, entering orderRef in Pick List field");
			parms.formHeader.click("img/Inventory_GoodsIssue_SalesIssue_PickListCheck.png");
			Utils.pauseExecution(300);
			mItem = parms.formHeader.exists("img/Inventory_GoodsIssue_SalesIssue_OrderNoCombo.png");
			if (mItem == null)
			{
				log.debug("The Order no. combo box wasn't found on parms.screen");
				return false;
			}
			Location l = new Location(mItem.getX() + mItem.getW() - 10, mItem.getY() + 10);
			Mouse.click(l, "L", 1);
			parms.screen.type(orderRef);
			log.trace("load form sales issue inventory data");
			parms.screen.type(Key.ENTER);

			if (parms.screen.exists("img/No_Data_Record_Found.png", 3) != null)
			{
				log.error("L'ordine '" + orderRef + "' non e' stato trovato");
				parms.screen.type(Key.ENTER);
				return false;
			}
			
			if (parms.appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png", 10) == null)
			{
				log.debug("The could not get the pick form out, abort");
				return false;
			}
			log.trace("The sales issue inventory data form is now exposed");

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
			mItem = parms.appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTab.png");
			mItem.click();
			Utils.pauseExecution(250);
			
			Location l = new Location(resourcesRegion.aboveAt().getX(), resourcesRegion.getY() + 10);
			Mouse.click(l, "L", 1);

			for(int i = 0; i < articles.size(); i++)
			{
				parms.screen.type("a", KeyModifier.CTRL);
				parms.screen.type("c", KeyModifier.CTRL);
				// Move to the X-Coordinate column
				parms.screen.type(Key.TAB);
				Utils.pauseExecution(100);
				String article = Utils.getClipboardContents();
				log.debug("Setting PID for article '" + article + "'");
				boolean artFound = false;
				for(Picking item : pickList)				
				{
					log.debug("Considering article '" + item.getArticle() + "' from pickList");
					if (item.getArticle().compareTo(article) != 0)
						continue;
					parms.screen.type(item.getWh());
					parms.screen.type(Key.TAB);
					parms.screen.type(item.getX());
					parms.screen.type(Key.TAB);
					Utils.pauseExecution(100);
					parms.screen.type(item.getY());
					parms.screen.type(Key.TAB);
					Utils.pauseExecution(100);
					parms.screen.type(item.getZ());
					parms.screen.type(Key.TAB);
					Utils.pauseExecution(100);
					for(int y = 0; y < 3; y++)
					{
						parms.screen.type(Key.TAB);
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
			
			parms.appBody.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
		}
		catch(Exception e)
		{
			log.error("Exception " + e.getMessage() + " in enterSalesIssueCoordinates");
			return false;
		}
		return true;
	}
	public static boolean isNumeric(String str) {
		  ParsePosition pos = new ParsePosition(0);
		  NumberFormat.getInstance().parse(str, pos);
		  return str.length() == pos.getIndex();
	}
	
	private boolean enterSalesIssueInventory()
	{
		ArrayList<String> articles;
		
		Region resourcesRegion;
		Match m;
//		parms.highlight = true;
		try{
			parms.appBody.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
			
			if ((m = parms.appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_Resource.png")) == null)
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
			articles = new ArrayList<String>();
			while(true)
			{
				Mouse.click(l, "L", 1);
				parms.screen.type("a", KeyModifier.CTRL);
				parms.screen.type("c", KeyModifier.CTRL);
				parms.screen.type(Key.TAB);
				String article = Utils.getClipboardContents();
				if (!isNumeric(article.trim())) 
				{
					// TODO: hack, a non numeric row indicates no more articles in the list
					break;
				}
				articles.add(article);
				log.debug("Setting PID for article '" + article + "'");
				boolean artFound = false;
				for(Picking item : pickList)				
				{
					log.debug("Considering article '" + item.getArticle() + "' from pickList");
					if (item.getArticle().compareTo(article) != 0)
						continue;
					parms.screen.type("1");
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
			mItem = parms.appBody.exists("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png");
			Location l = new Location(mItem.getX() + 350, mItem.getY() + 10);
			Mouse.click(l, "D", 1);
			parms.screen.type("c", KeyModifier.CTRL);
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
			match = parms.menu.exists("img/SaveIcon.png");
			
			if (match == null)
			{
				log.debug("Can't find the Save icon, aborting post");
			}
			else
			{
				match.click();
				parms.screen.exists("img/Inventory_GoodsIssue_SalesIssue_End.png", 120);
				
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
		log.debug("enterData has started");
		String imgPath = System.getProperty("user.dir");
		ImagePath.add(imgPath);
		
		try{
			if ((mItem = parms.screen.exists("img/InforLogo.png")) == null)
			{
				log.debug("La finestra di INFOR non e' aperta, cerco l'icona");

				if ((mItem = parms.toolBar.exists("img/InforIcon.png")) == null)
				{
					log.debug("Icona INFOR non trovata, non è in esecuzione. Esco");
					System.exit(-1);
				}
				mItem.click();
				parms.menu.wait("img/InforLogo.png");
			}
			log.trace("La finestra di INFOR e' aperta, procediamo");

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
				// Close the current window
				log.trace("Closing windows, wait for 15 secs to proceed");
				Utils.pauseExecution(parms.secondsToWaitBeforeClose * 1000);
				Location l = new Location(parms.screen.getX() + parms.screen.getW() - 30, 20);
				Mouse.click(l, "L", 1);
				Utils.pauseExecution(2000);
				
				log.trace("resetting menu to the start status");
				parms.menu.click("img/Inventory_GoodsIssueButton.png");	
				Utils.pauseExecution(500);
				parms.menu.click("img/Sales_Button.png");
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
