package xyz.groundx.android.count;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Application entry point allowing user to select which key to start with.
 */
public class MainActivity extends AppCompatActivity {

    private EditText mUserPrivateKeyField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserPrivateKeyField = findViewById(R.id.user_private_key_field);
    }

    /**
     * Creates a new activity populating Count BApp UI.
     *
     * @param key a private key to start Count BApp
     */
    private void start(String key) {
        SharedPreferences pref =
                getApplicationContext().getSharedPreferences(Constants.APP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.PRIVATE_KEY, key);
        editor.apply();

        startActivity(new Intent(MainActivity.this, CountActivity.class));
    }

    public void start(View view) {
        Editable submitted = mUserPrivateKeyField.getText();
        // TODO validate userPrivateKey; display error message if invalid
        if (submitted == null || submitted.toString().isEmpty()) {
            Toast.makeText(this, "Please enter a valid private key", Toast.LENGTH_SHORT).show();
            return;
        }
        start(submitted.toString());
    }
}
