package com.ansheng.model.hardware;

public class CPUModel {
    /**
     * cpu核数
     */
    private int CPUCoreNum;
    /**
     * cpu最大线程数
     */
    private int CPUThreadNum;
    /**
     * cpu使用情况。
     */
    private double CPUUsed;
    /**
     * CPU 描述
     */
    private String CPUDescribe;

    public int getCPUCoreNum() {
        return CPUCoreNum;
    }

    public void setCPUCoreNum(int CPUCoreNum) {
        this.CPUCoreNum = CPUCoreNum;
    }

    public int getCPUThreadNum() {
        return CPUThreadNum;
    }

    public void setCPUThreadNum(int CPUThreadNum) {
        this.CPUThreadNum = CPUThreadNum;
    }

    public double getCPUUsed() {
        return CPUUsed;
    }

    public void setCPUUsed(double CPUUsed) {
        this.CPUUsed = CPUUsed;
    }

    public String getCPUDescribe() {
        return CPUDescribe;
    }

    public void setCPUDescribe(String CPUDescribe) {
        this.CPUDescribe = CPUDescribe;
    }
}
