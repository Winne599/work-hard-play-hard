package sg.lwx.work.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

public class GetNonce {

    public static void main(String[] args) {
        //
        String alchemyEndpoint = CommonConstant.ALCHEMY_API_KEY;

        // address
        String ethAddress = "0x803d734f4097e6d1352dd96f472e6e442bd48657";

        // JSON-RPC
        Map<String, Object> jsonRpcRequest = new HashMap<>();
        jsonRpcRequest.put("jsonrpc", "2.0");
        jsonRpcRequest.put("method", "eth_getTransactionCount");
        jsonRpcRequest.put("params", new Object[]{ethAddress, "pending"});
        jsonRpcRequest.put("id", 1);

        String response = RpcClient.sendPostRequest(alchemyEndpoint, JSON.toJSONString(jsonRpcRequest));


        Map<String, Object> jsonResponse = jsonToMap(response);

//
//        String balanceHex = (String) jsonResponse.get("result");
//        long balanceWei = Long.parseLong(balanceHex.substring(2), 16);
//        double balanceEth = (double) balanceWei / 1e18;
//
//        System.err.println("ETH Balance for " + ethAddress + ": " + balanceEth + " ETH");
    }

    private static String mapToJson(Map<String, Object> map) {
        String jsonString = JSON.toJSONString(map);
        return jsonString;
    }


    /**
     * json 转 mpc
     *
     * @param json
     * @return
     */
    private static Map<String, Object> jsonToMap(String json) {
        // json 转 mpc
        Map<String, Object> resultMap = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
        return resultMap;
    }
}
