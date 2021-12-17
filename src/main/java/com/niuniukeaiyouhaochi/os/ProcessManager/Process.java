package com.niuniukeaiyouhaochi.os.ProcessManager;

import com.niuniukeaiyouhaochi.os.device.*;
import com.niuniukeaiyouhaochi.os.memory.*;

/**
 * @author pengchuang and dongye
 * @date 2021/10/2 23:06
 */
public class Process {
	/**
	 * PCB				: 进程控制块
	 * Address			: 进程内存地址
	 * Device			: 进程所需要的设备
	 * isUser			: 判断该进程是不是用户进程，true表示是，false表示是系统级进程
	 * id				: 进程 id
	 * priority			: 进程优先级
	 * restRunTime		: 进程剩余运行时间
	 * totalRunTime		: 进程总运行时间
	 * lastRestRunTime	: 进程运行最后剩余时间
	 * status			: 进程状态字，相当于psw
	 * 					  值为 0 : 就绪态
	 * 					  值为 1 : 运行态或者被中断
	 * 					  值为 -1 : 阻塞态
	 */
	private PCB pcb;
	private Address address;
	private Device device;
	private String deviceType;
	private boolean isUser;
	private int status;

	public Process(PCB pcb, Address address, boolean isUser, Device device) {
		this.pcb = pcb;
		this.address = address;
		this.isUser = isUser;
		this.device = device;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Process(PCB pcb) {
		this.address = new Address(0, 0);
		this.isUser = false;
		this.device = null;
	}

	// 设置就绪态
	public void setReady() {
		this.setStatus(0);
	}
	// 设置运行态
	public void setRunning() {
		this.setStatus(1);
	}
	// 设置阻塞态
	public void setBlock() {
		this.setStatus(-1);
	}
	public PCB getPcb() {
		return pcb;
	}

	public void setPcb(PCB pcb) {
		this.pcb = pcb;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public boolean isUser() {
		return isUser;
	}

	public void setUser(boolean user) {
		isUser = user;
	}

	public Device Device() {
		return device;
	}

	public void Device(Device device) {
		this.device = device;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.getPcb().setStatus(status);
		this.status = status;
	}
}
