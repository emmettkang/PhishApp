package tcss450.uw.edu.phishapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.phishapp.model.Credentials;


public class SecondFragment extends Fragment implements View.OnClickListener {
    private OnRegisterFragmentInteractionListener mListener;
    private tcss450.uw.edu.phishapp.model.Credentials mCredentials;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second, container, false);

        Button b = (Button) v.findViewById(R.id.register_button2);
        b.setOnClickListener(this);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.register_button2:
                    attemptRegister();
                    break;
            }
        }
    }
    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }
    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {

            Log.d("JSON result register", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            mListener.onWaitFragmentInteractionHide();
            if (success) {
                //Register was successful. Inform the Activity so it can do its thing.
                mListener.onRegisterSuccess(mCredentials);

            } else {
                //Register was unsuccessful. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.email_text2))
                        .setError("Register Unsuccessful");
                Log.w("Well shit", "IDK");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.email_text2))
                    .setError("Register Unsuccessful");
        }
    }

    private void attemptRegister() {
        EditText userName = getActivity().findViewById(R.id.uname_text);
        EditText firstName = getActivity().findViewById(R.id.fname_text);
        EditText lastName = getActivity().findViewById(R.id.lname_text);

        EditText emailEdit = getActivity().findViewById(R.id.email_text2);
        EditText passwordEdit = getActivity().findViewById(R.id.password_text2);
        EditText passConfirm = getActivity().findViewById(R.id.password_text3);

        boolean hasError = false;
        if (firstName.getText().toString().isEmpty()) {
            hasError = true;
            firstName.setError("Your first name is empty!");
        } else if (lastName.getText().toString().isEmpty()) {
            hasError = true;
            lastName.setError("Your last name is empty!");
        } else if (userName.getText().toString().isEmpty()) {
            hasError = true;
            userName.setError("Your username is empty!");
        } else if (emailEdit.getText().toString().isEmpty()) {
            hasError = true;
            emailEdit.setError("Your username is empty!");
        } else if (passwordEdit.getText().toString().isEmpty()) {
            hasError = true;
            passwordEdit.setError("Your password is empty!");
        } else if (passConfirm.getText().toString().isEmpty()) {
            hasError = true;
            passConfirm.setError("You haven't entered your password confirmation!");
        } else if (passwordEdit.length() < 6) {
            hasError = true;
            passwordEdit.setError("Your password can't be less than 6 characters!");
        } else if (passwordEdit.getText().toString().compareToIgnoreCase(passConfirm.getText().toString()) != 0) {
            hasError = true;
            passConfirm.setError("Your password does not match!");
        }

            if (!hasError) {
            tcss450.uw.edu.phishapp.model.Credentials credentials = new tcss450.uw.edu.phishapp.model.Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .addUsername(userName.getText().toString())
                    .addFirstName(firstName.getText().toString())
                    .addLastName(lastName.getText().toString())
                    .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();
            Log.w("URL for Register", uri.toString());
            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();

            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            //Feel free to add a handler for onPreExecution so that a progress bar
            //is displayed or maybe disable buttons.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegisterFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        // TODO: Update argument type and name
        void onRegisterSuccess(tcss450.uw.edu.phishapp.model.Credentials c);

    }



}