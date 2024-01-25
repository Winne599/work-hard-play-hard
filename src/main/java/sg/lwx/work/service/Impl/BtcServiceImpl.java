package sg.lwx.work.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;
import sg.lwx.work.service.BtcService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BtcServiceImpl implements BtcService {


    // todo lxx
    public List<JSONObject> testBtc() throws IOException {
        List<String> walletAddressList = new ArrayList<>();
//        walletAddressList.add("1EJDTf1bWu6cbFf7jWxuQ47aK1yTaSmSjS");
//        walletAddressList.add("15giTcLo1d2BVmymUMb5pBFeyyxoqiMYtq");
//        walletAddressList.add("31yDC23JXpv6ZLkXuLczfx9Exo4PoHe61d");
//        walletAddressList.add("3Fa3XrDZZqfPGd9wfN1284q6tRGx4q8tow");
//        walletAddressList.add("33yrpwbTw6GCN2JEjdv2o7HcaRRxfD9sWD");
//        walletAddressList.add("3QTC7gd9j7tRL9cNP8bcXMs14NbsPihwrm");
//        walletAddressList.add("3AZhDLuhrVNzLdHCdyCgR3VbLCjrkkLG56");
//        walletAddressList.add("3K525BLP3q5a8PoxTDPAcygXMkuXLgCD2y");
        //       walletAddressList.add("3Fmj5KTV6e3PXGpjXcf6vJ6as6uTrKqPAE"); // 一个交易
        walletAddressList.add("32H5wN4f1k3bkDZhF2gjE6uzyinfaAgAqo"); // 三个交易
//        walletAddressList.add("3EtxNqLwmRKNzKJVjVNaPBgoMhMmh4QWxM");
//        walletAddressList.add("3Nb258mM85xBTFDyNELeS5shgsdJoxU6Ga");

        List<JSONObject> resultList = new ArrayList<>();

        String sent = "Sent";
        String Received = "Received";

        String yes = "Y";
        String no = "N";


        var client = new OkHttpClient().newBuilder().build();
        for (String walletAddress : walletAddressList) {
            var url = String.format("%s/address/%s/txs", "https://blockstream.info/api", walletAddress);
            Request request = new Request.Builder().url(url).build();
            var response = client.newCall(request).execute();
            var content = response.body().string();
            var responseJSONArray = JSONArray.parseArray(content);

            // 遍历该地址的 所有交易
            for (var i = 0; i < responseJSONArray.size(); i++) {
                var itemObject = (JSONObject) responseJSONArray.get(i);
                var txid = itemObject.getString("txid");

                var vinJSONArray = itemObject.getJSONArray("vin");
                var voutJSONArray = itemObject.getJSONArray("vout");
                int voutCount = voutJSONArray.size();
                int vinCount = vinJSONArray.size();
                var status = itemObject.getJSONObject("status");
                Long blockTimeStamp = status.getLong("block_time");

                // vin处理
                for (int j = 0; j < vinJSONArray.size(); j++) {
                    var vinItem = (JSONObject) vinJSONArray.get(j);
                    JSONObject prevout = vinItem.getJSONObject("prevout");
                    String scriptpubkeyAddress = prevout.getString("scriptpubkey_address");
                    long originalValue = prevout.getLong("value");
                    BigDecimal vinValue = convertLongToBigDecimal(originalValue);

                    if (scriptpubkeyAddress.equals(walletAddress)) {
                        for (int k = 0; k < voutCount; k++) {
                            JSONObject voutItem = voutJSONArray.getJSONObject(k);

                            JSONObject result = new JSONObject(); // 根据 vout数量遍历生成result
                            result.put("TxId", txid);
                            result.put("FromAddress", walletAddress);
                            result.put("ToAddress", voutItem.getString("scriptpubkey_address"));
                            result.put("DateTime", convertTimestampToDate(blockTimeStamp));
                            result.put("TransactionType", sent);
                            result.put("ToValue", null);
                            result.put("IsPrivateKeyValidation", yes);
                            result.put("FromValue", vinValue);
                            resultList.add(result);
                        }
                    }
                }

                // vout处理
                for (int j = 0; j < voutJSONArray.size(); j++) {
                    var voutItem = (JSONObject) voutJSONArray.get(j);
                    String voutAddress = voutItem.getString("scriptpubkey_address");
                    long originValue = voutItem.getLongValue("value");
                    BigDecimal value = convertLongToBigDecimal(originValue);

                    if (voutAddress.equals(walletAddress)) {
                        for (int k = 0; k < vinCount; k++) {
                            JSONObject vinItem = vinJSONArray.getJSONObject(k);
                            JSONObject prevout = vinItem.getJSONObject("prevout");
                            String vinAddress = prevout.getString("scriptpubkey_address");
                            if (vinAddress.equals(walletAddress)) {
                                continue;
                            }

                            JSONObject result = new JSONObject();
                            result.put("TxId", txid);
                            result.put("FromAddress", vinAddress);
                            result.put("ToAddress", walletAddress);
                            result.put("DateTime", convertTimestampToDate(blockTimeStamp));
                            result.put("TransactionType", Received);
                            result.put("ToValue", value); // todo
                            result.put("IsPrivateKeyValidation", walletAddressList.contains(vinAddress)?yes:no); // todo
                            result.put("FromValue", null);
                            resultList.add(result);
                        }
                    }
                }
            }
        }
        return resultList;
    }


    private BigDecimal convertLongToBigDecimal(long originalValue) {
//        // 假设你有一个long类型的整数
//        long originalValue = 500000;

        // 将long转为BigDecimal
        BigDecimal bigDecimalValue = new BigDecimal(originalValue);

        // 设置精度为8位
        BigDecimal result = bigDecimalValue.movePointLeft(8);

        // 输出结果
//        System.out.println("原始long值: " + originalValue);
//        System.out.println("转换后的BigDecimal值（精度为8位）: " + result);


        return result;
    }


    private String convertTimestampToDate(long timestampSeconds) {
        // 输入秒级别的时间戳
//    long timestampSeconds = 1700535238;

        // 将秒级别的时间戳转为Instant对象
        Instant instant = Instant.ofEpochSecond(timestampSeconds);

        // 将Instant对象转为LocalDateTime对象（使用UTC时区）
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));

        // 定义时间格式，并指定时区为UTC
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));

        // 格式化LocalDateTime对象为指定格式的时间字符串
        String formattedDateTime = formatter.format(dateTime);

        // 输出结果
//    System.out.println("原始时间戳（秒级别）: " + timestampSeconds);
//    System.out.println("转换后的时间字符串: " + formattedDateTime);

        return formattedDateTime;
    }
}
