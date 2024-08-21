package sg.lwx.work.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthMaxPriorityFeePerGas;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class GetGasPrice {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Web3j web3j = Web3j.build(new HttpService(CommonConstant.ALCHEMY_MAINNET_API_KEY));

        EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        System.err.println("gasPrice: " + gasPrice);

        EthMaxPriorityFeePerGas ethMaxPriorityFeePerGas = web3j.ethMaxPriorityFeePerGas().sendAsync().get();
        BigInteger maxPriorityFeePerGas = ethMaxPriorityFeePerGas.getMaxPriorityFeePerGas();
        System.out.println("maxPriorityFeePerGas: " + maxPriorityFeePerGas);
    }
}
