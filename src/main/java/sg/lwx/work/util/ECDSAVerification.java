package sg.lwx.work.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * 此测试类为 jdk11版本，jdk17版本测试类为ECDSAVerification17
 */
public class ECDSAVerification {
    public static boolean verifyECDSASignature(String publicKeyStr, String originalMessage, String signatureStr) {
        try {
            // 将Base64编码的公钥字符串解码
            byte[] publicKeyBytes = hexStringToByteArray(publicKeyStr);

            // 使用X509EncodedKeySpec构造公钥
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 将Base64编码的签名字符串解码
            byte[] signatureBytes = hexStringToByteArray(signatureStr);

            // 使用ECDSA进行签名验证
            Signature signature = Signature.getInstance("SHA256withECDSA");
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
        String originalMessage = "{\"entityType\":\"METACOMP\",\"orderNo\":\"WD2025010811190001\",\"payeeAcctNm\":\"camp2024-fip-14\",\"payeeAcctNo\":\"11021210366\",\"payeeCurrencyTypeCd\":\"USD\",\"payerAcctNm\":\"MetaComp Pte. Ltd.\",\"payerAcctNo\":\"11021214213\",\"tradeAmount\":\"150\"}";
        String signatureStr = "304402200a82dd0eabcb9789a874fc718f2cb00013680c2527ad43a2d5cc19496fbfa6cc02207d86972e984c06f1263bcd69dcd4257d3ae15bac26f14e1b5675679eeb62496a";

        // 调用验证方法
        boolean isVerified = verifyECDSASignature(publicKeyStr, originalMessage, signatureStr);
        System.err.println("Signature verification result: " + isVerified);
    }
}

