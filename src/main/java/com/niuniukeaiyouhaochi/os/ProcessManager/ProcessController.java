package com.niuniukeaiyouhaochi.os.ProcessManager;

import com.niuniukeaiyouhaochi.os.CPU.CPU;
import com.niuniukeaiyouhaochi.os.UI.ProcessXMLController;
import com.niuniukeaiyouhaochi.os.device.Device;
import com.niuniukeaiyouhaochi.os.device.DeviceController;
import com.niuniukeaiyouhaochi.os.memory.Address;
import com.niuniukeaiyouhaochi.os.memory.Memory;
import com.niuniukeaiyouhaochi.os.memory.MemoryBlock;
import com.niuniukeaiyouhaochi.os.memory.MemoryController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author pengchuang and dongye
 * @date 2021/10/3 14:56
 */
public class ProcessController implements Runnable{
	/* *
	 * runningProcess : 当前运行进程
	 * hangOutProcess : 空闲进程
	 * currentPID : 进程 PID
	 * MAX_PRIORITY : 进程优先级最高为 10
	 * MIN_PRIORITY : 进程优先级最低为 0
	 * MAX_REST_RUN_TIME :
	 * runningQueue : 运行队列
	 * readyQueue : 就绪队列
	 * blockedQueue : 阻塞队列
	 * timeChip : 时间片
	 */
	private static ProcessController processController;
	private Process runningProcess;
	private Process hangOutProcess = createHangOutProcess();

	public List<Process> getRunningQueue() {
		return runningQueue;
	}

	public List<Process> getReadyQueue() {
		return readyQueue;
	}

	public List<Process> getBlockedQueue() {
		return blockedQueue;
	}

	private static int currentPID = 0;
	public static final int MAX_PRIORITY = 5;
	public static final int MIN_PRIORITY = 0;
	public static final int MAX_REST_RUN_TIME = 5;

	private List<Process> runningQueue = new ArrayList<>();		// 被中断队列
	private List<Process> readyQueue = new ArrayList<>();		// 时间片轮转的进程队列
	private List<Process> blockedQueue = new ArrayList<>();		// 阻塞队列
	private double timeChip = 2.0D;								// 时间片
	private double allRunTime = 0D;								// 轮转运行时间


	// CPU 单例模型调用
	private CPU cpu=CPU.getInstance();
	// 内存分配器
	private MemoryController mc = MemoryController.getInstance();
	private Memory memory = Memory.getInstance();

	/**
	 * test 测试函数
	 */
	public static void main(String[] args) {
		CPU.getInstance();
		new Thread(ProcessController.getInstance()).start();
	}

	/**
	 * description 创建闲置进程
	 * param null
	 * return Process
	 * author pc
	 * createTime  2021/10/10 11:16
	 **/
	public static Process createHangOutProcess() {
		return new Process(new PCB("#hang out", MIN_PRIORITY, (double) 0));
	}

	/**
	* 提供给内存模块的获取PID的方法
	* */
	public static String getCurrentPid() {
		return "#" + currentPID;
	}

	public Process getRunningProcess() {
		return runningProcess;
	}

	public Process getHangOutProcess() {
		return hangOutProcess;
	}

	public double getAllRunTime() {
		return allRunTime;
	}

