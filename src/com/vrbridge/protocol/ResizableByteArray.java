package com.vrbridge.protocol;

import java.util.Arrays;

public class ResizableByteArray {
	public byte[] buf;
	public int length;
	
	public ResizableByteArray(byte[] source) {
		this.buf = source;
		this.length = source.length;
	}
	
	public ResizableByteArray() {
		this(16);
	}
	
	public ResizableByteArray(int capacity) {
		buf = new byte[capacity];
		length = 0;
	}
	
	public void extend(int add) {
		length += add;
		if (length > buf.length) {
			// Resizing
	        int oldCapacity = buf.length;
	        int newCapacity = oldCapacity << 1;
	        if (newCapacity - length < 0)
	            newCapacity = length;
	        if (newCapacity < 0) {
	            if (length < 0) // overflow
	                throw new OutOfMemoryError();
	            newCapacity = Integer.MAX_VALUE;
	        }
	        buf = Arrays.copyOf(buf, newCapacity);
		}
	}
	
	public byte[] toByteArray() {
		return Arrays.copyOf(buf, length);
	}
}
