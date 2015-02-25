package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.appspot.speedy_baton_840.sictomApi.model.TCamion;
import com.appspot.speedy_baton_840.sictomApi.model.TTournee;
import com.appspot.speedy_baton_840.sictomApi.model.TUtilisateur;
import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.activity.MainActivity;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.endpoint.GetAllCamionsEndpointAsyncTask;
import com.savajolchauvet.isima.sictomproject.endpoint.GetAllTourneeEndpointAsyncTask;
import com.savajolchauvet.isima.sictomproject.endpoint.GetAllUserEndpointAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Settings extends Fragment {
    private List<TUtilisateur> mUsersList;
    private List<TCamion> mCamionsList;
    private List<Long> mTypeTourneeList;
    private TUtilisateur mUser;

    private TextView mChauffeur;
    private Spinner mFirstRipperSpinner;
    private Spinner mSecondRipperSpinner;
    private Spinner mCamionSpinner;
    private Spinner mTourneeSpinner;
    private Button mStartButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inflate the layout for this fragment
        String userParams = getArguments().getString(ConstanteMetier.UTILISATEUR_PARAM);
        String[] params = userParams.split(ConstanteMetier.SEPARATOR);
        mUser = new TUtilisateur();
        mUser.setId(Long.parseLong(params[0]));
        mUser.setNom(params[1]);
        mUser.setPrenom(params[2]);
        mUser.setLogin(params[3]);
        mUser.setMdp(params[4]);

        mChauffeur = (TextView) view.findViewById(R.id.chauffeurTextView);
        mChauffeur.setText(mUser.getNom() + " " + mUser.getPrenom());

        mFirstRipperSpinner = (Spinner) view.findViewById(R.id.spinnerRipper1);
        mSecondRipperSpinner = (Spinner) view.findViewById(R.id.spinnerRipper2);
        mCamionSpinner = (Spinner) view.findViewById(R.id.spinnerCamion);
        mTourneeSpinner = (Spinner) view.findViewById(R.id.spinnerTournee);

        try {
            mUsersList = new GetAllUserEndpointAsyncTask().execute(new Pair<Context, String>(view.getContext(), null)).get();
            mCamionsList = new GetAllCamionsEndpointAsyncTask().execute(new Pair<Context, String>(view.getContext(), null)).get();
            mTypeTourneeList = new ArrayList<>();

            setRippersList(view);
            setCamionList(view);
            setTourneeList(view);

            mStartButton = (Button) view.findViewById(R.id.buttonDemarrage);
            mStartButton.setOnClickListener(new ButtonListner());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    private class ButtonListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int ripper1 = mFirstRipperSpinner.getSelectedItemPosition();
            int ripper2 = mSecondRipperSpinner.getSelectedItemPosition();
            int camion = mCamionSpinner.getSelectedItemPosition();
            int typeTournee = mTourneeSpinner.getSelectedItemPosition();

//            Bundle args = new Bundle();
//            args.putLong(ConstanteMetier.CHAUFFEUR_ID_PARAM, mUser.getId());
//            args.putLong(ConstanteMetier.FIRST_RIPPER_ID_PARAM, mUsersList.get(ripper1).getId());
//            args.putLong(ConstanteMetier.SECOND_RIPPER_ID_PARAM, mUsersList.get(ripper2).getId());
//            args.putLong(ConstanteMetier.CAMION_ID_PARAM, mCamionsList.get(camion).getId());
//            args.putLong(ConstanteMetier.TOURNEE_ID_PARAM, mTypeTourneeList.get(typeTournee));

            if(getActivity() instanceof MainActivity){
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.startMaps(mUser, mUsersList.get(ripper1), mUsersList.get(ripper2), mCamionsList.get(camion), mTypeTourneeList.get(typeTournee));
            }
        }
    }

    private void setRippersList(View view) {
        List<String> listUsers = new ArrayList<>();

        for(TUtilisateur user : mUsersList){
            listUsers.add(user.getNom() + " " + user.getPrenom());
        }
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item, listUsers);

        mFirstRipperSpinner.setAdapter(usersAdapter);
        mSecondRipperSpinner.setAdapter(usersAdapter);
    }

    private void setCamionList(View view) {
        List<String> listCamions = new ArrayList<>();

        for(TCamion camion : mCamionsList){
            listCamions.add(camion.getNom());
        }
        ArrayAdapter<String> camionsAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item, listCamions);

        mCamionSpinner.setAdapter(camionsAdapter);
    }

    private void setTourneeList(View view) {
        List<String> typeTourneeList = new ArrayList<>();

        for(long i = 1; i <= 8; i++){
            mTypeTourneeList.add(i);
            typeTourneeList.add(String.valueOf(i));
        }

        ArrayAdapter<String> tourneesAdapters = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item, typeTourneeList);

        mTourneeSpinner.setAdapter(tourneesAdapters);
    }
}
