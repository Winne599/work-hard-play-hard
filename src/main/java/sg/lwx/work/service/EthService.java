package sg.lwx.work.service;

import com.alibaba.fastjson.JSONObject;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import sg.lwx.work.domain.exception.BusinessException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface EthService {
    JSONObject testEthTransferOnArbitrum(String fromAddress, String toAddress, BigInteger value, BigInteger maxFeePerGas) throws IOException;

    JSONObject getColdWalletTxInfo(    String walletAddress,  List<String> contractAddress) throws IOException;

    JSONObject transferERC20(String from, String to, BigDecimal amount, String contractAddress, BigInteger gasLimit, BigInteger maxFeePerGas, BigInteger maxPriorityFeePerGas) throws IOException, BusinessException;

    JSONObject speedupERC20Transfer(String transactionHash,BigInteger maxFeePerGas,BigInteger maxPriorityFeePerGas,Integer id ) throws IOException, BusinessException;

    TransactionReceipt getTransactionReceipt(String transactionHash) throws IOException;
}
