package io.brahmaos.setupwizard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.setupwizardlib.GlifLayout;

public class SIMCardActivity extends BaseActivity {
    private Button tvSkip;
    private TextView tvInsertSIM;

    @Override
    protected String tag() {
        return "SIMCardActivity";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_network);
        GlifLayout gl = (GlifLayout) findViewById(R.id.setup_wizard_layout);
        gl.setHeaderText(R.string.sim_connect);
        gl.setIcon(getDrawable(R.drawable.simcard));
        tvInsertSIM = (TextView) findViewById(R.id.insert_sim);
        tvSkip = (Button) findViewById(R.id.btn_skip);
        tvSkip.setOnClickListener(view->startWifiConnectActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SIM_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            if (Intent.ACTION_SIM_STATE_CHANGED.equals(intent.getAction())) {
                TelephonyManager teleManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (teleManager.getSimState() == TelephonyManager.SIM_STATE_NOT_READY) {
                    tvInsertSIM.setText(getString(R.string.sim_connecting));
                }
                if (teleManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                    startWifiConnectActivity();
                }
            }
        }
    };
}
