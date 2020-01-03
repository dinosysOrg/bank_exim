package com.rsa.eximbankapp.Fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rsa.securidlib.Otp;
import com.rsa.securidlib.exceptions.SecurIDLibException;
import com.rsa.securidlib.tokenstorage.TokenMetadata;
import com.rsa.eximbankapp.Activity.TransactionActivity;
import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.Network.NetworkCenter;
import com.rsa.eximbankapp.R;
import com.rsa.eximbankapp.Setting.SecurIDLibHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class TransactionFourFragment extends Fragment implements View.OnClickListener, TextWatcher {

    TransactionModel transactionModel;

    private EditText etOTP1;
    private EditText etOTP2;
    private EditText etOTP3;
    private EditText etOTP4;
    private EditText etOTP5;
    private EditText etOTP6;
    private EditText etOTP7;
    private EditText etOTP8;

    private TextView tvOTP1;
    private TextView tvOTP2;
    private TextView tvOTP3;
    private TextView tvOTP4;
    private TextView tvOTP5;
    private TextView tvOTP6;
    private TextView tvOTP7;
    private TextView tvOTP8;

    private TextView tvTXID1;
    private TextView tvTXID2;
    private TextView tvTXID3;
    private TextView tvTXID4;
    private TextView tvTXID5;
    private TextView tvTXID6;
    private TextView tvTXID7;
    private TextView tvTXID8;

    private TextView tvSIGN1;
    private TextView tvSIGN2;
    private TextView tvSIGN3;
    private TextView tvSIGN4;
    private TextView tvSIGN5;
    private TextView tvSIGN6;
    private TextView tvSIGN7;
    private TextView tvSIGN8;

    private TextView tvTimeRemain;

    private TextView btnResendOTP;
    private TextView btnPasswordOTP;
    private TextView btnFingerprintOTP;
    private LinearLayout llCodeBtn;
    private LinearLayout llCodeView;
    private LinearLayout llOtpView;
    private LinearLayout llTVView;
    private LinearLayout llSignView;
    private Button btnSignCode;

    private ProgressBar progressBar;
    private RelativeLayout rlRootview;

    private boolean isTokenSend;
    private Otp otpCode;
    private int timeRemain = 0;

    public interface OnAuthCallback {
        void onSuccess();
    }

    public static Fragment getInstance(TransactionModel transactionModel) {
        TransactionFourFragment fragment = new TransactionFourFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        fragment.transactionModel = transactionModel;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_four, container, false);

        etOTP1 = (EditText) view.findViewById(R.id.et_otp_1);
        etOTP2 = (EditText) view.findViewById(R.id.et_otp_2);
        etOTP3 = (EditText) view.findViewById(R.id.et_otp_3);
        etOTP4 = (EditText) view.findViewById(R.id.et_otp_4);
        etOTP5 = (EditText) view.findViewById(R.id.et_otp_5);
        etOTP6 = (EditText) view.findViewById(R.id.et_otp_6);
        etOTP7 = (EditText) view.findViewById(R.id.et_otp_7);
        etOTP8 = (EditText) view.findViewById(R.id.et_otp_8);

        tvOTP1 = (TextView) view.findViewById(R.id.tv_otp_1);
        tvOTP2 = (TextView) view.findViewById(R.id.tv_otp_2);
        tvOTP3 = (TextView) view.findViewById(R.id.tv_otp_3);
        tvOTP4 = (TextView) view.findViewById(R.id.tv_otp_4);
        tvOTP5 = (TextView) view.findViewById(R.id.tv_otp_5);
        tvOTP6 = (TextView) view.findViewById(R.id.tv_otp_6);
        tvOTP7 = (TextView) view.findViewById(R.id.tv_otp_7);
        tvOTP8 = (TextView) view.findViewById(R.id.tv_otp_8);

        tvTXID1 = (TextView) view.findViewById(R.id.tv_txid_1);
        tvTXID2 = (TextView) view.findViewById(R.id.tv_txid_2);
        tvTXID3 = (TextView) view.findViewById(R.id.tv_txid_3);
        tvTXID4 = (TextView) view.findViewById(R.id.tv_txid_4);
        tvTXID5 = (TextView) view.findViewById(R.id.tv_txid_5);
        tvTXID6 = (TextView) view.findViewById(R.id.tv_txid_6);
        tvTXID7 = (TextView) view.findViewById(R.id.tv_txid_7);
        tvTXID8 = (TextView) view.findViewById(R.id.tv_txid_8);

        tvSIGN1 = (TextView) view.findViewById(R.id.tv_sign_1);
        tvSIGN2 = (TextView) view.findViewById(R.id.tv_sign_2);
        tvSIGN3 = (TextView) view.findViewById(R.id.tv_sign_3);
        tvSIGN4 = (TextView) view.findViewById(R.id.tv_sign_4);
        tvSIGN5 = (TextView) view.findViewById(R.id.tv_sign_5);
        tvSIGN6 = (TextView) view.findViewById(R.id.tv_sign_6);
        tvSIGN7 = (TextView) view.findViewById(R.id.tv_sign_7);
        tvSIGN8 = (TextView) view.findViewById(R.id.tv_sign_8);

        etOTP1.addTextChangedListener(this);
        etOTP2.addTextChangedListener(this);
        etOTP3.addTextChangedListener(this);
        etOTP4.addTextChangedListener(this);
        etOTP5.addTextChangedListener(this);
        etOTP6.addTextChangedListener(this);
        etOTP7.addTextChangedListener(this);
        etOTP8.addTextChangedListener(this);

        if (transactionModel.amount < 50000000) {
            etOTP1.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP2.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP3.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP4.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP5.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP6.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP7.setInputType(InputType.TYPE_CLASS_NUMBER);
            etOTP8.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        tvTimeRemain = (TextView) view.findViewById(R.id.tv_time_remain);

        btnResendOTP = (TextView) view.findViewById(R.id.btn_resend_otp);
        btnPasswordOTP = (TextView) view.findViewById(R.id.btn_password_otp);
        btnFingerprintOTP = (TextView) view.findViewById(R.id.btn_fingerprint_otp);
        btnSignCode = (Button) view.findViewById(R.id.btn_sign_code);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        rlRootview = (RelativeLayout) view.findViewById(R.id.rl_rootview);
        llCodeBtn = (LinearLayout) view.findViewById(R.id.ll_code_btn);
        llCodeView = (LinearLayout) view.findViewById(R.id.ll_code_view);
        llOtpView = (LinearLayout) view.findViewById(R.id.ll_otp_view);
        llTVView = (LinearLayout) view.findViewById(R.id.ll_tx_view);
        llSignView = (LinearLayout) view.findViewById(R.id.ll_sign_view);

        InitVariable();
        InitView();
        InitListener();
        InitAuth();

        ShowKeyboard();
        return view;
    }

    private void InitVariable() {
        GenerateTxId();
    }

    private void InitView() {
        llCodeBtn.setVisibility(View.VISIBLE);
        llCodeView.setVisibility(View.GONE);
    }

    private void InitListener() {
        btnResendOTP.setOnClickListener(this);
        btnPasswordOTP.setOnClickListener(this);
        btnFingerprintOTP.setOnClickListener(this);
        btnSignCode.setOnClickListener(this);

        // ContentView is the root view of the layout of this activity/fragment
        rlRootview.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        rlRootview.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rlRootview.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        Log.d(TAG, "keypadHeight = " + keypadHeight);
                        //TODO: demo
//                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
//                            // keyboard is opened
//                            if (transactionModel.amount >= 50000000) {
//                                llOtpView.setVisibility(View.GONE);
//                                llTVView.setVisibility(View.GONE);
//                            }
//                        }  else {
//                            if (transactionModel.amount >= 50000000) {
//                                llOtpView.setVisibility(View.VISIBLE);
//                                llTVView.setVisibility(View.VISIBLE);
//                            }
//                        }
                    }
                });
    }

    private void ShowKeyboard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etOTP1.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etOTP1, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500 );//time in milisecond
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TransactionActivity) getActivity()).setActionbarTitle("Xác nhận OTP");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_resend_otp:
                ((TransactionActivity) getActivity()).ShowPopup("Gửi lại mã OTP", "Mã OTP đã được gửi lại thành công");
                break;
            case R.id.btn_password_otp:
                ((TransactionActivity) getActivity()).ShowPasswordPopup(new OnAuthCallback() {
                    @Override
                    public void onSuccess() {
                        AutoFillToken();
                    }
                });
                break;
            case R.id.btn_fingerprint_otp:
                ((TransactionActivity) getActivity()).DisplayBiometricPrompt(new OnAuthCallback() {
                    @Override
                    public void onSuccess() {
                        AutoFillToken();
                    }
                });
                break;
            case R.id.btn_sign_code:
                String txSigned = SignTransactionIdx(otpCode.getOtp(), transactionModel.txId);
                FillSigningCode(txSigned);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        EditText editText = (EditText) getActivity().getCurrentFocus();

        if (editText != null && editText.length() > 0)  {
            View next = editText.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
            if (next != null)
                next.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isAutoFill) return;
        if (etOTP1.getText().length() > 0 && etOTP2.getText().length() > 0 && etOTP3.getText().length() > 0 && etOTP4.getText().length() > 0
                && etOTP5.getText().length() > 0 && etOTP6.getText().length() > 0 && etOTP7.getText().length() > 0 && etOTP8.getText().length() > 0) {
            String otp = etOTP1.getText().toString() + etOTP2.getText().toString() + etOTP3.getText().toString() + etOTP4.getText().toString()
                    + etOTP5.getText().toString() + etOTP6.getText().toString() + etOTP7.getText().toString() + etOTP8.getText().toString();
            Log.v("afterTextChanged", "token: " + otp);
            SubmitToken(otp);
        }
    }


    /*
    Transaction auth
     */

    private SecurIDLibHelper m_libHelper;
    private TokenMetadata m_activeToken;

    private void InitAuth() {
        try {
            m_libHelper = SecurIDLibHelper.getInstance(getContext());
            ArrayList<String> tokenSNList = m_libHelper.getTokenSNList();
            if (tokenSNList.size() > 0)
                m_activeToken = m_libHelper.getTokenMetadata(tokenSNList.get(0));
        } catch (SecurIDLibException e) {
            e.printStackTrace();
        }
    }

    private boolean isAutoFill;
    private void AutoFillToken() {
        if (m_activeToken == null) {
            ((TransactionActivity) getActivity()).ShowPopup("Lỗi xảy ra", "Ứng dụng chưa được kích hoạt RSA");
            return;
        }

        //TODO: demo
//        llCodeBtn.setVisibility(View.GONE);
//        llCodeView.setVisibility(View.VISIBLE);

        isAutoFill = true;
        final Otp otp = m_libHelper.getTokenCode(m_activeToken.getSerialNumber(), "1234"); //TODO: hardcode PIN
        if (transactionModel.amount >= 50000000) {
            String txSigned = SignTransactionIdx(otp.getOtp(), transactionModel.txId);
            Log.d("AutoFillToken", txSigned);
            FillToken(txSigned);
            SubmitToken(txSigned);
        } else {
            FillToken(otp.getOtp());
            SubmitToken(otp.getOtp());
        }

        //TODO: demo
//        if (transactionModel.amount >= 50000000) {
//            llTVView.setVisibility(View.VISIBLE);
//            llSignView.setVisibility(View.VISIBLE);
//        } else {
//            llTVView.setVisibility(View.GONE);
//            llSignView.setVisibility(View.GONE);
//        }

        FillTransactionIdx(transactionModel.txId);

        progressBar.setVisibility(View.VISIBLE);
        startTimeRemain();
    }

    private void GenerateToken() {
        //TODO: regenerate token
        otpCode = m_libHelper.getTokenCode(m_activeToken.getSerialNumber(), "1234"); //TODO: hardcode PIN
        timeRemain = otpCode.getTimeRemaining();
    }

    private void SubmitToken(String otp) {
        progressBar.setVisibility(View.VISIBLE);

        isTokenSend = true;
        if (transactionModel.amount >= 50000000) {
            new AuthAsyncTaskII(otp).execute();
        } else {
            new AuthAsyncTaskI(otp).execute();
        }
    }

    private void GenerateTxId() {
        //TODO: HARD CODE TxID
        Random random = new Random();
        final String txId = String.valueOf(10000000 + random.nextInt(90000000));
        transactionModel.txId = txId;
    }

    private void startTimeRemain() {
        Log.v("startTimeRemain", "Checkpoint 1");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            public void run() {
                while (!isTokenSend) {
                    if (otpCode == null || timeRemain <= 0) {
                        GenerateToken();
                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    Log.v("startTimeRemain", "Checkpoint 2");
                    handler.post(new Runnable() {
                        public void run() {
                            Log.v("startTimeRemain", "Checkpoint 3 - time remain: " + otpCode.getTimeRemaining());
                            FillSystemToken(otpCode.getOtp());
                            tvTimeRemain.setText("(Còn hiệu lực trong " + timeRemain + "s )");
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    timeRemain -= 1;
                }
            }
        };
        new Thread(runnable).start();
    }

    public class AuthAsyncTaskI extends AsyncTask<Void, Integer, Boolean> {

        private String otp;

        public AuthAsyncTaskI(String otp) {
            this.otp = otp;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (otp != null) {
                Log.d("AuthAsyncTaskI", "otp " + otp);

                if (new NetworkCenter(getContext()).AuthTransactionI("user4", otp)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            progressBar.setVisibility(View.GONE);
            if (isSuccess) {
//                Toast.makeText(getContext(), "Auth Transaction I Success", Toast.LENGTH_SHORT).show();
                ((TransactionActivity) getActivity()).OpenTransactionDetail(transactionModel);
            } else {
//                Toast.makeText(getContext(), "Auth Transaction I Fail", Toast.LENGTH_SHORT).show();
                ((TransactionActivity) getActivity()).ShowTransactionFailPopup();
            }
        }
    }

    public class AuthAsyncTaskII extends AsyncTask<Void, Integer, Boolean> {

        private String otp;

        public AuthAsyncTaskII(String otp) {
            this.otp = otp;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (otp != null) {
                Log.d("AuthAsyncTaskII", "otp " + otp);

                if (new NetworkCenter(getContext()).AuthTransactionII("user4", otp, transactionModel.txId)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            progressBar.setVisibility(View.GONE);
            if (isSuccess) {
//                Toast.makeText(getContext(), "Auth Transaction II Success", Toast.LENGTH_SHORT).show();
                ((TransactionActivity) getActivity()).OpenTransactionDetail(transactionModel);
            } else {
//                Toast.makeText(getContext(), "Auth Transaction II Fail", Toast.LENGTH_SHORT).show();
                ((TransactionActivity) getActivity()).ShowTransactionFailPopup();
            }
        }
    }

    private String SignTransactionIdx(String otpString, String tidString) {
        try {
            //Encryption part using simple method: ciphertext=(first8bytes(hash(key)) + cleartext) mod 16, display in hex string
            java.security.MessageDigest msg = java.security.MessageDigest.getInstance("MD5");
            msg.update(tidString.getBytes(), 0, tidString.length() );

            String keyString = new BigInteger(1, msg.digest()).toString(16);
            //Toast.makeText(SecurIDSampleActivity.this, "Debug: " + keyString, Toast.LENGTH_LONG).show();

            byte[] clearBytes = otpString.getBytes();
            byte[] keyBytes = keyString.getBytes();

            char[] hexArray = "0123456789abcdef".toCharArray();
            char[] hexChars = new char[clearBytes.length];
            for (int i = 0; i < clearBytes.length; i++) {
                int cb = (clearBytes[i] < 59)? clearBytes[i] - '0': clearBytes[i] + 10 - 'a';
                int kb = (keyBytes[i] < 59)? keyBytes[i] - '0': keyBytes[i] + 10 - 'a';
                int cipherByte = (cb + kb)%16;
                hexChars[i] = hexArray[cipherByte];
            }

            Log.i("SID Test)", hexChars.toString());
            return new String(hexChars);
        } catch (Exception e) {
            Log.e("SID Test", "exception: " + e.getMessage());
            Log.e("SID Test", "exception: " + e.toString());
        }
        return null;
    }

    private void FillToken(String token) {
        int index = 0;
        for(char number : token.toCharArray()) {
            switch (index) {
                case 0:
                    etOTP1.setText(String.valueOf(number));
                    break;
                case 1:
                    etOTP2.setText(String.valueOf(number));
                    break;
                case 2:
                    etOTP3.setText(String.valueOf(number));
                    break;
                case 3:
                    etOTP4.setText(String.valueOf(number));
                    break;
                case 4:
                    etOTP5.setText(String.valueOf(number));
                    break;
                case 5:
                    etOTP6.setText(String.valueOf(number));
                    break;
                case 6:
                    etOTP7.setText(String.valueOf(number));
                    break;
                case 7:
                    etOTP8.setText(String.valueOf(number));
                    break;
            }
            index++;
        }
    }

    private void FillSystemToken(String token) {
        int index = 0;
        for(char number : token.toCharArray()) {
            switch (index) {
                case 0:
                    tvOTP1.setText(String.valueOf(number));
                    break;
                case 1:
                    tvOTP2.setText(String.valueOf(number));
                    break;
                case 2:
                    tvOTP3.setText(String.valueOf(number));
                    break;
                case 3:
                    tvOTP4.setText(String.valueOf(number));
                    break;
                case 4:
                    tvOTP5.setText(String.valueOf(number));
                    break;
                case 5:
                    tvOTP6.setText(String.valueOf(number));
                    break;
                case 6:
                    tvOTP7.setText(String.valueOf(number));
                    break;
                case 7:
                    tvOTP8.setText(String.valueOf(number));
                    break;
            }
            index++;
        }
    }

    private void FillTransactionIdx(String txid) {
        int index = 0;
        for(char number : txid.toCharArray()) {
            switch (index) {
                case 0:
                    tvTXID1.setText(String.valueOf(number));
                    break;
                case 1:
                    tvTXID2.setText(String.valueOf(number));
                    break;
                case 2:
                    tvTXID3.setText(String.valueOf(number));
                    break;
                case 3:
                    tvTXID4.setText(String.valueOf(number));
                    break;
                case 4:
                    tvTXID5.setText(String.valueOf(number));
                    break;
                case 5:
                    tvTXID6.setText(String.valueOf(number));
                    break;
                case 6:
                    tvTXID7.setText(String.valueOf(number));
                    break;
                case 7:
                    tvTXID8.setText(String.valueOf(number));
                    break;
            }
            index++;
        }
    }

    private void FillSigningCode(String signCode) {
        int index = 0;
        for(char number : signCode.toCharArray()) {
            switch (index) {
                case 0:
                    tvSIGN1.setText(String.valueOf(number));
                    break;
                case 1:
                    tvSIGN2.setText(String.valueOf(number));
                    break;
                case 2:
                    tvSIGN3.setText(String.valueOf(number));
                    break;
                case 3:
                    tvSIGN4.setText(String.valueOf(number));
                    break;
                case 4:
                    tvSIGN5.setText(String.valueOf(number));
                    break;
                case 5:
                    tvSIGN6.setText(String.valueOf(number));
                    break;
                case 6:
                    tvSIGN7.setText(String.valueOf(number));
                    break;
                case 7:
                    tvSIGN8.setText(String.valueOf(number));
                    break;
            }
            index++;
        }
    }
}
