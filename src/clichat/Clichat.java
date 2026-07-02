package clichat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Clichat {

	private ServerSocket server;
	private Socket client;
	private static Clichat app;

	private Clichat() {

	}

	public static Clichat getApp() {
		if (app == null) {
			app = new Clichat();
		}

		return app;
	}

	public void start() {
		Scanner s = new Scanner(System.in);

		for (;;) {
			try {
				printMenu();
				int in = Integer.parseInt(s.next());
				if (in == 0) {
					break;
				}

				try {
					switch (in) {
						case 1 -> initServerMode();
						case 2 -> printMessage("Mode not implemented yet.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (NumberFormatException e) {
				System.err.println("Invalid option. Try [0], [1] or [2].");
			}
		}
		s.close();

	}

	void initServerMode() throws IOException {
		server = new ServerSocket(4444);
		client = server.accept(); // Wait for connection

		listenClient(client);
		server.close();
	}

	private void listenClient(Socket client) throws IOException {
		String readInput;
		// The earphones
		InputStream clientIn = client.getInputStream();
		// the mic
		OutputStream clientOut = client.getOutputStream();
		PrintWriter pout = new PrintWriter(clientOut, true);
		BufferedReader bin = new BufferedReader(new InputStreamReader(clientIn));

		for (;;) {
			sendMessageToClient("Tell me something: ", pout);
			readInput = bin.readLine();

			if (!isValidInput(readInput)) {
				sendMessageToClient("Please, tell me something useful...\n", pout);
				continue;
			}

			try {
				if (isChatEndedByClient(readInput)) {
					sendMessageToClient("Bye...\n\n", pout);
					client.close();
					break;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			sendMessageToClient("You say: " + readInput + "\n", pout);
		}
	}

	private boolean isValidInput(String in) {
		return in != null && !in.isBlank();
	}

	private boolean isChatEndedByClient(String in) {
		return in.length() == 1 && Integer.parseInt(in) == 0;
	}

	private void sendMessageToClient(String msg, PrintWriter client) {
		client.printf("%s", msg);
	}

	private void printMessage(String msg) {
		System.out.printf("%s", msg);
	}

	private void printMenu() {
		System.out.print("1. Server mode \n");
		System.out.print("2. Client mode \n");
		System.out.print("0. Exit \n");
	}
}
