package com.example.autoslider.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.autoslider.R;
import com.example.autoslider.ScreenSizeUtilityClass;

public class PhoneFragment extends Fragment {
    TextView otpbtn;
    EditText phoneno;
    ImageView backbtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ScreenSizeUtilityClass.fullWhiteScreen(getActivity());
        View view = inflater.inflate(R.layout.phone_number_layout,container,false);
        backbtn = view.findViewById(R.id.phone_layout_backbtn);

        otpbtn = view.findViewById(R.id.phone_number_layout_otpbtn);
        phoneno = view.findViewById(R.id.phone_number_layout_number);

        otpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("phone",phoneno.getText().toString());
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                OtpFragment otpFragment= new OtpFragment();
                otpFragment.setArguments(bundle);
                transaction.replace(R.id.landing_container,otpFragment);
                transaction.commit();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.landing_container,new LoginFragment());
                transaction.commit();
            }
        });

        return view;
    }
}
