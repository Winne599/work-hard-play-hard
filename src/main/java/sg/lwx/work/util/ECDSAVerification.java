package sg.lwx.work.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

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
        String publicKeyStr = "3056301006072a8648ce3d020106052b8104000a034200049bb55bdebad51f2abea4b3a6dd0b7226204c6b517694129eee8a3d090f224c1a758f367d5a75b71ce7049ded9cca823c6cb23247380de8e1953af574196c5e1f";
        String originalMessage = "{\"payeeAcctNm\":\"balbala\",\"payeeAcctNo\":\"7861igdhiquyw\",\"payeeCurrencyTypeCd\":\"USD\",\"payerAcctNm\":\"METACOMP PTE. LTD.\",\"payerAcctNo\":\"11020018283\",\"tradeAmont\":987.0}";
        String signatureStr = "304402204eb7575371ce46eb151eb7ca92f96ce35c641d557e2e8d8955dd3afce382aa5602201583482a909d318ba4c179d8fdbcd6c7ab9c869dbe5c682e51eebbf47a55a64f";

        // 调用验证方法
        boolean isVerified = verifyECDSASignature(publicKeyStr, originalMessage, signatureStr);
        System.err.println("Signature verification result: " + isVerified);
    }
}