	/**
	 *  这里拿来监视cpu的运行时间，或者用runningProcess在此运行
	 */
	@Override
	public void run() {
		this.runningProcess=this.hangOutProcess;
		while (true) {
			// 判断cpu有无手动停止
			if (cpu.isRunning()) {
				create();
				// cpu 刷新时间单位
				double runtime = 0;
				allRunTime = 0;
				double first = cpu.getCurrentTime();
				boolean interrupt = false;
				// 时间片轮转函数
				while (allRunTime < timeChip && processController.runningProcess != processController.hangOutProcess
						&& !interrupt){
					// 进程运行结束
					if(runningProcess.getPcb().getRestRunTime() <= 0){
						System.out.println("进程结束 : [ id : " + runningProcess.getPcb().getId() + " ]");
						destroy(runningProcess);
						runningProcess = hangOutProcess;

						// 尝试从被抢占队列中获取进程
						Process runningPro = runningQueenToRunning();
						if (runningPro != null) {
							runningProcess = runningPro;
							break;
						} else {
							runningPro = readyQueenToRunning();//尝试在ready队列中获取进程
							if (runningPro != null) {
								runningProcess = runningPro;
								break;
							}
						}
						break;
					}
					runtime = (cpu.getCurrentTime() - first) / 1000;
					allRunTime = allRunTime + runtime;

					//更新进程的剩余运行时间
					runningProcess.getPcb().setRestRunTime(runningProcess.getPcb().getRestRunTime() - runtime);
					first = cpu.getCurrentTime();

					//检测有无抢占进程
					 for(int i = 0; i < readyQueue.size(); i++) {
						if(readyQueue.get(i).getPcb().getPriority() > runningProcess.getPcb().getPriority()) {
							interrupt(readyQueue.get(i));
							interrupt = true;
							break;
						}
					}

					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						System.out.printf(e.toString());
					}
				}
				//时间片轮转结束的情况
				if(allRunTime >= timeChip) {
					Process p=runningQueenToRunning();
					readyQueue.add(runningProcess);
					if(p!=null)
					{
						runningProcess=p;
					}
					else {
						runningProcess = hangOutProcess;
					}
				}
				//判断没有阻塞队列时，cpu正常从就绪队列中获取进程
				if(runningProcess == hangOutProcess) {
					Process process = readyQueenToRunning();
					if (process != null) {
						runningProcess = process;
					} else {
						runningProcess = hangOutProcess;
					}
				}
				//打印cpu运行进程
				if(runningProcess == hangOutProcess)
				{
					System.out.println("hangOutProcess");
				}
				else {
					System.out.println("Cpu进程: [ id :" + runningProcess.getPcb().getId() +" ;" +
							" 优先级 : " + runningProcess.getPcb().getPriority() + " ; 设备类型"+runningProcess.getDeviceType()+"]");
				}
				//以下三个循环为打印三个队列里的进程
				System.out.println("以下是就绪队列");
				for(int i = 0; i < readyQueue.size(); i++)
				{
					System.out.println("就绪队列: [ id :" + readyQueue.get(i).getPcb().getId() +" ;" +
							" 优先级 : " + readyQueue.get(i).getPcb().getPriority() + " ; 设备类型"+readyQueue.get(i).getDeviceType()+"]");
				}

				System.out.println("以下是被中断队列,每次时间片轮转后都增加一点优先级");
				for(int i = 0; i < runningQueue.size(); i++)
				{
					runningQueue.get(i).getPcb().setPriority( runningQueue.get(i).getPcb().getPriority() + 1 );
					System.out.println("中断队列: [ id :" +runningQueue.get(i).getPcb().getId() +" ;" +
							" 优先级 : " + runningQueue.get(i).getPcb().getPriority() +" ; 设备类型"+runningQueue.get(i).getDeviceType()+ "]");
				}

				System.out.println("以下是阻塞队列");
				for(int i = 0; i < blockedQueue.size(); i++)
				{
					System.out.println("阻塞队列: [ id :" + blockedQueue.get(i).getPcb().getId() +" ;" +
							" 优先级 : " + blockedQueue.get(i).getPcb().getPriority() + " ; 设备类型"+blockedQueue.get(i).getDeviceType()+"]");
				}
			} else {
				try {
					Thread.sleep(10L);
				} catch (Exception e) {

				}
			}
		}
	}

	public void runningToReady() {
		runningProcess.setReady();
		readyQueue.add(runningProcess);
	}


	/**
	 * 进程创建函数
	 * 在这里就分配好资源，然后通过getFreeDevice函数实现模拟多线程进程请求资源
	 * @return Process
	 */
	public Process create() {

		double GenerationProbability = Math.random();
		if (GenerationProbability <= 0.3 || blockedQueue.size() > 4) {
			return null;
		}

		// 设置进程创建时间
		try {
			Thread.sleep((long) (GenerationProbability * 100));
		} catch (InterruptedException e) {
			System.out.printf(e.toString());
		}

		if (!mc.hasBlankPCB()) {
			return null;
		}
		// 分配随机内存空间
		int size = (int)(Math.random() * 512.0D / 8.0D) + 30;

		//TODO:首次适配需要改动，内存块用新的

		// 判断 memoryBlock 能够 findBlankAddress 找到空闲块，能的话获取address，没有返回null
		Address address;
		MemoryBlock memoryBlock = mc.findBlankAddress(size);
		if (memoryBlock != null) {
			address = memoryBlock.getAddress();
		} else {
			address = null;
		}


		// 判断是否有多余的空间，没有返回null,在process里增加type参数，每个进程获得的device都是那五个，
		// 不用new，需要改动的地方有：onCreate，getFreeDevice,destroy;
		if (address == null) {
			return null;
		} else {
			// todo ： 设备管理写完，添加部分
			DeviceController dc = DeviceController.getInstance();
			PCB pcb = new PCB("#" + currentPID++, getRandomPriority(), getRandomRestRunTime());

			String type = getRandomDevice();
			Device device = dc.getDevice(type);
			Process process = new Process(pcb, address, true, device);
			process.setDeviceType(type);
			process.setDevice(device);
			//获取到空闲设备,设置设备占用信息
			if(device!=null) {
				device.setPID("#"+(currentPID-1));
				device.setOccupy(true);
				ready(process);
			}
			//没有空闲设备，阻塞进程
			else {
				System.out.println("没有设备");
				block(process);
			}
			//如果获取设备，则直接进入就绪队列ready（）,否则设置block（）
			ProcessXMLController.MemoryOccupy(memoryBlock);
			return process;
		}
	}

	public static String getRandomDevice() {
		Random random=new Random();
		int num=random.nextInt(10);
		if(num%3==0) {
			return "A";
		}
		if(num%3==1) {
			return "B";
		}
		if(num%3==2) {
			return "C";
		}
		return "A";
	}


	/**
	 * 包括内存释放，进程销毁，设备释放
	 */
	public boolean destroy(Process process) {
		//todo：释放设备后就要调用getFreeDevice（），再根据返回进程来调用awake（）:done
		// 设备释放

		//TODO:释放内存需要改动，内存块用新的:done
		System.out.println("销毁进程 [" + "id : " + process.getPcb().getId() +" ]");
		mc.release(mc.getMemoryBlock(process.getAddress()));
		runningQueue.remove(process);
		blockedQueue.remove(process);
		readyQueue.remove(process);

		Device device = process.getDevice();
		//设备释放
		DeviceController dc=DeviceController.getInstance();
		dc.resetDevice(device);

		Process blockedProcess = getFreeDevice(device);
		if (blockedProcess != null) {
			this.awake(blockedProcess);
		}

		// todo : 记得要改

		return true;
	}

	/*
	* 新建进程到就绪队列
	* */
	public void ready(Process process) {
		readyQueue.add(process);
		process.setReady();
	}

	/**
	 * 阻塞进程
	 * 设置阻塞类型并把程序放到阻塞对象中
	 * @param process
	 */
	public void block(Process process) {
		blockedQueue.add(process);
		process.setBlock();
	}

	/**
	* 唤醒进程
	* */
	public void awake(Process process) {
		this.blockToReady(process);
		// todo ： 设备分配:Done
	}


	/**
	 * 将进程放入内存中
	 * @param process
	 * todo ： 代写，等内存管理写完
	 */
	public void store(Process process) {

	}

	/**
	 * 阻塞队列完成资源请求到就绪队列,remove只有一个元素的数组不知道会不会出错,调用之前先判断是否满足抢占cpu
	 */
	public  void blockToReady(Process prc)
	{
		readyQueue.add(prc);
		blockedQueue.remove(prc);
		prc.setReady();
	}

	/**
	 * readyQueenToRunning 就绪态到进程态,
	 * @return Process 进程
	 */
	public Process readyQueenToRunning() {
		if(readyQueue.size() > 0) {
			Process process=null;
			process=readyQueue.get(0);
			readyQueue.remove(process);
			process.setRunning();
			return process;
		}
		else {
			return null;
		}
	}

	/**
	 * runningQueenToRunning
	 * 先判断是否中断表中是否存在进程，有则开始中断
	 * 先调用这个再调用readyToRunning；
	 * @return Process 进程
	 */
	public Process runningQueenToRunning() {
		if(runningQueue.size()>0) {
			Process process=null;
			process=runningQueue.get(0);
			runningQueue.remove(process);
			return process;
		} else {
			return null;
		}
	}

	/**
	 * 发生抢占cpu的状况
	 * @param process
	 */
	public void interrupt(Process process) {
		runningQueue.add(runningProcess);
		runningProcess=process;
		readyQueue.remove(process);
	}

	/**
	 * getFreeDevice 释放设备后判断阻塞队列有无因为此设备而被阻塞的进程并分配设备给此进程,然后调用进程到就绪队列
	 * @param device
	 * @return
	 */
	public Process getFreeDevice(Device device) {
		Process process = null;
		for (int i = 0; i < this.blockedQueue.size(); i++) {
			//todo:还需要更改,把PID设置一下，并设置占用状态,设置进程设备
			if(blockedQueue.get(i).getDeviceType() == device.getType()) {
				device.setOccupy(true);
				device.setPID(this.blockedQueue.get(i).getPcb().getId());
				this.blockedQueue.get(i).setDevice(device);
				process = this.blockedQueue.get(i);
				break;
			}
		}
		return process;
	}

	/**
	 * ProcessController 单例模式
	 * @return ProcessController 进程控制类
	 */
	public static ProcessController getInstance() {
		if (processController == null) {
			processController = new ProcessController();
		}
		return processController;
	}

	/**
	 * 随机生成程序优先级
	 * @return int 优先级
	 */
	private static int getRandomPriority() {
		Random random=new Random();
		return random.nextInt(MAX_PRIORITY) + 1;
	}

	/**
	 * getRandomRestRunTime 获取随机程序运行剩余时间，值为 1 ~ 4
	 * @return double 运行时间
	 */
	private static double getRandomRestRunTime() {
		return (Math.random() * 3.0D) + 1;
	}
}
