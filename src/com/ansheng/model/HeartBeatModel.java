package com.ansheng.model;

public class HeartBeatModel {

    /**
     * 心跳维持功能:
     * 	1. 需要发送的信息：
     * 		1） 机器的唯一ID。
     * 		2） 机器的IP(从连接中可以获得)
     * 		3） 身份【normal,leader,s_leader】
     * 		4） 组内编号。（一样着表示全部用于备份。）
     * 		5） 事件端口
     * 		6） 总空间（组长时：组内编号不一样的总容量之和）
     * 		7） 已使用空间（组长时：组内编号不一样的已使用容量之和）
     * 		8） 可用空间（组长时：组内编号不一样时的最小容量之和）
     * 		9） 系统版本。（组长时：为组长本身）
     * 		10） 连接数。（组长时：为组长本身）
     * 		11） 宽带使用情况。（组长时：为组长本身）
     * 		12） 内存使用情况。（组长时：为组长本身）
     * 		13） CPU使用情况。（组长时：为组长本身）
     *
     * 	2. 需要发送的时间，每30s发送一次。
     *
     * 	3. 对于组长：需要和多个跟踪服务器进行连接，发送相同的数据。（未实现）
     *
     * 	4. 对于副组长：
     * 		1） 需要和跟踪服务器建立连接【多个跟踪服务器】，获得组长的IP和端口。（未实现）
     * 		2） 和组长服务建立连接，发送实时心跳
     *
     * 	5. 心跳管理：对于组长、副组长和跟踪服务器应把第1条的信息记录到内存中。
     */

    /**
     * 唯一ID
     */
    private String ID;
    /**
     * 身份，正常组员，组长-正，组长-副
     * Normal,Leader,S_Leader
     */
    private String Role;
    /**
     * 组内编号
     */
    private int GroupNum;
    /**
     * 处理绑定端口-文件上传下载的
     */
    private int FP;
    /**
     * 总的可用空间，单位B
     */
    private long TS;
    /**
     * 已使用的空间，单位B
     */
    private long US;
    /**
     * 可用的空间，单位B
     */
    private long FS;
    /**
     * 系统类型，Windows,Linux,Unix等
     */
    private String SysName;
    /**
     * 连接数
     */
    private int ConnectNum;
    /**
     * 宽带使用情况，已使用/总宽带
     */
    private String NetUsed;
    /**
     * 内存使用情况; 已使用/总大小；单位B
     */
    private String MemoryUsed;
    /**
     * CPU使用情况。
     */
    private double CPUUsed;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * 构造器
     * @param role 身份
     * @param groupNum 组内编号
     * @param FP    处理绑定的端口
     * @param TS    总大小
     * @param US    已使用大小
     * @param FS    可用大小
     */
    public HeartBeatModel(String ID,String role,int groupNum, int FP, long TS, long US, long FS) {
        this.ID = ID;
        this.Role = role;
        this.FP = FP;
        this.TS = TS;
        this.US = US;
        this.FS = FS;
        this.GroupNum = groupNum;
    }

    public HeartBeatModel() {

    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public int getGroupNum() {
        return GroupNum;
    }

    public void setGroupNum(int groupNum) {
        GroupNum = groupNum;
    }

    public int getFP() {
        return FP;
    }

    public void setFP(int FP) {
        this.FP = FP;
    }

    public long getTS() {
        return TS;
    }

    public void setTS(long TS) {
        this.TS = TS;
    }

    public long getUS() {
        return US;
    }

    public void setUS(long US) {
        this.US = US;
    }

    public long getFS() {
        return FS;
    }

    public void setFS(long FS) {
        this.FS = FS;
    }

    public String getSysName() {
        return SysName;
    }

    public void setSysName(String sysName) {
        SysName = sysName;
    }

    public int getConnectNum() {
        return ConnectNum;
    }

    public void setConnectNum(int connectNum) {
        ConnectNum = connectNum;
    }

    public String getNetUsed() {
        return NetUsed;
    }

    public void setNetUsed(String netUsed) {
        NetUsed = netUsed;
    }

    public String getMemoryUsed() {
        return MemoryUsed;
    }

    public void setMemoryUsed(String memoryUsed) {
        MemoryUsed = memoryUsed;
    }

    public double getCPUUsed() {
        return CPUUsed;
    }

    public void setCPUUsed(double CPUUsed) {
        this.CPUUsed = CPUUsed;
    }
}
