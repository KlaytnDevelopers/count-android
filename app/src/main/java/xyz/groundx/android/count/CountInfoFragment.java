package xyz.groundx.android.count;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klaytn.caver.crypto.KlayCredentials;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import xyz.groundx.android.count.contract.Count;

import static android.content.Context.MODE_PRIVATE;
import static xyz.groundx.android.count.Constants.APP_NAME;
import static xyz.groundx.android.count.Constants.CHAIN_ID;
import static xyz.groundx.android.count.Constants.CONTRACT_ADDRES;
import static xyz.groundx.android.count.Constants.GAS_PROVIDER;
import static xyz.groundx.android.count.Constants.PRIVATE_KEY;

public class CountInfoFragment extends Fragment {

    private TextView mCountText;

    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private Count mContract;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_count, container, false);
        mCountText = rootView.findViewById(R.id.text_count);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref =
                getActivity().getSharedPreferences(APP_NAME, MODE_PRIVATE);
        String privateKey = pref.getString(PRIVATE_KEY, null);

        assert privateKey != null;

        mContract = Count.load(
                CONTRACT_ADDRES,
                CaverFactory.get(),
                KlayCredentials.create(privateKey),
                CHAIN_ID,
                GAS_PROVIDER
        );

        // Creates and configures a Timer object for periodic, repeated task execution
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> new CountCheckTask().execute());
            }
        }, 0, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    // A simple task implementation fetching data from an endpoint.
    class CountCheckTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                return mContract.count().send().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                mCountText.setText(s);
            }
        }
    }
}
