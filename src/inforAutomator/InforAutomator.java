package inforAutomator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class InforAutomator {
    public static void main(String[] args) {
        try {
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(9000);
            System.out.println("Server started. Listening on port 9000...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress());

                // Spawn a new thread to handle the connection
                Thread thread = new ConnectionHandler(clientSocket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConnectionHandler extends Thread {
        private final Socket clientSocket;
    	String pickListJson = null;
    	String orderRef = null;
    	int idOrder = 0;

        public ConnectionHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                for(int i = 0; i < 2; i++)
                {
	                // Read the packet length (4 bytes)
	                byte[] lengthBytes = new byte[4];
	                inputStream.read(lengthBytes);
	                int packetLength = byteArrayToInt(lengthBytes);
	
	                // Read the message based on the packet length
	                byte[] messageBytes = new byte[packetLength];
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
                FormFiller ff = new FormFiller(pickList, orderRef);
                String DTVName = ff.enterData();

                // Convert the return code to bytes
                byte[] returnCodeBytes = intToByteArray(DTVName.length());

                // Send the return code back to the client
                outputStream.write(returnCodeBytes);
                outputStream.write(DTVName.getBytes());
                clientSocket.close();
                System.out.println("Connection closed.");
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
}	
