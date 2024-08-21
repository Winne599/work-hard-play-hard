package sg.lwx.work.util;

import java.math.BigDecimal;

public class BitcoinFeeCalculator {

    public static void main(String[] args) {
        int numberOfInputs = 1;  // 交易输入的数量
        int numberOfOutputs = 1; // 交易输出的数量
        int feeRate = 3081;        // 每字节费用率（satoshi/byte）

        int estimatedSize = estimateTransactionSize(numberOfInputs, numberOfOutputs);
        long estimatedFee = calculateFee(estimatedSize, feeRate);

        System.out.println("Estimated Transaction Size: " + estimatedSize + " bytes");
        System.out.println("Estimated Transaction Fee: " + estimatedFee + " satoshis");

        BigDecimal b = new BigDecimal(estimatedFee);
        b = b.movePointLeft(8);
        System.out.println("Estimated Transaction Fee: " + b + " BTC");

    }

    /**
     * 估算交易大小
     *
     * @param inputs  输入的数量
     * @param outputs 输出的数量
     * @return 交易的估算大小（字节）
     */
    public static int estimateTransactionSize(int inputs, int outputs) {
        int inputSize = 148;  // 每个输入大约占148字节
        int outputSize = 34;  // 每个输出大约占34字节
        int baseSize = 10;    // 基本交易大小大约10字节

        return baseSize + (inputSize * inputs) + (outputSize * outputs);
    }

    /**
     * 计算交易费用
     *
     * @param size    交易的大小（字节）
     * @param feeRate 每字节费用率（satoshi/byte）
     * @return 交易费用（satoshis）
     */
    public static long calculateFee(int size, int feeRate) {
        return (long) size * feeRate;
    }
}

