package com.example.sanida.togoapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sanida.togoapp.R;


public class ChatFragment extends Fragment {

    private TextView noReqTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        noReqTxt = view.findViewById(R.id.noreq3);

        return view;
    }


}
