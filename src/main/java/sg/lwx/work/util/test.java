//package sg.lwx.work.util;
//
//import org.bitcoinj.core.*;
//import org.bitcoinj.crypto.TransactionSignature;
//import org.bitcoinj.net.discovery.DnsDiscovery;
//import org.bitcoinj.params.MainNetParams;
//import org.bitcoinj.params.TestNet3Params;
//import org.bitcoinj.script.Script;
//import org.bitcoinj.script.ScriptBuilder;
//import org.bitcoinj.store.BlockStore;
//import org.bitcoinj.store.MemoryBlockStore;
//import org.bitcoinj.store.SPVBlockStore;
//import org.bitcoinj.wallet.Wallet;
//
//import java.io.File;
//import java.util.List;
//
//public class test {
//    public static void main(String[] args) throws Exception {
//        // 设置网络参数，这里使用MainNet（主网）
//      //  NetworkParameters params = MainNetParams.get();
//        NetworkParameters params = TestNet3Params.get();
//
//
//        // 私钥和地址 from: tb1qmvh2m2lx9nkk4zpah6d7hm90kupaqulffre3ee
//        String privateKey = "cUSoueaYArq2b2rRvKveJgo7L2foUuW5juzYxVNGtojrANXmDRah"; // 替换为你的私钥
//        String toAddress = "tb1q9wt8rxap3k4hptnvd3rf5j3z9m7h0uqt86rc6e"; // 替换为接收地址
//        ECKey key = DumpedPrivateKey.fromBase58(params, privateKey).getKey();
//
//        // 接收地址和金额
//        Address targetAddress = Address.fromString(params, toAddress);
//        Coin amount = Coin.valueOf(100000); // 0.001 BTC (100,000 satoshis)
//
//        // 初始化钱包并导入私钥
//        Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
//        wallet.importKey(key);
//
////        // 连接到比特币网络
////        BlockStore blockStore = new MemoryBlockStore(params);
////        BlockChain chain = new BlockChain(params, wallet, blockStore);
////        PeerGroup peerGroup = new PeerGroup(params, chain);
////        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
////        peerGroup.start();
////        peerGroup.downloadBlockChain();
//
//        // 设置区块链数据存储路径
//        File blockStoreFile = new File("D:/ideaProjects/blockchain.spv");
//        BlockStore blockStore = new SPVBlockStore(params, blockStoreFile);
//
//        // 连接到比特币网络
//        BlockChain chain = new BlockChain(params, wallet, blockStore);
//        PeerGroup peerGroup = new PeerGroup(params, chain);
//        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
//        peerGroup.start();
//        peerGroup.downloadBlockChain();
//
//        // 检查余额是否足够
//        Coin balance = wallet.getBalance();
//        if (balance.isLessThan(amount)) {
//            System.out.println("余额不足，无法进行交易。");
//            return;
//        }
//
//        // 创建交易
//        Transaction tx = new Transaction(params);
//        tx.addOutput(amount, targetAddress);
//
//        // 查找未花费的交易输出（UTXO）
//        List<TransactionOutput> utxos = wallet.getUnspents();
//        Coin value = Coin.ZERO;
//        for (TransactionOutput utxo : utxos) {
//            tx.addInput(utxo);
//            value = value.add(utxo.getValue());
//            if (value.isGreaterThan(amount)) {
//                break;
//            }
//        }
//
//        // 添加找零输出
//        Coin change = value.subtract(amount);
//        if (change.isGreaterThan(Coin.ZERO)) {
//            Address changeAddress = LegacyAddress.fromKey(params, key);
//            tx.addOutput(change, changeAddress);
//        }
//
//        // 签署交易
//        for (TransactionInput input : tx.getInputs()) {
//            TransactionSignature txSig = tx.calculateSignature(input.getIndex(), key, input.getConnectedOutput().getScriptPubKey(), Transaction.SigHash.ALL, false);
//            input.setScriptSig(ScriptBuilder.createInputScript(txSig, key));
//        }
//
//        // 广播交易
//        peerGroup.broadcastTransaction(tx).broadcast().get();
//
//        System.out.println("Transaction broadcasted: " + tx.getTxId());
//
//        // 停止PeerGroup
//        peerGroup.stop();
//    }
//}
