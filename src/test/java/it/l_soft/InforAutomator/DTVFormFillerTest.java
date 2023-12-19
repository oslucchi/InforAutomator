package test.java.it.l_soft.InforAutomator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import main.java.it.l_soft.InforAutomator.Parameters;
import main.java.it.l_soft.InforAutomator.Utils;

class DTVFormFillerTest {
//	private static InforAutomator ia = new InforAutomator();
	private static Parameters prop = new Parameters();
	static String orderRef = "IM025534";

	private enum InforActions {
		PICK("PICK"),
		MOVE_STOCK("MOVE_STOCK");

		private String action;
		private InforActions(String action) {
			this.action = action;
		}

		@Override
		public String toString(){
			return action;
		}
	}

	@Test
	void test() {
    	int i = 0;
    	
		String pickList = 
			"[{" +
				"\"article\" : \"094920104\", " +
				"\"x\" : \"512\", " +
				"\"y\" : \"016\", " +
				"\"z\" : \"01\"" +
			 "},{" +
				"\"article\" : \"094920104\", " +
				"\"x\" : \"512\", " +
				"\"y\" : \"016\", " +
				"\"z\" : \"01\"" +
			 "},{" +
				"\"article\" : \"095110373\", " +
				"\"x\" : \"512\", " +
				"\"y\" : \"014\", " +
				"\"z\" : \"02\"" +
			 "},{" +
				"\"article\" : \"094940108\", " +
				"\"x\" : \"512\", " +
				"\"y\" : \"018\", " +
				"\"z\" : \"03\"" +
			 "}]";
		
        try {
            Socket socket = new Socket(prop.getInforAutomatorHost(), prop.getInforAutomatorPort());
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            byte[] lengthBytes;
                       
	        // Compose the message in the protocol format

	        lengthBytes = Utils.intToByteArray(InforActions.PICK.toString().length());
            outputStream.write(lengthBytes);
            outputStream.write(InforActions.PICK.toString().getBytes());        
	        
            lengthBytes = Utils.intToByteArray(orderRef.length());
            outputStream.write(lengthBytes);
            outputStream.write(orderRef.getBytes());

            // Compose the message in the protocol format
					
            lengthBytes = Utils.intToByteArray(pickList.length());
            outputStream.write(lengthBytes);
            outputStream.write(pickList.getBytes());

            // Read the return code from the server
            inputStream.read(lengthBytes);
            int packetLength = Utils.byteArrayToInt(lengthBytes);
        	
            // Read the message based on the packet length
            byte[] messgeToRead = new byte[packetLength];
            inputStream.read(messgeToRead);
            String DTVName = new String(messgeToRead);

            socket.close();            
        	assertTrue(DTVName.substring(0,3).compareTo("DTV") == 0);
        }
        catch(Exception e)
        {
        	assertTrue(i > 0);
        }
	}
}
