package com.example.task23ya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FourthFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        view.findViewById(R.id.btn_to_first).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FirstFragment()).commit());

        view.findViewById(R.id.btn_to_second).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new SecondFragment()).commit());

        view.findViewById(R.id.btn_to_third).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ThirdFragment()).commit());

        return view;
    }
}