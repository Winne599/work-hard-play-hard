package sg.lwx.work.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author lianwenxiu
 *
 */
public class FileWriteExample {
    public static void main(String[] args) {
        // 1.
        File targetFile = new File("D:\\ideaProjects/play/src/main/java/com/lwxtest/web3/utils/test_file_write.txt");


        //
        CharSequence dataToWrite = "Hello, FileUtils!";

        //
        try {
            FileUtils.write(targetFile, dataToWrite, "UTF-8");
            System.out.println("Data has been written to the file.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
