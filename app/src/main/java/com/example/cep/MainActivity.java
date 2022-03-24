package com.example.cep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int statusCode = 500;
    private final String METHOD = "GET";

    ListView listView;
    EditText txtCep, txtState, txtCity, txtStreet;
    TextView tvStreet, tvState, tvNeighborhood, tvDdd, tvCity, tvBackground2;
    TextView lblStreet, lblState, lblNeighborhood, lblDdd, lblCity;
    Button btnClear1, btnClear2, btnAddress, btnCep;
    String textCep = "", textState = "", textCity = "", textStreet = "";
    List<Address> listAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        txtCep.requestFocus();
        setVisibility(false);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher mtw1 = new MaskTextWatcher(txtCep, smf);
        txtCep.addTextChangedListener(mtw1);

        btnAddress.setOnClickListener(v -> {
            textCep = txtCep.getText().toString();

            Address address = searchAddress(textCep);

            tvStreet.setText(address.getLogradouro());
            tvState.setText(address.getUf());
            tvNeighborhood.setText(address.getBairro());
            tvDdd.setText(address.getDdd());
            tvCity.setText(address.getLocalidade());

            setVisibility(true);
        });

        btnCep.setOnClickListener(v -> {
            textState = txtState.getText().toString();
            textCity = txtCity.getText().toString();
            textStreet = txtStreet.getText().toString();
            listAddress = new ArrayList<>();

            listAddress = searchCep(textState, textCity, textStreet);

            if (listAddress.size() > 0) {
                listView.setAdapter(null);
                listView.invalidateViews();
                AddressAdapter adapter = new AddressAdapter(this, listAddress);
                listView.setAdapter(adapter);
                InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtStreet.getWindowToken(), 0);
            } else {
                Toast.makeText(this, "Nenhum endereço encontrado.", Toast.LENGTH_LONG).show();
            }
        });

        btnClear1.setOnClickListener(v -> {
            txtCep.setText("");
            tvStreet.setText("");
            tvState.setText("");
            tvNeighborhood.setText("");
            tvDdd.setText("");
            tvCity.setText("");
            setVisibility(false);
        });

        btnClear2.setOnClickListener(v -> {
            txtState.setText("");
            txtCity.setText("");
            txtStreet.setText("");
            listView.setAdapter(null);
            listView.invalidateViews();
            txtState.requestFocus();
        });
    }

    private void initComponents() {
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
        btnAddress = findViewById(R.id.btnAddress);
        btnCep = findViewById(R.id.btnCep);
        btnClear1 = findViewById(R.id.btnClear1);
        btnClear2 = findViewById(R.id.btnClear2);
    }

    private List<Address> searchCep(String state, String city, String street) {

        String API = "https://viacep.com.br/ws/" + state + "/" + city + "/" + street + "/json/";

        String response = HttpUtil.getConnect(METHOD, API);
        List<Address> addressList = new ArrayList<>();
        if(response != null) {
            Address[] addresses = new Gson().fromJson(response, Address[].class);
            addressList = Arrays.asList(addresses);
        }

        return addressList;
    }

    private Address searchAddress(String c) {

        String API = "https://viacep.com.br/ws/" + c + "/json/";

        String response = HttpUtil.getConnect(METHOD, API);
        Address address = new Address();
        if (response != null)
            address = new Gson().fromJson(response, Address.class);
        address.setStatus(statusCode);

        return address;
    }

    private void setVisibility(boolean visibility) {
        tvStreet.setVisibility(visibility ? View.VISIBLE : View.GONE);
        tvStreet.setVisibility(visibility ? View.VISIBLE : View.GONE);
        tvState.setVisibility(visibility ? View.VISIBLE : View.GONE);
        tvNeighborhood.setVisibility(visibility ? View.VISIBLE : View.GONE);
        tvDdd.setVisibility(visibility ? View.VISIBLE : View.GONE);
        tvCity.setVisibility(visibility ? View.VISIBLE : View.GONE);
        lblStreet.setVisibility(visibility ? View.VISIBLE : View.GONE);
        lblState.setVisibility(visibility ? View.VISIBLE : View.GONE);
        lblNeighborhood.setVisibility(visibility ? View.VISIBLE : View.GONE);
        lblDdd.setVisibility(visibility ? View.VISIBLE : View.GONE);
        lblCity.setVisibility(visibility ? View.VISIBLE : View.GONE);
        tvBackground2.setVisibility(visibility ? View.VISIBLE : View.GONE);
        btnClear1.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}