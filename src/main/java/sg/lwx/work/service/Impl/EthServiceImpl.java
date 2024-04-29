package sg.lwx.work.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import sg.lwx.work.service.EthService;
import sg.lwx.work.util.TestL2Network;

import java.io.IOException;
import java.math.BigInteger;

@Service
public class EthServiceImpl implements EthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestL2Network.class);
    private Web3j web3j;
    // alchemy  arbitrum-sepolia node
    private final String arbiEthServiceUrl = "https://arb-sepolia.g.alchemy.com/v2/r1oIu-hvSB5hcfXO3Fn55aWi-dulTdaT";

    @Override
    public JSONObject testEthTransferOnArbitrum(String fromAddress, String toAddress, BigInteger value, BigInteger maxFeePerGas) throws IOException {
        web3j = Web3j.build(new HttpService(arbiEthServiceUrl));

        BigInteger nonce = this.getNonce(fromAddress, DefaultBlockParameterName.PENDING);
        BigInteger gasLimit = BigInteger.valueOf(31000L);
        long chainId = 421614; // sepolia arbitrum
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(0L);

        maxFeePerGas = BigInteger.valueOf(3181645989L);


        /**
         * long chainId,
         * BigInteger nonce,
         * BigInteger gasLimit,
         * String to,
         * BigInteger value,
         * BigInteger maxPriorityFeePerGas,
         * BigInteger maxFeePerGas
         */
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                chainId,
                nonce,
                gasLimit,
                toAddress,
                value,
                maxPriorityFeePerGas,
                maxFeePerGas
        );
        LOGGER.info("sendTransaction transferEth ===> chainId: {}, nonce: {}, gasLimit: {}, value: {}, maxPriorityFeePerGas: {}, maxFeePerGas: {}", chainId, nonce, gasLimit, value, maxPriorityFeePerGas, maxFeePerGas);

        Credentials credentials = this.getCredentials();
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String signedMessageHex = Numeric.toHexString(signedMessage);

        Response rawResponse = web3j.ethSendRawTransaction(signedMessageHex).send();
        LOGGER.info("transferEth response: {}", JSONObject.toJSONString(rawResponse));

        JSONObject response = (JSONObject) JSON.toJSON(rawResponse);
        return response;
    }

    private BigInteger getNonce(String address, DefaultBlockParameterName defaultBlockParameterName) throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, defaultBlockParameterName).send();
        if (ethGetTransactionCount == null) {
            return null;
        }
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        return nonce;
    }


    private Credentials getCredentials() {
        // 0xF3b2e8eb0A56cBa18faB91E762a67ECB15198eb9
        BigInteger privateKeyValue = new BigInteger("563fb23f8356e5eb4b21dad25dbda3c1e25827d241d503668b0f2c6806a01587", 16);
        ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyValue);
        Credentials credential = Credentials.create(ecKeyPair);
        return credential;
    }
}
