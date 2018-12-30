package tcss450.uw.edu.phishapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.phishapp.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FirstFragment extends Fragment implements View.OnClickListener {
    private Credentials mCredentials;
    private OnLoginFragmentInteractionListener mListener;
    private String mFirebaseToken;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);

        Button b = (Button) v.findViewById(R.id.register_button);
        b.setOnClickListener(this);

        b = (Button) v.findViewById(R.id.login_button);
        b.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.email_text);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.password_text);
            passwordEdit.setText(password);
            getFirebaseToken(email, password);
        }
    }


    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
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

    @Override
    public void onClick(View v) {
        EditText emailEdit = getActivity().findViewById(R.id.email_text);
        EditText passwordEdit = getActivity().findViewById(R.id.password_text);

        if (mListener != null) {
            switch (v.getId()) {
                case R.id.register_button:
                    mListener.onRegisterClicked();
                    break;
                case R.id.login_button:
                    getFirebaseToken(emailEdit.getText().toString(), passwordEdit.getText().toString());
                    //attemptLogin();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
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
    private void handleLoginOnPre() {
       // mListener.onWaitFragmentInteractionShow();
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {

            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

         //   mListener.onWaitFragmentInteractionHide();
            Log.w("Logging: ", mCredentials.getEmail() +"EMAIL "+ mCredentials.getUsername() + " Between " + mCredentials.getPassword());
            if (success) {
                saveCredentials(mCredentials);
                //Login was successful. Inform the Activity so it can do its thing.
                //getFirebaseToken(mCredentials.getEmail(), mCredentials.getPassword());
                mListener.onLoginSuccess(mCredentials);
            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.email_text))
                        .setError("Login Unsuccessful");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.email_text))
                    .setError("Login Unsuccessful");
        }
    }
    private void getFirebaseToken(final String email, final String password) {
        mListener.onWaitFragmentInteractionShow();

        //add this app on this device to listen for the topic all
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        //the call to getInstanceId happens asynchronously. task is an onCompleteListener
        //similar to a promise in JS.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM: ", "getInstanceId failed", task.getException());
                        mListener.onWaitFragmentInteractionHide();
                        return;
                    }

                    // Get new Instance ID token
                    mFirebaseToken = task.getResult().getToken();

                    Log.d("FCM: ", mFirebaseToken);
                    //the helper method that initiates login service
                    attemptLogin();
                    //doLogin(email, password);
                });
        //no code here. wait for the Task to complete.
    }
    private void attemptLogin() {

        EditText emailEdit = getActivity().findViewById(R.id.email_text);
        EditText passwordEdit = getActivity().findViewById(R.id.password_text);

        boolean hasError = false;
        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        }  else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address.");
        }
        if (passwordEdit.getText().length() == 0) {
            hasError = true;
            passwordEdit.setError("Field must not be empty.");
        }

        if (!hasError) {
            Credentials credentials = new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_login))
                    .appendPath(getString(R.string.ep_with_token))
                    .build();

            Log.w("URL for Login", uri.toString());

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();
            try {
                msg.put("token", mFirebaseToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            //Feel free to add a handler for onPreExecution so that a progress bar
            //is displayed or maybe disable buttons.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleLoginOnPre)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }


    private void doLogin(String email, String password) {
        Credentials credentials = new Credentials.Builder(
                email,
                password)
                .build();
        mCredentials = credentials;
        mListener.onLoginSuccess(mCredentials);
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
    public interface OnLoginFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        // TODO: Update argument type and name
        void onLoginSuccess(Credentials c);
        void onRegisterClicked();

    }

}
