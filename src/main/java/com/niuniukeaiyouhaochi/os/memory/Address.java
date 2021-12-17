package com.niuniukeaiyouhaochi.os.memory;

/**
 * @author pengchuang
 * @date 2021/10/3 14:51
 */
public class Address {
	private int startAddress;
	private int endAddress;

	public Address(int startAddress, int endAddress) {
		this.startAddress = startAddress;
		this.endAddress = endAddress;
	}

	public int getStartAddress() {
		return this.startAddress;
	}

	public void setStartAddress(int startAddress) {
		this.startAddress = startAddress;
	}

	public int getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(int endAddress) {
		this.endAddress = endAddress;
	}
}
