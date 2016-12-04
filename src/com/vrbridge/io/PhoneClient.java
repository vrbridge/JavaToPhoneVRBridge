package com.vrbridge.io;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.net.SocketException;

import com.vrbridge.protocol.SimpleByteProtocol;

public class PhoneClient extends AbstractSocketClient {
	
	public PhoneClient(Socket socket) throws Exception {
		this.socket = socket;
        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());
        // Hand-shake
        byte protocolType = readByte();
        if (protocolType == PROTOCOL_SIMPLE_BYTE_TYPE) {
        	this.protocol = new SimpleByteProtocol();
        } else {
        	return;
        }
        String name = readObject(String.class);
        SemanticVersion version = readObject(SemanticVersion.class);
        version = new SemanticVersion(version.getMajor(), version.getMinor() + 1, 
        		version.getPatch());
        writeObject("Hi, " + name);
        writeObject(version);
	}
	
	public void processCalls(Object caller) throws Exception {
		while(true) {
			String methodName;
			try {
				methodName = readObject(String.class);
			} catch (SocketException ex) {
				// Client broke the connection
				break;
			}
			int argCount = readObject(Integer.class);
			Method method = null;
			for (Method m : caller.getClass().getMethods()) {
				if (m.getName().equals(methodName) && m.getParameterTypes().length == argCount &&
						(m.getModifiers() & Modifier.STATIC) == 0 &&
						(m.getModifiers() & Modifier.ABSTRACT) == 0 &&
						(m.getModifiers() & Modifier.PUBLIC) != 0) {
					method = m;
				}
			}
			Object ret = null;
			String errorText = null;
			if (method == null) {
				for (int i = 0; i < argCount; i++) {
					readObject(Object.class);
				}
				errorText = "Method " + methodName + " with " + argCount + 
						" parameter(s) is not found";
			} else {
				Object[] args = new Object[argCount];
				for (int i = 0; i < argCount; i++) {
					args[i] = readObject(method.getParameterTypes()[i]);
				}
				try {
					ret = method.invoke(caller, args);
				} catch (Exception ex) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					pw.close();
					errorText = sw.toString();
				}
			}
			writeObject(errorText != null);
			if (errorText == null) {
				writeObject(ret);
			} else {
				writeObject(errorText);
			}
		}
	}
}
