package inforAutomator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


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
}