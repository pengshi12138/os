package com.niuniukeaiyouhaochi.os.memory;

import javafx.scene.paint.Color;

import java.util.Random;

public class MemoryBlock {
    private Address address;        // 内存块起始位置
    private int endPosition;        // 内存块终止位置
    private String PID;             // 占用内存的进程序号
    private int length;             // 内存块长度
    private boolean isEmpty;        // 占用状态
    private Color color;			// 设置内存块颜色
    public static Color randomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.rgb(r, g, b);
    }

    public String getPID() {
        return PID;
    }

    public MemoryBlock(int startPosition, int length, boolean isEmpty, String PID){
        address = new Address(startPosition, startPosition + length - 1);
        this.endPosition = startPosition + length - 1;
        this.length = length;
        this.isEmpty = isEmpty;
        this.PID = PID;
        this.color = randomColor();
    }

    public MemoryBlock(MemoryBlock block,boolean isEmpty){
        this(block.getStartPosition(),block.getLength(),isEmpty, "-1");
    }

    public int getEndPosition() {
        return endPosition;
    }

    public Color getColor() {
        return color;
    }

    public int getStartPosition(){
        return address.getStartAddress();
    }

    public Address getAddress() {
        return address;
    }

    public int getLength(){
        return length;
    }

    public boolean isEmpty(){
        return  isEmpty;
    }

    public boolean equals(MemoryBlock block){
        return block.getLength() == this.getLength() && block.getStartPosition()==this.getStartPosition();
    }
}
