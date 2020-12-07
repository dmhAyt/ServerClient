package com.ansheng.model.hardware;

public class HardDiskCapacity {
    /**
     * 总容量
     */
    private long TotalCapacity = 0L;
    /**
     * 可用容量
     */
    private long AvailableCapacity = 0L;
    /**
     * 已使用容量
     */
    private long UsableCapacity = 0L;

    public long getTotalCapacity() {
        return TotalCapacity;
    }

    public void setTotalCapacity(long totalCapacity) {
        TotalCapacity = totalCapacity;
    }

    public long getAvailableCapacity() {
        return AvailableCapacity;
    }

    public void setAvailableCapacity(long availableCapacity) {
        AvailableCapacity = availableCapacity;
    }

    public long getUsableCapacity() {
        return UsableCapacity;
    }

    public void setUsableCapacity(long usableCapacity) {
        UsableCapacity = usableCapacity;
    }

    @Override
    public String toString() {
        return "HardDiskCapacity{" +
                "总容量=" + TotalCapacity/1024/1024/1024 +
                "G, 可用容量=" + AvailableCapacity/1024/1024/1024 +
                "G, 已使用=" + UsableCapacity/1024/1024/1024 +
                "G}";
    }
}
