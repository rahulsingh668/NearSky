package com.nearsky.tv;

import android.service.dreams.DreamService;
import android.content.Intent;
import android.widget.FrameLayout;

public final class FlightDreamService extends DreamService {
    private FlightView display;
    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(true); setFullscreen(true); setScreenBright(false);
        FrameLayout root=new FrameLayout(this);
        display = new FlightView(this);root.addView(display,new FrameLayout.LayoutParams(-1,-1));
        SettingsButton button=SettingsButton.attach(root,()->{
            startActivity(new Intent(this,MainActivity.class).putExtra("open_settings",true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));finish();
        });
        setContentView(root);button.requestFocus();
    }
    @Override public void onDreamingStarted() { super.onDreamingStarted(); if (display != null) display.start(false); }
    @Override public void onDreamingStopped() { if (display != null) display.stop(); super.onDreamingStopped(); }
    @Override public void onDetachedFromWindow() { if (display != null) { display.stop(); display = null; } super.onDetachedFromWindow(); }
}
