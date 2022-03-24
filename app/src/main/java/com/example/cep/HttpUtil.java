package com.example.cep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    private static String response = null;

    public static String getConnect(String httpMethod, String requestURL) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(requestURL);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod(httpMethod);
                httpConn.connect();
                int statusCode = httpConn.getResponseCode();
                InputStream is = null;
                if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST){
                    is = httpConn.getInputStream();
                    response = convertInputStreamToString(is);
                }

                if (is != null) is.close();
                httpConn.disconnect();
            } catch (IOException e) { e.printStackTrace(); }

        });
        thread.start();
        try { thread.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return response;
    }

    private static String convertInputStreamToString(InputStream is){
        StringBuffer buffer = new StringBuffer();
        BufferedReader br;
        String line;
        try{
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine())!=null){
                buffer.append(line);
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

}