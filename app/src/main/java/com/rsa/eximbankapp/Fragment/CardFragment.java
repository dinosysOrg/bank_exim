package com.rsa.eximbankapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rsa.eximbankapp.Activity.TransactionActivity;
import com.rsa.eximbankapp.Adapter.CardAdapter;
import com.rsa.eximbankapp.R;


public class CardFragment extends Fragment {

    private CardView cardView;
    private FrameLayout layoutBackground;
    private TextView textTitle;
    private TextView textAmount;

    private RelativeLayout btnTransfer;
    private RelativeLayout btnTopup;
    private RelativeLayout btnBill;

    public static Fragment getInstance(int position) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_card, container, false);

        cardView = (CardView) view.findViewById(R.id.card_view);
        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        layoutBackground = (FrameLayout) view.findViewById(R.id.layout_background);
        textTitle = (TextView) view.findViewById(R.id.text_title);
        textAmount = (TextView) view.findViewById(R.id.text_amount);

        btnTransfer = (RelativeLayout) view.findViewById(R.id.btn_transfer);
        btnTopup = (RelativeLayout) view.findViewById(R.id.btn_topup);
        btnBill = (RelativeLayout) view.findViewById(R.id.btn_bill);

        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        if (position == 0) {
            layoutBackground.setBackgroundResource(R.drawable.background_card_1);
            textTitle.setText("TK TIẾT KIỆM | 101940294942");
            textAmount.setText("550.350.000");

            btnTransfer.setBackgroundResource(R.drawable.round_button_1);
            btnTopup.setBackgroundResource(R.drawable.round_button_1);
            btnBill.setBackgroundResource(R.drawable.round_button_1);
        } else if (position == 1) {
            layoutBackground.setBackgroundResource(R.drawable.background_card_2);
            textTitle.setText("TK THANH TOÁN | 10293808324");
            textAmount.setText("299.050.000");

            btnTransfer.setBackgroundResource(R.drawable.round_button_2);
            btnTopup.setBackgroundResource(R.drawable.round_button_2);
            btnBill.setBackgroundResource(R.drawable.round_button_2);
        } else {
            layoutBackground.setBackgroundResource(R.drawable.background_card_3);
            textTitle.setText("TK TÍN DỤNG | 103929301039");
            textAmount.setText("65.000.000");

            btnTransfer.setBackgroundResource(R.drawable.round_button_3);
            btnTopup.setBackgroundResource(R.drawable.round_button_3);
            btnBill.setBackgroundResource(R.drawable.round_button_3);
        }

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TransactionActivity.class);
                startActivity(myIntent);
            }
        });

        return view;
    }

    public CardView getCardView() {
        return cardView;
    }
}
