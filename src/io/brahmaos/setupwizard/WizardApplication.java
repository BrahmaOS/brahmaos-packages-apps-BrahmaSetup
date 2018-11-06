package io.brahmaos.setupwizard;

import android.app.Application;
import android.app.StatusBarManager;
import brahmaos.app.WalletManager;
import android.content.Context;
import brahmaos.content.WalletData;
import android.os.UserHandle;
import android.os.UserManager;

import brahmaos.content.BrahmaConstants;
import io.brahmaos.setupwizard.util.NetworkMonitor;
import io.brahmaos.setupwizard.util.PhoneMonitor;
import io.brahmaos.setupwizard.util.SHAEncrypt;
import io.brahmaos.setupwizard.util.BLog;
import io.brahmaos.setupwizard.util.BrahmaConst;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.support.annotation.Nullable;
import brahmaos.util.DataCryptoUtils;
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

import static brahmaos.content.BrahmaContext.WALLET_SERVICE;

public class WizardApplication extends Application {
    private static final String TAG = "Application";
    private StringBuilder mMnemonicStr;
    private StatusBarManager mStatusBarManager;
    private UserManager mUserManager;
    private int mUserId;
    public static int mCurIndex = 0;

    private String mBrahmaAccount;
    private String mMnemonicCryptoHex;
    private String mPublicKeyHex;
    private String mPrivateCryptoHex;
    private HashMap<String, String> mWalletMap = new HashMap<String, String>();//<path, walletAddr>
    private WalletManager mWalletManager;

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
        mWalletManager = (WalletManager) getSystemService(WALLET_SERVICE);

        mUserId = UserHandle.myUserId();

        NetworkMonitor.initInstance(this);
        PhoneMonitor.initInstance(this);
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
            //clean the default wallets already exist
            List<WalletData> allWallets = mWalletManager.getAllWallets();
            if (allWallets != null) {
                for (WalletData wallet : allWallets) {
                    if (wallet != null && wallet.isDefault) {
                        mWalletManager.deleteWalletByAddress(wallet.address);
                    }
                }
            }

            //save user name
            mUserManager.setUserName(mUserId, name);

            //create ETH wallet and save wallet data to WalletManager
            WalletData ethWallet = mWalletManager.createDefaultETHWallet(name, mnemonics, password);

            if (null == ethWallet) {
                return false;
            }
            //map wallet address
            mWalletMap.put(ethWallet.keyPath, ethWallet.address);

            //generate encrypted mnemonics
            mMnemonicCryptoHex = DataCryptoUtils.aes128Encrypt(mnemonics, password);
            if (null == mMnemonicCryptoHex) {
                return false;
            }

            //generate brahmaos account
            mBrahmaAccount = SHAEncrypt.shaEncrypt(mMnemonicCryptoHex, "SHA-256");

            //generate key pair by Brahma OS chain path
            ECKeyPair defaultKeyPair = getECKeyPairForPath(mnemonics, BrahmaConstants.BIP_BRM_OS_PATH);
            if (null == defaultKeyPair) {
                return false;
            }
            BigInteger privateKey = defaultKeyPair.getPrivateKey();
            BigInteger publicKey = defaultKeyPair.getPublicKey();
            //generate public key
            mPublicKeyHex = publicKey.toString(16);
            //generate encrypted private key
            mPrivateCryptoHex = DataCryptoUtils.aes128Encrypt(privateKey.toString(16), password);
            if (null == mPrivateCryptoHex) {
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
            BLog.d(TAG, e.toString());
        }

        return result;
    }

    /** This must be called only once
     *  in the last of successfully create or import brahma os account **/
    public void saveBrahmaData() {
        mUserManager.setUserBrahmaAccount(mUserId, mBrahmaAccount);

        mUserManager.setUserDefaultMnemonicHex(mUserId, mMnemonicCryptoHex);

        Iterator<HashMap.Entry<String, String>> iterator = mWalletMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            mUserManager.setUserDefaultWalletAddr(mUserId, entry.getValue(), entry.getKey());
        }

        mUserManager.setUserDefaultPublicKey(mUserId, mPublicKeyHex);

        mUserManager.setUserDefaultPrivateKeyHex(mUserId, mPrivateCryptoHex);
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

    private boolean getWalletAddressForPath(String mnemonics, String password, String path) {
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

                mWalletMap.put(path, walletAddr);
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
