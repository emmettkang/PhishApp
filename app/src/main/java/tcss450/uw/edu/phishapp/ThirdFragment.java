package tcss450.uw.edu.phishapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class ThirdFragment extends Fragment {

    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);

    }

    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String email = getArguments().getString(getString(R.string.info_key));
            TextView emailText = getActivity().findViewById(R.id.textView7);
            emailText.setText(email);
        }
    }



}
