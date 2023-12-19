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
		Debug.off(); // any debugging messages
		Settings.ActionLogs = false; // messages from click, ...
		Settings.InfoLogs = false; //other information messages
		menu = new Region(0, 0, 240, 1080);
		Utils.highlightSelection(parms, menu, Parameters.HIGHLIGHT_DURATION);
		toolBar = new Region(Parameters.TOOLBAR_OFFSET_X, Parameters.TOOLBAR_OFFSET_Y, 
							 1920 - Parameters.TOOLBAR_OFFSET_X, 1080 - Parameters.TOOLBAR_OFFSET_Y);
		Utils.highlightSelection(parms, toolBar, Parameters.HIGHLIGHT_DURATION);
		formHeader = new Region(Parameters.FORM_HEADER_OFFSET_X, Parameters.FORM_HEADER_OFFSET_Y, 
								1920 - Parameters.FORM_HEADER_OFFSET_X, 250);
		Utils.highlightSelection(parms, formHeader, Parameters.HIGHLIGHT_DURATION);
		appBody = new Region(Parameters.APPBODY_OFFSET_X, Parameters.APPBODY_OFFSET_Y, 
							 1920 - Parameters.APPBODY_OFFSET_X, 1080 - Parameters.APPBODY_OFFSET_Y);
		Utils.highlightSelection(parms, appBody, Parameters.HIGHLIGHT_DURATION);
		textOpt = OCR.globalOptions().fontSize(12);
	}

	public void getSalesIssueFeatureOn()
	{
		try{
			if (!Utils.shownAmongRegionEntries("Goods issue", menu, textOpt))
			{
				menu.click("img/InventoryButton.png");
				menu.wait("img/InventoryMenuOpened.png");
			}
			if (!Utils.shownAmongRegionEntries("Sales issue", menu, textOpt))
			{
				menu.click("img/Inventory_GoodsIssueButton.png");
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
			mItem = formHeader.find("img/Inventory_GoodsIssue_SalesIssue_OrderNoCombo.png");
			rItem = new Region(Parameters.FORM_HEADER_OFFSET_X + mItem.x + 50, Parameters.FORM_HEADER_OFFSET_Y + mItem.y, mItem.w - 50, 400);
			
			rItem.click("img/ComboArrowDown.png");
			Utils.pauseExecution(500);
			
			List<Match> ordersPicked = OCR.readLines(rItem, textOpt);
			
			Match entry = null;
			for(int i = 0; i < ordersPicked.size(); i++)
			{
				System.out.println(ordersPicked.get(i).getText());
				if (ordersPicked.get(i).getText().contains(orderRef.substring(2)))
				{
					entry = ordersPicked.get(i);
					break;
				}
			}
			if (entry == null)
			{
				System.out.println("Ordine non trovato");
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
				System.out.println(" Searching for article '" + articleRef + "' at instance " + articlesPicked.get(articleRef));

				int instance = 1;
				for(Match match : resources)
				{
					System.out.println("Comparing '" + match.getText() + 
									   "' to '"  + item.getArticle() + "' (instance " + instance + ")");				
					if (match.getText().trim().compareTo(articleRef) == 0)
					{
						if (articlesPicked.get(articleRef) == instance)
						{
							System.out.println(" found '" + match.getText() + "' At " + 
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
			System.out.println("\n\n********     *****************");
			
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
					System.out.println("Comparing '" + match.getText() + "' to '"  + item.getArticle() + "'");
					if (match.getText().compareTo(item.getArticle()) == 0)
					{						
						if (articlesPicked.get(item.getArticle()) == instance)
						{
							System.out.println(" found '" + match.getText() + "' At " + 
									   match.getX() + ", " + match.getY() + 
									   " - len " + match.getW() + " width " + match.getH());
							rItem = new Region(resourcesRegion.getX() + 140, resourcesRegion.getY() + match.getY(), 150, 18);
							String wh = OCR.readWord(rItem);
							if (wh.compareTo("NLIT05") != 0)
							{
								rItem.click();
								rItem.type(Key.DELETE);
								Utils.pauseExecution(200);
								rItem.type("NLIT05");
								rItem.type(Key.ENTER);
							}
							
							rItem = new Region(rItem.getX()+155, rItem.getY(), 100, 18);
							rItem.click();
							rItem.type(Key.DELETE);
							Utils.pauseExecution(200);
							rItem.type(item.getX());
							rItem.type(Key.ENTER);
							
							rItem = new Region(rItem.getX()+100, rItem.getY(), 70, 18);
							rItem.click();
							rItem.type(Key.DELETE);
							Utils.pauseExecution(200);
							rItem.type(item.getY());
							rItem.type(Key.ENTER);
		
							rItem = new Region(rItem.getX()+70, rItem.getY(), 45, 18);
							rItem.click();
							rItem.type(Key.DELETE);
							Utils.pauseExecution(200);
							rItem.type(item.getZ());
							rItem.type(Key.ENTER);
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
			System.out.println("Words found in the DTV field");
			DTVName = "";
			for(Match match : OCR.readWords(rItem, textOpt))
			{
				System.out.println("'" + match.getText() + "' At " + 
						   match.getX() + ", " + match.getY() + 
						   " - len " + match.getW() + " width " + match.getH());
				DTVName += match.getText().toUpperCase();
			}
			System.out.println("DTVName " + DTVName);
		}
		catch(Exception e)
		{
			
		}
		return DTVName;
	}
	
	public void saveAndClose() 
	{
		Match match;
		try
		{
			match = menu.find("img/Menu_Functions.png");
			match.click();
			Utils.pauseExecution(500);
			
			rItem = new Region(0, 0, 1920, 250);
			match = rItem.find("img/Menu_Functions_FullInventoryIssue.png");
			if (match == null)
			{
				System.out.println("Can't find Functions_Post image");
			}
			else
			{
				if (!parms.testRun)
				{
					match.click();
				}
			}

			Utils.pauseExecution(10000);
			match  = rItem.find("img/X_CloseForm.png");
			if (match == null)
			{
				System.out.println("Can't find window close button");
			}
			else
			{
				System.out.println("found closeform at " + 
					match.getX() + ", " + match.getY() + 
					" - len " + match.getW() + " width " + match.getH());
			}
			formHeader.click("img/X_CloseForm.png");
			menu.click("img/Inventory_GoodsIssueButton.png");	
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
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
					System.out.println("Infor non è in esecuzione");
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
			
			saveAndClose();
		
			return DTVName;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return "";
	}
}
