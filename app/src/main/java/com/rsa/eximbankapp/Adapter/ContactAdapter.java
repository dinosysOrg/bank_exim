package com.rsa.eximbankapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsa.eximbankapp.Model.ContactModel;
import com.rsa.eximbankapp.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    Context context;
    int resource;

    List<ContactModel> contacts;

    private View.OnClickListener onClickListener;

    public ContactAdapter(Context context, int resource, List<ContactModel> contacts, View.OnClickListener onClickListener){
        this.context = context;
        this.resource = resource;
        this.contacts = contacts;
        this.onClickListener = onClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, final int position) {
        final ContactModel contactModel = contacts.get(position);

        holder.ivAvatar.setImageResource(contactModel.avatar);
        holder.tvName.setText(contactModel.name);
        holder.tvBankName.setText(contactModel.bankName);
        holder.tvBankAcc.setText(" | STK: " + contactModel.bankAcc);
    }


    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        row.setOnClickListener(onClickListener);

        ViewHolder holder = new ViewHolder(row);
        holder.ivAvatar = (ImageView) row.findViewById(R.id.iv_avatar);
        holder.tvName = (TextView) row.findViewById(R.id.tv_name);
        holder.tvBankName = (TextView) row.findViewById(R.id.tv_bank_name);
        holder.tvBankAcc = (TextView) row.findViewById(R.id.tv_bank_acc);

        return holder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvBankName;
        TextView tvBankAcc;

        public ViewHolder(View view){
            super(view);
        }
    }

}