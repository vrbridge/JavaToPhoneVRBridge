package com.vrbridge.protocol;

import java.io.IOException;

public interface TypeDrivenProtocol {

	public <T> T bytesToObject(ByteFragment data, Class<T> type) throws IOException;

    public ByteFragment objectToBytes(Object val) throws IOException;

}
