package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.speedy_baton_840.sictomApi.model.TUtilisateur;
import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.endpoint.TUtilisateurEndpointAsyncTask;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;


public class Signin extends Fragment {
    private EditText mId;
    private EditText mPassword;
    private TextView mErrorMsg;
    private int id ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        Button button = (Button) view.findViewById(R.id.buttonConnexion);
        mId = (EditText) view.findViewById(R.id.idEditText);
        mPassword = (EditText) view.findViewById(R.id.passwordEditText);
        mErrorMsg = (TextView) view.findViewById(R.id.loginOrPwdMsg);
        button.setOnClickListener(new ButtonListener());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String id = mId.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if(id.length() > 0 || password.length() > 0){
                String idParam = id + ConstanteMetier.SEPARATOR + password;
                try {
                    TUtilisateur user = new TUtilisateurEndpointAsyncTask().execute(new Pair<Context, String>(v.getContext(), idParam)).get();
                    if(user != null){
                        mErrorMsg.setVisibility(View.INVISIBLE);

                        Settings settingsFragment = new Settings();
                        Bundle args = new Bundle();
                        String params = user.getId() + ConstanteMetier.SEPARATOR +
                                        user.getNom() + ConstanteMetier.SEPARATOR +
                                        user.getPrenom() + ConstanteMetier.SEPARATOR +
                                        user.getLogin() + ConstanteMetier.SEPARATOR +
                                        user.getMdp();
                        args.putString(ConstanteMetier.UTILISATEUR_PARAM, params);
                        settingsFragment.setArguments(args);

                        getFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                    }
                    else{
                        mId.setText("");
                        mPassword.setText("");
                        mErrorMsg.setVisibility(View.VISIBLE);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            else{
                mId.setText("");
                mPassword.setText("");
                mErrorMsg.setVisibility(View.VISIBLE);
            }
        }
    }
}
