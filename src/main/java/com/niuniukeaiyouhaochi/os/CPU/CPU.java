package com.niuniukeaiyouhaochi.os.CPU;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author pengchuang
 * @date 2021/10/1 21:20
 */
public class CPU extends Thread {
	private static CPU cpu;					// 单例模式的cpu对象
	private double startTime = 0.0D;		// 当前开始时间
	private double stopTime = 0.0D;			// 当前结束时间
	private double time = 0.0D;				// 运行时间
	private boolean stopFlag = true;		// 是否cpu停止


	@Override
	public void run() {
		while(true) {
			if (!this.stopFlag) {
				this.cpu.time = (this.cpu.stopTime + ((double)System.currentTimeMillis() - this.cpu.startTime));
			}

			try {
				Thread.sleep(10L);
			} catch (InterruptedException var2) {
				Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, (String)null, var2);
			}
		}
	}

	public CPU() {
		// 设置为守护进程
		this.setDaemon(true);
		this.start();
	}

	public static CPU getInstance() {
		if (cpu == null) {
			cpu = new CPU();
		}
		return cpu;
	}
	public void cpuStop() {
		this.cpu.stopFlag = true;
		this.cpu.stopTime = this.cpu.time;
	}

	public void reset() {//更换进程时重新设置数值
		this.cpu.cpuStop();
		this.cpu.time = 0.0D;
	}

	public boolean isRunning() {
		return !this.cpu.stopFlag;
	}

	public void play() {
		this.cpu.startTime = (double)System.currentTimeMillis();
		this.cpu.stopFlag = false;
	}

	public double timeProperty() {
		return this.cpu.time;
	}

	public double getCurrentTime() {
		return this.cpu.time;
	}
}
