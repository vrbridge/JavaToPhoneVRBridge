package com.vrbridge.protocol.test;

import java.util.Arrays;

public class TestClass {
	private int propInt;
	private int[] propIntArray;
	private Integer propIntCap;
	private Integer[] propIntCapArray;
	private String propString;
	private String[] propStringArray;
	private TestClass propObj;
	private TestClass[] propObjArray;

	public TestClass() {}
	
	public TestClass(TestClass propObj) {
		this.propObj = propObj;
	}
	
	public int getPropInt() {
		return propInt;
	}
	
	public void setPropInt(int propInt) {
		this.propInt = propInt;
	}
	
	public int[] getPropIntArray() {
		return propIntArray;
	}
	
	public void setPropIntArray(int[] propIntArray) {
		this.propIntArray = propIntArray;
	}
	
	public Integer getPropIntCap() {
		return propIntCap;
	}
	
	public void setPropIntCap(Integer propIntCap) {
		this.propIntCap = propIntCap;
	}
	
	public Integer[] getPropIntCapArray() {
		return propIntCapArray;
	}
	
	public void setPropIntCapArray(Integer[] propIntCapArray) {
		this.propIntCapArray = propIntCapArray;
	}
	
	public String getPropString() {
		return propString;
	}
	
	public void setPropString(String propString) {
		this.propString = propString;
	}
	
	public String[] getPropStringArray() {
		return propStringArray;
	}
	
	public void setPropStringArray(String[] propStringArray) {
		this.propStringArray = propStringArray;
	}
	
	public TestClass getPropObj() {
		return propObj;
	}
	
	/*public void setPropObj(TestClass propObj) {
		this.propObj = propObj;
	}*/
	
	public TestClass[] getPropObjArray() {
		return propObjArray;
	}
	
	public void setPropObjArray(TestClass[] propObjArray) {
		this.propObjArray = propObjArray;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + propInt;
		result = prime * result + Arrays.hashCode(propIntArray);
		result = prime * result
				+ ((propIntCap == null) ? 0 : propIntCap.hashCode());
		result = prime * result + Arrays.hashCode(propIntCapArray);
		result = prime * result + ((propObj == null) ? 0 : propObj.hashCode());
		result = prime * result + Arrays.hashCode(propObjArray);
		result = prime * result
				+ ((propString == null) ? 0 : propString.hashCode());
		result = prime * result + Arrays.hashCode(propStringArray);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestClass other = (TestClass) obj;
		if (propInt != other.propInt)
			return false;
		if (!Arrays.equals(propIntArray, other.propIntArray))
			return false;
		if (propIntCap == null) {
			if (other.propIntCap != null)
				return false;
		} else if (!propIntCap.equals(other.propIntCap))
			return false;
		if (!Arrays.equals(propIntCapArray, other.propIntCapArray))
			return false;
		if (propObj == null) {
			if (other.propObj != null)
				return false;
		} else if (!propObj.equals(other.propObj))
			return false;
		if (!Arrays.equals(propObjArray, other.propObjArray))
			return false;
		if (propString == null) {
			if (other.propString != null)
				return false;
		} else if (!propString.equals(other.propString))
			return false;
		if (!Arrays.equals(propStringArray, other.propStringArray))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestClass [propInt=" + propInt + ", propIntArray="
				+ Arrays.toString(propIntArray) + ", propIntCap=" + propIntCap
				+ ", propIntCapArray=" + Arrays.toString(propIntCapArray)
				+ ", propString=" + propString + ", propStringArray="
				+ Arrays.toString(propStringArray) + ", propObj=" + propObj
				+ ", propObjArray=" + Arrays.toString(propObjArray) + "]";
	}
}
