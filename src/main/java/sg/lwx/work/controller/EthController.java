package sg.lwx.work.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sg.lwx.work.model.request.GetColdWalletTxInfoReqeust;
import sg.lwx.work.model.request.TransferErc20Request;
import sg.lwx.work.service.EthService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@RestController
public class EthController {

    @Autowired
    private EthService ethService;

    @PostMapping("/testEthTransferOnArbitrum")
    public ResponseEntity<?> testEthTransferOnArbitrum(@RequestBody JSONObject request) throws IOException {
        String fromAddress = request.getString("fromAddress");
        String toAddress = request.getString("toAddress");
        BigInteger value = request.getBigInteger("value");
        JSONObject result = ethService.testEthTransferOnArbitrum(fromAddress, toAddress, value, null);
        return ResponseEntity.ok().body(result);
    }


    @PostMapping("/cold-wallet/tx/info/get")
    public ResponseEntity<?> getColdWalletTxInfo(@RequestBody GetColdWalletTxInfoReqeust request) throws IOException {

        String walletAddress = request.getWalletAddress();
         List<String> contractAddressList = request.getContractAddressList();
        JSONObject result = ethService.getColdWalletTxInfo(walletAddress,contractAddressList);
        return ResponseEntity.ok().body(result);
    }


    @PostMapping("/erc20/transfer")
    public ResponseEntity<?> transferERC20(@RequestBody TransferErc20Request request) throws IOException {

         String from = request.getFrom();
         String to = request.getTo();
         BigDecimal amount = request.getAmount();
         String contractAddress = request.getContractAddress();
         BigInteger gasLimit = request.getGasLimit();
         BigInteger maxFeePerGas = request.getMaxFeePerGas();
         BigInteger maxPriorityFeePerGas = request.getMaxPriorityFeePerGas();

        JSONObject result = ethService.transferERC20(from,to,amount,contractAddress,gasLimit,maxFeePerGas,maxPriorityFeePerGas);
        return ResponseEntity.ok().body(result);
    }


    @PostMapping("/erc20/transfer/speedup")
    public ResponseEntity<?> speedupERC20Transfer(@RequestBody TransferErc20Request request) throws IOException {

        String transactionHash = request.getTransactionHash();
        BigInteger maxFeePerGas = request.getMaxFeePerGas();
        BigInteger maxPriorityFeePerGas = request.getMaxPriorityFeePerGas();
        Integer id =  request.getId();

        JSONObject result = ethService.speedupERC20Transfer(transactionHash,maxFeePerGas,maxPriorityFeePerGas,id);
        return ResponseEntity.ok().body(result);
   }

}
