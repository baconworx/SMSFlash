package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baconworx.smsflash.R;

public class FlashDisplay extends Activity {
    private RelativeLayout backgroundLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_display);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Bundle extras = getIntent().getExtras();
        final TextView captionTextView = (TextView) findViewById(R.id.captionTextView);
        final TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
        backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);

        if (extras != null) {
            captionTextView.setText((String) extras.get("caption"));
            contentTextView.setText((String) extras.get("text"));

            backgroundLayout.setBackgroundColor(extras
                    .getInt("backgroundColor"));
            backgroundLayout.setAlpha(1);
            backgroundLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    new Fader(0, 500).start();
                }
            });

            new Fader(extras.getInt("timeout"), 100).start();
        }
    }

    private class Fader extends CountDownTimer {
        private long millis;
        private long wait;

        public Fader(long wait, long millis) {
            super(millis + wait, 25);
            this.millis = millis;
            this.wait = millis;
        }

        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished < wait) {
                backgroundLayout.setAlpha(millisUntilFinished / (float) millis);
            }
        }

        public void onFinish() {
            FlashDisplay.this.finish();
        }
    }
}
