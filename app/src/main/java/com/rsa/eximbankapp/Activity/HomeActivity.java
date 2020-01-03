package com.rsa.eximbankapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.rsa.eximbankapp.Adapter.CardFragmentPagerAdapter;
import com.rsa.eximbankapp.Adapter.TransactionAdapter;
import com.rsa.eximbankapp.Model.TransactionModel;
import com.rsa.eximbankapp.R;
import com.rsa.eximbankapp.Setting.ImportTokenActivity;
import com.rsa.eximbankapp.View.ShadowTransformer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity {

    final static int IMPORT_TOKEN_REQUEST = 1;

    ViewPager viewPager;
    RecyclerView listTransaction;

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

        setContentView(R.layout.activity_home);

        InitToken();
        InitView();
        SetupView();
    }

    private void InitView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        listTransaction = (RecyclerView) findViewById(R.id.list_transaction);

        //Setup Viewpager
        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, this));
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                pageChangeListener.onPageSelected(viewPager.getCurrentItem());
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(viewPager, true);

        findViewById(R.id.btn_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent myIntent = new Intent(view.getContext(), SecurIDSampleActivity.class);
//                startActivity(myIntent);
            }
        });

        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iImportToken = new Intent(view.getContext(), ImportTokenActivity.class);
                startActivityForResult(iImportToken, IMPORT_TOKEN_REQUEST);
            }
        });
    }

    private void SetupView() {

    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) { }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        @Override
        public void onPageSelected(int position) {
            final List<TransactionModel> transactions = GenerateTransactions(position);

            TransactionAdapter transactionAdapter = new TransactionAdapter(getBaseContext(), R.layout.item_transaction, transactions, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = listTransaction.getChildLayoutPosition(view);

                    Intent intent = new Intent(view.getContext(), TransactionDetailActivity.class);
                    intent.putExtra("TransactionModel", transactions.get(position));
                    intent.putExtra("isShowSaveContact", false);
                    startActivity(intent);
                }
            });
            listTransaction.setHasFixedSize(true);
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
            listTransaction.setLayoutManager(mLayoutManager);
            listTransaction.setAdapter(transactionAdapter);
        }
    };

    private List<TransactionModel> GenerateTransactions(int position) {
        List<TransactionModel> transactions = new ArrayList<TransactionModel>();
        switch (position) {
            case 0:
                transactions.add(new TransactionModel(false, "Nạp tiền vào tài khoản", "Nguyen Quy Truong", R.drawable.ic_coin, new Date(1554394750*1000), -50000000, "Eximbank", "101940294942"));
                transactions.add(new TransactionModel(false, "Nạp tiền vào tài khoản", "Nguyen Quy Truong", R.drawable.ic_coin, new Date(1554120310*1000), -20000000, "Eximbank", "101940294942"));
                transactions.add(new TransactionModel(true, "Tất toán cuối kì", "Nguyen Quy Truong", R.drawable.ic_coin, new Date(1552425300*1000), 75000000, "Eximbank", "101940294942"));
                transactions.add(new TransactionModel(false, "Nạp tiền vào tài khoản", "Nguyen Quy Truong", R.drawable.ic_coin, new Date(1552250715*1000), -10000000, "Eximbank", "101940294942"));
                break;
            case 1:
                transactions.add(new TransactionModel(true,"Nạp tiền điện thoại", "Nguyen Quy Truong", R.drawable.ic_phone, new Date(1554567738*1000), -100000,"Eximbank", "10293808324"));
                transactions.add(new TransactionModel(false, "Nhận tiền từ Ái nhân", "Ái nhân", R.drawable.avatar_4, new Date(1554194430*1000), 5000000, "Vietcombank", "10383838951"));
                transactions.add(new TransactionModel(true,"Chuyển tiền cho Hương Anh", "Hương Anh", R.drawable.avatar_3, new Date(1553732579*1000), -7500000, "ACB", "1029389384"));
                transactions.add(new TransactionModel(false, "Nhận tiền từ Nguyên Vũ", "Nguyên Vũ", R.drawable.avatar_1, new Date(1553509840*1000), 1000000, "Dong A Bank", "10393838328"));
                break;
            case 2:
                transactions.add(new TransactionModel(false, "Nạp tiền vào tài khoản", "Nguyen Quy Truong", R.drawable.ic_coin, new Date(1554568116*1000), 8000000, "Eximbank", "103929301039"));
                transactions.add(new TransactionModel(false, "Nhận tiền từ Mai Trinh", "Mai Trinh", R.drawable.avatar_2, new Date(1553423440*1000), 2000000, "TP Bank", "10584939393"));
                transactions.add(new TransactionModel(true, "Nạp tiền điện thoại", "Nguyen Quy Truong", R.drawable.ic_phone, new Date(1553199640*1000), -200000,"Eximbank", "103929301039"));
                transactions.add(new TransactionModel(false, "Nhận tiền từ Hương Anh", "Hương Anh", R.drawable.avatar_3, new Date(1552233020*1000), 5500000,"ACB", "1029389384"));
                break;
        }
        return transactions;
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


    /**
     * Import Token
     */

    protected void InitToken() {
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == IMPORT_TOKEN_REQUEST && data != null) {

        }
    }
}
