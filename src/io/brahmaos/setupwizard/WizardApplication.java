package io.brahmaos.setupwizard;

import android.app.Application;
import android.app.StatusBarManager;
import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;

import io.brahmaos.setupwizard.util.AES128;
import io.brahmaos.setupwizard.util.SHAEncrypt;
import io.brahmaos.setupwizard.util.BLog;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Splitter;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Numeric;

public class WizardApplication extends Application {
    private static final String TAG = "SETUP_Application";
    private StringBuilder mMnemonicStr;
    private StatusBarManager mStatusBarManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WizardApplication--onCreate");
        mStatusBarManager = (StatusBarManager) getSystemService(Context.STATUS_BAR_SERVICE);
        mStatusBarManager.disable(StatusBarManager.DISABLE_EXPAND
                | StatusBarManager.DISABLE_HOME
                | StatusBarManager.DISABLE_SEARCH
                | StatusBarManager.DISABLE_RECENT);//StatusBarManager.DISABLE_BACK
    }

    public void disableNavigation(int flag) {
        if (mStatusBarManager != null) {
            mStatusBarManager.disable(flag);
        }
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mStatusBarManager != null) {
            mStatusBarManager.disable(StatusBarManager.DISABLE_NONE);
        }
    }

    public String getMnemonicString() {
        if (mMnemonicStr == null) {
            return null;
        }
        return mMnemonicStr.toString();
    }

    /**
     * Generate ethereum account via mnemonics
     */
    public boolean createEthereumAccWithMnemonic(String name, String password) {
        boolean result = true;

        try {
            String passphrase = "";
            SecureRandom secureRandom = new SecureRandom();
            long creationTimeSeconds = System.currentTimeMillis() / 1000;
            DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
            List<String> mnemonicCode = deterministicSeed.getMnemonicCode();

            if (mnemonicCode != null && mnemonicCode.size() > 0) {
                mMnemonicStr = new StringBuilder();
                for (String mnemonic : mnemonicCode) {
                    mMnemonicStr.append(mnemonic).append(" ");
                }
                //create ethereum private key according to mnemonicCode
                long timeSeconds = System.currentTimeMillis() / 1000;
                DeterministicSeed seed = new DeterministicSeed(mMnemonicStr.toString().trim(), null, "", timeSeconds);
                DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
                List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
                DeterministicKey key = chain.getKeyByPath(keyPath, true);
                BigInteger privateKey = key.getPrivKey();
                // get wallet address by private key
                ECKeyPair ecKeyPair = ECKeyPair.create(privateKey);
                WalletFile walletFile = Wallet.createLight(password, ecKeyPair);

                String walletAddr = walletFile.getAddress();
                String mnemonicHex = AES128.encrypt(mMnemonicStr.toString(), password);
                String brahmaAccount = SHAEncrypt.shaEncrypt(mnemonicHex, "SHA-256");
                final UserManager um = (UserManager) getSystemService(USER_SERVICE);

                //save data into UserData
                int userId = UserHandle.myUserId();
                um.setUserName(userId, name);
                um.setUserBrahmaAccount(userId, brahmaAccount);
                um.setUserDefaultMnemonicHex(userId, mnemonicHex);
                um.setUserDefaultWalletAddr(userId, walletAddr);
            }
        } catch (CipherException | UnreadableWalletException e) {
            result = false;
            e.printStackTrace();
            BLog.d(TAG, e.toString());
        }
        return result;
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public static boolean isValidMnemonics(String mnemonics) {
        try {
            List<String> mnemonicsCodes = Splitter.on(" ").splitToList(mnemonics);
            MnemonicCode.INSTANCE.check(mnemonicsCodes);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean importAccountWithMnemonics(String mnemonics, String password, String name) {
        boolean result = true;

        try {
            //produce private key by mnemonic
            long timeSeconds = System.currentTimeMillis() / 1000;
            DeterministicSeed seed = new DeterministicSeed(mnemonics.trim(), null, "", timeSeconds);
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
            DeterministicKey key = chain.getKeyByPath(keyPath, true);
            BigInteger privateKey = key.getPrivKey();

            // get wallet address by private key
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKey);
            WalletFile walletFile = Wallet.createLight(password, ecKeyPair);

            String walletAddr = walletFile.getAddress();
            String mnemonicHex = AES128.encrypt(mnemonics, password);
            String brahmaAccount = SHAEncrypt.shaEncrypt(mnemonicHex, "SHA-256");
            final UserManager um = (UserManager) getSystemService(USER_SERVICE);

            //save data into UserData
            int userId = UserHandle.myUserId();
            um.setUserName(userId, name);
            um.setUserBrahmaAccount(userId, brahmaAccount);
            um.setUserDefaultMnemonicHex(userId, mnemonicHex);
            um.setUserDefaultWalletAddr(userId, walletAddr);
        } catch (CipherException | UnreadableWalletException e) {
            result = false;
            e.printStackTrace();
            BLog.d(TAG, e.toString());
        }
        return result;
    }
}
