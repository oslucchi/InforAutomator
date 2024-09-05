package main.java.it.l_soft.InforAutomator;

import java.util.ArrayList;

import javax.json.JsonObject;

import org.apache.log4j.Logger;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Mouse;
import org.sikuli.script.Region;
import org.sikuli.script.Sikulix;

public class MoveStockFormFiller extends InforFunctions {
	private final Logger log = Logger.getLogger(this.getClass());
	
	Parameters parms;
	Match mItem = null;
	double movedQUantity = 0;
	Region rItem, r;
	
	public MoveStockFormFiller(JsonObject sm, Parameters parms)
	{
		this.sm = sm;
		this.parms = parms;
	}
	
	private void closeAndResetMenu()
	{
		if (!parms.closeFunctionAtEnd)
		{
			log.debug("NOT CLOSING THE WINDOW because closeFunctionAtEnd is false!!!");
			return;
		}
		
		Match m;
		try
		{
			parms.formHeader.click("img/Main_Window_Close.png");
			Utils.pauseExecution(2000);
			log.debug("Post changes ? " + parms.postChanges);
			if (!parms.postChanges)
			{
				log.debug("NOT POSTING CHANGES since postChanges var is false!!!");
				m = parms.screen.exists("img/NOButton.png");
				log.debug("No button on parms.screen ? " + (m == null ? "no" : "yes"));
				if (m != null) m.click();
			}
			else
			{
				parms.screen.type(Key.ENTER);
				Utils.pauseExecution(250);
				parms.screen.type(Key.ENTER);
				Utils.pauseExecution(3000);
				parms.screen.type(Key.ENTER);
			}
			Utils.pauseExecution(1000);

			parms.menu.click("img/Inventory_Movements.png");
			Utils.pauseExecution(500);
			parms.menu.click("img/Sales_Button.png");
		}
		catch(Exception e)
		{
			;
		}
	}
		
	private boolean getInventoryMovementsFeatureOn()
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

		log.debug("Check if 'Transfer' is already exposed on parms.menu");
		
		if ((m = Utils.findTextInRegion("Transfer", parms.menu, parms.textOpt)) == null)
		{
			log.debug("Transfer option is not exposed, expand the Inventory Movements option");
			try {
				parms.menu.click("img/Inventory_Movements.png");
				Utils.pauseExecution(500);
			}
			catch(Exception e)
			{
				log.error("Can't opened the Inventory movements option. Aborting function");
				return false;
			}
		}
		log.trace("The Inventory Movements option is exposed");
		Mouse.move(50, 0);

		if ((m = Utils.findTextInRegion("Transfer", parms.menu, parms.textOpt)) == null)
		{
			log.error("Can't see the Transfer option. Aborting function");
			return false;
		}
		
