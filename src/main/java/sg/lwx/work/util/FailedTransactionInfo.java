package sg.lwx.work.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * transaction hash 哈希
 */
public class FailedTransactionInfo {

    public static void main(String[] args) {
        String transactionHash = "0x8deff9cdcdfd6ca85e22aea9b0d462c7475e1a36e5c9c181bf2c120463de3ea8";

        //  Web3j web3j = Web3j.build(new HttpService("https://goerli.infura.io/v3/1921523b28104a489f7337c3d1f68822"));
        //  Web3j web3j = Web3j.build(new HttpService("https://eth-goerli.g.alchemy.com/v2/WQdUQg73So5Lx0uoFdEfX_I2AyU2XZFm"));
        //   Web3j web3j = Web3j.build(new HttpService("https://sepolia.infura.io/v3/1921523b28104a489f7337c3d1f68822"));
        Web3j web3j = Web3j.build(new HttpService("https://eth-sepolia.g.alchemy.com/v2/WQdUQg73So5Lx0uoFdEfX_I2AyU2XZFm"));




        try {
            //
            EthGetTransactionReceipt transactionReceiptResponse = web3j
                    .ethGetTransactionReceipt(transactionHash)
                    .send();

            if (transactionReceiptResponse.hasError()) {
                System.out.println("Error retrieving transaction receipt: " + transactionReceiptResponse.getError().getMessage());
            } else {
                TransactionReceipt transactionReceipt = transactionReceiptResponse.getResult();
                if (transactionReceipt != null) {
                    if (transactionReceipt.isStatusOK()) {
                        System.out.println("Transaction succeeded.");
                    } else {
                        System.out.println("Transaction failed. Error message: " + transactionReceipt.getRevertReason());
                    }
                } else {
                    System.out.println("Transaction receipt not available.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

