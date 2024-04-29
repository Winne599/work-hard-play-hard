package sg.lwx.work.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * create by lianwenxiu on 2023/12/3
 *
 * @author lianwenxiu
 */
public class RpcClient {

    public static void main(String[] args) {
        try {
            //
            String rpcUrl = "http://example.com/rpc-endpoint";

            //
            String rpcRequest = "{\"method\": \"yourRpcMethod\", \"params\": [\"param1\", \"param2\"], \"id\": 1}";

            //
            URL url = new URL(rpcUrl);

            //
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //
            connection.setRequestMethod("POST");

            //
            connection.setDoOutput(true);

            //
            connection.setRequestProperty("Content-Type", "application/json");

            //
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = rpcRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            //
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            //
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Content: " + response.toString());
            }

            //
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String sendPostRequest(String rpcUrl, String rpcRequest) {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            //
            URL url = new URL(rpcUrl);
            // 
            connection = (HttpURLConnection) url.openConnection();
            //
            connection.setRequestMethod("POST");
            // 
            connection.setDoOutput(true);
            // 
            connection.setRequestProperty("Content-Type", "application/json");
            //
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = rpcRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            // 
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            // 
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Content: " + response.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //
            connection.disconnect();
        }
        return response.toString();
    }
}


