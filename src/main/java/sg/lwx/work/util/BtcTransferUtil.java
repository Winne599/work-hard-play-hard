//package sg.lwx.work.util;
//
//import org.bitcoinj.core.*;
//import org.bitcoinj.net.discovery.DnsDiscovery;
//import org.bitcoinj.params.TestNet3Params;
//import org.bitcoinj.store.BlockStore;
//import org.bitcoinj.store.MemoryBlockStore;
//import org.bitcoinj.wallet.SendRequest;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.web3j.crypto.Wallet;
//
//import java.io.File;
//
//public class BtcTransferUtil {
//    public static void main(String[] args) throws Exception {
//        // 设置网络参数，这里使用TestNet
//        NetworkParameters params = TestNet3Params.get();
//
//        // 创建钱包或加载现有钱包
//        org.bitcoinj.wallet.Wallet wallet;
//        File walletFile = new File("mywallet.wallet");
//        if (walletFile.exists()) {
//            wallet = org.bitcoinj.wallet.Wallet.loadFromFile(walletFile);
//        } else {
//            wallet = new org.bitcoinj.wallet.Wallet(params);
//            wallet.saveToFile(walletFile);
//        }
//
//        // 从私钥导入密钥
//        String privateKey = "your_private_key";
//        ECKey key = DumpedPrivateKey.fromBase58(params, privateKey).getKey();
//        wallet.importKey(key);
//
//        // 设置接收地址和金额
//        String toAddress = "receiver_btc_address";
//        Address targetAddress = Address.fromString(params, toAddress);
//        Coin amount = Coin.valueOf(100000); // 0.001 BTC (100,000 satoshis)
//
//        // 创建交易请求
//        SendRequest req = SendRequest.to(targetAddress, amount);
//        req.feePerKb = Coin.valueOf(1000); // 设置每KB的手续费，调整此值以适应网络情况
//        wallet.completeTx(req);
//
//        // 签署交易
//        wallet.signTransaction(req);
//
//        // 连接到比特币网络并广播交易
//        BlockStore blockStore = new MemoryBlockStore(params);
//        BlockChain chain = new BlockChain(params, wallet, blockStore);
//        PeerGroup peerGroup = new PeerGroup(params, chain);
//        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
//        peerGroup.start();
//        peerGroup.downloadBlockChain();
//        ListenableFuture<Transaction> s = (ListenableFuture<Transaction>) peerGroup.broadcastTransaction(req.tx).broadcast();
//
//        // 保存钱包
//        wallet.saveToFile(walletFile);
//
//        System.out.println("Transaction broadcasted: " + req.tx.getTxId());
//    }
//}
