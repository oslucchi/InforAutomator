package main.java.it.l_soft.InforAutomator;

import java.util.List;

import javax.json.JsonObject;

import org.sikuli.script.KeyModifier;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class MoveStockFormFiller extends InforFunctions {
//	private Match getPosition(List<Match> matchList, String article)
//	{
//		for(Match item: matchList)
//		{
//			if (item.getText().contains(article))
//			{
//				System.out.println(item.getText() + "found");
//				return item;
//			}
//		}
//		return null;
//	}

	public MoveStockFormFiller(JsonObject sm)
	{
		this.sm = sm;
	}

	@Override
	public String enterData()
	{
		Screen s = new Screen(0);
		Match m = null;
		Region r = null, menu = new Region(1, 14, 339, 28);

		try{
			if ((m = s.exists("img/InforLogo.png")) == null)
			{
				if ((m = s.exists("img/InforIcon.png")) == null)
				{
					System.out.println("Infor non è in esecuzione");
					System.exit(-1);
				}
				m.click();
			}
			s.wait("img/InforLogo.png");
			r = new Region(0, 0, 220, 1030);
			
			if ((m = r.exists("img/InventoryMenuOpened.png")) == null)
			{
				r.click("img/InventoryButton.png");
			}
			r.wait("img/InventoryMenuOpened.png");
			r.click("img/Inventory_Information.png");
			r.click("img/Inventory_Information_InventoryInformation.png");
			r = new Region(800,100,500,130);
			m = r.wait("img/Inventory_Information_InventoryInformation_Form.png");
			int rFrom = 1100, rTo = 1380;
			String locationFrom[] = sm.getString("locationFrom").split("-");
			String locationTo[] = sm.getString("locationTo").split("-");
			for(int i = 0; i < 4; i++)
			{
				r = new Region(rFrom,120 + i * 23, 190, 20);
				r.highlight(1);
				r.click();
				r.type(locationFrom[i]);
				r = new Region(rTo,120 + i * 23, 190, 20);
				r.highlight(1);
				r.click();
				r.type(locationFrom[i]);
			}
			
			s.click("img/Inventory_Information_InventoryInformation_Load.png");
			r = new Region(277, 291, 127, 16);
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

			List<Match> matchList = r.collectLines();
			System.out.println("using collectLines...");
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
			m = s.wait("img/OKConfirm.png");
			m.click();
			s.wait("img/GoTo_Transfer_Completed.png");
			s.click("img/OKButton.png");
			s.wait("img/GoTo_Transfer_Completed_FileReloaded.png");
			s.click("img/OKButton.png");
			r = new Region(1850,0,70,27);
			r.click("img/X_CloseForm.png");
			r = new Region(0, 0, 220, 1030);
			r.click("img/Inventory_Information.png");
		}
		catch(Exception e){
			e.printStackTrace();
			return "KO";
		}
		
		return "OK";
	}
}
