package com.example.cep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Evandro", "ok");

        apiInterface = APIClient.getClient().create(APIInterface.class);
        initComponents();
        txtCep.requestFocus();
        setVisibility(false);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher mtw1 = new MaskTextWatcher(txtCep, smf);
        txtCep.addTextChangedListener(mtw1);

        btnAddress.setOnClickListener(v -> {
            textCep = txtCep.getText().toString();

            Call<Address> call = apiInterface.getAddress("94130400");
            call.enqueue(new Callback<Address>() {
                @Override
                public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response) {
                    if (response.body() != null) {
                        Log.i("Evandro", response.body().getLocalidade());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Address> call, @NonNull Throwable t) {
                    Log.e("Evandro", "Error.");
                }
            });

//            Address address = searchAddress(textCep);
//
//            tvStreet.setText(address.getLogradouro());
//            tvState.setText(address.getUf());
//            tvNeighborhood.setText(address.getBairro());
//            tvDdd.setText(address.getDdd());
//            tvCity.setText(address.getLocalidade());
//
//            setVisibility(true);
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

        btnClear1.setOnClickListener(v -> clear1());
        btnClear2.setOnClickListener(v -> clear2());
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

    private void clear1() {
        txtCep.setText("");
        tvStreet.setText("");
        tvState.setText("");
        tvNeighborhood.setText("");
        tvDdd.setText("");
        tvCity.setText("");
        setVisibility(false);
    }

    private void clear2() {
        txtState.setText("");
        txtCity.setText("");
        txtStreet.setText("");
        listView.setAdapter(null);
        listView.invalidateViews();
        txtState.requestFocus();
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