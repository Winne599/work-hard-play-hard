package sg.lwx.work.util.btc;

import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;

import java.util.List;

public class FindVoutExample {

    public static void main(String[] args) {
        NetworkParameters params = TestNet3Params.get();

        // 假设你有交易哈希和目标地址
        String txHash = "c083e699a42781ad496e2bfeaf8726becfdd61be9360602ec3c08941d73fc856";
        String addressStr = "mhKU81FCyVusRdUxYFkxo5TkG2HQVLMDrh";

        // 创建一个地址对象
        Address address = Address.fromString(params, addressStr);

        // 获取交易信息
        Transaction transaction = getTransactionByHash(txHash);

        // 查找 vout
        int vout = findVoutByAddress(transaction, address);
        if (vout != -1) {
            System.out.println("Vout for the address is: " + vout);
        } else {
            System.out.println("Address not found in the transaction outputs.");
        }
    }

    public static Transaction getTransactionByHash(String txHash) {
        // 这里你需要实现一个方法从区块链或节点获取交易信息
        // 这是一个占位符，需要根据具体情况实现
        return null;
    }

    public static int findVoutByAddress(Transaction transaction, Address address) {
        List<TransactionOutput> outputs = transaction.getOutputs();

        for (int i = 0; i < outputs.size(); i++) {
            TransactionOutput output = outputs.get(i);
            Script scriptPubKey = output.getScriptPubKey();

            if (scriptPubKey.getToAddress(transaction.getParams()).equals(address)) {
                // 返回 vout
                return i;
            }
        }
        // 地址未在输出中找到
        return -1;
    }
}

