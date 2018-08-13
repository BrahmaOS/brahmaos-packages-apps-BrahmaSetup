package io.brahmaos.setupwizard;

import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract String tag();

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
        finish();
    }

    public void startCreateAccountActivity() {
        startActivity(new Intent(this, CreateAccountActivity.class));
        finish();
    }

    public void startMnemonicBackupActivity() {
        startActivity(new Intent(this, MnemonicBackupActivity.class));
        finish();
    }

    public void startMnemonicConfirmActivity() {
        startActivity(new Intent(this, MnemonicConfirmActivity.class));
        finish();
    }

    public void setupWizardComplete() {
        try {
            // Add a persistent setting to allow other apps to know the device has been provisioned.
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);


            ((WizardApplication)getApplication()).disableNavigation(StatusBarManager.DISABLE_NONE);

            // remove this activity from the package manager.
            PackageManager pm = getPackageManager();
            ComponentName name = new ComponentName(BaseActivity.this, "io.brahmaos.setupwizard.CreateAccountActivity");
            pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            finish();
        }catch (Exception e) {
            io.brahmaos.setupwizard.util.BLog.d("SETUP_BaseActivity", "fail: " + e.toString());
        }
    }
}
