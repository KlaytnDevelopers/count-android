package xyz.groundx.android.count;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.klaytn.caver.crypto.KlayCredentials;
import com.klaytn.caver.methods.response.KlayTransactionReceipt.TransactionReceipt;
import com.klaytn.caver.utils.Convert;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.FragmentActivity;
import java8.util.concurrent.CompletableFuture;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.groundx.android.count.contract.Count;
import xyz.groundx.android.count.retrofit.PayerResponse;
import xyz.groundx.android.count.retrofit.PayerService;
import xyz.groundx.android.count.task.CompletableFutureResolverTask;
import xyz.groundx.android.count.task.PayerTask;
import xyz.groundx.android.count.task.WithProgressView;

import static xyz.groundx.android.count.Constants.APP_NAME;
import static xyz.groundx.android.count.Constants.CHAIN_ID;
import static xyz.groundx.android.count.Constants.CONTRACT_ADDRES;
import static xyz.groundx.android.count.Constants.PRIVATE_KEY;
import static xyz.groundx.android.count.Constants.SCOPE_BASE_URL;

/**
 * Main activity to play with.
 */
public class CountActivity extends FragmentActivity implements WithProgressView {

    private static final String TAG = CountActivity.class.getSimpleName();

    private KlayCredentials mUserCredential;
    private Count mContract;

    private AppCompatCheckBox mPayerCheckbox;
    private EditText mPayerURL;
    private View mProgress;

    private String mCurrentServiceURL;
    private PayerService mPayerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        mPayerCheckbox = findViewById(R.id.checkbox_payer);
        mPayerURL = findViewById(R.id.edittext_payer_url);
        mProgress = findViewById(R.id.view_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getSharedPreferences(APP_NAME, MODE_PRIVATE);
        String privateKey = pref.getString(PRIVATE_KEY, null);

        assert privateKey != null;

        mUserCredential = KlayCredentials.create(privateKey);

        mContract = Count.load(
                CONTRACT_ADDRES,
                CaverFactory.get(),
                mUserCredential,
                CHAIN_ID,
                new DefaultGasProvider()
        );
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Event handler for TransactionReceipt object.
     *
     * @param receipt a nullable TransactionReceipt object which contains confirmed tx information
     */
    private void onTransactionReceipt(TransactionReceipt receipt) {
        String msg;

        if (receipt == null) {
            msg = "Something went wrong";
        } else {
            // BigDecimal is handy when dealing with large integers
            BigDecimal gasUsed = Utils.hexToBigDecimal(receipt.getGasUsed());
            BigDecimal gasPrice = Utils.hexToBigDecimal(receipt.getGasPrice());

            // Convert the total amount of gas spent in KLAY using Convert.fromPeb
            BigDecimal gasSpent = Convert.fromPeb(gasUsed.multiply(gasPrice), Convert.Unit.KLAY);

            msg = gasSpent.toString() + " KLAY spent";
        }

        // Prepare Snackbar
        Snackbar snackbar = Snackbar.make(mProgress, msg, Snackbar.LENGTH_LONG);

        // Provide a Klaytnscope link for the (confirmed) transaction information
        if (receipt != null) {
            snackbar.setActionTextColor(getColor(R.color.white))
                    .setAction("Open in Scope", view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(getScopeUri(receipt.getTransactionHash()));
                        startActivity(intent);
                    });
        }

        // Show message
        snackbar.show();
    }

    /**
     * Create a Klaytnscope Uri for the provided transaction hash string.
     *
     * @param txHash a transaction hash
     * @return Klaytnscope Uri object containing a link to the corresponding tx information
     */
    private Uri getScopeUri(String txHash) {
        return Uri.parse(SCOPE_BASE_URL + "/tx/" + txHash);
    }

    /**
     * Event handler for PayerResponse object.
     *
     * @param resp a PayerResponse object (nullable)
     */
    private void onPayerResponse(PayerResponse resp) {
        String msg;

        if (resp == null || resp.getError() != null) {
            msg = resp == null ? "Failed to communicate with payer"
                    : "Something went wrong: " + resp.getError();
        } else {
            msg = "Accepted; your TX hash is " + resp.getTxhash();
            Log.d(TAG, resp.getTxhash());
        }

        // Show message
        Snackbar.make(mProgress, msg, Snackbar.LENGTH_LONG).show();

        // TODO start polling the result
    }

    /**
     * A helper method creating/updating PayerService object depending on the url submitted.
     *
     * @param url an endpoint URL compatible with PayerService interface
     * @return a PayerService object
     */
    private PayerService getPayerService(String url) {
        if (mPayerService != null && url.equals(mCurrentServiceURL)) return mPayerService;

        mCurrentServiceURL = url;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mCurrentServiceURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mPayerService = retrofit.create(PayerService.class);
        return mPayerService;
    }

    /**
     * Event handler for add operation.
     *
     * @param view (not used)
     */
    public void onAdd(View view) {
        if (mPayerCheckbox.isChecked()) {
            runWithPayer(new Function(
                    Count.FUNC_PLUS,
                    Collections.emptyList(),
                    Collections.emptyList()
            ));
        } else {
            run(mContract.plus().sendAsync());
        }
    }

    /**
     * Event handler for subtract operation.
     *
     * @param view (not used)
     */
    public void onSubtract(View view) {
        if (mPayerCheckbox.isChecked()) {
            runWithPayer(new Function(
                    Count.FUNC_MINUS,
                    Collections.emptyList(),
                    Collections.emptyList()
            ));
        } else {
            run(mContract.minus().sendAsync());
        }
    }

    /**
     * Creates and executes a CompletableFutureResolverTask for the submitted future.
     *
     * @param future a CompletableFuture object to run
     */
    private void run(CompletableFuture<TransactionReceipt> future) {
        //noinspection unchecked
        new CompletableFutureResolverTask<>(
                this,
                this::onTransactionReceipt
        ).execute(future);
    }

    /**
     * Creates and executes a PayerTask for the user requested function using PayerService.
     *
     * @param function a user requested operation object
     */
    private void runWithPayer(Function function) {
        new PayerTask(
                this,
                mUserCredential,
                this::onPayerResponse
        ).execute(
                getPayerService(mPayerURL.getText().toString()),
                Numeric.hexStringToByteArray(FunctionEncoder.encode(function)),
                CONTRACT_ADDRES
        );
    }
}
