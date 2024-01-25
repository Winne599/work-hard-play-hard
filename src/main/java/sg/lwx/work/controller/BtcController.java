package sg.lwx.work.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.lwx.work.service.Impl.BtcServiceImpl;

import java.io.IOException;
import java.util.List;

/**
 * @author lianwenxiu
 */
@RestController
public class BtcController {

    @Autowired
    private BtcServiceImpl btcService;

    @GetMapping("/testBtc")
    public ResponseEntity<?> testBtc() throws IOException {
        List<JSONObject> resultList = btcService.testBtc();
        return ResponseEntity.ok().body(resultList);
    }
}
