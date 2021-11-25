package com.example.cep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    URL url;
    int statusCode = 0;
    String response = null;
    Cep cep = new Cep();
    Thread t;
    EditText txtCep;
    TextView tvCep;
    String textCep = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCep = findViewById(R.id.txtCep);
        tvCep = findViewById(R.id.tvCep);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN-NNN");

        MaskTextWatcher mtw1 = new MaskTextWatcher(txtCep, smf);
        txtCep.addTextChangedListener(mtw1);

        Button btnSearch = findViewById(R.id.btnCep);
        btnSearch.setOnClickListener(v -> {

            t = new Thread(() -> {
                textCep = txtCep.getText().toString();
                cep = searchCep(textCep);
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("TAG", "btn cep: " + cep.toString());

            tvCep.setText("cep.toString()");

        });
    }


    private Cep searchCep(String c) {

        String API = "https://viacep.com.br/ws/" + c + "/json/";

        t = new Thread(() -> {
            try {
                url = new URL(API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                statusCode = connection.getResponseCode();
                InputStream is;
                if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST){
                    is = connection.getInputStream();
                }else{
                    is = connection.getErrorStream();
                }

                response = convertInputStreamToString(is);
                is.close();
                connection.disconnect();
            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                cep = new Gson().fromJson(response, Cep.class);
            }

            cep.setStatus(statusCode);
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("TAG", "CEP: " + cep);

        return  cep;
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