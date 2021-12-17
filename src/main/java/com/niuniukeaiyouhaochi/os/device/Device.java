package com.niuniukeaiyouhaochi.os.device;

public class Device {
	private String type;
	private  boolean isOccupy=false;
	private String PID;

	public String  getPID() {
		return PID;
	}

	public void setPID(String PID) {
		this.PID = PID;
	}

	public Device() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Device(String device) {
		this.type = device;
	}

	public boolean isOccupy() {
		return isOccupy;
	}

	public void setOccupy(boolean occupy) {
		isOccupy = occupy;
	}
}
