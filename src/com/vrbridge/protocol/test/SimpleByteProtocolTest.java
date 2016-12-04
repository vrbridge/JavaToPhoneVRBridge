package com.vrbridge.protocol.test;

import org.junit.Assert;
import org.junit.Test;

import com.vrbridge.protocol.ByteFragment;
import com.vrbridge.protocol.ResizableByteArray;
import com.vrbridge.protocol.SimpleByteProtocol;
import com.vrbridge.protocol.TypeDrivenProtocol;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimpleByteProtocolTest {
	private TypeDrivenProtocol protocol = new SimpleByteProtocol();
	
	@Test
	public void testBasicTypes() throws Exception {
		Byte byteVal = 123;
		Assert.assertEquals(byteVal, toBytesAndBack(byteVal, Byte.class, 5));
		Boolean boolVal = true;
		Assert.assertEquals(boolVal, toBytesAndBack(boolVal, Boolean.class, 5));
		Short shortVal = 12345;
		Assert.assertEquals(shortVal, toBytesAndBack(shortVal, Short.class, 6));
		Character charVal = 'Z';
		Assert.assertEquals(charVal, toBytesAndBack(charVal, Character.class, 6));
		Integer intVal = 1234567;
		Assert.assertEquals(intVal, toBytesAndBack(intVal, Integer.class, 8));
		Float floatVal = 1234.567f;
		Assert.assertEquals(floatVal, toBytesAndBack(floatVal, Float.class, 8), 0.00001f);
		Long longVal = 1234567890L;
		Assert.assertEquals(longVal, toBytesAndBack(longVal, Long.class, 12));
		Double doubleVal = 1.23456789;
		Assert.assertEquals(doubleVal, toBytesAndBack(doubleVal, Double.class, 12), 1e-14);
	}

	@Test
	public void testNull() throws Exception {
		Assert.assertNull(toBytesAndBack(null, String.class, 4));
	}
	
	@Test
	public void testString() throws Exception {
		String text = "Super-test!!!";
		Assert.assertEquals(text, toBytesAndBack(text, String.class, 4 + text.length()));
	}
	
	@Test
	public void testBasicArrays() throws Exception {
		byte[] byteArr = new byte[] {1, 2, 3};
		Assert.assertArrayEquals(byteArr, (byte[])toBytesAndBack(byteArr, 
				(Class)byteArr.getClass(), 4 + 3));
		Byte[] byteCapArr = new Byte[] {1, 2, 3};
		Assert.assertArrayEquals(byteCapArr, (Byte[])toBytesAndBack(byteCapArr, 
				(Class)byteCapArr.getClass(), 4 + 3));
		boolean[] boolArr = new boolean[] {true, false, true};
		Assert.assertArrayEquals(boolArr, (boolean[])toBytesAndBack(boolArr, 
				(Class)boolArr.getClass(), 4 + 3));
		Boolean[] boolCapArr = new Boolean[] {true, false, true};
		Assert.assertArrayEquals(boolCapArr, (Boolean[])toBytesAndBack(boolCapArr, 
				(Class)boolCapArr.getClass(), 4 + 3));
		char[] charArr = new char[] {'a', 'b', 'c'};
		Assert.assertArrayEquals(charArr, (char[])toBytesAndBack(charArr, 
				(Class)charArr.getClass(), 4 + 2 * 3));
		Character[] charCapArr = new Character[] {'a', 'b', 'c'};
		Assert.assertArrayEquals(charCapArr, (Character[])toBytesAndBack(charCapArr, 
				(Class)charCapArr.getClass(), 4 + 2 * 3));
		short[] shortArr = new short[] {1, 2, 3};
		Assert.assertArrayEquals(shortArr, (short[])toBytesAndBack(shortArr, 
				(Class)shortArr.getClass(), 4 + 2 * 3));
		Short[] shortCapArr = new Short[] {1, 2, 3};
		Assert.assertArrayEquals(shortCapArr, (Short[])toBytesAndBack(shortCapArr, 
				(Class)shortCapArr.getClass(), 4 + 2 * 3));
		int[] intArr = new int[] {1, 2, 3};
		Assert.assertArrayEquals(intArr, (int[])toBytesAndBack(intArr, 
				(Class)intArr.getClass(), 4 + 4 * 3));
		Integer[] intCapArr = new Integer[] {1, 2, 3};
		Assert.assertArrayEquals(intCapArr, (Integer[])toBytesAndBack(intCapArr, 
				(Class)intCapArr.getClass(), 4 + 4 * 3));
		float[] floatArr = new float[] {1.5f, 2.5f, 3.5f};
		Assert.assertArrayEquals(floatArr, (float[])toBytesAndBack(floatArr, 
				(Class)floatArr.getClass(), 4 + 4 * 3), 0.00001f);
		Float[] floatCapArr = new Float[] {1.5f, 2.5f, 3.5f};
		Assert.assertArrayEquals(floatCapArr, (Float[])toBytesAndBack(floatCapArr, 
				(Class)floatCapArr.getClass(), 4 + 4 * 3));
		long[] longArr = new long[] {1, 2, 3};
		Assert.assertArrayEquals(longArr, (long[])toBytesAndBack(longArr, 
				(Class)longArr.getClass(), 4 + 8 * 3));
		Long[] longCapArr = new Long[] {1L, 2L, 3L};
		Assert.assertArrayEquals(longCapArr, (Long[])toBytesAndBack(longCapArr, 
				(Class)longCapArr.getClass(), 4 + 8 * 3));
		double[] doubleArr = new double[] {1.5, 2.5, 3.5};
		Assert.assertArrayEquals(doubleArr, (double[])toBytesAndBack(doubleArr, 
				(Class)doubleArr.getClass(), 4 + 8 * 3), 1e-14);
		Double[] doubleCapArr = new Double[] {1.5, 2.5, 3.5};
		Assert.assertArrayEquals(doubleCapArr, (Double[])toBytesAndBack(doubleCapArr, 
				(Class)doubleCapArr.getClass(), 4 + 8 * 3));
	}

	@Test
	public void testEmptyArray() throws Exception {
		int[] intArr = new int[] {};
		Assert.assertArrayEquals(intArr, (int[])toBytesAndBack(intArr, 
				(Class)intArr.getClass(), 4));
		TestClass[] objArr = new TestClass[] {};
		Assert.assertArrayEquals(objArr, (TestClass[])toBytesAndBack(objArr, 
				(Class)objArr.getClass(), 4));
		objArr = new TestClass[] {null};
		Assert.assertArrayEquals(objArr, (TestClass[])toBytesAndBack(objArr, 
				(Class)objArr.getClass(), 8));
	}
	
	@Test
	public void testObject() throws Exception {
		TestClass obj = new TestClass();
		obj.setPropInt(12345);
		obj.setPropIntCap(67890);
		obj.setPropString("Super!!!");
		int len = 4 + 
				(4 + 7) + (4 + 4) +   // propInt -> 12345
				(4 + 10) + (4 + 4) +  // propIntCap -> 67890
				(4 + 10) + (4 + 8);   // propString -> Super!!!
		Assert.assertEquals(obj, toBytesAndBack(obj, TestClass.class, len));
		TestClass obj2 = new TestClass(obj);
		Assert.assertEquals(obj2, toBytesAndBack(obj2, TestClass.class, -1));
		TestClass obj3 = new TestClass();
		obj3.setPropIntArray(new int[] {1, 2, 3});
		obj3.setPropIntCapArray(new Integer[] {4, 5, 6});
		obj3.setPropObjArray(new TestClass[] {obj, obj2, null});
		Assert.assertEquals(obj3, toBytesAndBack(obj3, TestClass.class, -1));
	}

	public <T> T toBytesAndBack(T value, Class<T> type, int expectedSize) throws Exception {
		ByteFragment data = protocol.objectToBytes(value);
		if (expectedSize >= 0) {
			Assert.assertEquals(expectedSize, data.length);
		}
		data = cutLength(data);
		T ret = protocol.bytesToObject(data, type);
		return ret;
	}

	public void printBytes(ByteFragment data) {
		byte[] bytes = data.bytes.toByteArray();
		for (int i = 0; i < bytes.length; i++)
			System.out.println(bytes[i]);
	}
	
	private ByteFragment cutLength(ByteFragment data) throws Exception {
		ByteFragment stack = data.copy();
		int size = protocol.bytesToObject(stack, Integer.class);
		stack.pop(4);
		byte[] bytes = stack.toByteArray();
		if (size >= 0) {
			Assert.assertEquals(size, bytes.length);
		}
		return new ByteFragment(new ResizableByteArray(bytes), 0, size);
	}
}
