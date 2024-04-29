package sg.lwx.work.util;

import java.math.BigDecimal;

/**
 * todo 运行的时候如果报错 ”错误: 编码 UTF-8 的不可映射字“，可以点击 File——>File Properties——>File Encoding 然后改成 GBK
 * baseGwei: base gas price,根据网络波动决定
 * maxGwei: 可以接受的 最大 gas price， 自己设定
 * maxPriorityGwei： 愿意出给矿工的 gas price，自己设定
 * gasLimitUsages：链上叫 Usage by Txn， 实际上消耗的 gas （数量）
 * <p>
 * <p>
 * 实际上的愿意出给矿工的 gas price：min(maxGwei-baseGwei,maxPriorityGwei)，
 * 如果 maxGwei >= baseGwei + maxPriorityGwei, 则给矿工 gas price = maxPriorityGwei
 * 如果 maxGwei < baseGwei + maxPriorityGwei,则给矿工 maxGwei-baseGwei
 * 原理：maxGwei 代表你愿意出的最大单价，如果付给网络波动的和之前设定好给矿工的gas price 绰绰有余，那就会给  之前设定好给矿工的gas price；
 * 如果 maxGwei 给完网络波动的，剩下的不够给 之前说好给矿工的，那也没办法，总共就这么多，矿工只能拿走剩下那一部分了，尽管没达到之前设定好给他金额
 * 所以会取这两个中的最小值（min是取最小值的意思）
 * <p>
 * gas price = base + 实际给矿工的；
 * Transaction fee = gas price * usage;
 * burnt fee = base * usage;
 * mine fee = 实际上给矿工的 * usage;
 * max fee = max * usage;
 * tx savings = max fee - burnt fee - mine fee; (所以如果是maxGwei < baseGwei + maxPriorityGwei情况，那 tx savings 一定是0了，因为付给矿工都不够呢，跟别说自己剩余的了）
 *
 *
 * 传统交易（就是只给一个gas price 来作为 EIP1559中的base）底层其实也是走的 EIP1559。原理：把传统交易中的gas price 给到 EIP1559,把 EIP1559 的 Max 和 Max Priority 都设置成 gas price
 * 这样做的话，min(maxGwei-baseGwei,maxPriorityGwei)，一定是 maxGwei-baseGwei 最小，这样矿工就可以拿到除了烧掉的 的所有了，也就是符合传统交易了（传统交易就是除了烧掉的，剩下的全部给矿工）
 */
public class EIP1559Example {

    public static void main(String[] args) {
        System.out.println("aa我是1234");
        // https://etherscan.io/tx/0xfdfa80ab9049daaef2419fc91f777cfa367dc149e8229d7dcd8a9b613295784f

        BigDecimal maxGwei = new BigDecimal("30.842659783");
        BigDecimal baseGwei = new BigDecimal("24.118570932");
        BigDecimal maxPriorityGwei = new BigDecimal("0.01");
        Integer gasLimitUsages = 224521;

        System.err.println("Base:" + baseGwei + " Gwei");
        System.err.println("Max:" + maxGwei + " Gwei");
        System.err.println("Max Priority:" + maxPriorityGwei + " Gwei");
        System.err.println("Usage by Txn:" + gasLimitUsages);


        // mine gas price
        BigDecimal mineGasPriceGwei = maxGwei.subtract(baseGwei).min(maxPriorityGwei);
        System.out.println("mineGasPriceGwei:" + mineGasPriceGwei + " Gwei");
        if (mineGasPriceGwei.compareTo(new BigDecimal("0")) == -1) {
            System.out.println("Gas fee低于base，不能被打包上链");
            return;
        }
        // gas price
        BigDecimal gasPriceGwei = baseGwei.add(mineGasPriceGwei);
        System.out.println("Gas Price:" + gasPriceGwei + " Gwei");


        // burnt fee
        BigDecimal burntFeeGwei = baseGwei.multiply(BigDecimal.valueOf(gasLimitUsages));
        BigDecimal burntFee = gweiConvertEth(burntFeeGwei);
        System.err.println("Burnt:" + burntFee + " ETH");

        // max fee
        BigDecimal maxFeeGwei = maxGwei.multiply(BigDecimal.valueOf(gasLimitUsages));
        System.out.println("max Fee:" + maxFeeGwei + " Gwei");

        // mine Fee
        BigDecimal mineFeeGwei = mineGasPriceGwei.multiply(BigDecimal.valueOf(gasLimitUsages));
        System.out.println("mine fee:" + mineFeeGwei + " Gwei");

        // Transaction fee
        BigDecimal transactionFeeGwei = gasPriceGwei.multiply(BigDecimal.valueOf(gasLimitUsages));
        BigDecimal transactionFee = gweiConvertEth(transactionFeeGwei);
        System.err.println("Transaction Fee:" + transactionFee + " ETH");

        // savings fee
        BigDecimal txnSavingsGwei = maxFeeGwei.subtract(mineFeeGwei).subtract(burntFeeGwei);
        BigDecimal txnSavingsETH = gweiConvertEth(txnSavingsGwei);
        System.err.println("Txn Savings:" + txnSavingsETH + " ETH");


        //---------------------------------------分割线---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        BigDecimal wei = ethConvertWei(new BigDecimal("0.031977948294870523"));
        System.out.println("wei:" + wei);

        BigDecimal gweiConvertWei = gweiConvertWei(new BigDecimal("32.029060848"));
        System.out.println("gweiConvertWei:" + gweiConvertWei);


        BigDecimal weiConvertGwei = weiConvertGwei(new BigDecimal("40898948732"));
        System.out.println("weiConvertGwei:" + weiConvertGwei);



    }


    /**
     * Gwei 转成 ETH
     *
     * @param gwei
     * @return
     */
    private static BigDecimal gweiConvertEth(BigDecimal gwei) {
        BigDecimal eth = gwei.movePointLeft(9);
        return eth;
    }

    private static BigDecimal gweiConvertWei(BigDecimal gwei) {
        BigDecimal wei = gwei.movePointRight(9);
        return wei;
    }

    private static BigDecimal weiConvertGwei(BigDecimal wei) {
        BigDecimal gwei = wei.movePointLeft(9);
        return gwei;
    }


    private static BigDecimal ethConvertWei(BigDecimal eth) {
        BigDecimal wei = eth.movePointRight(18);
        return wei;
    }
}

