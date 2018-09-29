package io.brahmaos.setupwizard;

import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;
import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.GET_RECEIVERS;
import static android.content.pm.PackageManager.GET_SERVICES;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract String tag();

    private static final String ACTION_SETUP_FINGERPRINT = "android.settings.FINGERPRINT_SETUP";
    private static final String ACTION_SETUP_LOCKSCREEN = "com.android.settings.SETUP_LOCK_SCREEN";
    private static final String EXTRA_AUTO_FINISH = "wifi_auto_finish_on_connect";
    private static final String EXTRA_FIRST_RUN = "firstRun";
    private static final String EXTRA_ALLOW_SKIP = "allowSkip";
    private static final String EXTRA_USE_IMMERSIVE = "useImmersiveMode";
    private static final String EXTRA_THEME = "theme";
    private static final String EXTRA_MATERIAL_LIGHT = "material_light";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_DETAILS = "details";
    private static final int REQUEST_CODE_SETUP_FINGERPRINT = 1;
    private static final int REQUEST_CODE_SETUP_LOCKSCREEN = 2;
    private static final int REQUEST_CODE_SETUP_FINISH = 3;
    private static final int RESULT_SKIP = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showShortToast(int res) {
        showShortToast(getString(res));
    }

    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void showLongToast(int res) {
        showLongToast(getString(res));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startImportAccountActivity() {
        startActivity(new Intent(this, ImportAccountActivity.class));
//        finish();
    }

    public void startCreateAccountActivity() {
        startActivity(new Intent(this, CreateAccountActivity.class));
//        finish();
    }

    public void startMnemonicBackupActivity() {
        startActivity(new Intent(this, MnemonicBackupActivity.class));
//        finish();
    }

    public void startMnemonicConfirmActivity() {
        startActivity(new Intent(this, MnemonicConfirmActivity.class));
//        finish();
    }

    public void startSIMCardActivity() {
        startActivity(new Intent(this, SIMCardActivity.class));
//        finish();
    }

    public void startWifiConnectActivity() {
        Intent intent = new Intent("android.settings.WIZARD_WIFI_SETTINGS");
        startActivity(intent);
//        finish();
    }

    public void startFingerprintActivity() {
        Intent intent = new Intent(ACTION_SETUP_FINGERPRINT);
        intent.putExtra(EXTRA_FIRST_RUN, true);
        intent.putExtra(EXTRA_ALLOW_SKIP, true);
        intent.putExtra(EXTRA_USE_IMMERSIVE, true);
        intent.putExtra(EXTRA_THEME, EXTRA_MATERIAL_LIGHT);
        intent.putExtra(EXTRA_AUTO_FINISH, false);
        intent.putExtra(EXTRA_TITLE,
                getString(R.string.settings_fingerprint_setup_title));
        intent.putExtra(EXTRA_DETAILS,
                getString(R.string.settings_fingerprint_setup_details));
        startActivityForResult(intent, REQUEST_CODE_SETUP_FINGERPRINT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_SETUP_FINGERPRINT == requestCode) {
            if (RESULT_OK == resultCode || RESULT_SKIP == resultCode) {
//                setupWizardComplete();
//                disableComponentSets(this, GET_RECEIVERS | GET_SERVICES);
                startActivityForResult(new Intent(this, FinishSetupActivity.class), REQUEST_CODE_SETUP_FINISH);

                overridePendingTransition(R.anim.translucent_enter, R.anim.translucent_exit);
            } else {
                //start lock screen choose
                Intent intent = new Intent(ACTION_SETUP_LOCKSCREEN);
                intent.putExtra(EXTRA_TITLE,
                        getString(R.string.settings_lockscreen_setup_title));
                intent.putExtra(EXTRA_DETAILS,
                        getString(R.string.settings_lockscreen_setup_details));
                intent.putExtra(EXTRA_ALLOW_SKIP, true);
                startActivityForResult(intent, REQUEST_CODE_SETUP_LOCKSCREEN);
            }
        } else if (REQUEST_CODE_SETUP_LOCKSCREEN == requestCode) {
//            setupWizardComplete();
//            disableComponentSets(this, GET_RECEIVERS | GET_SERVICES);
            startActivityForResult(new Intent(this, FinishSetupActivity.class), REQUEST_CODE_SETUP_FINISH);

            overridePendingTransition(R.anim.translucent_enter, R.anim.translucent_exit);
        } else if (REQUEST_CODE_SETUP_FINISH == requestCode) {
            finish();
        }
    }

    public void setupWizardComplete() {
        disableComponentSets(this, GET_RECEIVERS | GET_SERVICES);
        ((WizardApplication)getApplication()).saveBrahmaData();
        try {
            // Add a persistent setting to allow other apps to know the device has been provisioned.
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);


            ((WizardApplication)getApplication()).disableNavigation(StatusBarManager.DISABLE_NONE);

            // remove this activity from the package manager.
//            disableComponentSets(this, GET_RECEIVERS | GET_SERVICES);
//            PackageManager pm = getPackageManager();
//            ComponentName name = new ComponentName(BaseActivity.this, "io.brahmaos.setupwizard.LanguagePickActivity");
//            pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//            finish();
        }catch (Exception e) {
            io.brahmaos.setupwizard.util.BLog.d("SETUP_BaseActivity", "fail: " + e.toString());
        }
    }

    private void disableComponentSets(Context context, int flags) {
        setComponentListEnabledState(context, getComponentSets(context, flags),
                COMPONENT_ENABLED_STATE_DISABLED);
    }
    private void setComponentListEnabledState(Context context,
                                              List<ComponentName> componentNames, int enabledState) {
        for (ComponentName componentName : componentNames) {
            setComponentEnabledState(context, componentName, enabledState);
        }
    }
    private void setComponentEnabledState(Context context, ComponentName componentName,
                                          int enabledState) {
        context.getPackageManager().setComponentEnabledSetting(componentName,
                enabledState, DONT_KILL_APP);
    }
    private List<ComponentName> getComponentSets(Context context, int flags) {
        int i = 0;
        List<ComponentName> componentNames = new ArrayList();
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.ImportAccountActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.MnemonicConfirmActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.MnemonicBackupActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.CreateAccountActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.PrivacyPolicyActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.LanguagePickActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.SIMCardActivity"));
        componentNames.add(new ComponentName(context, "io.brahmaos.setupwizard.ServiceTermsActivity"));
//        try {
//            PackageInfo allInfo = context.getPackageManager()
//                    .getPackageInfo(context.getPackageName(), flags);
//            if (allInfo != null) {
//                if (allInfo.activities != null && (flags & GET_ACTIVITIES) != 0) {
//                    for (ComponentInfo info : allInfo.activities) {
//                        componentNames.add(new ComponentName(context, info.name));
//                    }
//                }
//                if (allInfo.receivers != null && (flags & GET_RECEIVERS) != 0) {
//                    for (ComponentInfo info2 : allInfo.receivers) {
//                        componentNames.add(new ComponentName(context, info2.name));
//                    }
//                }
//                if (allInfo.services != null && (flags & GET_SERVICES) != 0) {
//                    ServiceInfo[] serviceInfoArr = allInfo.services;
//                    int length = serviceInfoArr.length;
//                    while (i < length) {
//                        componentNames.add(new ComponentName(context, serviceInfoArr[i].name));
//                        i++;
//                    }
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        }
        return componentNames;
    }
}
