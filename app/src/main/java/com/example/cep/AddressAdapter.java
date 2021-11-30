package com.example.cep;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AddressAdapter extends BaseAdapter {

    private List<Address> addresses;
    private Activity activity;

    public AddressAdapter(Activity activity, List<Address> addresses) {
        this.activity = activity;
        this.addresses = addresses;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int i) {
        return addresses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = activity.getLayoutInflater().inflate(R.layout.item, viewGroup, false);
        TextView tvItemStreet = v.findViewById(R.id.tvItemStreet);
        TextView tvItemState = v.findViewById(R.id.tvItemState);
        TextView tvItemNeighborhood = v.findViewById(R.id.tvItemNeighborhood);
        TextView tvItemCity = v.findViewById(R.id.tvItemCity);
        TextView tvItemCep = v.findViewById(R.id.tvItemCep);

        Address a = addresses.get(i);

        tvItemStreet.setText(a.getLogradouro());
        tvItemState.setText(a.getUf());
        tvItemNeighborhood.setText(a.getBairro());
        tvItemCity.setText(a.getLocalidade());
        tvItemCep.setText(a.getCep());

        return v;
    }
}
