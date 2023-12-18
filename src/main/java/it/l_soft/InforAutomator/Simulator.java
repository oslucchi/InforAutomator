package main.java.it.l_soft.InforAutomator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Simulator  {
	BufferedReader reader;

	public class MoveStock extends InforFunctions {
		@Override
		public String enterData() {
			System.out.print("Che risposta per MoveStock? ");
			reader = new BufferedReader(new InputStreamReader(System.in));

			// Reading data using readLine
			String userResponse;
			try {
				userResponse = reader.readLine();
			} catch (IOException e) {
				return "KO";
			}

			// Printing the read line
			return userResponse;
		}
	}
	
	public class DTV extends InforFunctions {
		public DTV(ArrayList<Picking> pickList, String orderRef) {
			
		}
		@Override
		public String enterData() {
			// Enter data using BufferReader
			System.out.print("Che risposta per DTVFormFiller? ");
			reader = new BufferedReader(new InputStreamReader(System.in));

			// Reading data using readLine
			String userResponse;
			try {
				userResponse = reader.readLine();
			} catch (IOException e) {
				return "KO";
			}

			// Printing the read line
			return userResponse;
		}
	}
	
	public Simulator() {};
}
