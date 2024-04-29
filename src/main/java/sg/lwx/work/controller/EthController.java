package sg.lwx.work.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sg.lwx.work.service.EthService;

import java.io.IOException;
import java.math.BigInteger;

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

}
