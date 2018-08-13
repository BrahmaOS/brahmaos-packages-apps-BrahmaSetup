package io.brahmaos.setupwizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

public class MnemonicBackupActivity extends BaseActivity {
    private TextView tvMnemonic;
    private Button btnNext;

    @Override
    protected String tag() {
        return "MnemonicBackup";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic_backup);
        tvMnemonic = (TextView) findViewById(R.id.mnemonic_str);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(view->startMnemonicConfirmActivity());
        String content = ((WizardApplication)getApplication()).getMnemonicString();
        tvMnemonic.setText(null == content ? "" : content.replace(" ", "    "));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
