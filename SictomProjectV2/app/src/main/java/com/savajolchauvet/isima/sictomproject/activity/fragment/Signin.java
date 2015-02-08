package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.savajolchauvet.isima.sictomproject.R;


public class Signin extends Fragment {
    private EditText mId;
    private EditText mPassword;

    public Signin() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        Button button = (Button) view.findViewById(R.id.buttonConnexion);
        mId = (EditText) view.findViewById(R.id.idEditText);
        mPassword = (EditText) view.findViewById(R.id.passwordEditText);
        button.setOnClickListener(new ButtonListener());

        return view;
    }

    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Id : " + mId.getText() + " password : " + mPassword.getText(), Toast.LENGTH_LONG).show();
        }
    }
}
