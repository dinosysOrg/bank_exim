package com.rsa.eximbankapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rsa.eximbankapp.Activity.TransactionActivity;
import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.R;

public class TransactionThirdFragment extends Fragment {

    TextView tvUsername;
    TextView tvBankAcc;
    TextView tvBankName;
    TextView tvAmount;
    TextView tvComment;
    Button btnSubmit;

    ProgressBar progressBar;

    TransactionModel transactionModel;

    public static Fragment getInstance(TransactionModel transactionModel) {
        TransactionThirdFragment fragment = new TransactionThirdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        fragment.transactionModel = transactionModel;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_third, container, false);

        tvUsername = (TextView) view.findViewById(R.id.tv_username);
        tvBankAcc = (TextView) view.findViewById(R.id.tv_bank_acc);
        tvBankName = (TextView) view.findViewById(R.id.tv_bank_name);
        tvAmount = (TextView) view.findViewById(R.id.tv_amount);
        tvComment = (TextView) view.findViewById(R.id.tv_comment);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        tvUsername.setText(transactionModel.username);
        tvBankAcc.setText(transactionModel.bankAcc);
        tvBankName.setText(transactionModel.bankName);
        tvAmount.setText(String.format("%,d", transactionModel.amount).replace(',', '.'));
        tvComment.setText(transactionModel.title);

        InitView();
        InitListener();
        return view;
    }

    private void InitView() {

    }

    private void InitListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show progress bar
                progressBar.setVisibility(View.VISIBLE);

                ((TransactionActivity) getActivity()).AddFragment(TransactionFourFragment.getInstance(transactionModel));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TransactionActivity) getActivity()).setActionbarTitle("Xác nhận chuyển tiền");

        //Hide keyboard
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //Hide progress bar
        progressBar.setVisibility(View.GONE);
    }
}
