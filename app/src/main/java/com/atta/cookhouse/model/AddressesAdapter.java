package com.atta.cookhouse.model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atta.cookhouse.newaddress.NewAddressActivity;
import com.atta.cookhouse.R;

import java.util.List;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.MyViewHolder> {

    private Context mContext;

    private List<Address> addresses;

    public AddressesAdapter(Context mContext, List<Address> properties) {
        this.mContext = mContext;
        this.addresses = properties;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.address_list_item, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final Address address = addresses.get(i) ;

        final int id = address.getId();
        final String name = address.getAddressName();

        final String area = address.getArea();
        final String fullAddress = address.getFullAddress();

        myViewHolder.addressName.setText(name);
        myViewHolder.area.setText(area);
        myViewHolder.fullAddress.setText(String.valueOf(fullAddress));

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewAddressActivity.class);
                intent.putExtra("address", address);
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView addressName, area, fullAddress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            addressName = itemView.findViewById(R.id.address_name_txt);
            area = itemView.findViewById(R.id.area_txt);
            fullAddress = itemView.findViewById(R.id.full_address_txt);
        }
    }

    public void removeItem(int position) {
        addresses.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Address item, int position) {
        addresses.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


    public List<Address> getList(){
        return addresses;
    }
}
