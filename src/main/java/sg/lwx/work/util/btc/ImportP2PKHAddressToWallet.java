package sg.lwx.work.util.btc;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;

public class ImportP2PKHAddressToWallet {

    public static void main(String[] args) {
        NetworkParameters parameters = TestNet3Params.get();
        Wallet wallet = importP2PKHAddressToWallet(parameters);

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
}


