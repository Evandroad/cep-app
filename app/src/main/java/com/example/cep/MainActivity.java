package com.example.cep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    URL url;
    int statusCode = 600;
    String response = null;
    Address address = new Address();
    Thread t;
    ListView listView;
    EditText txtCep, txtState, txtCity, txtStreet;
    TextView tvStreet, tvState, tvNeighborhood, tvDdd, tvCity, tvBackground2;
    TextView lblStreet, lblState, lblNeighborhood, lblDdd, lblCity;
    JsonArray jArray;
    String textCep = "", textState = "", textCity = "", textStreet = "";
    List<Address> listAddress = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCep = findViewById(R.id.txtCep);
        txtState = findViewById(R.id.txtState);
        txtCity = findViewById(R.id.txtCity);
        txtStreet = findViewById(R.id.txtStreet);
        tvStreet = findViewById(R.id.tvStreet);
        tvState = findViewById(R.id.tvState);
        tvNeighborhood = findViewById(R.id.tvNeighborhood);
        tvDdd = findViewById(R.id.tvDdd);
        tvCity = findViewById(R.id.tvCity);
        lblStreet = findViewById(R.id.lblStreet);
        lblState = findViewById(R.id.lblState);
        lblNeighborhood = findViewById(R.id.lblNeighborhood);
        lblDdd = findViewById(R.id.lblDdd);
        lblCity = findViewById(R.id.lblCity);
        tvBackground2 = findViewById(R.id.tvBackground2);
        listView = findViewById(R.id.listView);
        Button btnAddress = findViewById(R.id.btnAddress);
        Button btnCep = findViewById(R.id.btnCep);

        txtCep.requestFocus();

        hide();

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN-NNN");

        MaskTextWatcher mtw1 = new MaskTextWatcher(txtCep, smf);
        txtCep.addTextChangedListener(mtw1);

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

            tvStreet.setText(address.getLogradouro());
            tvState.setText(address.getUf());
            tvNeighborhood.setText(address.getBairro());
            tvDdd.setText(address.getDdd());
            tvCity.setText(address.getLocalidade());

            show();

        });

        btnCep.setOnClickListener(v -> {
            textState = txtState.getText().toString();
            textCity = txtCity.getText().toString();
            textStreet = txtStreet.getText().toString();

            t = new Thread(() -> {
                jArray = searchCep(textState, textCity, textStreet);
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < jArray.size() - 1; i++) {
                address = new Gson().fromJson(jArray.get(i).toString(), Address.class);
                listAddress.add(address);
            }

            listView.invalidateViews();
            ArrayAdapter<Address> adapter = new ArrayAdapter<Address>(this, android.R.layout.simple_list_item_1, listAddress);
            listView.setAdapter(adapter);
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

        if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            address = new Gson().fromJson(response, Address.class);
        }

        address.setStatus(statusCode);

        return address;
    }

    private JsonArray searchCep(String state, String city, String street) {

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

        if(statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            jArray = new JsonParser().parse(response).getAsJsonArray();
        }

        return jArray;
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

    private void hide() {
        tvStreet.setVisibility(View.GONE);
        tvState.setVisibility(View.GONE);
        tvNeighborhood.setVisibility(View.GONE);
        tvDdd.setVisibility(View.GONE);
        tvCity.setVisibility(View.GONE);
        lblStreet.setVisibility(View.GONE);
        lblState.setVisibility(View.GONE);
        lblNeighborhood.setVisibility(View.GONE);
        lblDdd.setVisibility(View.GONE);
        lblCity.setVisibility(View.GONE);
        tvBackground2.setVisibility(View.GONE);
    }

    private void show() {
        tvStreet.setVisibility(View.VISIBLE);
        tvState.setVisibility(View.VISIBLE);
        tvNeighborhood.setVisibility(View.VISIBLE);
        tvDdd.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        lblStreet.setVisibility(View.VISIBLE);
        lblState.setVisibility(View.VISIBLE);
        lblNeighborhood.setVisibility(View.VISIBLE);
        lblDdd.setVisibility(View.VISIBLE);
        lblCity.setVisibility(View.VISIBLE);
        tvBackground2.setVisibility(View.VISIBLE);
    }
}