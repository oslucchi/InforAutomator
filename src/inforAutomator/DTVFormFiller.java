package inforAutomator;

import java.util.ArrayList;
import java.util.List;

import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class DTVFormFiller {	

	ArrayList<Picking> pickList = null;
	String orderRef = null;

	private Match getPosition(List<Match> matchList, String article)
	{
		for(Match item: matchList)
		{
			if (item.getText().contains(article))
			{
				System.out.println(item.getText() + "found");
				return item;
			}
		}
		return null;
	}

	public DTVFormFiller(ArrayList<Picking> pickList, String orderRef)
	{
		this.pickList = pickList;
		this.orderRef = orderRef;
	}

	public String enterData()
	{
		Screen s = new Screen(0);
		Match m = null;
		Region r = null;
		String DTVName = null;
		ImagePath.add(System.getProperty("user.dir"));
		
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
			if ((m = s.exists("img/InventoryMenuOpened.png")) == null)
			{
				s.click("img/InventoryButton.png");
			}
			s.wait("img/InventoryMenuOpened.png");
			s.click("img/Inventory_GoodsIssueButton.png");
			s.click("img/Inventory_GoodsIssue_SalesIssue.png");
			s.wait("img/Inventory_GoodsIssue_SalesIssue_Form.png");
			s.click("img/Inventory_GoodsIssue_SalesIssue_PickListCheck.png");
			m = s.find("img/Inventory_GoodsIssue_SalesIssue_OrderNoCombo.png");
			r = new Region(520,164,265,26);
//			r.highlight(1);
			System.out.println("Found order no combobox");
			r.click("img/ComboArrowDown.png");
			Thread.sleep(500);
			r = new Region(520, 200, 600, 400);
			@SuppressWarnings("deprecation")
			List<Match> matchList = r.collectLines();
			for(Match item : matchList)
			{
				System.out.println(item.getText());
			}
			m = getPosition(matchList, orderRef);
			m.click();
			s.click("img/Inventory_GoodsIssue_LoadButton.png");
			s.wait("img/Inventory_GoodsIssue_SalesIssue_InputFormReady.png",10000);

			m = s.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_Resource.png");
			System.out.println(m.getX() + " " + m.getY());
			r = new Region(m.getX(), m.getY() + 18, 100, 300);
//			r.highlight(1);
			matchList = r.collectLines();
			for(Picking item : pickList)
			{
				System.out.println("Searching for " + item.getArticle() + " in region");
				if ((m = getPosition(matchList, item.getArticle())) != null)
				{
					System.out.println("At " + m.getX() + ", " + m.getY() + " - len " + m.getW() + " width " + m.getH());
					r = new Region(m.getX() + 115, m.getY(), 20, 18);
					r.click();
					r.type("1" + Key.ENTER);
				}
			}


			s.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTab.png");
			s.wait("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTabReady.png");
			m = s.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_Resource.png");
			Region art = new Region(m.getX(), m.getY() + 18, 110, 2500);
//			r.highlight(1);
			System.out.println("\n\n********     *****************");
			matchList = art.collectLines();
			for(Match item: matchList)
			{
				System.out.println(item.getText() + " at " + item.getX() + ", " + item.getY() + " - len " + item.getW() + " width " + item.getH());
			}

			for(Picking item : pickList)
			{
				System.out.println("Searching for " + item.getArticle() + " in textlist");
				if ((m = getPosition(matchList, item.getArticle())) != null)
				{
					System.out.println("At " + m.getX() + ", " + m.getY() + " - len " + m.getW() + " width " + m.getH());
					
					r = new Region(m.getX()+112, m.getY(), 80, 18);
//					r.highlight(1);
					String wh = r.textLines().get(0);
					if ((wh.compareTo("NLIT05") != 0) && (wh.compareTo("NLITOS") != 0))
					{
						r.click();
						r.type("NLIT05");
					}
					r = new Region(r.getX()+153, r.getY(), 80, 18);
//					r.highlight(1);
					r.click();
					r.type(item.getX() + Key.ENTER);
					r = new Region(r.getX() + 105, r.getY(), 50, 18);
//					r.highlight(1);
					r.click();
					r.type(item.getY() + Key.ENTER);
					r = new Region(r.getX() + 65, r.getY(), 50, 18);
//					r.highlight(1);
					r.click();
					r.type(item.getZ());
				}
			}
			s.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_InventoryTab.png");
			Thread.sleep(500);
			
			m = s.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png");
			r = new Region(m.getX() + 250, m.getY(), 200, 18);
			DTVName = r.textLines().get(0);
			DTVName = DTVName.substring(DTVName.indexOf("DTV") + 8);
			System.out.println("DTVName " + DTVName);
			s.click("img/Inventory_GoodsIssue_CloseOption.png");
//			Thread.sleep(200);
//			s.click("img/MainMenu_Save.png");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return DTVName;
	}
}
