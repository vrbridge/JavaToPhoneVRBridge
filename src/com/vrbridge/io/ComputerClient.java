package com.vrbridge.io;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vrbridge.protocol.SimpleByteProtocol;

public class ComputerClient extends AbstractSocketClient {

	public ComputerClient(String host, int port) throws Exception {
		socket = new Socket(host, port);
		socket.setKeepAlive(true);
		is = new ObjectInputStream(socket.getInputStream());
		os = new ObjectOutputStream(socket.getOutputStream());
		// Hand-shake
		writeByte(PROTOCOL_SIMPLE_BYTE_TYPE);
		this.protocol = new SimpleByteProtocol();
		writeObjects(Arrays.<Object>asList("VR-bridge", new SemanticVersion(0, 1, 1)));
		String answer = readObject(String.class);
		System.out.println(answer);
		SemanticVersion version = readObject(SemanticVersion.class);
		System.out.println(version);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T makeWrapper(Class<T> type) {
		return (T)Proxy.newProxyInstance(type.getClassLoader(), 
				new Class[] {type}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				List<Object> objList = new ArrayList<Object>();
				objList.add(method.getName());
				if (args == null) {
					objList.add(0);
				} else {
					objList.add(args.length);
					for (Object arg : args) {
						objList.add(arg);
					}
				}
				writeObjects(objList);
				boolean isError = readObject(Boolean.class);
				if (isError) {
					String errorText = readObject(String.class);
					throw new RuntimeException(errorText);
				} else {
					Class<?> retType = method.getReturnType();
					if (retType == null) {
						retType = Object.class;
					}
					return readObject(retType);
				}
			}
		});
	}
}
