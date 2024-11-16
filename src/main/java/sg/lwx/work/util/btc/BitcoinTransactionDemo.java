package sg.lwx.work.util.btc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinTransactionDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinTransactionDemo.class);

    public static void main(String[] args) throws Exception {
        // 设置网络参数（主网）
        NetworkParameters params = TestNet3Params.get();

        // 发送者的私钥
        String fromPrivateKey = "cTtwoWvNujnphVFhrSSh3VFurQisn4bJ2MgxJE6g42D4k2hTYdjZ"; // 替换为你的私钥
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, fromPrivateKey);
        ECKey fromKey = dumpedPrivateKey.getKey();

        // 目标接收地址
        String toAddressString = "tb1q9wt8rxap3k4hptnvd3rf5j3z9m7h0uqt86rc6e"; // 替换为接收者的地址
        Address toAddress = Address.fromString(params, toAddressString);

        // 通过 Blockstream API 获取 UTXO 信息
        String fromAddress = "mhKU81FCyVusRdUxYFkxo5TkG2HQVLMDrh";
        JSONArray utxos = getUtxos(fromAddress);

        // 构建交易
        Transaction tx = new Transaction(params);

        Coin totalInput = Coin.ZERO;
        for (int i = 0; i < utxos.size(); i++) {
            JSONObject utxo = utxos.getJSONObject(i);
            Sha256Hash utxoTxHash = Sha256Hash.wrap(utxo.getString("txid"));
            if ("66b880e902878d1929f11028fcb4f06eba0f7922cf4fb2f2613af25c6d3d9eec".equals(utxo.getString("txid"))) {
                int outputIndex = utxo.getIntValue("vout");
                Coin amount = Coin.valueOf(utxo.getLong("value"));

                // 创建输入
                TransactionOutPoint outPoint = new TransactionOutPoint(params, outputIndex, utxoTxHash);
                Script script = ScriptBuilder.createOutputScript(Address.fromString(params, fromAddress));
                // tx.addInput(outPoint, ScriptBuilder.createOutputScript(Address.fromString(params,fromAddress)), amount);
                tx.addInput(utxoTxHash, outputIndex, script);

                totalInput = totalInput.add(amount);
            }
        }

        // 定义发送金额和手续费
        Coin valueToSend = Coin.valueOf(4000); // 发送90000 satoshis
        Coin fee = Coin.valueOf(10000); // 设定10000 satoshis的手续费
        Coin change = totalInput.subtract(valueToSend).subtract(fee);

        // 创建输出
        tx.addOutput(valueToSend, toAddress);
        tx.addOutput(change, Address.fromString(params, fromAddress)); // 找零

        // 签署交易
        for (int i = 0; i < tx.getInputs().size(); i++) {
            TransactionInput input = tx.getInput(i);
            Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromString(params, fromAddress));
            Sha256Hash hash = tx.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecdsaSignature = fromKey.sign(hash);
            TransactionSignature txSignature = new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, false);
            input.setScriptSig(ScriptBuilder.createInputScript(txSignature, fromKey));
        }

        // 将Transaction序列化为字节数组
        byte[] serializedTransaction = tx.bitcoinSerialize();

        // // 获取原始交易数据 将字节数组转换为十六进制字符串
        String rawTransactionHex = Utils.HEX.encode(serializedTransaction);

        System.out.println("rawTransactionHex: " + rawTransactionHex);


        // 广播交易
        String broadcastedHash = broadcastTransaction(rawTransactionHex);
        System.out.println("broadcastedHash: " + broadcastedHash);
    }

    private static JSONArray getUtxos(String address) throws Exception {
        OkHttpClient client = new OkHttpClient();
        String url = "https://blockstream.info/testnet/api/address/" + address + "/utxo";
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发送请求并获取响应
        Response response = client.newCall(request).execute();

        // 获取响应体的字符串表示形式
        String responseBody = response.body().string();
        return JSONObject.parseArray(responseBody);
    }


    public static String broadcastTransaction(String rawTxHex) throws Exception {
        var url = "https://blockstream.info/testnet/api/tx";

        var client = new OkHttpClient().newBuilder()
                .build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, rawTxHex);
        var request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .build();
        var response = client.newCall(request).execute();
        var content = response.body().string();

        if (isBitcoinTransactionHash(content)) {
            return content;
        } else {
            var error = content;
            LOGGER.error("btc broadcastTransaction error: {}", error);
            return null;
        }
    }


    private static boolean isBitcoinTransactionHash(String str) {
        // Step 1
        if (str.length() != 64) {
            return false;
        }
        // Step 2
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLowerCase(c)) {
                return false;
            }
        }
        // Step 3
        return str.matches("^[0-9a-fA-F]{64}$");
    }
}
