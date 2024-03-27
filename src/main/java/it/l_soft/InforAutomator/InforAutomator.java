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
        		break;

        	case "--simulator":
        	case "-s":
        		parms.simulator = true;
        		break;
        		
        	case "--test":
        	case "-t":
        		parms.testRun = true;
        		break;

        	case "--debug":
        	case "-d":
        		parms.debug = true;
        		break;

        	case "--post":
        	case "-p":
        		parms.postChanges = true;
        		break;
        	}
        }
        
        System.out.println("Parms configuration:");
        System.out.println("\thighlight: " + parms.highlight);
        System.out.println("\tsimulator: " + parms.simulator);
        System.out.println("\ttestRun: " + parms.testRun);
        System.out.println("\tdebug: " + parms.debug);
        System.out.println("\tpostChanges: " + parms.postChanges);
        
        try {
            @SuppressWarnings("resource")
//			ServerSocket serverSocket = new ServerSocket(9000, 0, InetAddress.getByName("192.168.11.131"));
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