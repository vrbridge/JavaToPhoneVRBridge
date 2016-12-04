package com.vrbridge.protocol;

import java.io.EOFException;
import java.io.IOException;

public class ByteFragment {
	public ResizableByteArray bytes;
	public int from;
	public int length;
	
	public ByteFragment(ResizableByteArray bytes, int from, int length) {
		this.bytes = bytes;
		this.from = from;
		this.length = length;
	}

	public ByteFragment(ByteFragment copy) {
		this.bytes = copy.bytes;
		this.from = copy.from;
		this.length = copy.length;
	}

	public ByteFragment copy() {
		return new ByteFragment(this);
	}
	
	public int pop(int size) throws EOFException {
		if (size < 0) {
			return from;
		}
    	if (length < size) {
    		throw new EOFException();
    	}
		int ret = from;
		from += size;
		length -= size;
		return ret;
	}
	
	/**
	 * Add N not-yet-set bytes at the end of fragment.
	 * @param size
	 */
	public int push(int size) {
		int ret = from + length;
		length += size;
		if (length > bytes.length) {
			bytes.extend(length - bytes.length);
		}
		return ret;
	}
	
	public byte getByte(int pos) throws IOException {
		if (pos < 0)
			throw new EOFException("Negative positions are not supported");
		if (pos >= length)
			throw new EOFException();
		return bytes.buf[from + pos];
	}
	
	public byte[] toByteArray() {
		byte[] ret = new byte[length];
		if (length > 0) {
			System.arraycopy(bytes.buf, from, ret, 0, length);
		}
		return ret;
	}
}
