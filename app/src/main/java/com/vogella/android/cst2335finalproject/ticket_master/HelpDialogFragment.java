package com.vogella.android.cst2335finalproject.ticket_master;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vogella.android.cst2335finalproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpDialogFragment extends DialogFragment {

    TextView textHelpInfo;
    Button buttonOk;

    public HelpDialogFragment() {
        // Required empty public constructor
    }


    public static HelpDialogFragment newInstance(String info) {
        HelpDialogFragment fragment = new HelpDialogFragment();
        Bundle args = new Bundle();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_dialog, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(R.string.dialog_title);

        textHelpInfo = view.findViewById(R.id.textHelpInfo);
        buttonOk = view.findViewById(R.id.buttonOk);

        textHelpInfo.setText(getArguments().getString("info", "Help!"));

        buttonOk.setOnClickListener(view1 -> dismiss());

    }

}