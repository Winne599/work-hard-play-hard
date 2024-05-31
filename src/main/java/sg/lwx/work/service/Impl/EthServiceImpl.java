package sg.lwx.work.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
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
import sg.lwx.work.domain.*;
import sg.lwx.work.mapper.ContractInfoMapper;
import sg.lwx.work.mapper.CurrentBlockNoMapper;
import sg.lwx.work.mapper.TransactionMapper;
import sg.lwx.work.service.EthService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class EthServiceImpl implements EthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthServiceImpl.class);
    private Web3j web3j;
    // alchemy arbitrum-sepolia node
    private final String arbiEthServiceUrl = "https://arb-sepolia.g.alchemy.com/v2/r1oIu-hvSB5hcfXO3Fn55aWi-dulTdaT";

    private static final String mainAlchemyUrl = "https://eth-mainnet.g.alchemy.com/v2/NKpBa-CS4J5HVtThu--luWbdIdzozMCY";

    private static final String ERC_20_TOKEN_TYPE = "erc20-token";

    @Autowired
    private CurrentBlockNoMapper currentBlockNoMapper;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private ContractInfoMapper contractInfoMapper;


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


    @Override
    public JSONObject getColdWalletTxInfo(String walletAddress, List<String> contractAddressList) throws IOException {
        JSONObject result = new JSONObject();

        web3j = Web3j.build(new HttpService(mainAlchemyUrl));

        BigInteger toBlockNo = web3j.ethBlockNumber().send().getBlockNumber();
        CurrentBlockNo tokenBlockNo = currentBlockNoMapper.selectByType(ERC_20_TOKEN_TYPE);
        var fromBlockNo = tokenBlockNo.getBlockNo();


        fromBlockNo = new BigInteger("19838058");
        toBlockNo = new BigInteger("19838059");

        LOGGER.info("scan, from block: {}, to block: {}", fromBlockNo, toBlockNo);


        if (fromBlockNo.compareTo(toBlockNo) == 1) {
            return result;
        }

        BigInteger currentBlockNo = fromBlockNo;
        while (currentBlockNo.compareTo(toBlockNo) < 0) {
            EthFilter ethFilter = new EthFilter(
                    new DefaultBlockParameterNumber(fromBlockNo),
                    new DefaultBlockParameterNumber(toBlockNo),
                    contractAddressList
            );
            ethFilter.addOptionalTopics("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
            EthLog ethLog = web3j.ethGetLogs(ethFilter).send();


            List<EthLog.LogResult> logList = ethLog.getLogs();
            Iterator iterator = logList.iterator();
            while (iterator.hasNext()) {
                EthLog.LogObject logObject = (EthLog.LogObject) iterator.next();
                String contractAddress = logObject.getAddress();
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


                boolean deposit = walletAddress.equalsIgnoreCase(toAddress.toLowerCase());
                boolean withdraw = walletAddress.equalsIgnoreCase(fromAddress.toLowerCase());
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

                org.web3j.protocol.core.methods.response.Transaction transactionOnChain = web3j.ethGetTransactionByHash(transactionHash).send().getResult();
                LOGGER.info("transaction details: {}", JSON.toJSONString(transactionOnChain));
                Transaction transactionDb = transactionMapper.selectByHash(transactionHash);
                if (Optional.ofNullable(transactionDb).isPresent()) {
                    // 幂等控制
                    continue;
                }

                String currencyId = null;
                String currencyName = null;
                Integer tokenDecimals = null;

                // 根据 contract 查询信息
                if (StringUtils.isNotEmpty(contractAddress)) {
                  ContractInfo contractInfo = contractInfoMapper.selectByContractAddress(contractAddress);
                  if (contractInfo != null){
                      currencyId = contractInfo.getTokenSymbol();
                      currencyName = contractInfo.getTokenName();
                      tokenDecimals = contractInfo.getTokenDecimals();
                  }


                }


                Transaction transaction = new Transaction();
                if (deposit) {
                    transaction.setTransactionHash(transactionHash);
                    transaction.setNetwork("ETH");
                    transaction.setNonce(BigInteger.ZERO);
                    transaction.setFromAddress(transactionOnChain.getFrom());
                    transaction.setToAddress(transactionOnChain.getTo());
                    transaction.setCurrencyId(currencyId);
                    transaction.setCurrencyName(currencyName);
                    transaction.setContractAddress(contractAddress);
                    transaction.setBalanceDelta(new BigDecimal(value));
                    transaction.setAmount(transaction.getBalanceDelta().movePointLeft(tokenDecimals));
                    transaction.setGasFee(new BigDecimal(0));
                    transaction.setCurrBlockNo(transactionOnChain.getBlockNumber());
                    transaction.setType((TransactionTypeEnum.RECEIVE.getId()));
                    transaction.setStatus(TransactionStatusEnum.ON_CHAIN.getId());
                    // insert db
                    int count = transactionMapper.insert(transaction);
                    if (count != 1) {
                        LOGGER.error("insert deposit transaction failed");
                    }


                    // 存数据库
                }

                if (withdraw) {
                    // 存数据库
                    transaction.setTransactionHash(transactionHash);
                    transaction.setNetwork("ETH");
                    transaction.setNonce(transactionOnChain.getNonce());

                    transaction.setFromAddress(transactionOnChain.getFrom());
                    transaction.setToAddress(transactionOnChain.getTo());
                    transaction.setCurrencyId(currencyId);
                    transaction.setCurrencyName(currencyName);
                    transaction.setContractAddress(contractAddress);
                    transaction.setBalanceDelta(new BigDecimal(value));
                    transaction.setAmount(transaction.getBalanceDelta().movePointLeft(tokenDecimals));
                    transaction.setGasFee(new BigDecimal(0)); // todo
                    transaction.setCurrBlockNo(transactionOnChain.getBlockNumber());
                    transaction.setType((TransactionTypeEnum.SEND.getId()));
                    transaction.setStatus(TransactionStatusEnum.ON_CHAIN.getId());
                    // insert db
                    int count = transactionMapper.insert(transaction);
                    if (count != 1) {
                        LOGGER.error("insert withdraw transaction failed");
                    }
                }
            }
            currentBlockNo = currentBlockNo.add(BigInteger.ONE);
        }
        return result;
    }


    public TransactionReceipt getTransactionReceipt(String transactionHash) throws IOException {
        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
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
