package main.java.it.l_soft.InforAutomator;

import javax.json.JsonObject;

import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class MoveStockFormFiller extends InforFunctions {
	OCR.Options textOpt;
	Parameters parms;
	Screen s = new Screen(1);
	Match mItem = null;
	Region menu, formHeader, appBody, rItem, toolBar, r;

	public MoveStockFormFiller(JsonObject sm, Parameters parms)
	{
		this.sm = sm;
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
		textOpt = OCR.globalOptions().fontSize(12);
	}
	
	private void getInventoryInformationFeatureOn()
	{
		try{
			if (!Utils.shownAmongRegionEntries("Information", menu, textOpt))
			{
				menu.click("img/InventoryButton.png");
				Utils.pauseExecution(150);
			}
			if (!Utils.shownAmongRegionEntries("Inventory information", menu, textOpt))
			{
				menu.click("img/Inventory_Information.png");
			}
			menu.click("img/Inventory_Information_InventoryInformation.png");
		}
		catch(Exception e)
		{
			
		}
	}

	@Override
	public String enterData()
	{
		String imgPath = System.getProperty("user.dir");
		ImagePath.add(imgPath);
		
		try{
			if ((mItem = toolBar.exists("img/InforIcon.png")) == null)
			{
				System.out.println("Infor non è in esecuzione");
				System.exit(-1);
			}
			mItem.click();
			menu.wait("img/InforLogo.png");
			
			getInventoryInformationFeatureOn();
			
			mItem = formHeader.wait("img/Inventory_Information_InventoryInformation_Form.png");
			mItem = formHeader.find("img/Inventory_Information_InventoryArea.png");

			
			String locationFrom[] = sm.getString("locationFrom").split("-");
//			String locationTo[] = sm.getString("locationTo").split("-");

			for(int i = 0; i < 4; i++)
			{
				rItem = new Region(mItem.getX() + 251, mItem.getY() + 23 * i, 200, 23);
				Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
				Utils.pauseExecution(100);
				rItem.click();
				rItem.type(locationFrom[i]);
				rItem.type(Key.TAB);
				Utils.pauseExecution(100);
				rItem.type(locationFrom[i]);
				rItem.type(Key.DOWN);
				Utils.pauseExecution(150);
			}
			formHeader.click("img/Inventory_Information_InventoryInformation_Load.png");
			System.out.println("Searching fora article '" + sm.getString("article") + "' in appBody");
			mItem = appBody.find("img/1.png"); // new Region(277, 291, 127, 16);
			rItem = new Region(appBody.getX() + mItem.getX() +10, mItem.getY(), 50, 17);
			Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
/*
			r.click("img/ExpandSection.png");
			r = new Region(292, 310, 168, 17);
			r.click("img/ExpandSection.png");
			r = new Region(979, 328, 89, 240);
			List<String> rText = r.textLines();
			System.out.println("Using text...");
			for(String item : rText)
			{
				System.out.println(item);
			}

			List<Match> matchList = OCR.readLines(r, textOpt);
			for(String item : rText)
			{
				System.out.println(item);
			}
			double quantity = 0;
			double requiredQuantity = (double) sm.getInt("quantity");
			r = new Region(235, 320, 40, 200);
			r.highlight(1);
			int rowCount = 3;
			for(Match item : matchList)
			{
				System.out.println("Line " + rowCount + " quantity " + item.getText());
				quantity += Double.parseDouble(item.getText().replaceAll(",",  "."));
				if (rowCount == 3)
				{
					r.click("img/" + rowCount++ + ".png");
				}
				else
				{
					r.click("img/" + rowCount++ + ".png", KeyModifier.CTRL);
				}
				if (quantity >= requiredQuantity)
					break;
			}
			r = new Region(181, 21, 182, 293);
			menu.click("img/GoTo.png");
			r.click("img/GoTo_Transfer.png");
			r = new Region(232, 77, 260, 88);
			r.wait("img/GoTo_Transfer_Form.png");
			
			
			// now move quantities
			Region srcQtyRows = new Region(245, 160, 33, 181);
			Region quantityAvailble = new Region(687, 140, 124, 17);
			Region dstQtyRow[] = {
					new Region(277, 420, 194,20),
					new Region(471, 420, 123,22),
					new Region(594, 420, 98,22),
					new Region(691, 420, 100,19),
					new Region(1170, 420, 153,18),
			};
			quantity = 0;
			rowCount = 2;
			while(quantity < requiredQuantity)
			{
				quantityAvailble.highlight(1);
				double qtyToWrite = Double.parseDouble(quantityAvailble.text().replaceAll(",", "."));
				System.out.println("Qty to write " + qtyToWrite);
				
				if (qtyToWrite > requiredQuantity - quantity)
				{
					qtyToWrite = requiredQuantity - quantity;
				}
				quantity += qtyToWrite;
				dstQtyRow[0].click();
				dstQtyRow[0].type(locationTo[0]);
				dstQtyRow[1].click();
				dstQtyRow[1].type(locationTo[1]);
				dstQtyRow[2].click();
				dstQtyRow[2].type(locationTo[2]);
				dstQtyRow[3].click();
				dstQtyRow[3].type(locationTo[3]);
				dstQtyRow[4].click();
				dstQtyRow[4].type(String.valueOf(qtyToWrite).replaceAll("\\.", ","));
				if (quantity < requiredQuantity) 
				{
					quantityAvailble = quantityAvailble.offset(0, 20);
					srcQtyRows.click("img/" + rowCount++ + ".png");
				}
			}
			r = new Region(122, 21, 151, 115);
			menu.click("img/Menu_Functions.png");
			r.click("img/Functions_Post.png");
			mItem= s.wait("img/OKConfirm.png");
			mItem.click();
			s.wait("img/GoTo_Transfer_Completed.png");
			s.click("img/OKButton.png");
			s.wait("img/GoTo_Transfer_Completed_FileReloaded.png");
			s.click("img/OKButton.png");
			r = new Region(1850,0,70,27);
			r.click("img/X_CloseForm.png");
			r = new Region(0, 0, 220, 1030);
			r.click("img/Inventory_Information.png");
*/
		}
		catch(Exception e){
			e.printStackTrace();
			return "KO";
		}
		
		return "OK";
	}
}
