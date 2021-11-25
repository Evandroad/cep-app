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
    int statusCode = 600;
    String response = null;
    Address address = new Address();
    Thread t;
    EditText txtCep;
    EditText txtState;
    EditText txtCity;
    EditText txtStreet;
    TextView tvCep;
    String res;
    String textCep = "", textState = "", textCity = "", textStreet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCep = findViewById(R.id.txtCep);
        txtState = findViewById(R.id.txtState);
        txtCity = findViewById(R.id.txtCity);
        txtStreet = findViewById(R.id.txtStreet);
        tvCep = findViewById(R.id.tvCep);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN-NNN");

        MaskTextWatcher mtw1 = new MaskTextWatcher(txtCep, smf);
        txtCep.addTextChangedListener(mtw1);

        Button btnAddress = findViewById(R.id.btnAddress);
        btnAddress.setOnClickListener(v -> {

            textCep = txtCep.getText().toString();

            t = new Thread(() -> {
                address = searchAddress(textCep);
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("TAG", "btn address: " + address.toString());

            tvCep.setText(address.toString());

        });

        Button btnCep = findViewById(R.id.btnCep);
        btnCep.setOnClickListener(v -> {
            textState = txtState.getText().toString();
            textCity = txtCity.getText().toString();
            textStreet = txtStreet.getText().toString();

            t = new Thread(() -> {
                res = searchCep(textState, textCity, textStreet);
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("TAG", "btn address: " + res);

            tvCep.setText(res);
        });
    }


    private Address searchAddress(String c) {

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
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("TAG", "CEP: " + address);

        if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            address = new Gson().fromJson(response, Address.class);
        }

        address.setStatus(statusCode);

        return address;
    }

    private String searchCep(String state, String city, String street) {

        String API = "https://viacep.com.br/ws/" + state + "/" + city + "/" + street + "/json/";

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
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("TAG", "CEP: " + response);

        /*if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            address = new Gson().fromJson(response, Address.class);
        }*/

        //address.setStatus(statusCode);

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