package main.java.it.l_soft.InforAutomator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class DTVFormFiller extends InforFunctions {	
	ArrayList<Picking> pickList = null;
	String orderRef = null;
	Parameters parms;
	Screen s = new Screen(1);
	Match mItem = null;
	Region menu, formHeader, appBody, rItem, toolBar, r;
	String DTVName = null;
	OCR.Options textOpt;
	List<Match> resources;
	
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
		menu = new Region(0, 0, 240, 1080);
		Debug.log("Region menu at " + 
				menu.getX() + ", " + 
				menu.getY() + ", " + 
				menu.getW() + ", " + 
				menu.getH());
		Utils.highlightSelection(parms, menu, Parameters.HIGHLIGHT_DURATION);

		toolBar = new Region(Parameters.TOOLBAR_OFFSET_X, Parameters.TOOLBAR_OFFSET_Y, 
							 1915 - Parameters.TOOLBAR_OFFSET_X, 1075 - Parameters.TOOLBAR_OFFSET_Y);
		Debug.log("Region toolBar at " + 
							 toolBar.getX() + ", " + 
							 toolBar.getY() + ", " + 
							 toolBar.getW() + ", " + 
							 toolBar.getH());
		
		Utils.highlightSelection(parms, toolBar, Parameters.HIGHLIGHT_DURATION);
		
		formHeader = new Region(Parameters.FORM_HEADER_OFFSET_X, Parameters.FORM_HEADER_OFFSET_Y, 
								1915 - Parameters.FORM_HEADER_OFFSET_X, 250);
		Debug.log("Region formHeader at " + 
				formHeader.getX() + ", " + 
				formHeader.getY() + ", " + 
				formHeader.getW() + ", " + 
				formHeader.getH());
		Utils.highlightSelection(parms, formHeader, Parameters.HIGHLIGHT_DURATION);
		
		appBody = new Region(Parameters.APPBODY_OFFSET_X, Parameters.APPBODY_OFFSET_Y, 
							 1915 - Parameters.APPBODY_OFFSET_X, 1075 - Parameters.APPBODY_OFFSET_Y);
		Debug.log("Region appBody at " + 
				appBody.getX() + ", " + 
				appBody.getY() + ", " + 
				appBody.getW() + ", " + 
				appBody.getH());
		Utils.highlightSelection(parms, appBody, Parameters.HIGHLIGHT_DURATION);
		textOpt = OCR.globalOptions().fontSize(12);
	}

	public void getSalesIssueFeatureOn()
	{
		try{
			if (Utils.findTextInRegion("Goods issue", menu, textOpt) == null)
			{
				menu.click("img/InventoryButton.png");
				menu.wait("img/InventoryMenuOpened.png");
			}
			if (Utils.findTextInRegion("Sales issue", menu, textOpt) == null)
			{
				menu.click("img/Inventory_GoodsIssueButton.png");
				menu.mouseMove(0, 100);
				Utils.pauseExecution(200);
				menu.wait("img/Inventory_GoodsIssue_SalesIssue.png");
				Utils.pauseExecution(200);
			}
			menu.click("img/Inventory_GoodsIssue_SalesIssue.png");
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void getSalesIssueData()
	{
		try{
			formHeader.wait("img/Inventory_GoodsIssue_SalesIssue_Form.png");
			formHeader.click("img/Inventory_GoodsIssue_SalesIssue_PickListCheck.png");
			Utils.pauseExecution(300);
			mItem = formHeader.find("img/Inventory_GoodsIssue_SalesIssue_OrderNoCombo.png");
			rItem = new Region(Parameters.FORM_HEADER_OFFSET_X + mItem.x + 50, 
							   Parameters.FORM_HEADER_OFFSET_Y + mItem.y, 
							   mItem.w - 50, 400);
			
			rItem.click("img/ComboArrowDown.png");
			Utils.pauseExecution(2000);
			rItem = new Region(510, 200, 140, 150);
//			Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
//			
//			rItem.hover();
//			String[] orders = Utils.readTextEntries(rItem, textOpt);
//			for(String order : orders)
//			{
//				Debug.log("Entry in region: '" + order + "'");
//			}
			
			List<Match> ordersPicked = OCR.readLines(rItem, textOpt);
			
			Match entry = null;
			String lookFor = orderRef.substring(2);
			while (lookFor.startsWith("0"))
			{
				lookFor = lookFor.substring(1);
			}
			
			for(int i = 0; i < ordersPicked.size(); i++)
			{
				Debug.log("Entry '" + ordersPicked.get(i).getText() + "', checking if '" + lookFor + "' is contained");
				if (ordersPicked.get(i).getText().contains(lookFor))
				{
					Debug.log("Found, proceeding with click");
					entry = ordersPicked.get(i);
					break;
				}
			}
			if (entry == null)
			{
				Debug.log("Ordine non trovato");
				System.exit(-1);
			}
			
			rItem = new Region(rItem.x + entry.x, rItem.y + entry.y, entry.w, entry.h);
			rItem.click();
			
			formHeader.click("img/Inventory_GoodsIssue_LoadButton.png");
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void enterSalesIssueInventory(Region resourcesRegion)
	{
		HashMap<String, Integer> articlesPicked = new HashMap<String, Integer>();
		
		try{
			resources = OCR.readLines(resourcesRegion, textOpt);
			Debug.log("OCR found " + resources.size() + " resources on resourcesRegion");
			for(Picking item : pickList)				
			{
				String articleRef = item.getArticle().trim();
				if (articlesPicked.containsKey(articleRef))
				{
					articlesPicked.replace(articleRef, articlesPicked.get(articleRef) + 1);
				}
				else
				{
					articlesPicked.put(articleRef, 1);
				}
				Debug.log(" Searching for article '" + articleRef + "' at instance " + articlesPicked.get(articleRef));

				int instance = 1;
				for(Match match : resources)
				{
					Debug.log("Comparing '" + match.getText() + 
									   "' to '"  + item.getArticle() + "' (instance " + instance + ")");				
					if (match.getText().trim().indexOf(articleRef) >= 0)
					{
						if (articlesPicked.get(articleRef) == instance)
						{
							Debug.log(" found '" + match.getText() + "' At " + 
											   match.getX() + ", " + match.getY() + 
											   " - len " + match.getW() + " width " + match.getH());
							rItem = new Region(resourcesRegion.getX() + 130, resourcesRegion.getY() + match.getY(), 20, 17);
							rItem.click();
							rItem.type("1");
							rItem.type(Key.ENTER);
							break;
						}
						instance += 1;
					}
				}

			}
		}
		catch(Exception e)
		{
			
		}
	}

	public void enterSalesIssueCoordinates(Region resourcesRegion)
	{
		HashMap<String, Integer> articlesPicked = new HashMap<String, Integer>();
		
		try{
			
			mItem = appBody.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTab.png");
			mItem.click();
			
			rItem = new Region(Parameters.APPBODY_OFFSET_X, mItem.y - 20, 1920 - Parameters.APPBODY_OFFSET_X, 60);
			rItem.wait("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTabReady.png");
			Debug.log("\n\n********     *****************");
			
			Utils.pauseExecution(500);
			for(Picking item : pickList)
			{
				if (articlesPicked.containsKey(item.getArticle()))
				{
					articlesPicked.replace(item.getArticle(), articlesPicked.get(item.getArticle()) + 1);
				}
				else
				{
					articlesPicked.put(item.getArticle(), 1);
				}
				int instance = 1;
				for(Match match : resources)
				{
					Debug.log("Comparing '" + match.getText() + "' to '"  + item.getArticle() + "'");
					if (match.getText().indexOf(item.getArticle()) >= 0)
					{						
						if (articlesPicked.get(item.getArticle()) == instance)
						{
							Debug.log(" found '" + match.getText() + "' At " + 
									   match.getX() + ", " + match.getY() + 
									   " - len " + match.getW() + " width " + match.getH());
							rItem = new Region(resourcesRegion.getX() + 140, resourcesRegion.getY() + match.getY(), 150, 18);
							Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
							String wh = OCR.readWord(rItem).trim();
							if (!wh.contains("NLIT05"))
							{
								rItem.click();
//								rItem.type(Key.DELETE);
								Utils.pauseExecution(200);
								rItem.type("NLIT05");
								rItem.type(Key.ENTER);
								Utils.pauseExecution(200);
							}
							
							rItem = new Region(rItem.getX()+145, rItem.getY(), 90, 18);
							Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
							rItem.click();
//							rItem.type(Key.DELETE);
							Utils.pauseExecution(200);
							rItem.type(item.getX());
							rItem.type(Key.ENTER);
							Utils.pauseExecution(200);
							
							rItem = new Region(rItem.getX()+100, rItem.getY(), 70, 18);
							Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
							rItem.click();
//							rItem.type(Key.DELETE);
							Utils.pauseExecution(200);
							rItem.type(item.getY());
							rItem.type(Key.ENTER);
							Utils.pauseExecution(200);
		
							rItem = new Region(rItem.getX()+70, rItem.getY(), 45, 18);
							Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
							rItem.click();
//							rItem.type(Key.DELETE);
							Utils.pauseExecution(200);
							rItem.type(item.getZ());
							rItem.type(Key.ENTER);
							Utils.pauseExecution(200);
							break;
						}
						instance += 1;
					}
				}
			}

			appBody.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
		}
		catch(Exception e)
		{
			
		}

	}

	public String getSalesIssueDTV()
	{
		try{
			mItem = appBody.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png");
			rItem = new Region(mItem.getX() + 300, mItem.getY(), 115, 20);
			Utils.pauseExecution(2000);
			Debug.log("Words found in the DTV field");
			DTVName = "";
			for(Match match : OCR.readWords(rItem, textOpt))
			{
				Debug.log("'" + match.getText() + "' At " + 
						   match.getX() + ", " + match.getY() + 
						   " - len " + match.getW() + " width " + match.getH());
				DTVName += match.getText().toUpperCase();
			}
			Debug.log("DTVName " + DTVName);
		}
		catch(Exception e)
		{
			
		}
		return DTVName;
	}
	
	public void postData() 
	{
		Match match;
		try
		{
			match = menu.find("img/Menu_Functions.png");
			
			if (match == null)
			{
				Debug.log("Can't find Functions_Post image");
			}
			else
			{
				match.click();
				Utils.pauseExecution(500);
				if (!parms.testRun)
				{
					rItem = new Region(match.getX(), match.getY() + 20, 500, 400);
					match = rItem.find("img/Menu_Functions_Post.png");
					match.click();
					rItem = new Region(200, 190, 300, 290);
					rItem.wait("img/Inventory_GoodsIssue_SalesIssue_End.png");
				}
				else
				{
					match.click();
					Debug.log("would have posted... but testRun is active");
					Utils.pauseExecution(3000);
				}
			}
		}
		catch(Exception e)
		{
			Debug.log(e.getMessage());
		}
	}

	@Override
	public String enterData()
	{
		String imgPath = System.getProperty("user.dir");
		ImagePath.add(imgPath);
		
		try{
			if ((mItem = menu.exists("img/InforLogo.png")) == null)
			{
				if ((mItem = toolBar.exists("img/InforIcon.png")) == null)
				{
					Debug.log("Infor non è in esecuzione");
					System.exit(-1);
				}
				mItem.click();
			}
			menu.wait("img/InforLogo.png");
			getSalesIssueFeatureOn();
			
			getSalesIssueData();
			
			appBody.wait("img/Inventory_GoodsIssue_SalesIssue_InputFormReady.png",2000.0);

			appBody.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
			
			Match resourceTab = appBody.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_Resource.png");		
			Region resourcesRegion = new Region(resourceTab.getX() - 2, resourceTab.getY() + 18, 120, 500);
			Utils.highlightSelection(parms, resourcesRegion, Parameters.HIGHLIGHT_DURATION);
			
			enterSalesIssueInventory(resourcesRegion);
			
			enterSalesIssueCoordinates(resourcesRegion);
			
			getSalesIssueDTV();
			Debug.log("Shall I post changes? " + parms.postChanges);
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
				rItem = new Region(1880, 0, 20, 20);
				rItem.click();
				menu.click("img/Inventory_GoodsIssueButton.png");	
			}		
			return DTVName;
		}
		catch(Exception e)
		{
			Debug.log(e.getMessage());
		}
		return "";
	}
}
