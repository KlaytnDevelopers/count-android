package xyz.groundx.android.count;

import com.klaytn.caver.tx.gas.DefaultGasProvider;
import com.klaytn.caver.utils.ChainId;

import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.math.BigInteger;

public class Constants {
    public static final String EN_URL = "https://api.baobab.klaytn.net:8651";
    public static final int CHAIN_ID = ChainId.BAOBAB_TESTNET;
    public static final DefaultGasProvider GAS_PROVIDER = new DefaultGasProvider();
    public static final BigInteger GAS_LIMIT = GAS_PROVIDER.getGasLimit();
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(25000000000L);
    public static final DefaultBlockParameter BLOCK_PARAM = DefaultBlockParameterName.LATEST;
    public static final String APP_NAME = "BAPP_COUNT";
    public static final String PRIVATE_KEY = "private_key";
    public static final String SCOPE_BASE_URL = "https://baobab.scope.klaytn.com";
    // For demonstration purpose, we use contract pre-deployed at the following address
    public static final String CONTRACT_ADDRES = "0xb4bF60383C64D47F2E667f2fE8F7ED0c9380f770";
    // FYI, the contract we use for this app looks like this:
    /*
        pragma solidity ^0.5.6;

        contract Count {
          uint public count = 0;
          address public lastParticipant;

          function plus() public {
            count++;
            lastParticipant = msg.sender;
          }

          function minus() public {
            count--;
            lastParticipant = msg.sender;
          }
        }
     */
}
