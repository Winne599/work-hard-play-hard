package sg.lwx.work.util.btc;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BTCBroadcastDemo {

    // todo BTC广播交易 blockstream
//    public static void main(String[] args) {
//        var url = String.format("%s/tx", this.btcApiUrl);
//
//        var client = new OkHttpClient().newBuilder()
//                .build();
//
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, signature);
//        var request = new Request.Builder()
//                .url(url)
//                .method("POST", body)
//                .addHeader("Content-Type", "text/plain")
//                .build();
//        var response = client.newCall(request).execute();
//        var content = response.body().string();
//
//        if (isBitcoinTransactionHash(content)) {
//            return content;
//        } else {
//            var error = content;
//            LOGGER.error("btc broadcastTransaction error: {}", error);
//            throw new BusinessException(ReturnCodeEnum.BROADCAST_ERROR, content);
//        }
//    }

    public static void main(String[] args) {
        //  System.out.println(new BigDecimal(100000).movePointLeft(8));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime result = now.plusHours(128).plusMinutes(37);
        System.out.println("now: " + now);
        System.out.println("result: " + result);

    }
}
