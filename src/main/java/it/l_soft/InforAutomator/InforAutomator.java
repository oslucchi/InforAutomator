package main.java.it.l_soft.InforAutomator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class InforAutomator {
	private static Parameters parms = new Parameters();
	
    public static void main(String[] args) {
    	
        for (String arg: args) {
        	switch(arg)
        	{
        	case "--highlight":
        	case "-hl":
        		parms.highlight = true;
        		System.out.println("Highlight flag is " + parms.highlight);
        		break;

        	case "--simulator":
        	case "-s":
        		parms.simulator = true;
        		System.out.println("Simulator flag is " + parms.simulator);
        		
        	case "--test":
        	case "-t":
        		parms.testRun = true;
        		System.out.println("testRunflag is " + parms.testRun);
        	}
        }
        
        try {
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(9000);
            System.out.println("Server started. Listening on port 9000...");
             

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress());

                // Spawn a new thread to handle the connection
                Thread thread = new ConnectionHandler(clientSocket, parms);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void callFromTest(String[] args)
    {
    	main(args);
    }
}