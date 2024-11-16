package sg.lwx.work.util.eth;

import com.alibaba.fastjson.JSON;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import sg.lwx.work.util.CommonConstant;

import java.io.IOException;
import java.util.Optional;

public class Web3jUtil {


    public static TransactionReceipt getTransactionReceipt(String transactionHash) throws IOException {
        Web3j web3j = Web3j.build(new HttpService(CommonConstant.ALCHEMY_SEPOLIA_API_KEY));
        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
        Optional<TransactionReceipt> optional = transactionReceipt.getTransactionReceipt();
        if (optional.isPresent()) {
            TransactionReceipt receipt = optional.get();// status = 0x0 是Failed
            System.out.println("receipt: " + JSON.toJSONString(receipt));
            return receipt;
        } else {
            return null;
        }
    }


    public static void main(String[] args) throws IOException {
        getTransactionReceipt("0x2cdceffadad3b108b951ec34cd1c60aa99514475a8543c4e12ea8d8c469c8080"); // failed
        //  getTransactionReceipt("0x08f829087f78e9f6bfbb192a4380e8cb06f05f9c3ef650640d03c57610b7d85f");


    }


}
