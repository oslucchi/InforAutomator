package inforAutomator;

import java.util.ArrayList;
import java.util.List;

import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class FormFiller {	

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

	public FormFiller(ArrayList<Picking> pickList, String orderRef)
	{
		this.pickList = pickList;
		this.orderRef = orderRef;
	}

	public int enterData()
	{
		Screen s = new Screen(0);
		Match m = null;
		Region r = null;
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
			System.out.println("Found order no combobox");
			m.click("img/ComboArrowDown.png");
			Thread.sleep(500);
			r = new Region(m.getX() + 225, m.getY() + 35, 600, 400);
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
			r.highlight(1);
			matchList = r.collectLines();
			for(Picking item : pickList)
			{
				System.out.println("Searching for " + item.getArticle() + " in region");
				if ((m = getPosition(matchList, item.getArticle())) != null)
				{
					System.out.println("At " + m.getX() + ", " + m.getY() + " - len " + m.getW() + " width " + m.getH());
					r = new Region(m.getX() + 115, m.getY() + 2, 20, 15);
					r.click();
					r.type("1" + Key.ENTER);
				}
			}


			s.click("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTab.png");
			Thread.sleep(500);
			m = s.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_CoordinatesTabReady.png");
			Region art = new Region(m.getX() + 110, m.getY() + 18, 110, 2500);
			r.highlight(1);
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
					r = new Region(m.getX()+260, m.getY(), 80, 18);
					r.click();
					r.type(item.getX() + Key.ENTER);
					r = new Region(r.getX() + 100, r.getY(), 50, 18);
					r.click();
					r.type(item.getY() + Key.ENTER);
					r = new Region(r.getX() + 60, r.getY(), 50, 18);
					r.click();
					r.type(item.getZ());
				}
			}
			m = s.find("img/Inventory_GoodsIssue_SalesIssue_InputForm_ASN.png");
			r = new Region(m.getX() + 250, m.getY(), 200, 18);
			String DTVName = r.textLines().get(0);
			DTVName = DTVName.substring(DTVName.indexOf("DTV") + 8);
			System.out.println("DTVName " + DTVName);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return 0;
	}
}