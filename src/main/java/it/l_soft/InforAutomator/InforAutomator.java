package main.java.it.l_soft.InforAutomator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import org.sikuli.basics.Debug;
import org.sikuli.script.Screen;

public class InforAutomator {
	private static Parameters parms = Parameters.getInstance();
	
    public static void main(String[] args) 
    		throws NumberFormatException, InterruptedException {
    	if (args.length == 0)
    	{
            System.out.println("usage: java -jar InforAutomator [OPT]");
            System.out.println("\t--highlight | -h: enables highlighting of Regions, where configure, for better debuggin");
            System.out.println("\t--simulator | -s: behaves as simulator responding without any effective action on INFOR");
            System.out.println("\t--testRun | -t: it runs in test mode");
            System.out.println("\t--debug | -d: enable debugging on local file");
            System.out.println("\t--postChanges | -p: enables to post changes at the end of the pick function");
            System.out.println("\t--closeFunctionAtEnd | -c: enable to close window after completion");
            System.out.println("\t--screen use the screen number specified (default is 0)");
            System.out.println("\t--info gives sikuli and other relevanti info");
            System.out.println("\t--pauseToStart waits 5 seconds before starting to allow other setup to be done");
            System.out.println("\t--highlightMain highlights the 5 main regions at startup *screen, menu, header, footer, app)");
            System.exit(0);
    	}
        for (int i = 0; i < args.length; i++) {
        	String  arg = args[i];
        	
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
        		
        	case "--screen":
        		try {
					parms.useScreen = Integer.valueOf(args[i + 1]);
				} 
        		catch (NumberFormatException e) {
					parms.useScreen = 0;
				}
        		i += 1;
        		break;
        		
        	case "--info":
        		Screen.showMonitors();
        		System.out.println(
        				String.format("Screen details: # %d, X %g, Y %g, W %g, H %g",
						parms.useScreen, 
						Screen.getBounds(parms.useScreen).getX(),
						Screen.getBounds(parms.useScreen).getY(),
						Screen.getBounds(parms.useScreen).getWidth(),
						Screen.getBounds(parms.useScreen).getHeight()));
        		System.exit(0);
        		break;

        	case "--pauseAtStart":
        		parms.pauseAtStart = Integer.valueOf(args[i + 1]) * 1000;
        		i = i+1;
        		break;
        		
        	case "--version":
        	case "-v":
        		System.out.println("InforAutomator version: " + 
        						   InforAutomator.class.getPackage().getImplementationVersion());
        		System.exit(0);
        		break;
        		
        	case "--highlightMain":
        		parms.highlightMainRegions = true;
        	}
        }
        
        System.out.println("Parms configuration:");
        System.out.println("\thighlight: " + parms.highlight);
        System.out.println("\tsimulator: " + parms.simulator);
        System.out.println("\ttestRun: " + parms.testRun);
        System.out.println("\tdebug: " + parms.debug);
        System.out.println("\tpostChanges: " + parms.postChanges);
        System.out.println("\tcloseFunctionAtEnd: " + parms.closeFunctionAtEnd);
        System.out.println("\tuseScreen: " + parms.useScreen);
        System.out.println("\tpauseAtStart: " + parms.pauseAtStart);
        System.out.println("\thighlightMain: " + parms.highlightMainRegions);

        Utils.pauseExecution(parms.pauseAtStart);
        
        parms.setScreenRegions(parms.highlightMainRegions);
       
        Logger log = Logger.getLogger(InforAutomator.class);
        log.debug("Parms configuration:");
        log.debug("\thighlight: " + parms.highlight);
        log.debug("\tsimulator: " + parms.simulator);
        log.debug("\ttestRun: " + parms.testRun);
        log.debug("\tdebug: " + parms.debug);
        log.debug("\tpostChanges: " + parms.postChanges);
        log.debug("\tcloseFunctionAtEnd: " + parms.closeFunctionAtEnd);
        log.debug("\tuseScreen: " + parms.useScreen);
        log.debug("\tpauseAtStart: " + parms.pauseAtStart);
        log.debug("\thighlightMain: " + parms.highlightMainRegions);
        
        if (parms.debug)
        {
        	Debug.setLogFile("./logs/sikuli.log");
        	Debug.setDebugLevel(3);
        }
        
        try {
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(parms.getInforAutomatorPort());
            System.out.println("Server started. Listening on port " + parms.getInforAutomatorPort() + "...");
             

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress());

                // Spawn a new thread to handle the connection
                if (parms.pauseAtStart > 0)
                {
                	Thread.sleep(parms.pauseAtStart);
                }
                Thread thread = new ConnectionHandler(clientSocket, parms);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void callFromTest(String[] args) 
    		throws NumberFormatException, InterruptedException
    {
    	main(args);
    }
}