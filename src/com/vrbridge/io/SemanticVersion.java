package com.vrbridge.io;

public class SemanticVersion {
	private int major;
	private int minor;
	private int patch;
	
	public SemanticVersion() {}
	
	public SemanticVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	@Override
	public String toString() {
		return major + "." + minor + "." + patch;
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public int getPatch() {
		return patch;
	}
}
