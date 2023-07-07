package inforAutomator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler extends Thread 
{
    private final Socket clientSocket;

    private InputStream inputStream;
	private OutputStream outputStream;
	private byte[] messageBytes;
	
	private byte[] lengthBytes = new byte[4];
	private int packetLength = byteArrayToInt(lengthBytes);
	
    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

	private void handlePick() throws IOException
	{
	    String pickListJson = null;
	    String orderRef = null;
	    int idOrder = 0;

	    for(int i = 0; i < 2; i++)
	    {
            // Read the packet length (4 bytes)
            lengthBytes = new byte[4];
            inputStream.read(lengthBytes);
            packetLength = byteArrayToInt(lengthBytes);
	
	        // Read the message based on the packet length
	        messageBytes = new byte[packetLength];
	        inputStream.read(messageBytes);
	        String message = new String(messageBytes);
	
	        System.out.println("Received message: " + message);
	
	        // Split the serialized array into individual items
	        switch(i)
	        {
	        case 0:
	        	orderRef = new String(message);
	        	break;
	        
	        case 1:
	        	pickListJson = new String(message);
	            break;
	            
	        case 2:
	        	idOrder = Integer.parseInt(message);
	        }
	    }
	    System.out.println(orderRef);
	    System.out.println(pickListJson);
	    System.out.println(idOrder);
	    
	
		@SuppressWarnings("unchecked")
		ArrayList<Picking> pickList = (ArrayList<Picking>) JavaJSONMapper.JSONArrayToJava(JavaJSONMapper.StringToJSONArray(pickListJson, true), Picking.class);
		
	    // Invoke the doSomething function and get the return value
	    DTVFormFiller ff = new DTVFormFiller(pickList, orderRef);
	    String DTVName = ff.enterData();
	
	    // Convert the return code to bytes
	    byte[] returnCodeBytes = intToByteArray(DTVName.length());
	
	    // Send the return code back to the client
	    outputStream.write(returnCodeBytes);
	    outputStream.write(DTVName.getBytes());
	    clientSocket.close();
	    System.out.println("Connection closed.");
	}

	private void handleMoveStock() throws IOException
	{
	    String stockMoveJSON = null;

        // Read the packet length (4 bytes)
        lengthBytes = new byte[4];
        inputStream.read(lengthBytes);
        packetLength = byteArrayToInt(lengthBytes);
	
        // Read the message based on the packet length
        messageBytes = new byte[packetLength];
        inputStream.read(messageBytes);
        stockMoveJSON = new String(messageBytes);
       
        System.out.println("Received message: " + stockMoveJSON);
        StockMove sm = (StockMove) JavaJSONMapper.JSONToJava(JavaJSONMapper.StringToJSON(stockMoveJSON), StockMove.class);
	
		// Invoke the doSomething function and get the return value
	    MoveStockFormFiller ff = new MoveStockFormFiller(sm);
	    String result = ff.enterData();
	
	    // Convert the return code to bytes
	    byte[] returnCodeBytes = intToByteArray(result.length());
	
	    // Send the return code back to the client
	    outputStream.write(returnCodeBytes);
	    outputStream.write(result.getBytes());
	    clientSocket.close();
	    System.out.println("Connection closed.");
	}

	@Override
    public void run() {
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

        	/*
        	 * Protocol is:
        	 * - first packet: action required (PICK, MOVE_STOCK)
        	 * - following packets data related
        	 */
        	
            // Read the packet length (4 bytes)
            lengthBytes = new byte[4];
            inputStream.read(lengthBytes);
            packetLength = byteArrayToInt(lengthBytes);

            // Read the action requested
            byte[] messageBytes = new byte[packetLength];
            inputStream.read(messageBytes);
            String action = new String(messageBytes);
            System.out.println("Action requested '" + action + "'");

            switch(action)
            {
            case "PICK":
            	handlePick();
            	break;
            	
            case "MOVE_STOCK":
            	handleMoveStock();
            	break;
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private int byteArrayToInt(byte[] bytes) 
    {
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | bytes[3] & 0xFF;
    }

    private byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >> 24);
        bytes[1] = (byte) (value >> 16);
        bytes[2] = (byte) (value >> 8);
        bytes[3] = (byte) value;
        return bytes;
    }
}

