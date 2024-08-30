package main.java.it.l_soft.InforAutomator;

import java.util.List;

import javax.json.JsonObject;

import org.apache.log4j.Logger;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Mouse;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.Sikulix;

public class MoveStockFormFiller extends InforFunctions {
	private final Logger log = Logger.getLogger(this.getClass());
	
	OCR.Options textOpt;
	Parameters parms;
	Match mItem = null;
	Region screen, menu, formHeader, appBody, rItem, toolBar, r;
	double movedQUantity = 0;
	int xOffset, yOffset, screenH, screenW;

	public MoveStockFormFiller(JsonObject sm, Parameters parms)
	{
		this.sm = sm;
		this.parms = parms;

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
	
	private boolean getInventoryMovementsFeatureOn()
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

		log.debug("Check if 'Transfer' is already exposed on menu");
		
		if ((m = Utils.findTextInRegion("Transfer", menu, textOpt)) == null)
		{
			log.debug("Transfer option is not exposed, expand the Inventory Movements option");
			try {
				Utils.pauseExecution(1000);
				menu.click("img/Inventory_Movements.png");
			}
			catch(Exception e)
			{
				log.error("Can't opened the Inventory movements option. Aborting function");
				return false;
			}
		}
		log.trace("The Inventory Movements option is exposed");
		Mouse.move(20, 20);
		Utils.pauseExecution(1000);

		if ((m = Utils.findTextInRegion("Transfer", menu, textOpt)) == null)
		{
			log.error("Can't see the Transfer option. Aborting function");
			return false;
		}
		
		log.debug("Click on Inventory Movements Trasfer button");
		Mouse.move(new Location(m.getX() + 10, m.getY() + 5));
		m.click();
		return true;
	}
	
	private void closeAndResetMenu()
	{
		Match m;
		try
		{
			formHeader.click("img/Main_Window_Close.png");
			Utils.pauseExecution(2000);
			log.debug("Post changes ? " + parms.postChanges);
			if (!parms.postChanges)
			{
				m = screen.exists("img/NOButton.png");
				log.debug("No button on screen ? " + (m == null ? "no" : "yes"));
				if (m != null) m.click();
			}
			else
			{
				screen.type(Key.ENTER);
				Utils.pauseExecution(250);
				screen.type(Key.ENTER);
				Utils.pauseExecution(3000);
				screen.type(Key.ENTER);
			}
			Utils.pauseExecution(1000);

			menu.click("img/Inventory_Movements.png");
			Utils.pauseExecution(500);
			menu.click("img/Sales_Button.png");
		}
		catch(Exception e)
		{
			;
		}
	}
	
	private boolean getLocationContent()
	{
		try {
			log.debug("Wait for the form to appear");

			mItem = formHeader.exists("img/Inventory_Information_InventoryInformation_Form.png", 10);
			
			log.debug("Entering data");
			
			mItem = formHeader.find("img/Inventory_Movements_ItemID.png");
			log.debug("ItemID found at " + mItem.getX() + "-" + mItem.getY());
			rItem = new Region(mItem.getX() + 300, mItem.getY()-3, 200, 23);
			log.debug("Entering article '" + sm.getString("article"));
			rItem.type(sm.getString("article"));
			
			log.debug("search for area to input location");
			mItem = formHeader.find("img/Inventory_Information_InventoryInformation_Form.png");
			String locationFrom[] = sm.getString("locationFrom").split("-");
			for(int i = 0; i < 4; i++)
			{
				log.debug("entering data in location at " + 
						  (mItem.getX() + 251) + "," + mItem.getY());
				rItem = new Region(mItem.getX() + 251, mItem.getY() + 23 * i, 200, 23);
				rItem.click();
				Utils.pauseExecution(200);
				rItem.type(locationFrom[i]);
				Utils.pauseExecution(300);
				rItem = new Region(mItem.getX() + 545, mItem.getY() + 23 * i, 200, 23);
				rItem.click();
				Utils.pauseExecution(200);
				rItem.type(locationFrom[i]);
				Utils.pauseExecution(300);
			}
			formHeader.click("img/Inventory_Information_InventoryInformation_Load.png");
			
			rItem = appBody.exists("img/No_Data_Record_Found.png", 2);
			if (rItem != null)
			{
				appBody.type(Key.ENTER);
				Utils.pauseExecution(500);
				closeAndResetMenu();
				return false;
			}
			appBody.wait("img/Inventory_Movements_BacTracked_Inventory.png");
			rItem = appBody.find("img/Inventory_Movements_Movable_Quantity.png");
		}
		catch(Exception e)
		{
			log.error("Exception while getting location content");
			return false;
		}
		return true;
	}
	
	
	public void postData()
	{
	}

	
	private boolean doTheTrasfer(double requiredQuantity, double quantityInLocation)
	{
		try {
			String locationTo[] = sm.getString("locationTo").split("-");

			Region rDest = appBody.find("img/Inventory_Movements_Destination_Area.png");
			rDest = rDest.below(50);
			rDest.click();
			Utils.pauseExecution(200);
			rDest.type(locationTo[0]);
			rDest.type(Key.ENTER);
			Utils.pauseExecution(300);
			
			rDest.type(locationTo[1]);
			rDest.type(Key.ENTER);
			Utils.pauseExecution(300);

			rDest.type(locationTo[2]);
			rDest.type(Key.ENTER);
			Utils.pauseExecution(300);

			rDest.type(locationTo[3]);
			rDest.type(Key.ENTER);
			Utils.pauseExecution(300);

			String qtyToEnter;
			if (requiredQuantity < quantityInLocation)
			{
				qtyToEnter = String.valueOf((int)requiredQuantity);
			}
			else
			{
				qtyToEnter = String.valueOf((int)quantityInLocation);

			}
			rDest.type(qtyToEnter);
			rDest.type(Key.ENTER);
			Utils.pauseExecution(300);

			if (parms.postChanges)
			{
				postData();
			}
			else
			{
				log.debug("NOT POSTING CHANGES since postChanges var is false!!!");
				Utils.pauseExecution(2000);
			}
		}
		catch(Exception e)
		{
			log.error("Exception during transfer destination data entry", e);
			return false;
		}
		return true;
	}
	
