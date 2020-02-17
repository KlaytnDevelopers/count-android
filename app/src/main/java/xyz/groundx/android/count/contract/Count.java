package xyz.groundx.android.count.contract;

import com.klaytn.caver.Caver;
import com.klaytn.caver.crypto.KlayCredentials;
import com.klaytn.caver.methods.response.KlayTransactionReceipt;
import com.klaytn.caver.tx.SmartContract;
import com.klaytn.caver.tx.manager.TransactionManager;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated smart contract code.
 * <p><strong>Do not modify!</strong>
 */
public class Count extends SmartContract {
    private static final String BINARY = "60806040526000805534801561001457600080fd5b506101d7806100246000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c806306661abd1461005157806314434fa51461006f57806318b0c3fd14610079578063bfe7e4e314610083575b600080fd5b6100596100cd565b6040518082815260200191505060405180910390f35b6100776100d3565b005b610081610128565b005b61008b61017c565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b60005481565b60008081548092919060019003919050555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600080815480929190600101919050555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168156fea265627a7a723158204bbac611f823fc9b9279fdaa461c50b62d0e82a15c8f54b0a1473f8fc09b09e964736f6c634300050d0032";

    public static final String FUNC_COUNT = "count";

    public static final String FUNC_LASTPARTICIPANT = "lastParticipant";

    public static final String FUNC_MINUS = "minus";

    public static final String FUNC_PLUS = "plus";

    protected Count(String contractAddress, Caver caver, KlayCredentials credentials, int chainId, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, caver, credentials, chainId, contractGasProvider);
    }

    protected Count(String contractAddress, Caver caver, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, caver, transactionManager, contractGasProvider);
    }

    public RemoteCall<BigInteger> count() {
        final Function function = new Function(FUNC_COUNT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> lastParticipant() {
        final Function function = new Function(FUNC_LASTPARTICIPANT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<KlayTransactionReceipt.TransactionReceipt> minus() {
        final Function function = new Function(
                FUNC_MINUS,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<KlayTransactionReceipt.TransactionReceipt> plus() {
        final Function function = new Function(
                FUNC_PLUS,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Count load(String contractAddress, Caver caver, KlayCredentials credentials, int chainId, ContractGasProvider contractGasProvider) {
        return new Count(contractAddress, caver, credentials, chainId, contractGasProvider);
    }

    public static Count load(String contractAddress, Caver caver, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Count(contractAddress, caver, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Count> deploy(Caver caver, KlayCredentials credentials, int chainId, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Count.class, caver, credentials, chainId, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Count> deploy(Caver caver, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Count.class, caver, transactionManager, contractGasProvider, BINARY, "");
    }
}