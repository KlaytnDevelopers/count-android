package xyz.groundx.android.count;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klaytn.caver.crypto.KlayCredentials;
import com.klaytn.caver.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class AccountInfoFragment extends Fragment {
    private static final String TAG = AccountInfoFragment.class.getSimpleName();

    private TextView mBalanceText;
    private TextView mAddressText;

    private Handler handler = new Handler();
    private Timer timer;
    private KlayCredentials mUserCredential;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        mBalanceText = rootView.findViewById(R.id.text_balance);
        mAddressText = rootView.findViewById(R.id.text_address);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref =
                getActivity().getSharedPreferences(Constants.APP_NAME, MODE_PRIVATE);
        String privateKey = pref.getString(Constants.PRIVATE_KEY, null);

        assert privateKey != null;

        mUserCredential = KlayCredentials.create(privateKey);
        mAddressText.setText(mUserCredential.getAddress());

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> new BalanceCheckTask().execute());
            }
        }, 0, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    class BalanceCheckTask extends AsyncTask<Void, Void, BigInteger> {

        @Override
        protected BigInteger doInBackground(Void... params) {
            try {
                return CaverFactory.get().klay().getBalance(
                        mUserCredential.getAddress(),
                        Constants.BLOCK_PARAM
                ).send().getValue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BigInteger balance) {
            super.onPostExecute(balance);
            if (balance != null) {
                BigDecimal balanceInKLAY = Convert.fromPeb(balance.toString(), Convert.Unit.KLAY);

                if (balanceInKLAY.compareTo(BigDecimal.ZERO) != 0) {
                    balanceInKLAY = balanceInKLAY.setScale(8, RoundingMode.DOWN);
                }

                mBalanceText.setText(String.format("%s KLAY", balanceInKLAY.toString()));
            } else {
                Log.e(TAG, "Failed to retrieve user balance of address: " + mUserCredential.getAddress());
            }
        }
    }
}
