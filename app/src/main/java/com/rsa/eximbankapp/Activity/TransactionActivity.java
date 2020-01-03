package com.rsa.eximbankapp.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.rsa.eximbankapp.Fragment.TransactionFirstFragment;
import com.rsa.eximbankapp.Fragment.TransactionFourFragment;
import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.R;

public class TransactionActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    TextView actionbarTitle;
    FragmentManager fragmentManager;
    Dialog dialog;

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

        setContentView(R.layout.activity_transaction);

        InitVariable();
        InitView();
        SetupView();
    }

    private void InitVariable() {
        fragmentManager = getSupportFragmentManager();
    }

    private void InitView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionbarTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        toolbar.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialog = new Dialog(this);
    }

    private void SetupView() {
        AddFragment(TransactionFirstFragment.getInstance());

//        TransactionModel transactionModel = new TransactionModel(true, "Nhận tiền từ Nguyên Vũ", "Nguyên Vũ", R.drawable.avatar_1, new Date(1553509840), 100000000, "Dong A Bank", "10393838328");
//        AddFragment(TransactionFourFragment.getInstance(transactionModel));
    }

    public void AddFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_transaction, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void OpenTransactionDetail(TransactionModel transactionModel) {
        Intent intent = new Intent(this, TransactionDetailActivity.class);
        intent.putExtra("TransactionModel", transactionModel);
        intent.putExtra("isShowSaveContact", true);
        startActivity(intent);

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

    public void ShowTransactionFailPopup() {
        final Dialog dialogFail = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogFail.setContentView(R.layout.popup_transaction_fail);
        dialogFail.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFail.dismiss();
                onBackPressed();
            }
        });
        dialogFail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFail.show();
    }

    public void ShowPasswordPopup(final TransactionFourFragment.OnAuthCallback callback) {
        dialog.setContentView(R.layout.popup_password);
        final EditText etPassword = (EditText) dialog.findViewById(R.id.et_password);

        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getText().toString().equals("1234")) {
                    dialog.dismiss();
                    callback.onSuccess();
                }else {
                    dialog.dismiss();
                    ShowPopup("Lỗi xảy ra", "Mật khẩu không đúng");
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setActionbarTitle(String title) {
        if (actionbarTitle != null) {
            actionbarTitle.setText(title);
        }
    }

    public void DisplayBiometricPrompt(final TransactionFourFragment.OnAuthCallback callback) {
        dialog.setContentView(R.layout.popup_finger);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView) dialog.findViewById(R.id.tv_message)).setText("Sử dụng vân tay để xác nhận");
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
                dialog.dismiss();
                callback.onSuccess();
            }

            public void onFailure(AuthenticationFailureReason failureReason, boolean fatal,
                                  CharSequence errorMessage, int moduleTag, int errorCode) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 2).onResume();
        } else finish();

        super.onBackPressed();
    }
}
