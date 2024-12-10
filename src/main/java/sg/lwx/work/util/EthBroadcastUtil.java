package sg.lwx.work.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.http.HttpService;
import sg.lwx.work.constant.CommonConstant;

import java.util.concurrent.ExecutionException;

/**
 * @author lianwenxiu
 *
 */
public class EthBroadcastUtil {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String signedTransactionData = "0xf8aa0385064acf286d8301388094e980e37de697598e0999d09b563e528be6e6731680b844a9059cbb000000000000000000000000f3b2e8eb0a56cba18fab91e762a67ecb15198eb900000000000000000000000000000000000000000000000000000000002dc6c01ca07524c95df725daddc2c67a62ca7806020b3d955e8de7528599193b0f6324c1cda0785c57f419549eb04d478795e25b1021a9dff38b8ed9a4272627749441fc21ad";


        Web3j web3j = Web3j.build(new HttpService(CommonConstant.ALCHEMY_SEPOLIA_API_KEY2));
        Response ethSendTransactionResponse = web3j.ethSendRawTransaction(signedTransactionData).sendAsync().get();

        System.out.println("2d2f2dfd2f2d");
        System.err.println("ethSendTransactionResponse:"+ethSendTransactionResponse);

    }


}

