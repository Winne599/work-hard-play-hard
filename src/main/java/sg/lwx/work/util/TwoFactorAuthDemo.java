package sg.lwx.work.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.jboss.aerogear.security.otp.api.Base32;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TwoFactorAuthDemo {



    public String generateSecret() {
        // 1.  GoogleAuthenticator
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        // 2.
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        //
        String secretKey = key.getKey();

        return secretKey;
    }

    public String generateSecretByBase32() {
        String secret = Base32.random();
        return secret;
    }


    /**
     *
     * @param secretKey
     * @return
     */
    public int generate2FaBySecret(String secretKey) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        //
        int verificationCode = gAuth.getTotpPassword(secretKey);
        return verificationCode;
    }


    /**
     *
     * @param secretKey
     * @param verificationCode
     * @return
     */
    public boolean verify2Fa(String secretKey,int verificationCode) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean isCodeValid = gAuth.authorize(secretKey, verificationCode);
        if (isCodeValid) {
            System.out.println("Verification Code is valid.");
        } else {
            System.out.println("Verification Code is invalid.");
        }
        return isCodeValid;
    }




    public static String  generateQRCode(String text, int width, int height) throws WriterException, IOException {

        //
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 1);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        //  QRCodeWriter 
        Writer writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

        //
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);

        //  BufferedImage  Base64
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        return Base64.getEncoder().encodeToString(os.toByteArray());
    }


    public static void main(String[] args) throws IOException, WriterException {
        String qrCode = generateQRCode("lianwenxiu",400,400);
        System.err.println("qrCode:"+qrCode);


    }


}

