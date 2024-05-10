package sg.lwx.work.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigInteger;

public interface EthService {
    JSONObject testEthTransferOnArbitrum(String fromAddress, String toAddress, BigInteger value, BigInteger maxFeePerGas) throws IOException;

    JSONObject getColdWalletTxInfo() throws IOException;
}
