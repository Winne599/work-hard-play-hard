package sg.lwx.work.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface EthService {
    JSONObject testEthTransferOnArbitrum(String fromAddress, String toAddress, BigInteger value, BigInteger maxFeePerGas) throws IOException;

    JSONObject getColdWalletTxInfo(    String walletAddress,  List<String> contractAddress) throws IOException;


}
