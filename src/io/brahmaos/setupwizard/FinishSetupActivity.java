package io.brahmaos.setupwizard;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;

import com.android.setupwizardlib.GlifLayout;

public class FinishSetupActivity extends BaseActivity {
    @Override
    protected String tag() {
        return "FinishSetupActivity";
    }
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_finish_setup);

        GlifLayout gl = (GlifLayout) findViewById(R.id.setup_wizard_layout);
        gl.setHeaderText(R.string.finish_waiting);
        gl.setIcon(getDrawable(R.drawable.connecting));
        gl.setProgressBarShown(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                setupWizardComplete();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK);
                    }
                }, 3000);
            }
        }).start();
    }
}
