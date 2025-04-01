package com.abd.server.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LongIdGenerator {

    // 上一次生成ID的时间戳（分钟级）
    private  volatile long lastTimestamp = getCurrentMinuteTimestamp();

    // 用于在同一分钟内生成多个ID时的序列号
    private  final AtomicInteger sequence = new AtomicInteger(0);

    // 获取当前时间戳（分钟级）
    private  long getCurrentMinuteTimestamp() {
        long millis = System.currentTimeMillis();
        return millis / (1000 * 60); // 转换为分钟级时间戳
    }

    // 生成唯一ID的静态方法
    public  long generateId() {
        long currentTimestamp = getCurrentMinuteTimestamp();

        // 如果当前时间小于上一次生成ID的时间（可能是时间回拨），则重置序列号并更新lastTimestamp
        if (currentTimestamp < lastTimestamp) {
            sequence.set(0);
            lastTimestamp = currentTimestamp;
        }

        // 获取当前分钟内的序列号，并递增
        int seq = sequence.getAndIncrement();

        // 检查序列号是否超过限制（这里假设每分钟最多生成100万个ID）
        if (seq >= 1000000) {
            throw new RuntimeException("ID generation rate exceeded the limit for this minute.");
        }

        // 更新上一次生成ID的时间戳
        lastTimestamp = currentTimestamp;

        // 计算ID：13位时间戳（分钟级，前面可以补0以保持位数） + 6位序列号
        // 注意：这里我们实际上没有真正地将ID限制为19位数字，而是生成了一个64位的Long值
        // 该值的数值部分看起来像是19位数字（前13位是时间戳，后6位是序列号）
        // 如果需要严格的19位数字ID，你可能需要考虑其他方法，比如使用BigInteger或自定义编码
        long id = (currentTimestamp * 1000000L) + seq; // 乘以1000000是为了将序列号加到时间戳后面

        return id;
    }

//    public static void main(String[] args) {
//        // 测试生成ID
//        for (int i = 0; i < 10; i++) {
//            long id = generateId();
//            System.out.println(id); // 输出的ID将是一个64位的Long值，但数值上看起来像是19位数字
//            // 注意：这里的输出实际上是超过19位的，因为Long类型是64位的
//            // 但是，如果你只关心数值部分的前19位，那么可以将其视为19位数字ID
//        }
//    }
}