package main.java.it.l_soft.InforAutomator;

import java.util.List;

import org.sikuli.script.Match;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;

public class Utils {
	Parameters parms;
	
	static public void pauseExecution(long mills)
	{
		try {
			Thread.sleep(mills);
		}
		catch(Exception e)
		{
			
		}
	}
	

	static public void highlightSelection(Parameters parms, Object sikuliObj, int duration)
	{
		if (parms.highlight)
		{
			String className = sikuliObj.getClass().getName();
			className = className.substring(className.lastIndexOf('.') + 1);
			switch(className)
			{
			case "Region":
				((Region) sikuliObj).highlight(duration);
				break;
			case "Match":
				((Match) sikuliObj).highlight(duration);
				break;
			}
		}
	}
	
    static public int byteArrayToInt(byte[] bytes) 
    {
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | bytes[3] & 0xFF;
    }

    static public byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >> 24);
        bytes[1] = (byte) (value >> 16);
        bytes[2] = (byte) (value >> 8);
        bytes[3] = (byte) value;
        return bytes;
    }
    
	public static String[] readTextEntries(Region r, OCR.Options textOpt)
	{
		List<Match> rItems = OCR.readLines(r, textOpt);
		String[] rEntries = new String[rItems.size()];
		int i = 0;
		for(Match m : rItems)
		{
			rEntries[i++] = m.getText();				
		}
		
		return rEntries;
	}
    
	public static boolean shownAmongRegionEntries(String lookFor, Region r, OCR.Options textOpt) {
		String[] menuEntries = readTextEntries(r, textOpt);
		
		for(String menuItem : menuEntries)
		{
			if (menuItem.startsWith("(") && (menuItem.length() > 4))
			{
				menuItem = menuItem.substring(4);
			}
			if (menuItem.startsWith(">") && (menuItem.length() > 2))
			{
				menuItem = menuItem.substring(2);
			}
			if (menuItem.startsWith(lookFor))
			{
				System.out.println("Found on '" + menuItem + "'");
				return true;
			}
		}
		return false;
	}


}
