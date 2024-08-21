package sg.lwx.work.util;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author lianwenxiu
 */
public class RsaUtil {

    public static void main(String[] args) throws Exception {
        // mvgx iwt环境下的数据， n1Zb1nxBb6dtFAiuNb2aFRq5FpGK7sDBTz 是冷钱包地址
        String address = "n1Zb1nxBb6dtFAiuNb2aFRq5FpGK7sDBTz";
        String coldWalletValidatePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh9qX1+TAbNLFpDvla/og7OiU5jQU6THC0mVxxytsFnd01C9ooU0peOU1PsNUTtIDJnMQqFNzw27zSSxfcWkLhSOVJnRuJcd2het/fVix2DC6qeUwkfsB4+YZ/SThafHFPRzhr4efYrtfrrGvOmFitX8Tb0ImgDWTJBzqmB4q4clJHCj36L/tb7MawQgjgTE7ovkhXixD/9n8PPVlPjjngg0VIgRDRD70e90x+PwMKIUEy+L0WAq2gXezjkHkbTrOMGS9U1xFBkgH2wREln3sd3aRQj6NyCjO0VDvCz3CatF4yitRU7rHqcR+AyeDrR/v1z1UjCGJvM+Cs8JRDexGkQIDAQAB";
        String signedString = "eaR/7eeZ0HLmflPLNSSG/6xIKR0itfvo6E7NGasT/FC/CZF9ucSgz6UZKE5L/MZavlDo1dvJjohy8Yg/lxJSBsMvFcSizb0DOgEWb2Jxx2vaHSjs59VX06LGJgLHp0WusZvAGllJ3siYxKtoZJWY3Gcc2QF6N0HJ5ywxrdY9phDHu9LePxGck/QzKjG4Gsdh0JlxFtl2Uavo3pERFO8yuDJP4OH/+ILZiB1QEuQSyfrHRpVl/EAOOMNgAOHuQkCDvccCnNc0cM8oGuCTo7liCdRiDYxWsFNTxwfEwRU/kKqvKoycqpB68UKuGn6DkzFboI9G5eTbR8u6UlL4A0taEg==";


        var result = verify(address, coldWalletValidatePublicKey, signedString);
        System.err.println("result: " + result);
    }


    public static boolean verify(String content, String publicKey, String signedString) throws Exception {
        byte[] data = content.getBytes("UTF-8");
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicK);
        signature.update(data);
        byte[] signedData = Base64.decodeBase64(signedString);
        return signature.verify(signedData);
    }
}
