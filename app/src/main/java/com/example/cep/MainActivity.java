package com.example.cep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText txtCep, txtState, txtCity, txtStreet;
    TextView tvStreet, tvState, tvNeighborhood, tvDdd, tvCity, tvBackground2;
    TextView lblStreet, lblState, lblNeighborhood, lblDdd, lblCity;
    Button btnClear1, btnClear2, btnAddress, btnCep;
    APIInterface apiInterface;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        initComponents();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        txtCep.requestFocus();
        setVisibility(false);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher mtw1 = new MaskTextWatcher(txtCep, smf);
        txtCep.addTextChangedListener(mtw1);

        btnAddress.setOnClickListener(v -> searchAddress());
        btnClear1.setOnClickListener(v -> clear1());
        btnCep.setOnClickListener(v -> searchCep());
        btnClear2.setOnClickListener(v -> clear2());
    }

    private void searchAddress() {
        Call<Address> call = apiInterface.getAddress(txtCep.getText().toString());
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response) {
                if (response.body() == null) return;

                tvStreet.setText(response.body().getLogradouro());
                tvState.setText(response.body().getUf());
                tvNeighborhood.setText(response.body().getBairro());
                tvDdd.setText(response.body().getDdd());
                tvCity.setText(response.body().getLocalidade());

                setVisibility(true);
            }

            @Override
            public void onFailure(@NonNull Call<Address> call, @NonNull Throwable t) { Log.e("Evandro", "Error."); }
        });
    }

    private void searchCep() {
        String state = txtState.getText().toString();
        String city = txtCity.getText().toString();
        String street = txtStreet.getText().toString();

        Call<List<Address>> call = apiInterface.getAddresses(state, city, street);
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                if (response.body() == null) return;

                List<Address> _listAddress = new ArrayList<>(response.body());

                if (response.body().size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Nenhum endereço encontrado.", Toast.LENGTH_LONG).show();
                    return;
                }

                listView.setAdapter(null);
                listView.invalidateViews();
                AddressAdapter adapter = new AddressAdapter(activity, _listAddress);
                listView.setAdapter(adapter);
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtStreet.getWindowToken(), 0);
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) { Log.e("Evandro", "Error"); }
        });
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