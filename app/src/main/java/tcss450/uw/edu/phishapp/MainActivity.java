package tcss450.uw.edu.phishapp;

import android.content.Intent;
import android.net.Credentials;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements FirstFragment.OnLoginFragmentInteractionListener,
                                                                    SecondFragment.OnRegisterFragmentInteractionListener {
    private boolean mLoadFromChatNotification = false;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("type")) {
                Log.d(TAG, "type of message: " + getIntent().getExtras().getString("type"));
                mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("contacrt");
            } else {
                Log.d(TAG, "NO MESSAGE");
            }
        }
        if(savedInstanceState == null) {
            if (findViewById(R.id.frame_main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_main_container, new FirstFragment())
                        .commit();
            }
        }
    }



    @Override
    public void onLoginSuccess(tcss450.uw.edu.phishapp.model.Credentials c) {
        startHomeActivity();

    }

    @Override
    public void onRegisterClicked() {
        SecondFragment secondFragment;
        secondFragment = new SecondFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, secondFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onRegisterSuccess(tcss450.uw.edu.phishapp.model.Credentials c) {
        /*
        EditText editText = (EditText) findViewById(R.id.email_text2);
        EditText pass = findViewById(R.id.password_text2);
        EditText passConfirm = findViewById(R.id.password_text3);
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Your username is empty!");
        } else if (pass.getText().toString().isEmpty()) {
            pass.setError("Your password is empty!");
        } else if (passConfirm.getText().toString().isEmpty()) {
            passConfirm.setError("You haven't entered your password confirmation!");
        } else if (pass.length() < 6) {
            pass.setError("Your password can't be less than 6 characters!");
        } else if (pass.getText().toString().compareToIgnoreCase(passConfirm.getText().toString()) != 0) {
            passConfirm.setError("Your password does not match!");
        } else {

        }*/
        startHomeActivity();

    }



    private void startHomeActivity() {
        EditText email = findViewById(R.id.email_text);
        if (email == null) {
            email = findViewById(R.id.email_text2);
        }
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(getString(R.string.info_key), email.getText().toString());
        intent.putExtra(getString(R.string.keys_intent_notifification_msg), mLoadFromChatNotification);
        startActivity(intent);

        //Ends this Activity and removes it from the Activity back stack.
        finish();

    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_main_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }
}
