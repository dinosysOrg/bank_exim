package com.rsa.eximbankapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionAdapter  extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>  {

    Context context;
    int resource;

    List<TransactionModel> transactions;

    private View.OnClickListener onClickListener;

    public TransactionAdapter(Context context, int resource, List<TransactionModel> transactions, View.OnClickListener onClickListener){
        this.context = context;
        this.resource = resource;
        this.transactions = transactions;
        this.onClickListener = onClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, final int position) {
        final TransactionModel transactionModel = transactions.get(position);

        holder.ivAvatar.setImageResource(transactionModel.avatar);
        holder.tvTitle.setText(transactionModel.title);

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        holder.tvDatetime.setText(sdfDate.format(transactionModel.datetime) + " vào lúc " + sdfTime.format(transactionModel.datetime));

        if (transactionModel.amount > 0) {
            holder.tvAmount.setText("+" + String.format("%,d", transactionModel.amount).replace(',', '.'));
            holder.tvAmount.setTextColor(Color.parseColor("#0FB0F3"));
        } else {
            holder.tvAmount.setText(String.format("%,d", transactionModel.amount).replace(',', '.'));
            holder.tvAmount.setTextColor(Color.parseColor("#003863"));
        }
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        row.setOnClickListener(onClickListener);

        ViewHolder holder = new ViewHolder(row);
        holder.ivAvatar = (ImageView) row.findViewById(R.id.iv_avatar);
        holder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
        holder.tvDatetime = (TextView) row.findViewById(R.id.tv_datetime);
        holder.tvAmount = (TextView) row.findViewById(R.id.tv_amount);

        return holder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvTitle;
        TextView tvDatetime;
        TextView tvAmount;

        public ViewHolder(View view){
            super(view);
        }
    }

}