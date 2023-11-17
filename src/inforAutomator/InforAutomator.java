package inforAutomator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class InforAutomator {
	public static boolean highlight = false;
	
    public static void main(String[] args) {
    	if (args.length > 0 && args[0].compareTo("highlight") == 0)
    	{
    		highlight = true;
    		
    	}
        try {
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(9000);
            System.out.println("Server started (highlight is " + highlight + "). Listening on port 9000...");
             

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress());

                // Spawn a new thread to handle the connection
                Thread thread = new ConnectionHandler(clientSocket, highlight);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}