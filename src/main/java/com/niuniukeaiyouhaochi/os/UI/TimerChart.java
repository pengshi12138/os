package com.niuniukeaiyouhaochi.os.UI;

import com.niuniukeaiyouhaochi.os.ProcessManager.ProcessController;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.ArrayList;

/**
 * @description:
 * @projectName:DPYos
 * @see:com.niuniukeaiyouhaochi.os.UI
 * @author: pc
 * @createTime:2021/10/21 20:36
 * @version:1.0
 */
public class TimerChart extends ScheduledService<ArrayList<Integer>> {
	ProcessXMLController main;
	public TimerChart(ProcessXMLController main){
		this.main = main;
	}
	public Task<ArrayList<Integer>> createTask(){
		Task<ArrayList<Integer>> task = new Task<ArrayList<Integer>>() {
			@Override
			protected ArrayList<Integer> call() throws Exception {
				ArrayList<Integer> list = new ArrayList();
				list.add(ProcessController.getInstance().getRunningQueue().size());
				list.add(ProcessController.getInstance().getBlockedQueue().size());
				list.add(ProcessController.getInstance().getReadyQueue().size());
				return list;
			}
		};
		return task;
	}
}