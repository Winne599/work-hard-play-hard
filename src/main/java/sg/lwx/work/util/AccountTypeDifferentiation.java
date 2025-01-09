package sg.lwx.work.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class AccountTypeDifferentiation {

    public static void main(String[] args) {
        String address = "0x0EFA0337740b45434be57d151De44476806aFcfE";  // replace your address

        //  Web3j web3j = Web3j.build(new HttpService("https://goerli.infura.io/v3/1921523b28104a489f7337c3d1f68822"));

        Web3j web3j = Web3j.build(new HttpService("https://eth-sepolia.g.alchemy.com/v2/60lgTZwoGyr12FPegLDQqHQSsVIW6CPj"));

        try {
            // get contract
            EthGetCode ethGetCode = web3j
                    .ethGetCode(address, DefaultBlockParameterName.LATEST)
                    .send();

            String code = ethGetCode.getCode();

            // 判断合约地址还是账户地址
            if (code != null && !code.equals("0x")) {
                System.out.println("This is a contract account.");
            } else {
                System.out.println("This is a regular account.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

