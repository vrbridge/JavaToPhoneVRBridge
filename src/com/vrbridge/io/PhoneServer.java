package com.vrbridge.io;

import java.net.ServerSocket;
import java.net.Socket;

public class PhoneServer {
	private ServerSocket ssocket;
	
	public PhoneServer(Object caller) throws Exception {
		ssocket = new ServerSocket(33233);
		Socket socket = ssocket.accept();
		new PhoneClient(socket).processCalls(caller);
	}
}
