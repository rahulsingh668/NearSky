package com.nearsky.tv;

import android.service.dreams.DreamService;

public final class FlightDreamService extends DreamService {
    private FlightView display;
    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false); setFullscreen(true); setScreenBright(false);
        display = new FlightView(this); setContentView(display);
    }
    @Override public void onDreamingStarted() { super.onDreamingStarted(); if (display != null) display.start(false); }
    @Override public void onDreamingStopped() { if (display != null) display.stop(); super.onDreamingStopped(); }
    @Override public void onDetachedFromWindow() { if (display != null) { display.stop(); display = null; } super.onDetachedFromWindow(); }
}
