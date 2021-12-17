package com.niuniukeaiyouhaochi.os.ProcessManager;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * @author pengchuang and dongye
 * @date 2021/10/1 21:32
 */
public class PCB {
	private String id;							// 唯一标识
	private int status;							// 状态
	private int priority;						// 优先级
	private int type;							// 程序类型
	private Double restRunTime;					// 程序剩余运行时间
	private Double totalRunTime;				// 总运行时间
	private Double lastRestRunTime;				// 时间片剩余时间
	private boolean isInterrupt;  				// 是否中断



	public PCB() {
	}

	public PCB(String id, int priority, Double restRunTime) {
		this.id = id;
		this.priority = priority;
		this.restRunTime = restRunTime;
		this.totalRunTime = restRunTime;
		// CPU赋值
		this.lastRestRunTime = restRunTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Double getRestRunTime() {
		return restRunTime;
	}

	public void setRestRunTime(Double restRunTime) {
		this.restRunTime = restRunTime;
	}

	public Double getTotalRunTime() {
		return totalRunTime;
	}

	public void setTotalRunTime(Double totalRunTime) {
		this.totalRunTime = totalRunTime;
	}

	public Double getLastRestRunTime() {
		return lastRestRunTime;
	}

	public void setLastRestRunTime(Double lastRestRunTime) {
		this.lastRestRunTime = lastRestRunTime;
	}

	public boolean isInterrupt() {
		return isInterrupt;
	}

	public void setInterrupt(boolean interrupt) {
		isInterrupt = interrupt;
	}


}
