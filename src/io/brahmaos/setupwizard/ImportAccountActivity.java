package io.brahmaos.setupwizard;

import io.brahmaos.setupwizard.view.CustomProgressDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.setupwizardlib.GlifLayout;

public class ImportAccountActivity  extends BaseActivity {
    @Override
    protected String tag() {
        return "ImportAccount";
    }

    private Spinner spinner;
    private CheckBox checkBoxReadProtocol;
    private TextView tvService;
    private TextView tvPrivacyPolicy;
    private Button btnNext;
//    private TextView tvPrevious;
    private CustomProgressDialog customProgressDialog;
    private EditText etMnemonic;
    private EditText etAccountName;
    private EditText etPassword;
    private EditText etRepeatPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_account);

        GlifLayout gl = (GlifLayout) findViewById(R.id.setup_wizard_layout);
        gl.setHeaderText(R.string.action_import_account);
        gl.setIcon(getDrawable(R.drawable.account));

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mnemonic_path, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        checkBoxReadProtocol = (CheckBox) findViewById(R.id.checkbox_read_protocol);
        checkBoxReadProtocol.setOnCheckedChangeListener((buttonView, isChecked) -> btnNext.setEnabled(isChecked));

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

        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(view->importMnemonicAccount());

//        tvPrevious = (TextView) findViewById(R.id.btn_skip);
//        tvPrevious.setText(R.string.action_previous);
//        tvPrevious.setOnClickListener(view->startCreateAccountActivity());

        etMnemonic = (EditText) findViewById(R.id.et_mnemonic);
        etAccountName = (EditText) findViewById(R.id.et_account_name);
        etPassword = (EditText) findViewById(R.id.et_password);
        etRepeatPassword = (EditText) findViewById(R.id.et_repeat_password);
    }

    private void importMnemonicAccount() {
        btnNext.setEnabled(false);
        // Reset errors.
        etAccountName.setError(null);
        etPassword.setError(null);
        etRepeatPassword.setError(null);

        // Store values at the time of the create account.
        String mnemonics = etMnemonic.getText().toString().trim();
        String name = etAccountName.getText().toString().trim();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mnemonics.
        if (TextUtils.isEmpty(mnemonics)) {
            focusView = etMnemonic;
            Toast.makeText(ImportAccountActivity.this, R.string.error_field_required, Toast.LENGTH_LONG).show();
            cancel = true;
        }

        if (!cancel && !((WizardApplication)getApplication()).isValidMnemonics(mnemonics)) {
            focusView = etMnemonic;
            Toast.makeText(ImportAccountActivity.this, R.string.error_mnemonics, Toast.LENGTH_LONG).show();
            cancel = true;
        }

        // Check for a valid account name.
        if (!cancel && TextUtils.isEmpty(name)) {
            etAccountName.setError(getString(R.string.error_field_required));
            focusView = etAccountName;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!cancel && (TextUtils.isEmpty(password)
                || !((WizardApplication)getApplication()).isPasswordValid(password))) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
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

        customProgressDialog = new CustomProgressDialog(ImportAccountActivity.this,
                R.style.CustomProgressDialogStyle,
                getString(R.string.progress_import_account));
        customProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();

        // check the private key valid
        boolean result = ((WizardApplication) getApplication()).createBrahmaAccountByMnemonics(mnemonics, password, name);
        customProgressDialog.cancel();
        if (result) {
            showLongToast(R.string.success_import_account);
            startFingerprintActivity();
        } else {
            showLongToast(R.string.error_import_account);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
