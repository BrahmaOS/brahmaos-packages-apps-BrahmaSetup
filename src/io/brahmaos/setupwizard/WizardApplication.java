package io.brahmaos.setupwizard;

import android.app.Application;
import android.app.StatusBarManager;
import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;

import io.brahmaos.setupwizard.util.AES128;
import io.brahmaos.setupwizard.util.SHAEncrypt;
import io.brahmaos.setupwizard.util.BLog;
import io.brahmaos.setupwizard.util.BrahmaConst;

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
    private static final String TAG = "Application";
    private StringBuilder mMnemonicStr;
    private StatusBarManager mStatusBarManager;
    private UserManager mUserManager;
    private int mUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WizardApplication--onCreate");
        mStatusBarManager = (StatusBarManager) getSystemService(Context.STATUS_BAR_SERVICE);
        mStatusBarManager.disable(StatusBarManager.DISABLE_EXPAND
                | StatusBarManager.DISABLE_HOME
                | StatusBarManager.DISABLE_SEARCH
                | StatusBarManager.DISABLE_RECENT);//StatusBarManager.DISABLE_BACK

        mUserManager = (UserManager) getSystemService(USER_SERVICE);

        mUserId = UserHandle.myUserId();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mStatusBarManager != null) {
            mStatusBarManager.disable(StatusBarManager.DISABLE_NONE);
        }
    }

    /** For create new account.
     * Generate mnemonics and create brahma account via mnemonics for Brahma OS
     */
    public boolean createMnemonicsAndBrahmaAccount(String name, String password) {
        boolean result = true;

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

            result = createBrahmaAccountByMnemonics(mMnemonicStr.toString().trim(), password, name);
        } else {
            return false;
        }
        return result;
    }

    /** For import account.
     * create brahma account via mnemonics for Brahma OS
     */
    public boolean createBrahmaAccountByMnemonics(String mnemonics, String password, String name) {
        boolean result = true;
        try {
            mUserManager.setUserName(mUserId, name);

            //save wallet address
            result = handleSaveWalletAddressForPath(mnemonics, name, BrahmaConst.ETH_PATH);
            if (!result) {
                return false;
            }
            //result = handleSaveWalletAddressForPath(mnemonics, name, BrahmaConst.BRM_PATH);
            //if (!result) {
            //    return false;
            //}

            //save encrypted mnemonics
            String mnemonicHex = AES128.encrypt(mnemonics, password);
            if (null == mnemonicHex) {
                return false;
            }
            mUserManager.setUserDefaultMnemonicHex(mUserId, mnemonicHex);

            //save brahmaos account
            String brahmaAccount = SHAEncrypt.shaEncrypt(mnemonicHex, "SHA-256");
            mUserManager.setUserBrahmaAccount(mUserId, brahmaAccount);

            //save data encrypt key pair
            ECKeyPair defaultKeyPair = getECKeyPairForPath(mnemonics, BrahmaConst.DEFAULT_PATH);
            if (null == defaultKeyPair) {
                return false;
            }
            BigInteger privateKey = defaultKeyPair.getPrivateKey();
            BigInteger publicKey = defaultKeyPair.getPublicKey();
            mUserManager.setUserDefaultPublicKey(mUserId, publicKey.toString());

            //encrypt the private key before saving it
            String privateEncrypt = AES128.encrypt(privateKey.toString(), password);
            if (null == privateEncrypt) {
                return false;
            }
            mUserManager.setUserDefaultPrivateKeyHex(mUserId, privateEncrypt);
        }catch (Exception e) {
            e.printStackTrace();
            BLog.d(TAG, e.toString());
        }

        return result;
    }

    private ECKeyPair getECKeyPairForPath(String mnemonics, String path) {
        try {
            //produce private key by mnemonic
            long timeSeconds = System.currentTimeMillis() / 1000;
            DeterministicSeed seed = new DeterministicSeed(mnemonics.trim(), null, "", timeSeconds);
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            List<ChildNumber> keyPath = HDUtils.parsePath(path);
            DeterministicKey key = chain.getKeyByPath(keyPath, true);
            BigInteger privateKey = key.getPrivKey();

            // get wallet address by private key
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKey);
            return ecKeyPair;
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
            BLog.d(TAG, "getECKeyPairForPath fail for " + path + ": " + e.toString());
        }
        return null;
    }

    private boolean handleSaveWalletAddressForPath(String mnemonics, String password, String path) {
        boolean result = true;
        try {
            //produce private key by mnemonic
            ECKeyPair ecKeyPair = getECKeyPairForPath(mnemonics, path);
            if (ecKeyPair != null) {
                WalletFile walletFile = Wallet.createLight(password, ecKeyPair);
                String walletAddr = walletFile.getAddress();
                if (null == walletAddr) {
                    result = false;
                }

                mUserManager.setUserDefaultWalletAddr(mUserId, walletAddr, path);
                return true;
            }
        } catch (CipherException e) {
            BLog.d(TAG, e.toString());
        }
        return result;
    }

    public void disableNavigation(int flag) {
        if (mStatusBarManager != null) {
            mStatusBarManager.disable(flag);
        }
    }

    public String getMnemonicString() {
        if (mMnemonicStr == null) {
            return null;
        }
        return mMnemonicStr.toString();
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
}
