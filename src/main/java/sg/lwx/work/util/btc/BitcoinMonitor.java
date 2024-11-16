package sg.lwx.work.util.btc;

import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class BitcoinMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinMonitor.class);
    private static final String fromAddress = "mzxSYHkxNssd1AMugjJdahCT7bbTQqg5YP";
    private static final String toAddressString = "miYW8BWNKwzmdUbhYoiTA6qDx3B9xxDow5";

    public static void main(String[] args) {
        // 设置本地的 VPN 代理
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "33210");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "33210");

        // Setup network parameters
        //     NetworkParameters params = MainNetParams.get(); // 主网
        NetworkParameters params = TestNet3Params.get(); // 测试网

        // 初始化 WalletAppKit 钱包应用程序，并指定存储钱包和区块链数据的目录
        File walletDirectory = new File(".");
        String filePrefix = "bitcoin_wallet";
        WalletAppKit kit = new WalletAppKit(params, walletDirectory, filePrefix);

        // 使用 checkpoints 文件加速区块链同步
        kit.setCheckpoints(BitcoinMonitor.class.getClassLoader().getResourceAsStream("checkpoints-testnet-2024-08-28"));
//        kit.setCheckpoints(BitcoinMonitor.class.getClassLoader().getResourceAsStream("checkpoints-testnet-20240828"));

        // 启动 WalletAppKit 钱包服务
        kit.startAsync();
        kit.awaitRunning();
        System.out.println("WalletAppKit started!");

        Wallet wallet = kit.wallet();

        // 加载或定义需要监控的地址列表
        Set<Address> monitoredAddresses = new HashSet<>();
        monitoredAddresses.add(Address.fromString(params, fromAddress));
        // 添加监控地址
        wallet.addWatchedAddress(Address.fromString(params, fromAddress));
      //  wallet = importP2PKHAddressToWallet(params);

        // 打印当前区块高度
        System.out.printf("LastBlockSeenHeight: %s\n", wallet.getLastBlockSeenHeight());

        // 设置一个监听器，当有新的交易时触发
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("===================1===================");

                System.out.printf("Transaction received => txHash: %s\n", tx.getTxId());
                String hash = "1f42bc82db5652733a710e609588b56e2a89418ce73c8966b810fc9be9b12c40";
                Sha256Hash sha256Hash = Sha256Hash.wrap(hash);
                if (tx.getTxId().equals(sha256Hash)) {
                    Coin amount = Coin.valueOf(100000);
                    Address toAddress = Address.fromString(params,toAddressString);
                    createTransactionUsingUtxo(params,wallet,hash,1,toAddress,amount,kit);
                }
            }
        });


        // 保持程序运行
        while (true) {
            try {
                Thread.sleep(1000);

                // 打印当前区块高度
                System.out.printf("LastBlockSeenHeight: %s\n", wallet.getLastBlockSeenHeight());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTransactionUsingUtxo(NetworkParameters params, Wallet wallet, String utxoHash,
                                                  int outputIndex, Address toAddress, Coin amount, WalletAppKit kit) {
        SendRequest sendRequest = SendRequest.to(toAddress, amount);
        // addInput
        Sha256Hash sha256Hash = Sha256Hash.wrap(utxoHash);
        Script script = ScriptBuilder.createOutputScript(Address.fromString(params, fromAddress));
        sendRequest.tx.addInput(sha256Hash, outputIndex, script);
        // 设定10000 satoshis的手续费
        Coin fee = Coin.valueOf(10000);

        // 找零
        Coin change = amount.subtract(fee);
        sendRequest.tx.addOutput(change, Address.fromString(params, fromAddress));

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
            wallet.commitTx(sendRequest.tx); // Commit the transaction to the wallet
            System.out.println("Transaction created: " + sendRequest.tx.getTxId());

            // Get raw transaction bytes
            byte[] rawTransaction = sendRequest.tx.bitcoinSerialize();

            // Convert the raw transaction to hex format
            String rawTransactionHex = Utils.HEX.encode(rawTransaction);
            System.out.println("Raw transaction hex: " + rawTransactionHex);

            final Wallet.SendResult sendResult = kit.wallet().sendCoins(sendRequest);
            checkNotNull(sendResult);

            // get actual amount sent and actual fee
            Coin output = sendResult.tx.getOutputSum();
            Coin sendFee = sendResult.tx.getFee();
            LOGGER.info("sendResult transaction output amt: {}, sendFee: {}", output.value, sendFee.value);

            sendResult.broadcastComplete.addListener(new Runnable() {
                 @Override
                public void run() {
                    LOGGER.info("Sending to cold wallet broadcast completed. Transaction hash is " + sendResult.tx.getTxId());
                    List<TransactionInput> tlist = sendResult.tx.getInputs();

                    for (TransactionInput tin : tlist) {
                        // find previous/parent txn connected to this input
                        Transaction ptxn = tin.getConnectedTransaction();
                        String ptxnid = ptxn.getTxId().toString();
                        LOGGER.info("Previous connected transaction: " + ptxnid);
                    }
                }
            }, MoreExecutors.directExecutor());


        } catch (Exception e) {
            System.out.println("occur error: " + e.getMessage());
            System.out.println(e);
        }
    }

    private static Wallet importP2PKHAddressToWallet(NetworkParameters params) {
        // 创建一个新的钱包
        Wallet wallet = Wallet.createBasic(params);

        // 假设你有一个私钥（WIF 格式）
        // mzxSYHkxNssd1AMugjJdahCT7bbTQqg5YP
        String wifPrivateKey = "cPQGL3KHwSVYSL72vSkCTioUZUQaQfvvcswmmsBya5xfynd5ZoiW";

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


    public static List<String> getFromAddressList(NetworkParameters params, Transaction tx) {
        System.out.printf("Trying to get from address for tx id: %s\n", tx.getTxId());
        System.out.printf("inputs size: %s\n", tx.getInputs().size());

        List<String> fromAddressList = new ArrayList<>();

        for (TransactionInput input : tx.getInputs()) {
            Transaction transaction = input.getConnectedTransaction();
            if (transaction != null) {
                System.out.println("Connected Transaction is not null");
            }

            TransactionOutput output = input.getOutpoint().getConnectedOutput();
            if (output != null) {
                Script script = new Script(output.getScriptBytes());
                if (script != null) {
                    String fromAddress = script.getToAddress(params).toString();
                    System.out.printf("from address is: %s\n", fromAddress);
                    if (StringUtils.isNotEmpty(fromAddress)) {
                        fromAddressList.add(fromAddress);
                    }
                } else {
                    System.out.printf("Unable to get from address as [output.getScriptBytes()] return null.\n");
                }
            } else {
                System.out.printf("Unable to get from address as [input.getOutpoint().getConnectedOutput()] return null.\n");
            }
        }

        return fromAddressList;
    }
}