		Location l = new Location(parms.xOffset+m.getX() + 10, parms.yOffset+m.getY()+5); 
		Mouse.move(l);
		log.debug("Click on Inventory Movements Trasfer button at " + Mouse.at().toString());
		Mouse.click(l, "L", 1);
		Utils.pauseExecution(500);
		return true;
	}
	
	private boolean getLocationContent()
	{
		try {
			log.debug("Wait for the form to appear");

			mItem = parms.formHeader.exists("img/Inventory_Information_InventoryInformation_Form.png", 10);

			log.debug("Entering article '" + sm.getString("article"));
			parms.formHeader.type(sm.getString("article"));
			for(int i = 0; i < 6; i++)
			{
				parms.formHeader.type(Key.TAB);
				Utils.pauseExecution(100);
			}
			Region test = new Region(Mouse.at().getX(), Mouse.at().getY(), 100, 20);
			test.highlight(parms.HIGHLIGHT_DURATION);
			
			Utils.pauseExecution(2000);

			log.debug("Entering location to filter");
			String locationFrom[] = sm.getString("locationFrom").split("-");
			log.debug("Locations array is [" + locationFrom + "]");
			
			for(int i = 0; i < 4; i++)
			{
				parms.formHeader.type(locationFrom[i]);
				parms.formHeader.type(Key.TAB);
				Utils.pauseExecution(100);					
				parms.formHeader.type(locationFrom[i]);
				parms.formHeader.type(Key.TAB);
				Utils.pauseExecution(100);					
			}
			parms.formHeader.type(Key.ENTER);

			
			rItem = parms.appBody.exists("img/No_Data_Record_Found.png", 2);
			if (rItem != null)
			{
				parms.appBody.type(Key.ENTER);
				Utils.pauseExecution(500);
				closeAndResetMenu();
				return false;
			}
			mItem = parms.appBody.exists("img/Inventory_Movements_Movable_Quantity.png", 10);
		}
		catch(Exception e)
		{
			log.error("Exception while getting location content", e);
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

			Region rDest = parms.appBody.find("img/Inventory_Movements_Destination_Area.png");
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
		}
		catch(Exception e)
		{
			log.error("Exception during transfer destination data entry", e);
			return false;
		}
		return true;
	}
	
	private double moveQuantityAcrossLocations(double requiredQuantity, boolean wholeQuantity) 
			throws Exception 
	{
//		parms.highlight = true;
		Region movQty = new Region(mItem.getX() + 50, mItem.getY() + 15, 78, 200);
		Utils.highlightSelection(parms, movQty, Parameters.HIGHLIGHT_DURATION);
		
		// get The available quantities to find the best suiting row
		ArrayList<String> lines = Utils.runTesseractOnRegion(movQty, ".,0123456789", true);
		log.debug("There are " + lines.size() + " lines for location containing items (" + lines.toString());
		
		Location l = new Location(movQty.getX(), movQty.getY() + 5);
		double quantityInLocation;
		int run = 0;
		while(true)
		{
			if (run >= lines.size())
			{
				break;
			}
			Mouse.click(l, "L", 1);
			parms.screen.type("a", KeyModifier.CTRL);
			parms.screen.type("c", KeyModifier.CTRL);
			parms.screen.type(Key.TAB);
			log.debug("Trying to convert '" + Utils.getClipboardContents() + "' in number");
			try {
				quantityInLocation = Utils.parseNumberInLocale(Utils.getClipboardContents()).doubleValue();

				if (wholeQuantity)
				{
					if (quantityInLocation >= requiredQuantity)
					{
						log.trace("Required to move the entire quantity. Enough found");
						
						// the inspected row has quantity enough for use to move, use it
						// Get the region where the row number is and click on it
						rItem = new Region(parms.appBody.getX()+20, movQty.getY() + Mouse.at().getY(), 50, 23);
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
					rItem = new Region(parms.appBody.getX()+ 20, Mouse.at().getY()-5, 50, 23);
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
				l = new Location(l.getX(), l.getY() + 20);
				run++;
			}
			catch(NumberFormatException e)
			{
				log.debug("A row was supposed to have a valid quantity but it hasn't");
				throw new Exception(e.getMessage());
			}
		}
//		parms.highlight = true;
		return requiredQuantity;
	}
	
	private void moveQuantityToDestination() throws Exception
	{
	
		double requiredQuantity = (double) sm.getInt("quantity");
		try
		{
			requiredQuantity = moveQuantityAcrossLocations(requiredQuantity, true);
			if (requiredQuantity > 0)
				moveQuantityAcrossLocations(requiredQuantity, false);
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

			if (!getInventoryMovementsFeatureOn()) return "KO";
			if (!getLocationContent()) return "KO";

			if (mItem == null)
			{
				Sikulix.popup("Non ho trovato l'articolo '" +
							  sm.getString("article") + 
							  "' nella locazione indicata '" +
							  sm.getString("locationFrom") + "'");
				return "KO";
			}

			moveQuantityToDestination();
			closeAndResetMenu();
		}
		catch(Exception e){
			e.printStackTrace();
			return "KO";
		}
		
		return "OK";
	}
}
