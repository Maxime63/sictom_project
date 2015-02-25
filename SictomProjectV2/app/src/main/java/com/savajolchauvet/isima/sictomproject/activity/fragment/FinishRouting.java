package com.savajolchauvet.isima.sictomproject.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.savajolchauvet.isima.sictomproject.R;
import com.savajolchauvet.isima.sictomproject.constante.ConstanteMetier;
import com.savajolchauvet.isima.sictomproject.endpoint.UpdateTourneeEndpointAsyncTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinishRouting extends Fragment {

    private EditText mChargeCamion;
    private Button mFinishButton;

    private long mTourneeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if(args != null){
            mTourneeId = args.getLong(ConstanteMetier.TOURNEE_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finish_routing, container, false);

        mFinishButton = (Button) view.findViewById(R.id.finishButton);
        mChargeCamion = (EditText) view.findViewById(R.id.chargeEditText);

        mFinishButton.setOnClickListener(new ButtonListner());

        return view;
    }

    private class ButtonListner implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            DateFormat df = new SimpleDateFormat(ConstanteMetier.STRING_DATE_FORMAT);

            double chargeCamion = Double.valueOf(mChargeCamion.getText().toString());
            Date date = new Date(System.currentTimeMillis());

            //Update tournée !
            String params = mTourneeId + ConstanteMetier.SEPARATOR +
                            chargeCamion + ConstanteMetier.SEPARATOR +
                            df.format(date);
            new UpdateTourneeEndpointAsyncTask().execute(new Pair<Context, String>(getActivity(), params));

            getFragmentManager().beginTransaction().replace(R.id.content_frame, new Signin()).commit();
        }
    }
}
