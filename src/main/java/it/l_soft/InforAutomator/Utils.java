package main.java.it.l_soft.InforAutomator;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.sikuli.script.Image;
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
	
	public static Number parseNumberInLocale(String strNumber)
	{
    	Parameters parms = Parameters.getInstance();

        // Get the NumberFormat instance for the specified locale
        NumberFormat numberFormat = NumberFormat.getInstance(parms.appLocale);
        Number number = null;
        // Parse the string into a Number
        try {
			number = numberFormat.parse(strNumber);
			return number;
		} 
        catch (ParseException e) {
        	e.printStackTrace();
		}
        return null;
	}
	
    public static String getClipboardContents() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable content = clipboard.getContents(null);
            if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) content.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static BufferedImage invertColors(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage invertedImage = new BufferedImage(width, height, original.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(original.getRGB(x, y));
                int r = 255 - color.getRed();
                int g = 255 - color.getGreen();
                int b = 255 - color.getBlue();
                Color invertedColor = new Color(r, g, b);
                invertedImage.setRGB(x, y, invertedColor.getRGB());
            }
        }

        return invertedImage;
    }

    public static ArrayList<String> runTesseractOnRegion(Region r, String whitelist, boolean skipEmptyLines)
    {
    	Parameters parms = Parameters.getInstance();

        try {
            Image sikuliImage = r.getImage();
            // Convert the image to black and white
            BufferedImage bImage = invertColors(sikuliImage.get());
            
            File imageFile = new File("./tesseract/captured_region.png");
        	ImageIO.write(bImage, "png", imageFile);
            
            // Output file base name (no extension)
            String outputBaseName = "./tesseract/ocr_output";

            // Command to run Tesseract with custom whitelist
            String command = parms.pathToTesseract + " " + imageFile.getPath() + " " + 
            				 outputBaseName + " -c tessedit_char_whitelist=" + whitelist;

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // Read the output from the .txt file generated by Tesseract
            File outputFile = new File(outputBaseName + ".txt");
            BufferedReader reader = new BufferedReader(new FileReader(outputFile));
            ArrayList<String> lines = new ArrayList<String>();
            String line;
            log.debug("Scanning returns from tesseract");
            while ((line = reader.readLine()) != null) {
                log.debug("line read is '" + line + 
                		  "' (lenght " + line.length() + 
                		  " - isEmpy " + line.isEmpty() + 
                		  " - isBlank " + line.isBlank() + ")");
                if (!line.isBlank())
                {
                	lines.add(line);
                }
            }
            reader.close();
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