	private double moveQuantityAcrossLocations(Region movQty, double requiredQuantity, boolean wholeQuantity) 
			throws Exception
	{
		if (requiredQuantity == 0)
			return 0;
		
		log.debug("Willing to move " + requiredQuantity);

		// get The available quantities to find the best suiting row
		List<Match> matches = OCR.readLines(movQty, textOpt);
		log.debug("Found " + matches.size() + " quantities in region");
		int row = 0;
		double quantityInLocation;
		for(Match m: matches)
		{
			log.debug("match text is '" + m.getText() + "'");
			if (!m.getText().contains(","))
			{
				log.debug("it is not a quantity");
				continue;
			}
			String quantity = m.getText().substring(0, m.getText().indexOf(','));
			try {
				quantityInLocation = Double.parseDouble(quantity);
			}
			catch(NumberFormatException e)
			{
				log.debug("The quantity field content is malformed");
				continue;
			}
			
			log.debug("Found quantity '" + quantity + "' on row " + row + 
					  " at " + (appBody.getX()+20) + " " + m.getY());
			try {
				
	
				if (wholeQuantity)
				{
					if (quantityInLocation >= requiredQuantity)
					{
						log.trace("Required to move the entire quantity. Enough found");
						
						// the inspected row has quantity enough for use to move, use it
						// Get the region where the row number is and click on it
						rItem = new Region(appBody.getX()+20, movQty.getY() + m.getY(), 50, 23);
						Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
						rItem.click();
						if (doTheTrasfer(requiredQuantity, quantityInLocation))
						{
							requiredQuantity = 0;
						}
						else
						{
							return -1;
						}
					}
					else
					{
						log.trace("whole quantity required but location has not enough (" + 
								  quantityInLocation + " vs " + requiredQuantity);
					}
				}
				else
				{
					rItem = new Region(appBody.getX()+ 20, m.getY()-5, 50, 23);
					rItem.click();
					log.trace("whole quantity not required transfer what is here " + 
							  quantityInLocation);

					if (doTheTrasfer(requiredQuantity, quantityInLocation))
					{
						requiredQuantity -= (quantityInLocation < requiredQuantity ? 
													quantityInLocation : requiredQuantity);
					}
					else
					{
						return -1;
					}
				}
				if (requiredQuantity == 0)
				{
					break;
				}
			}
			catch(NumberFormatException e)
			{
				log.debug("A row was supposed to have a valid quantity but it hasn't");
				throw new Exception(e.getMessage());
			}
		}
		log.debug("Duties performed, returning with quantity " + requiredQuantity);
		return requiredQuantity;
	}
	
	private void moveQuantityToDestination() throws Exception
	{
		Match m = appBody.exists("img/Inventory_Movements_Movable_Quantity.png", 1);
		if (m == null)
		{
			throw new Exception("Colonna quantita' movibile non trovata");
		}
		Region movQty = new Region(m.getX(), m.getY() + 15, 140, 200);
		Utils.highlightSelection(parms, movQty, Parameters.HIGHLIGHT_DURATION);
		
		double requiredQuantity = (double) sm.getInt("quantity");
		try
		{
			requiredQuantity = moveQuantityAcrossLocations(movQty, requiredQuantity, true);
			moveQuantityAcrossLocations(movQty, requiredQuantity, false);
		}
		catch(Exception e)
		{
			throw e;
		}
		log.trace("Required quantity fullfilled");
	}
	


	@Override
	public String enterData()
	{
		String imgPath = System.getProperty("user.dir");
		ImagePath.add(imgPath);
		
		try{
			if ((mItem = screen.exists("img/InforLogo.png")) == null)
			{
				log.debug("La finestra di INFOR non [ aperta, cerco l'icona");

				if ((mItem = toolBar.exists("img/InforIcon.png")) == null)
				{
					log.debug("Icona INFOR non trovat, non è in esecuzione. Esco");
					System.exit(-1);
				}
				mItem.click();
			}
			menu.wait("img/InforLogo.png");
			if (!getInventoryMovementsFeatureOn()) return "KO";
			if (!getLocationContent()) return "KO";

			if (rItem == null)
			{
				Sikulix.popup("Non ho trovato l'articolo '" +
							  sm.getString("article") + 
							  "' nella locazione indicata '" +
							  sm.getString("locationFrom") + "'");
				return "KO";
			}
			moveQuantityToDestination();
			if (parms.postChanges)
			{
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return "KO";
		}
		finally {
			closeAndResetMenu();
		}
		
		return "OK";
	}
}
