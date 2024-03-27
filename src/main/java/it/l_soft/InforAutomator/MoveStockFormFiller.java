package main.java.it.l_soft.InforAutomator;

import java.util.List;

import javax.json.JsonObject;

import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
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
//		Utils.highlightSelection(parms, menu, Parameters.HIGHLIGHT_DURATION);
		toolBar = new Region(Parameters.TOOLBAR_OFFSET_X, Parameters.TOOLBAR_OFFSET_Y, 
							 1920 - Parameters.TOOLBAR_OFFSET_X, 1080 - Parameters.TOOLBAR_OFFSET_Y);
//		Utils.highlightSelection(parms, toolBar, Parameters.HIGHLIGHT_DURATION);
		formHeader = new Region(Parameters.FORM_HEADER_OFFSET_X, Parameters.FORM_HEADER_OFFSET_Y, 
								1920 - Parameters.FORM_HEADER_OFFSET_X, 250);
//		Utils.highlightSelection(parms, formHeader, Parameters.HIGHLIGHT_DURATION);
		appBody = new Region(Parameters.APPBODY_OFFSET_X, Parameters.APPBODY_OFFSET_Y, 
							 1920 - Parameters.APPBODY_OFFSET_X, 1080 - Parameters.APPBODY_OFFSET_Y);
//		Utils.highlightSelection(parms, appBody, Parameters.HIGHLIGHT_DURATION);
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

			System.out.println("InventoryArea found at " + mItem.getX() + "-" + mItem.getY());
			
			String locationFrom[] = sm.getString("locationFrom").split("-");
			String locationTo[] = sm.getString("locationTo").split("-");

			for(int i = 0; i < 4; i++)
			{
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
			appBody.wait("img/Inventory_MoveStock_Overview.png");
			Utils.pauseExecution(250);
			System.out.println("Searching for article '" + sm.getString("article") + "' in appBody");
			
			rItem = new Region(appBody.getX(), appBody.getY(), 200, 200);

			Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
			mItem =rItem.find("img/ExpandSection.png");
			mItem.click();
			Utils.pauseExecution(250);
			rItem = new Region(mItem.getX(), mItem.getY() + 20, rItem.getW(), 50);
			Utils.highlightSelection(parms, rItem, Parameters.HIGHLIGHT_DURATION);
			mItem = rItem.find("img/ExpandSection.png");
			mItem.click();

			double[] quantity = new double[5];
			double totQuantity = 0;
			double requiredQuantity = (double) sm.getInt("quantity");
			int rowCount = 3;

			Match dummy = appBody.find("img/Inventory_Information_AvailableQuantity.png");
			rItem = new Region(dummy.getX() + 50, mItem.getY() + 20, 140, 150);
			Utils.highlightSelection(parms, rItem, 3);
			System.out.println("Text in region '" + rItem.text());
			List<Match> matches = OCR.readLines(rItem, textOpt);
			
			System.out.println("Found " + matches.size() + " text segment in region");
			
			for(Match m: matches)
			{
				System.out.println("Line " + rowCount + " quantity " + m.getText());
				totQuantity += quantity[rowCount - 3] = Double.parseDouble(m.getText().replaceAll(",",  "."));
				if (rowCount == 3)
				{
					appBody.click("img/" + rowCount++ + ".png");
				}
				else
				{
					appBody.click("img/" + rowCount++ + ".png", KeyModifier.CTRL);
				}
				if (totQuantity >= requiredQuantity)
					break;
			}
			System.out.println("Quantity of " + requiredQuantity + " is " + (totQuantity < requiredQuantity ? "not " : "") + "possible");
			if (totQuantity < requiredQuantity)
			{
				return "KO";
			}
			
			mItem = menu.find("img/GoTo.png");
			mItem.click();
			rItem = new Region(mItem.getX(), mItem.getY() + 15, 200, 300);
			rItem.click("img/GoTo_Transfer.png");
			formHeader.wait("img/GoTo_Transfer_Form.png");
			
			int currentRow = 0;
			mItem = appBody.find("img/1.png");
			while(requiredQuantity > 0)
			{
				System.out.println("Quantity left to move " + requiredQuantity);
				formHeader.click("img/" + (currentRow + 1) + ".png");
				rItem = new Region(mItem.getX() + 20, mItem.getY(), 100, 20);
				rItem.click();
				Utils.pauseExecution(150);
				rItem.type(locationTo[0]);
				rItem.type(Key.TAB);
				Utils.pauseExecution(150);
				rItem.type(locationTo[1]);
				rItem.type(Key.TAB);
				Utils.pauseExecution(150);
				rItem.type(locationTo[2]);
				rItem.type(Key.TAB);
				Utils.pauseExecution(150);
				rItem.type(locationTo[3]);
				rItem.type(Key.TAB);
				Utils.pauseExecution(150);
				String strQty = String.valueOf(requiredQuantity < quantity[currentRow] ? (int) requiredQuantity : (int) quantity[currentRow]);
				System.out.println("moving " + strQty + " to location " + locationTo[1] + "-" + locationTo[2] + "-" + locationTo[3]);
				rItem.type(strQty);
				requiredQuantity -= quantity[currentRow];
			}

			/*
			mItem = menu.find("img/Menu_Functions.png");
			mItem.click();
			rItem = new Region(mItem.getX(), mItem.getY() + 15, 200, 300);
			rItem.click("img/Functions_Post.png");
			mItem= s.wait("img/OKConfirm.png");
			mItem.click();

			s.wait("img/GoTo_Transfer_Completed.png");
			s.click("img/OKButton.png");
			s.wait("img/GoTo_Transfer_Completed_FileReloaded.png");
			s.click("img/OKButton.png");

			formHeader.click("img/X_CloseForm.png");
			Utils.pauseExecution(300);
			menu.click("img/Inventory_Information.png");
			*/
		}
		catch(Exception e){
			e.printStackTrace();
			return "KO";
		}
		
		return "OK";
	}
}
