package sg.lwx.work.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlockstreamTxTimeFetcher {
    private static final String BLOCKSTREAM_API_URL = "https://blockstream.info/api/tx/";

    public static void main(String[] args) {
        String txid = "7023b66f710d6843de02f5a1818350c0f384e3025503f63904c9640af930d735";  // 你的交易哈希
        Long blockTime = getBlockTime(txid);
        if (blockTime != null) {
            System.out.println("Transaction confirmed at: " + blockTime);
        } else {
            System.out.println("Transaction is not yet confirmed.");
        }
    }

    public static Long getBlockTime(String txid) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BLOCKSTREAM_API_URL + txid)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body().string());

                // 获取 block_time
                JsonNode statusNode = jsonNode.path("status");
                if (statusNode.has("block_time")) {
                    return statusNode.get("block_time").asLong();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // 如果交易未确认，block_time 不存在
    }
}

