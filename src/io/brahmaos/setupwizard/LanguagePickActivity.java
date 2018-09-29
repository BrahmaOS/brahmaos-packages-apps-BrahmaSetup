package io.brahmaos.setupwizard;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.internal.app.LocalePicker;
import com.android.setupwizardlib.GlifLayout;

import java.util.Locale;

import io.brahmaos.setupwizard.util.BLog;

public class LanguagePickActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroupPicker;
    private Button mNextBtn;
    private static final String[] ITEMSTR = new String[]{"English", "简体中文"};
    private static final String[] TAGS = new String[]{"en", "zh"};

    @Override
    protected String tag() {
        return "LanguagePickActivity";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_pick);
        GlifLayout gl = findViewById(R.id.setup_wizard_layout);
        gl.setHeaderText(R.string.title_language_pick);
        gl.setIcon(getDrawable(R.drawable.language));
        radioGroupPicker = (RadioGroup) findViewById(R.id.radiogroup);
        for (int i = 0; i < ITEMSTR.length; i++) {
            RadioButton tempButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.language_radiobutton, null);
            LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tempButton.setText(ITEMSTR[i]);
            tempButton.setLayoutParams(tempParams);
            tempButton.setTag(i);
            radioGroupPicker.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (WizardApplication.mCurIndex == i) {tempButton.setChecked(true);}
        }
        radioGroupPicker.setOnCheckedChangeListener(this);

        mNextBtn = (Button) findViewById(R.id.btn_next);
        mNextBtn.setOnClickListener(view->startSIMCardActivity());
//        mNextBtn.setOnClickListener(view->startFingerprintActivity());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton selected = (RadioButton) group.findViewById(checkedId);
        if (selected == null || !selected.isPressed()) {
            return;
        }
        WizardApplication.mCurIndex = (int) selected.getTag();
        if (WizardApplication.mCurIndex >= ITEMSTR.length && WizardApplication.mCurIndex < 0) {
            WizardApplication.mCurIndex = 0;
        }
        LocalePicker.updateLocale(Locale.forLanguageTag(TAGS[WizardApplication.mCurIndex]));
        try {
            IActivityManager am = ActivityManager.getService();
            Configuration config = am.getConfiguration();
            config.setLocale(Locale.forLanguageTag(TAGS[WizardApplication.mCurIndex]));
            am.updateConfiguration(config);
        } catch (Exception e) {
            BLog.d(tag(), "" + e.toString());
        }
    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(CURINDEX, mCurIndex);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
