package sg.lwx.work.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
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
import sg.lwx.work.constant.ExceptionConstant;
import sg.lwx.work.domain.*;
import sg.lwx.work.domain.enums.ChainIdEnum;
import sg.lwx.work.domain.exception.BusinessException;
import sg.lwx.work.mapper.*;
import sg.lwx.work.service.EthService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EthServiceImpl implements EthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthServiceImpl.class);
    private Web3j web3j;
    // alchemy arbitrum-sepolia node
    private final String arbiEthServiceUrl = "https://arb-sepolia.g.alchemy.com/v2/r1oIu-hvSB5hcfXO3Fn55aWi-dulTdaT";

    private static final String mainAlchemyUrl = "https://eth-mainnet.g.alchemy.com/v2/NKpBa-CS4J5HVtThu--luWbdIdzozMCY";

    private static final String sepoliaAlchemyUrl = "https://eth-sepolia.g.alchemy.com/v2/NKpBa-CS4J5HVtThu--luWbdIdzozMCY";

    private static final String ERC_20_TOKEN_TYPE = "erc20-token";

    @Autowired
    private CurrentBlockNoMapper currentBlockNoMapper;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private ContractInfoMapper contractInfoMapper;
    @Autowired
    private TransferRecordMapper transferRecordMapper;
    @Autowired
    private CredentialsMapper credentialsMapper;

    @Value("blockchain.network")
    private String network;

    /**
     * 初始化 以太坊节点
     */
    public EthServiceImpl() {
        this.web3j = Web3j.build(new HttpService(sepoliaAlchemyUrl));
    }

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


        org.web3j.crypto.Credentials credentials = this.getCredentials(fromAddress);
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
                    if (contractInfo != null) {
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

    @Override
    public JSONObject transferERC20(String from, String to, BigDecimal amount, String contractAddress, BigInteger gasLimit, BigInteger maxFeePerGas, BigInteger maxPriorityFeePerGas) throws IOException, BusinessException {
        LOGGER.info("===========transferERC20===========contractAddress: {}, fromAddress: {}, toAddress: {}, amount: {}", contractAddress, from, to, amount);

        TransferRecord record = new TransferRecord();
        record.setFrom(from);
        record.setTo(to);
        record.setAmount(amount);
        record.setContractAddress(contractAddress);
        record.setGasLimit(gasLimit);
        record.setMaxFeePerGas(maxFeePerGas);
        record.setMaxPriorityFeePerGas(maxPriorityFeePerGas);

        ContractInfo contractInfo = contractInfoMapper.selectByContractAddress(contractAddress);
        if (!Optional.ofNullable(contractInfo).isPresent()) {
            throw new BusinessException(ExceptionConstant.CONTRACT_IS_NOT_FOUND);
        }
        Integer decimals = contractInfo.getTokenDecimals();
        BigInteger amountGwei = amount.movePointRight(decimals).toBigInteger();
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(to),
                        new Uint256(amountGwei)),
                Arrays.asList(new TypeReference<Bool>() {
                })
        );

        // PENDING、 LATEST
        BigInteger nonce = getNonce(from, DefaultBlockParameterName.PENDING);
        long chainId = this.getEthereumChainId();
        String data = FunctionEncoder.encode(function);
        BigInteger value = BigInteger.ZERO;

        record.setNonce(nonce);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        transferRecordMapper.insert(record);
        /**
         * long chainId,
         * BigInteger nonce,
         * BigInteger gasLimit,
         * String contractAddress,
         * BigInteger value,
         * String data,
         * BigInteger maxPriorityFeePerGas,
         * BigInteger maxFeePerGas
         */
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                chainId,
                nonce,
                gasLimit,
                contractAddress,
                value,
                data,
                maxPriorityFeePerGas,
                maxFeePerGas
        );
        LOGGER.info("sendTransaction rawTransaction ===> chainId: {}, nonce: {}, gasLimit: {}, contractAddress: {}, value: {}, data: {}, maxPriorityFeePerGas: {}, maxFeePerGas: {}", chainId, nonce, gasLimit, contractAddress, value, data, maxPriorityFeePerGas, maxFeePerGas);

        org.web3j.crypto.Credentials credentials = this.getCredentials(from);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String signedMessageHex = Numeric.toHexString(signedMessage);

        Response rawResponse = web3j.ethSendRawTransaction(signedMessageHex).send();
        LOGGER.info("sendTransaction response: {}", JSONObject.toJSONString(rawResponse));
        // {"id":1,"jsonrpc":"2.0","result":"0x3b3c3ce077c59c0b7ba86cae2c6ea8c5710e596d93f319f0ca66f82a54aca50f","transactionHash":"0x3b3c3ce077c59c0b7ba86cae2c6ea8c5710e596d93f319f0ca66f82a54aca50f"}
        JSONObject response = (JSONObject) JSON.toJSON(rawResponse);
        String transactionHash = response.getString("transactionHash");
        if (StringUtils.isNotEmpty(transactionHash)) {
            Integer id = record.getId();
            record = transferRecordMapper.selectByPrimaryKey(id);
            record.setTransactionHash(transactionHash);
            transferRecordMapper.updateHashByPrimaryKey(record);
        }

        return response;
    }

    @Override
    public JSONObject speedupERC20Transfer(String transactionHash, BigInteger maxFeePerGas, BigInteger maxPriorityFeePerGas, Integer id) throws IOException, BusinessException {
        //   boolean feeCheckFlag = feeCheck();
        TransferRecord record = new TransferRecord();
        if (StringUtils.isNotEmpty(transactionHash)) {
            record = transferRecordMapper.selectByTransactionHash(transactionHash);
        } else {
            record = transferRecordMapper.selectByPrimaryKey(id);
        }

        if (!Optional.ofNullable(record).isPresent()) {
            LOGGER.error("speedupERC20Transfer transaction record not found");
            return null;
        }

        String to = record.getTo();
        BigDecimal amount = record.getAmount();
        String contractAddress = record.getContractAddress();
        BigInteger gasLimit = record.getGasLimit();
        BigInteger nonce = record.getNonce();
        String from = record.getFrom();

        ContractInfo contractInfo = contractInfoMapper.selectByContractAddress(contractAddress);
        if (!Optional.ofNullable(contractInfo).isPresent()) {
            throw new BusinessException(ExceptionConstant.CONTRACT_IS_NOT_FOUND);
        }

        Integer decimals = contractInfo.getTokenDecimals();
        BigInteger amountGwei = amount.movePointRight(decimals).toBigInteger();
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(to),
                        new Uint256(amountGwei)),
                Arrays.asList(new TypeReference<Bool>() {
                })
        );

        long chainId = this.getEthereumChainId();
        String data = FunctionEncoder.encode(function);
        BigInteger value = BigInteger.ZERO;

        // 构建交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                chainId,
                nonce,
                gasLimit,
                contractAddress,
                value,
                data,
                maxPriorityFeePerGas,
                maxFeePerGas
        );
        LOGGER.info("sendTransaction rawTransaction ===> chainId: {}, nonce: {}, gasLimit: {}, contractAddress: {}, value: {}, data: {}, maxPriorityFeePerGas: {}, maxFeePerGas: {}", chainId, nonce, gasLimit, contractAddress, value, data, maxPriorityFeePerGas, maxFeePerGas);

        org.web3j.crypto.Credentials credentials = getCredentials(from);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String signedMessageHex = Numeric.toHexString(signedMessage);

        Response rawResponse = web3j.ethSendRawTransaction(signedMessageHex).send();
        LOGGER.info("sendTransaction response: {}", JSONObject.toJSONString(rawResponse));
        // {"id":1,"jsonrpc":"2.0","result":"0x3b3c3ce077c59c0b7ba86cae2c6ea8c5710e596d93f319f0ca66f82a54aca50f","transactionHash":"0x3b3c3ce077c59c0b7ba86cae2c6ea8c5710e596d93f319f0ca66f82a54aca50f"}
        JSONObject response = (JSONObject) JSON.toJSON(rawResponse);

        return response;
    }


    @Override
    public TransactionReceipt getTransactionReceipt(String transactionHash) throws IOException {
        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
        Optional<TransactionReceipt> optional = transactionReceipt.getTransactionReceipt();
        if (optional.isPresent()) {
            TransactionReceipt receipt = optional.get();
            LOGGER.info("transaction hash: {}, transaction receipt: {}", transactionHash, JSON.toJSONString(receipt));
            return receipt;
        } else {
            return null;
        }
    }

    private BigInteger getNonce(String address, DefaultBlockParameterName defaultBlockParameterName) throws IOException {
        web3j = Web3j.build(new HttpService(sepoliaAlchemyUrl));
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, defaultBlockParameterName).send();
        if (ethGetTransactionCount == null) {
            return null;
        }
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        return nonce;
    }


    /**
     * 查库，通过地址找私钥
     *
     * @param address
     * @return
     */
    private org.web3j.crypto.Credentials getCredentials(String address) {
        sg.lwx.work.domain.Credentials credentials = credentialsMapper.selectPrivateKeyByAddress(address);
        if (Optional.ofNullable(credentials).isPresent()) {
            BigInteger privateKeyValue = new BigInteger(credentials.getPrivateKey(), 16);
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyValue);
            org.web3j.crypto.Credentials credential = org.web3j.crypto.Credentials.create(ecKeyPair);
            return credential;
        }
        return null;
    }


    // 获取以太坊的 chainId
    private long getEthereumChainId() {
        Long chainId = ChainIdEnum.getChainIdByNetwork(network.toLowerCase(Locale.ROOT));
        return chainId;
    }
}
