package sg.lwx.work.util;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

/**
 * 地址导入wallet，创建
 */
public class Testtest {

    public static void main(String[] args) {
        NetworkParameters params = TestNet3Params.get();

        // 创建钱包并导入私钥
        Wallet wallet = importP2PKHAddressToWallet(params);
        String utxoHash = "c083e699a42781ad496e2bfeaf8726becfdd61be9360602ec3c08941d73fc856"; // 交易的哈希值 (hex 格式)
        int outputIndex = 1; // 输出索引
        Coin amount = Coin.valueOf(400000); // 0.00004 BTC
        Address toAddress = Address.fromString(params, "mr2g2TywT5DqySioXzqpSGrDms3pgovztH");

        createTransactionUsingUtxo(params,wallet,utxoHash,outputIndex,toAddress,amount);


    }



    public static void createTransactionUsingUtxo(NetworkParameters params, Wallet wallet, String utxoHash, int outputIndex, Address toAddress, Coin amount) {
        Sha256Hash sha256Hash = Sha256Hash.wrap(utxoHash);
        // TransactionOutPoint outPoint = new TransactionOutPoint(params, outputIndex, sha256Hash);
        String fromAddress = "mhKU81FCyVusRdUxYFkxo5TkG2HQVLMDrh";

        Script script = ScriptBuilder.createOutputScript(Address.fromString(params,fromAddress));
        Coin value = amount; // Value of the UTXO
        //TransactionInput input = new TransactionInput(params, null, script.getProgram(), outPoint, value);

        SendRequest sendRequest = SendRequest.to(toAddress,amount);

        //sendRequest.tx.addInput(input);
        sendRequest.tx.addInput(sha256Hash, outputIndex, script);


        // 创建输出
       // sendRequest.tx.addOutput(value, toAddress);
    //    Coin totalInput = Coin.valueOf(5000000);
        Coin fee = Coin.valueOf(10000); // 设定10000 satoshis的手续费
      //  Coin change = totalInput.subtract(value).subtract(fee);

       // sendRequest.tx.addOutput(amount, toAddress);
    //    sendRequest.tx.addOutput(change, Address.fromString(params, fromAddress)); // 找零
        sendRequest.feePerKb = fee;

        for (int i = 0; i < sendRequest.tx.getInputs().size(); i++) {
            TransactionInput input = sendRequest.tx.getInput(i);
            Sha256Hash hash = sendRequest.tx.hashForSignature(i, script, Transaction.SigHash.ALL, false);

            // 使用 findKeyFromPubKeyHash 来查找对应的 ECKey
            ECKey fromKey = wallet.findKeyFromPubKeyHash(script.getPubKeyHash(), Script.ScriptType.P2PKH);

            if (fromKey == null) {
                throw new IllegalArgumentException("Key not found in wallet for the given public key hash.");
            }

            ECKey.ECDSASignature ecdsaSignature = fromKey.sign(hash);
            TransactionSignature txSignature = new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, false);
            input.setScriptSig(ScriptBuilder.createInputScript(txSignature, fromKey));
        }



        try {
            wallet.signTransaction(sendRequest);
      //      wallet.commitTx(sendRequest.tx); // Commit the transaction to the wallet
            System.out.println("Transaction created: " + sendRequest.tx.getTxId());

            // 获取估算的交易费用
            Coin estimatedFee = sendRequest.tx.getFee();
            System.out.println("estimatedFee: " + estimatedFee);

            // Get raw transaction bytes
            byte[] rawTransaction = sendRequest.tx.bitcoinSerialize();

            // Convert the raw transaction to hex format
            String rawTransactionHex = Utils.HEX.encode(rawTransaction);
            System.out.println("Raw transaction hex: " + rawTransactionHex);



        } catch (Exception e) {
            System.out.println("Insufficient funds: " + e.getMessage());
            System.out.println("Insufficient funds e: " + e);
        }
    }

    private static Wallet importP2PKHAddressToWallet(NetworkParameters params) {
        // 创建一个新的钱包
        Wallet wallet = Wallet.createBasic(params);

        // 假设你有一个私钥（WIF 格式）
        String wifPrivateKey = "cTtwoWvNujnphVFhrSSh3VFurQisn4bJ2MgxJE6g42D4k2hTYdjZ";

        // 从WIF格式的私钥创建ECKey对象
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, wifPrivateKey);
        ECKey key = dumpedPrivateKey.getKey();

        // 将 ECKey 导入钱包
        wallet.importKey(key);

        // 从私钥生成 P2PKH 地址
        Address importedAddress = Address.fromKey(params, key, Script.ScriptType.P2PKH);

        System.out.println("Imported P2PKH address: " + importedAddress.toString());

        // 验证地址是否成功导入
        if (wallet.isAddressMine(importedAddress)) {
            System.out.println("P2PKH address successfully imported into wallet.");
        } else {
            System.out.println("P2PKH address import failed.");
        }

        return wallet;
    }



}
