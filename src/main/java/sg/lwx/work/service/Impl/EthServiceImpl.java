package sg.lwx.work.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import sg.lwx.work.domain.GetColdWalletTxInfo;
import sg.lwx.work.service.EthService;
import sg.lwx.work.util.TestL2Network;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

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


    //    private static String ethServiceUrl = "";
    private static final String mainAlchemyUrl = "https://eth-mainnet.g.alchemy.com/v2/NKpBa-CS4J5HVtThu--luWbdIdzozMCY";

    @Override
    public JSONObject getColdWalletTxInfo() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        var secondsPerDay = 60 * 60 * 24;
        System.out.println("secondsPerDay= " + secondsPerDay);

        var ethBlock = 13;
        var blockNumberPerDay = secondsPerDay / ethBlock;
        System.out.println("blockNumberPerDay= " + blockNumberPerDay);

        var blockNumberPerMonth = blockNumberPerDay * 1; // 1 天


        // 定义返回结构
        List<GetColdWalletTxInfo> infoList = new ArrayList<>();


        // 获取区块最新高度
        // 监控 【blockNumberPerMonth,最新区块高度】 的链上内容
        // 监控内容：ETH，合约（想监控的币种）
        // 可以生成一个json，然后遍历这个json，计算一下这个月入账多少，转出多少

        Web3j web3j = Web3j.build(new HttpService(mainAlchemyUrl));

        BigInteger ethereumBlockNo = web3j.ethBlockNumber().send().getBlockNumber();
        var currentBlockNo = ethereumBlockNo.subtract(BigInteger.valueOf(blockNumberPerMonth));

        String usdtContractAddress = "0xdAC17F958D2ee523a2206206994597C13D831ec7"; // mainnet
        String coldWalletAddress = "0xc9F9F694Ae147b1333c4f53266c5ccd5c6E4c04f";


        LOGGER.info("scan, from block: {}, to block: {}",currentBlockNo,ethereumBlockNo);



        EthFilter ethFilter = new EthFilter(
                new DefaultBlockParameterNumber(currentBlockNo),
                new DefaultBlockParameterNumber(ethereumBlockNo),
                usdtContractAddress
        );
        ethFilter.addOptionalTopics("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
        EthLog ethLog = web3j.ethGetLogs(ethFilter).send();


        List<EthLog.LogResult> logList = ethLog.getLogs();
        Iterator iterator = logList.iterator();
        while (iterator.hasNext()) {
            EthLog.LogObject logObject = (EthLog.LogObject) iterator.next();

            // topicList[0] 事件方法名；topicList[1] 发送者；topicList[2] 接收者；topicList[3] 数量
            List<String> topicList = logObject.getTopics();
            String fromAddress = topicList.get(1).replaceFirst("0x000000000000000000000000", "0x");
            String toAddress = topicList.get(2).replaceFirst("0x000000000000000000000000", "0x");

            BigInteger value;
            String data = logObject.getData();
            if ("0x".equals(data)) {
                value = Numeric.decodeQuantity(topicList.get(3));
            } else {
                value = Numeric.decodeQuantity(data);
            }

            // 防止 0 转账攻击
            if (BigInteger.ZERO.compareTo(value) == 0) {
                continue;
            }

            // 如果接收者是 0 地址，则直接弃用该条日志
            if ("0x0000000000000000000000000000000000000000000000000000000000000000".equals(topicList.get(2))) {
                continue;
            }

            boolean deposit = coldWalletAddress.equals(toAddress.toLowerCase());
            boolean withdraw = coldWalletAddress.equals(fromAddress.toLowerCase());
            if (!deposit && !withdraw) {
                continue;
            }

            String transactionHash = logObject.getTransactionHash();


            TransactionReceipt receipt = this.getTransactionReceipt(transactionHash);
            LOGGER.info("{} TransactionReceipt: {}", transactionHash, JSONObject.toJSONString(receipt));
            // 排除失败的 transactionHash
            if ("0x0".equals(receipt.getStatus())) {
                LOGGER.info("get failed transaction, hash:{}, TransactionReceipt: {}", transactionHash, JSONObject.toJSONString(receipt));
                continue;
            }

            // 代币转账到冷钱包
            if (deposit) {
                GetColdWalletTxInfo info = new GetColdWalletTxInfo();
                info.setType(2);
                String amountHex = receipt.getLogs().get(0).getData();
                BigInteger amount = new BigInteger(amountHex.substring(2), 16);
                info.setAmount(amount);

                infoList.add(info);
            }

            // 由延迟扫块去保证 transaction 一定已经存在了，此处不再进行判断
            if (withdraw) {
                GetColdWalletTxInfo info = new GetColdWalletTxInfo();
                info.setType(1);
                String amountHex = receipt.getLogs().get(0).getData();
                BigInteger amount = new BigInteger(amountHex.substring(2), 16);
                info.setAmount(amount);

                infoList.add(info);
            }
        }

        BigInteger receivedAmount = null;
        BigInteger sentAmount = null;


        if (infoList.size() > 0){
            for (GetColdWalletTxInfo info: infoList){
                if (info.getType() == 1){
                    sentAmount.add(info.getAmount());
                }
                if (info.getType() == 2){
                    receivedAmount.add(info.getAmount());
                }
            }
        }

        LOGGER.info("sent amount: {}", sentAmount);
        LOGGER.info("received amount: {}", receivedAmount);


        BigDecimal receivedResult = null;
        BigDecimal sentResult = null;

        JSONObject jsonObject = new JSONObject();
        if (receivedAmount != null){
            receivedResult = new BigDecimal(receivedAmount).movePointLeft(6); // 因为usdt的精度是6
        }
        if (sentAmount != null){
            sentResult = new BigDecimal(sentAmount).movePointLeft(6); // 因为usdt的精度是6
        }


        jsonObject.put("receivedResult",receivedResult);
        jsonObject.put("sentResult",sentResult);






        return jsonObject;
    }


    public TransactionReceipt getTransactionReceipt(String transactionHash) throws IOException {
        EthGetTransactionReceipt transactionReceipt = this.web3j.ethGetTransactionReceipt(transactionHash).send();
        Optional<TransactionReceipt> optional = transactionReceipt.getTransactionReceipt();
        if (optional.isPresent()) {
            TransactionReceipt receipt = optional.get();
            return receipt;
        } else {
            return null;
        }
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
