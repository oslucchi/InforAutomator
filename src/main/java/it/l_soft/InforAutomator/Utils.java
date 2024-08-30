package main.java.it.l_soft.InforAutomator;

import java.util.List;

import org.apache.log4j.Logger;
import org.sikuli.script.Match;
import org.sikuli.script.OCR;
import org.sikuli.script.Region;


public class Utils {	
	final static Logger log = Logger.getLogger(Utils.class);
	
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
		if (Parameters.getInstance().highlight)
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
			Utils.pauseExecution(500);
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
    
	public static List<Match> getTextLineEntriesInRegion(Region r, OCR.Options textOpt)
	{
		List<Match> rItems = OCR.readLines(r, textOpt);
//		Match[] rEntries = new Match[rItems.size()];
//		int i = 0;
//		for(Match m : rItems)
//		{
//			Debug.log(String.format("Item at %d,%d len %d '%s'",
//									m.getX(), m.getY(), m.getW(), m.getText()));
//			rEntries[i].text = m.getText();				
//			rEntries[i].x = m.getX();				
//			rEntries[i].y = m.getY();				
//			rEntries[i].w = m.getW();				
//			rEntries[i].h = m.getH();				
//		}
		
		return rItems;
	}
    
	public static Match findTextInRegion(String lookFor, Region r, OCR.Options textOpt) {
		List<Match> matches = OCR.readLines(r, textOpt); 
		log.debug("Found " + matches.size() + " strings in region");
		for(Match menuItem : matches)
		{
			log.debug("Investigating '" + menuItem.getText() + "'");
			if (menuItem.getText().contains(lookFor))
			{
				log.debug("Found the text we were looking for");
				return menuItem;
			}
		}
		return null;
	}


}
