package sg.lwx.work.util;

import com.alibaba.fastjson.JSONObject;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.Optional;

public class EthTransactionReceipt {

//    public static void main(String[] args) throws IOException {
//        String transactionHash = "0x0d4317869a36108b51a98060cd810af60918b23069d6d46d6f5b9a935e88a949";
//        Web3j web3j = Web3j.build(new HttpService("https://eth-sepolia.g.alchemy.com/v2/60lgTZwoGyr12FPegLDQqHQSsVIW6CPj"));
//
//
//        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
//        Optional<TransactionReceipt> optional = transactionReceipt.getTransactionReceipt();
//        if (optional.isPresent()) {
//            TransactionReceipt receipt = optional.get();
//            System.err.println("receipt: "+JSONObject.toJSONString(receipt));
//        } else {
//            System.err.println("error..................");
//        }
//    }


    public static void main(String[] args) throws Exception {
       Web3j web3j = Web3j.build(new HttpService("https://eth-sepolia.g.alchemy.com/v2/60lgTZwoGyr12FPegLDQqHQSsVIW6CPj"));
        // 交易哈希
        String txHash = "0xcf66d30de56fc957ddc9763e8f2feee087d47aa0c1bd5baacf1c4bd257b7f747";

        // 获取交易详情
        EthTransaction ethTransaction = web3j.ethGetTransactionByHash(txHash).send();

        if (ethTransaction.getTransaction().isPresent()) {
            Transaction transaction = ethTransaction.getTransaction().get();

            // 获取 maxFeePerGas 和 maxPriorityFeePerGas
            BigInteger maxFeePerGas = transaction.getMaxFeePerGas();
            BigInteger maxPriorityFeePerGas = transaction.getMaxPriorityFeePerGas();

            System.err.println("maxFeePerGas: " + maxFeePerGas);
            System.err.println("maxPriorityFeePerGas: " + maxPriorityFeePerGas);
        } else {
            System.out.println("Transaction not found.");
        }

    }


}
