package com.rsa.eximbankapp.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.R;

import java.text.SimpleDateFormat;
import java.util.Random;

public class TransactionDetailActivity extends FragmentActivity implements View.OnClickListener {

    private Dialog dialog;

    public TransactionModel transactionModel;

    TextView tvDatetime;
    TextView tvTransactionId;

    TextView tvUsername;
    TextView tvBankName;
    TextView tvBankAcc;

    TextView tvUsernameMy;
    TextView tvBankNameMy;
    TextView tvBankAccMy;

    TextView tvComment;
    TextView tvAmount;

    Button btnSubmit;
    ImageView btnShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //Change notification icon to dark color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_transaction_detail);

        InitVariable();
        InitView();
        InitListener();
        SetupView();
    }

    private void InitVariable() {
        transactionModel = (TransactionModel) getIntent().getSerializableExtra("TransactionModel");
    }

    private void InitView() {
        dialog = new Dialog(this);

        tvDatetime = (TextView) findViewById(R.id.tv_datetime);
        tvTransactionId = (TextView) findViewById(R.id.tv_txid);

        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvBankName = (TextView) findViewById(R.id.tv_bank_name);
        tvBankAcc = (TextView) findViewById(R.id.tv_bank_acc);

        tvUsernameMy = (TextView) findViewById(R.id.tv_username_my);
        tvBankNameMy = (TextView) findViewById(R.id.tv_bank_name_my);
        tvBankAccMy = (TextView) findViewById(R.id.tv_bank_acc_my);

        tvComment = (TextView) findViewById(R.id.tv_comment);
        tvAmount = (TextView) findViewById(R.id.tv_amount);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnShare = (ImageView) findViewById(R.id.btn_share);
    }

    private void InitListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getBooleanExtra("isShowSaveContact", false)) {
                    ShowSaveContact();
                } else {
                    finish();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = String.format("%,d", transactionModel.amount).replace(',', '.') + " VND";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareBody);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, transactionModel.title);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    private void SetupView() {
        if (getIntent().getBooleanExtra("isShowSaveContact", false)) {
            btnSubmit.setText("Hoàn tất");
        } else {
            btnSubmit.setText("Đóng");
        }

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        tvDatetime.setText(sdfDate.format(transactionModel.datetime));

        if (transactionModel.txId == null) transactionModel.txId =String.valueOf(100000 + new Random().nextInt(900000));
        tvTransactionId.setText("TX" + transactionModel.txId);

        if (transactionModel.isSend) {
            tvUsername.setText(transactionModel.username);
            tvBankName.setText(transactionModel.bankName);
            tvBankAcc.setText(transactionModel.bankAcc);

            tvUsernameMy.setText("Nguyen Quy Truong");
            tvBankNameMy.setText("Eximbank");
            tvBankAccMy.setText("10230490403");
        } else {
            tvUsernameMy.setText(transactionModel.username);
            tvBankNameMy.setText(transactionModel.bankName);
            tvBankAccMy.setText(transactionModel.bankAcc);

            tvUsername.setText("Nguyen Quy Truong");
            tvBankName.setText("Eximbank");
            tvBankAcc.setText("10230490403");
        }

        if (transactionModel.title != null)
            tvComment.setText(transactionModel.title);
        tvAmount.setText(String.format("%,d", Math.abs(transactionModel.amount)).replace(',', '.') + " VND");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enter:

                break;
        }
    }

    private void ShowSaveContact() {
        dialog.setContentView(R.layout.popup_contact_save);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvUsername = dialog.findViewById(R.id.tv_username);
        TextView tvBankName = dialog.findViewById(R.id.tv_bank_name);
        TextView tvBankAcc = dialog.findViewById(R.id.tv_bank_acc);

        if (transactionModel != null) {
            tvUsername.setText(transactionModel.username);
            tvBankName.setText(transactionModel.bankName);
            tvBankAcc.setText(transactionModel.bankAcc);
        }

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Đã lưu vào danh sách", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
}
