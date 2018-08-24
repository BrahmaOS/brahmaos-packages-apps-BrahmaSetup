package io.brahmaos.setupwizard;

import android.app.ProgressDialog;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import io.brahmaos.setupwizard.view.CustomProgressDialog;

public class CreateAccountActivity extends BaseActivity {
    @Override
    protected String tag() {
        return "CreateAccount";
    }

    private EditText etAccountName;
    private EditText etPassword;
    private EditText etRepeatPassword;
    private CheckBox checkBoxReadProtocol;
    private TextView tvService;
    private TextView tvPrivacyPolicy;
    private TextView tvImportAccount;
    private Button btnNext;
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_account_create);
        etAccountName = findViewById(R.id.et_account_name);
        etPassword = findViewById(R.id.et_password);
        etRepeatPassword = findViewById(R.id.et_repeat_password);
        checkBoxReadProtocol = findViewById(R.id.checkbox_read_protocol);
        tvImportAccount = findViewById(R.id.btn_import_account);
        btnNext = findViewById(R.id.btn_create_account);

        btnNext.setOnClickListener(view->createAccount());
        checkBoxReadProtocol.setOnCheckedChangeListener((buttonView, isChecked) -> btnNext.setEnabled(isChecked));

        tvImportAccount.setOnClickListener(view->startImportAccountActivity());

        tvService = (TextView) findViewById(R.id.service_tv);
        tvService.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiceTermsActivity.class);
            startActivity(intent);
        });

        tvPrivacyPolicy = (TextView) findViewById(R.id.privacy_policy_tv);
        tvPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid name, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void createAccount() {
        btnNext.setEnabled(false);
        // Reset errors.
        etAccountName.setError(null);
        etPassword.setError(null);
        etRepeatPassword.setError(null);

        // Store values at the time of the create account.
        String name = etAccountName.getText().toString();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid account name.
        if (TextUtils.isEmpty(name)) {
            etAccountName.setError(getString(R.string.error_field_required));
            focusView = etAccountName;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!cancel && (TextUtils.isEmpty(password) || !((WizardApplication)getApplication()).isPasswordValid(password))) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (!cancel && !password.equals(repeatPassword)) {
            etPassword.setError(getString(R.string.error_incorrect_password));
            focusView = etPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            btnNext.setEnabled(true);
            return;
        }
        customProgressDialog = new CustomProgressDialog(this, R.style.CustomProgressDialogStyle, getString(R.string.progress_create_account));
        customProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();

        boolean result = ((WizardApplication)getApplication()).createMnemonicsAndBrahmaAccount(name, password);
        customProgressDialog.cancel();
        if (result) {
            showLongToast(R.string.success_create_account);
            startMnemonicBackupActivity();
        } else {
            showLongToast(R.string.error_create_account);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
