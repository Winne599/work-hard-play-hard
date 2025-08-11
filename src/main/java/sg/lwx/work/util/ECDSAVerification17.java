package sg.lwx.work.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * TODO：使用此类前操作，此测试类是jdk17的代码版本，jdk17中其中某个包的算法更改，无法使用原来代码，jdk11版本测试类为ECDSAVerification
 * gradle中引入包：implementation 'org.bouncycastle:bcprov-jdk18on:1.76'
 */
public class ECDSAVerification17 {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public static boolean verifyECDSASignature(String publicKeyStr, String originalMessage, String signatureStr) {
        try {
            // 将Base64编码的公钥字符串解码
            byte[] publicKeyBytes = hexStringToByteArray(publicKeyStr);

            // 使用X509EncodedKeySpec构造公钥
            KeyFactory keyFactory = KeyFactory.getInstance("EC","BC");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 将Base64编码的签名字符串解码
            byte[] signatureBytes = hexStringToByteArray(signatureStr);

            // 使用ECDSA进行签名验证
            Signature signature = Signature.getInstance("SHA256withECDSA","BC");
            signature.initVerify(publicKey);
            signature.update(originalMessage.getBytes());

            // 验证签名
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main(String[] args) {
        // 示例公钥、原文和签名（使用Base64编码）
        // camp2024 dev环境 WD2025010811190001
        String publicKeyStr = "3056301006072a8648ce3d020106052b8104000a03420004b782ef0fd73d6e5f6d3849a3621e9f90e4edf9840c046b706e49d470d2887a849c53b9f36eccbddb059b36ea48a935d7f9f3599febbe60017c6da9060fee8c48";
        String originalMessage = "{\"costFeeAssumeCd\":\"SHA\",\"entityType\":\"METACOMP\",\"orderNo\":\"WD2025042816460001\",\"payeeAcctNo\":\"22213122\",\"payeeAddr\":\"[\\\"CITIKZKAXXX1\\\",\\\"CITIKZKAXXX2\\\",\\\"CITIKZKAXXX3\\\"]\",\"payeeBankNm\":\"HSBCHKH0XXX\",\"payeeBankNo\":\"HSBCHKH0XXX\",\"payeeCountriesEnCd\":\"PR\",\"payeeCountriesNm\":\"Puerto Rico\",\"payeeNm\":\"CITIKZKAXXX\",\"payerAcctNm\":\"MetaComp PTE. LTD.\",\"payerAcctNo\":\"11021214213\",\"paymentAttributeCd\":\"OTHR\",\"tradeAmount\":\"50\",\"tradeCurrencyTypeCd\":\"EUR\"}";
        String signatureStr = "304402206565458e5ed2403e9f27d696ade2a3c6f534da0b9d99da0049b83352404f1e680220690609bb2e0d3edca559137d67c6fc40f09631990ec6d48eb2ad15c5e2910fa0";

        // 调用验证方法
        boolean isVerified = verifyECDSASignature(publicKeyStr, originalMessage, signatureStr);
        System.err.println("Signature verification result: " + isVerified);
    }
}

