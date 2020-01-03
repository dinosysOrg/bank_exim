package com.rsa.eximbankapp.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rsa.eximbankapp.Activity.TransactionActivity;
import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.R;
import com.rsa.eximbankapp.Utils.KeyboardUtils;

import java.sql.Date;
import java.util.Calendar;

public class TransactionSecondFragment extends Fragment {

    private Spinner spinnerBank;
    private Spinner spinnerCity;
    private Spinner spinnerAgency;
    private Spinner spinnerAccount;
    private EditText etName;
    private EditText etAmount;
    private EditText etComment;
    private Button btn_submit;

    public static Fragment getInstance(String username) {
        TransactionSecondFragment fragment = new TransactionSecondFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_second, container, false);

        spinnerBank = (Spinner) view.findViewById(R.id.sp_bank);
        spinnerCity = (Spinner) view.findViewById(R.id.sp_city);
        spinnerAgency = (Spinner) view.findViewById(R.id.sp_agency);
        spinnerAccount = (Spinner) view.findViewById(R.id.sp_account);
        etName = (EditText) view.findViewById(R.id.et_name);
        etAmount = (EditText) view.findViewById(R.id.et_amount);
        etComment = (EditText) view.findViewById(R.id.et_comment);

        btn_submit = (Button) view.findViewById(R.id.btn_submit);

        InitView();
        InitListener();
        return view;
    }

    private void InitView() {
        ArrayAdapter adapterBank = ArrayAdapter.createFromResource(getContext(), R.array.bank_arrays, R.layout.spinner_item_dropdown);
        spinnerBank.setAdapter(adapterBank);

        ArrayAdapter adapterCity = ArrayAdapter.createFromResource(getContext(), R.array.city_arrays, R.layout.spinner_item_dropdown);
        spinnerCity.setAdapter(adapterCity);

        ArrayAdapter adapterAgency = ArrayAdapter.createFromResource(getContext(), R.array.agency_arrays, R.layout.spinner_item_dropdown);
        spinnerAgency.setAdapter(adapterAgency);

        ArrayAdapter adapterAccount = ArrayAdapter.createFromResource(getContext(), R.array.account_arrays, R.layout.spinner_item_dropdown);
        spinnerAccount.setAdapter(adapterAccount);

        Bundle bundle = getArguments();
        String username = bundle.getString("username");
        etName.setText(username);
    }

    private void InitListener() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerBank.getSelectedItemPosition() == 0 || spinnerCity.getSelectedItemPosition() == 0
                        || spinnerAgency.getSelectedItemPosition() == 0 || spinnerAccount.getSelectedItemPosition() == 0
                        || etName.getText().toString().equals("") || etAmount.getText().toString().equals(""))  {
                    ((TransactionActivity) getActivity()).ShowPopup("Lỗi xảy ra", "Vui lòng điền đầy đủ thông tin");
                } else {
                    TransactionModel transactionModel = new TransactionModel(true, etComment.getText().toString(),
                            etName.getText().toString(), 0, new Date(Calendar.getInstance().getTime().getTime()),
                            Integer.parseInt(etAmount.getText().toString()),
                            spinnerBank.getSelectedItem().toString(), spinnerAccount.getSelectedItem().toString());

                    ((TransactionActivity) getActivity()).AddFragment(TransactionThirdFragment.getInstance(transactionModel));
                }
            }
        });

        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard", "keyboard visible: "+isVisible);
                if (isVisible) btn_submit.setVisibility(View.GONE);
                else btn_submit.setVisibility(View.VISIBLE);
            }
        });

        etComment.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btn_submit.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TransactionActivity) getActivity()).setActionbarTitle("Chuyển tiền qua tài khoản");
    }
}
