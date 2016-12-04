package com.vrbridge.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vrbridge.protocol.ByteFragment;
import com.vrbridge.protocol.ResizableByteArray;
import com.vrbridge.protocol.TypeDrivenProtocol;

public abstract class AbstractSocketClient {
	protected Socket socket;
	protected ObjectInputStream is;
	protected ObjectOutputStream os;
	protected TypeDrivenProtocol protocol;
	
	protected static final Charset utf8 = Charset.forName("utf8");
	protected static final byte PROTOCOL_SIMPLE_BYTE_TYPE = 1;
	

	protected ByteFragment readData() throws IOException {
		int length = is.readInt();
		if (length < 0) {
			return new ByteFragment(null, 0, length);
		}
		byte[] bytes = new byte[length];
		int pos = 0;
		while (pos < length) {
			int part = is.read(bytes, pos, length - pos);
			if (part < 0) {
				throw new IOException("Unexpected end of stream");
			}
			pos += part;
		}
		return new ByteFragment(new ResizableByteArray(bytes), 0, length);
	}

	protected <T> T readObject(Class<T> type) throws Exception {
		return protocol.bytesToObject(readData(), type);
	}

	protected byte readByte() throws IOException {
		return is.readByte();
	}
	
	protected void writeObject(Object val) throws IOException {
		writeData(protocol.objectToBytes(val));
	}

	protected void writeObjects(List<Object> vals) throws IOException {
		List<ByteFragment> dataList = new ArrayList<ByteFragment>();
		for (Object val : vals) {
			dataList.add(protocol.objectToBytes(val));
		}
		writeData(dataList);
	}

	protected void writeData(ByteFragment data) throws IOException {
		writeData(Arrays.asList(data));
	}
	
	protected void writeData(List<ByteFragment> dataList) throws IOException {
		for (ByteFragment data: dataList) {
			os.write(data.bytes.buf, 0, data.length);
		}
		os.flush();
		os.reset();
	}
	
	protected void writeByte(byte b) throws IOException {
		os.writeByte(b);
	}
}
