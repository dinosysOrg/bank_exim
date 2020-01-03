package com.rsa.eximbankapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.rsa.eximbankapp.Activity.TransactionActivity;
import com.rsa.eximbankapp.Adapter.ContactAdapter;
import com.rsa.eximbankapp.Model.ContactModel;
import com.rsa.eximbankapp.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionFirstFragment extends Fragment {

    RecyclerView listContact;
    EditText etSearch;
    CardView cvTransferAcc;

    List<ContactModel> contacts = new ArrayList<ContactModel>();
    ContactAdapter transactionAdapter;

    public static Fragment getInstance() {
        TransactionFirstFragment fragment = new TransactionFirstFragment();
        Bundle args = new Bundle();
        //args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_first, container, false);

        listContact = (RecyclerView) view.findViewById(R.id.list_contact);
        etSearch = (EditText) view.findViewById(R.id.et_search);
        cvTransferAcc = (CardView) view.findViewById(R.id.cv_transfer_acc);

        SearchData("");
        InitView();
        InitListener();
        return view;
    }

    private void SearchData(String searchStr) {
        contacts.clear();

        if ("NGUYEN THI AI NHAN".toLowerCase().contains(searchStr.toLowerCase()))
            contacts.add(new ContactModel("NGUYEN THI AI NHAN", R.drawable.avatar_4, "HSBC", "1029393393"));

        if ("NGUYEN VU AN".toLowerCase().contains(searchStr.toLowerCase()))
            contacts.add(new ContactModel("NGUYEN VU AN", R.drawable.avatar_1, "ACB", "483839439"));

        if ("HOANG HUONG ANH".toLowerCase().contains(searchStr.toLowerCase()))
            contacts.add(new ContactModel("HOANG HUONG ANH", R.drawable.avatar_3, "VCB", "2923838438"));

        if ("LUU NGUYEN MAI TRINH".toLowerCase().contains(searchStr.toLowerCase()))
            contacts.add(new ContactModel("LUU NGUYEN MAI TRINH", R.drawable.avatar_2, "Timo", "202834838"));
    }

    private void InitView() {
        transactionAdapter = new ContactAdapter(getContext(), R.layout.item_contact, contacts, new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = listContact.getChildLayoutPosition(view);

                ((TransactionActivity) getActivity()).AddFragment(TransactionSecondFragment.getInstance(contacts.get(position).name));
            }
        });
        listContact.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listContact.setLayoutManager(mLayoutManager);
        listContact.setAdapter(transactionAdapter);
    }

    private void InitListener() {
        etSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                SearchData(s.toString());
                transactionAdapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    etSearch.clearFocus();
                    return true;
                }
                return false;
            }
        });

        cvTransferAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TransactionActivity) getActivity()).AddFragment(TransactionSecondFragment.getInstance(""));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TransactionActivity) getActivity()).setActionbarTitle("Chuyển tiền");
    }
}
