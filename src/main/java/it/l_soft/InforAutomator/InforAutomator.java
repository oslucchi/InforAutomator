package main.java.it.l_soft.InforAutomator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class InforAutomator {
	private static Parameters parms = new Parameters();
	
    public static void main(String[] args) {
    	if (args.length == 0)
    	{
            System.out.println("usage: java -jar InforAutomator [OPT]");
            System.out.println("\t--highlight | -h: enables highlighting of Regions, where configure, for better debuggin");
            System.out.println("\t--simulator | -s: behaves as simulator responding without any effective action on INFOR");
            System.out.println("\t--testRun | -t: it runs in test mode");
            System.out.println("\t--debug | -d: enable debugging on local file");
            System.out.println("\t--postChanges | -p: enables to post changes at the end of the pick function");
            System.out.println("\t--closeFunctionAtEnd | -c: enable to close window after completion");
            
            System.exit(0);
    	}
        for (String arg: args) {
//        	System.out.println("Considering arg '" + arg + "'");
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
        	case "--postChanges":
        	case "-p":
        		parms.postChanges = true;
        		break;

        	case "--close":
        	case "--closeFunctionAtEnd":
        	case "-c":
        		parms.closeFunctionAtEnd= true;
        		break;
        	}
        }
        
        System.out.println("Parms configuration:");
        System.out.println("\thighlight: " + parms.highlight);
        System.out.println("\tsimulator: " + parms.simulator);
        System.out.println("\ttestRun: " + parms.testRun);
        System.out.println("\tdebug: " + parms.debug);
        System.out.println("\tpostChanges: " + parms.postChanges);
        System.out.println("\tcloseFunctionAtEnd: " + parms.closeFunctionAtEnd);
        
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