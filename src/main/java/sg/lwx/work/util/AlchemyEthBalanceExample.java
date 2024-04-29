package sg.lwx.work.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * alchemy 获取 ETH余额 API
 * @author xiuxiu
 */
public class AlchemyEthBalanceExample {
    public static void main(String[] args) {
        // Alchemy API Endpoint
        String alchemyEndpoint = CommonConstant.ALCHEMY_API_KEY;

        //
        String ethAddress = "0x803d734f4097e6d1352dd96f472e6e442bd48657";

        // JSON-RPC
        Map<String, Object> jsonRpcRequest = new HashMap<>();
        jsonRpcRequest.put("jsonrpc", "2.0");
        jsonRpcRequest.put("method", "eth_getBalance");
        jsonRpcRequest.put("params", new Object[]{ethAddress, "latest"});
        jsonRpcRequest.put("id", 1);

        String response = RpcClient.sendPostRequest(alchemyEndpoint, JSON.toJSONString(jsonRpcRequest));

        //
        Map<String, Object> jsonResponse = jsonToMap(response);

        // ��ȡ���
        String balanceHex = (String) jsonResponse.get("result");
        long balanceWei = Long.parseLong(balanceHex.substring(2), 16);
        double balanceEth = (double) balanceWei / 1e18;

        System.err.println("ETH Balance for " + ethAddress + ": " + balanceEth + " ETH");
    }

    /**
     * ��Mapת��ΪJSON�ַ���
     *
     * @param map
     * @return
     */
    private static String mapToJson(Map<String, Object> map) {
        String jsonString = JSON.toJSONString(map);
        return jsonString;
    }


    /**
     * ��JSON�ַ���ת��ΪMap
     *
     * @param json
     * @return
     */
    private static Map<String, Object> jsonToMap(String json) {
        // JSON 转 Map
        Map<String, Object> resultMap = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
        return resultMap;
    }
}
