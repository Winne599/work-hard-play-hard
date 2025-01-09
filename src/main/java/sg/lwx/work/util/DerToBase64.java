package sg.lwx.work.util;

import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

public class DerToBase64 {
    public static void main(String[] args) throws Exception {
        byte[] derBytes = Files.readAllBytes(Paths.get("D:\\public_key.der")); // der文件的位置
        String base64Str = Base64.getEncoder().encodeToString(derBytes);
        System.err.println(base64Str);
    }
}
