package com.niuniukeaiyouhaochi.os.memory;


import com.niuniukeaiyouhaochi.os.ProcessManager.ProcessController;
import com.niuniukeaiyouhaochi.os.UI.ProcessXMLController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongye
 * @date 2021/10/4 17:51
 */
public class MemoryController {
	private Memory memory;
	private static MemoryController memoryController;
	private List<MemoryBlock> myMemoryBlockList = new ArrayList<>();
	private static final int MAX_SIZE = 511;

	public List<MemoryBlock> getMyMemoryBlockList() {
		return myMemoryBlockList;
	}

	public MemoryController() {
		this.memory = Memory.getInstance();
	}

	public static MemoryController getInstance() {
		if (memoryController == null) {
			memoryController = new MemoryController();
		}
		return memoryController;
	}


	/**
	 * description 压缩内存
	 * param void
	 * return void
	 * author dongye
	 * createTime 2021/10/10
	 **/
	public static void mergeMemory()
	{
		int start=0;
		int length;
		for(int i = 0;i < memoryController.myMemoryBlockList.size(); i++)
		{
			System.out.println("Start="+memoryController.myMemoryBlockList.get(i).getAddress().getStartAddress());
			System.out.println("end="+memoryController.myMemoryBlockList.get(i).getAddress().getEndAddress());
			length=memoryController.myMemoryBlockList.get(i).getAddress().getEndAddress() -
					memoryController.myMemoryBlockList.get(i).getAddress().getStartAddress();
			memoryController.myMemoryBlockList.get(i).getAddress().setStartAddress(start);
			memoryController.myMemoryBlockList.get(i).getAddress().setEndAddress(start+length);
			System.out.println("Start2="+memoryController.myMemoryBlockList.get(i).getAddress().getStartAddress());
			System.out.println("end2="+memoryController.myMemoryBlockList.get(i).getAddress().getEndAddress());

			start=start+length+1;
		}
		ProcessXMLController.MemoryRefresh();
	}


	/**
	 * 判断程序运行时候的进程数量,实验要求小于10
	 * @return boolean
	 */
	public boolean hasBlankPCB() {
		// System.out.println(this.memory.getPCBList().size());
		return this.memory.getPCBList().size() < 10;
	}

	/**
	 * description 首次适配分配内存，有三种情况如下
	 * param int size 请求分配的内存块
	 * return MemoryBlock
	 * author dongye
	 * createTime 2021/10/10 11:52
	 **/
	public MemoryBlock findBlankAddress(int size)
	{
		int start = 0,end;
		for(int i = 0; i < myMemoryBlockList.size(); i++) {
			end = myMemoryBlockList.get(i).getStartPosition() - 1;
			if(( end - start + 1 ) >= size) {
				MemoryBlock myMemoryBlock =new MemoryBlock(start, size, false, ProcessController.getCurrentPid());
				//添加到list里
				myMemoryBlockList.add(myMemoryBlock);
				sortList();
				return myMemoryBlock;
			}
			start = myMemoryBlockList.get(i).getEndPosition() + 1;
		}
		if(start == 0 && (MAX_SIZE - start + 1) >= size) {
			MemoryBlock myMemoryBlock=new MemoryBlock(start, size, false, ProcessController.getCurrentPid());
			myMemoryBlockList.add(myMemoryBlock);
			sortList();
			return myMemoryBlock;
		} else if( ( MAX_SIZE - myMemoryBlockList.get( myMemoryBlockList.size() - 1 ).getEndPosition() ) >= size ) {
			//查看进程占用块间的空余内存是否有满足的，没有则通过以下的if语句来判断剩余区域有无满足块
			int st = myMemoryBlockList.get(myMemoryBlockList.size() - 1).getEndPosition() + 1;
			MemoryBlock myMemoryBlock=new MemoryBlock(st, size, false, ProcessController.getCurrentPid());
			myMemoryBlockList.add(myMemoryBlock);
			sortList();
			return myMemoryBlock;
		} else {
			return null;
		}
	}

	/**
	 * description 内存释放
	 * param MemoryBlock myMemoryBlock 内存块
	 * return void
	 * author pc
	 * createTime  2021/10/10 11:54
	 **/
	public void release(MemoryBlock myMemoryBlock)
	{
		ProcessXMLController.MemoryRelease(myMemoryBlock);
		myMemoryBlockList.remove(myMemoryBlock);
	}

	public MemoryBlock getMemoryBlock(Address address) {
		for (MemoryBlock memoryBlock : myMemoryBlockList) {
			if (memoryBlock.getAddress() == address) {
				return memoryBlock;
			}
		}
		return null;
	}

	/* *
	 * 创建排序的函数，每次新增内存块都要排序一次
	 */
	public void sortList() {
		MemoryBlock myMemoryBlock;
		for(int i = 0;i < myMemoryBlockList.size(); i++) {
			for(int j = 0; j < myMemoryBlockList.size() - i - 1; j++) {
				if(myMemoryBlockList.get(j).getStartPosition() > myMemoryBlockList.get(j+1).getStartPosition()) {
					myMemoryBlock = myMemoryBlockList.get(j);
					myMemoryBlockList.set(j, myMemoryBlockList.get(j + 1));
					myMemoryBlockList.set(j + 1, myMemoryBlock);
				}
			}
		}
	}
}
