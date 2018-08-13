package io.brahmaos.setupwizard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MnemonicConfirmActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected String tag() {
        return "MnemonicConfirm";
    }

    private static final int LINE_BUTTON_SIZE = 4;
    private TextView tvMnemonicTotal;
    private String[] mMnemonicArray;
    private String mMnemonicString;
    private Button btnNext;
    private TextView tvPrevious;
    private LinearLayout parentLinearLayout;
    private int inputCount = 0;
    private ArrayList<LinearLayout> mHorizLL = new ArrayList<LinearLayout>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic_confirm);
        tvMnemonicTotal = (TextView) findViewById(R.id.tv_mnemonic_total);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(view->setupWizardComplete());
        tvPrevious = (TextView) findViewById(R.id.btn_previous);
        tvPrevious.setOnClickListener(view->startMnemonicBackupActivity());
        initView();
    }

    private void initView() {
        parentLinearLayout = (LinearLayout) findViewById(R.id.layout_mnemonic_total);
        mMnemonicString = ((WizardApplication)getApplication()).getMnemonicString();
        mMnemonicArray = null == mMnemonicString ? null : mMnemonicString.split(" ");
        int size = null == mMnemonicArray ? 0 : mMnemonicArray.length;
        randomSort(mMnemonicArray, size);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int)getResources().getDimension(R.dimen.space_normal);
        layoutParams.setMargins(margin, 0, margin, 30);
        LinearLayout horizLL = null;
        for (int i = 0; i < size; i++) {
            if (i % LINE_BUTTON_SIZE == 0) {
                horizLL = new LinearLayout(this);
                horizLL.setOrientation(LinearLayout.HORIZONTAL);
                horizLL.setLayoutParams(layoutParams);
                horizLL.setPadding(0,0,0,0);
                mHorizLL.add(horizLL);
            }
            String item = mMnemonicArray[i];
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemParams.setMarginStart(20);
            Button child = (Button) LayoutInflater.from(this).inflate(R.layout.mnemonic_button, null);
            child.setText(item);
//            child.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_note_content));
            child.setTag(i);
            child.setLayoutParams(itemParams);
            child.setOnClickListener(this);
            if (horizLL != null) {
                horizLL.addView(child);
            }
            if (i % LINE_BUTTON_SIZE == 0) {
                parentLinearLayout.addView(horizLL);
            }
        }
    }

    private void randomSort(String[] array, int n) {
        int index;
        String temp;
        Random random = new Random();
        for (int i = n -1; i > 0; i--) {
            index = random.nextInt(i);
            temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }

    }

    @Override
    public void onClick(View v) {
        int index = (int)v.getTag();
        if (mHorizLL != null && mHorizLL.size() > 0) {
            mHorizLL.get(index / LINE_BUTTON_SIZE).removeView(v);
            parentLinearLayout.requestLayout();
            parentLinearLayout.invalidate();
        }
        if (parentLinearLayout != null) {
            parentLinearLayout.removeView(v);
        }
        tvMnemonicTotal.setText(tvMnemonicTotal.getText() + mMnemonicArray[index] + " ");
        inputCount++;

        if (inputCount == mMnemonicArray.length) {
            String finalMnemonic = tvMnemonicTotal.getText().toString();
            if (finalMnemonic.equals(mMnemonicString)) {
                btnNext.setEnabled(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
