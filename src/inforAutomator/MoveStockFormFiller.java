package inforAutomator;

import java.util.ArrayList;
import java.util.List;

import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class MoveStockFormFiller {
	StockMove sm;
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

	public MoveStockFormFiller(StockMove sm)
	{
		this.sm = sm;
	}

	public String enterData()
	{
		Screen s = new Screen(0);
		Match m = null;
		Region r = null;
		String DTVName = null;

		try{
			s.highlight(2);
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return DTVName;
	}
}
