package com.rsa.eximbankapp.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.rsa.eximbankapp.R;

public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private Spinner spinnerLanguage;
    private ImageButton btnEnter;

    private EditText etUsername;
    private EditText etPassword;

    private Dialog dialog;

    public static final int PERMISSIONS_REQUEST = 101;

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

        setContentView(R.layout.activity_login);
        Reprint.initialize(this);

        InitView();
        InitListener();
        SetupView();
        PermissionRequest();

//        Intent myIntent = new Intent(this, TransactionActivity.class);
//        startActivity(myIntent);
//        finish();
    }

    private void InitView() {
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
        btnEnter = (ImageButton) findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(this);

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    btnEnter.setImageResource(R.drawable.ic_finger);
                } else {
                    btnEnter.setImageResource(R.drawable.ic_enter);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        dialog = new Dialog(this);
    }

    private void InitListener() {
        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnEnter.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void SetupView() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.language_arrays, R.layout.spinner_item);
        spinnerLanguage.setAdapter(adapter);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        etUsername.setText(username);
    }

    private void PermissionRequest() {
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.USE_FINGERPRINT
        };
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enter:

                if (etUsername.getText().toString().equals("user") && etPassword.getText().toString().equals(""))  {
                    // Finger check
                    DisplayBiometricPrompt();
                } else if (etUsername.getText().toString().equals("user") && etPassword.getText().toString().equals("1234")) {
                    OpenHomePage();
                } else if (etUsername.getText().toString().equals("")){
                    ShowPopup("Lỗi xảy ra", "Vui lòng điền tên đăng nhập");
                } else {
                    ShowPopup("Lỗi xảy ra", "Mật khẩu hoặc tên đăng nhập không đúng");
                }

                break;
        }
    }

    private void OpenHomePage() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", etUsername.getText().toString());
        editor.apply();

        Intent myIntent = new Intent(this, HomeActivity.class);
        startActivity(myIntent);

        finish();
    }

    public void ShowPopup(String title, String message) {
        dialog.setContentView(R.layout.popup_message);
        TextView tvTitle =(TextView) dialog.findViewById(R.id.tv_title);
        TextView tvMessage =(TextView) dialog.findViewById(R.id.tv_message);

        tvTitle.setText(title);
        tvMessage.setText(message);

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void DisplayBiometricPrompt() {
        dialog.setContentView(R.layout.popup_finger);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Reprint.cancelAuthentication();
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        Reprint.authenticate(new AuthenticationListener() {
            public void onSuccess(int moduleTag) {
                OpenHomePage();
                dialog.dismiss();
            }

            public void onFailure(AuthenticationFailureReason failureReason, boolean fatal,
                                  CharSequence errorMessage, int moduleTag, int errorCode) {

            }
        });
    }
}
