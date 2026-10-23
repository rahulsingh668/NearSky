package com.nearsky.tv;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

/** A real focusable control, shared by the activity and interactive screensaver. */
final class SettingsButton extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private SettingsButton(Context context,Runnable open){
        super(context);setId(R.id.open_settings);setContentDescription("Open settings");
        setFocusable(true);setClickable(true);setOnClickListener(v->open.run());
        setOnFocusChangeListener((v,focused)->invalidate());
    }
    static SettingsButton attach(FrameLayout root,Runnable open){
        Context context=root.getContext();float density=context.getResources().getDisplayMetrics().density;
        SettingsButton button=new SettingsButton(context,open);
        FrameLayout.LayoutParams layout=new FrameLayout.LayoutParams(Math.round(48*density),Math.round(48*density),Gravity.BOTTOM|Gravity.LEFT);
        layout.leftMargin=Math.round(24*density);layout.bottomMargin=Math.round(16*density);
        root.addView(button,layout);return button;
    }
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);canvas.save();canvas.scale(getWidth()/48f,getHeight()/48f);
        // Keep the remote/touch target at 48 dp; only the visible artwork is smaller.
        // No filled tile: the quiet gear sits directly on the existing background.
        if(isFocused()){paint.setColor(Color.argb(65,151,167,177));paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(.7f);canvas.drawCircle(24,24,14,paint);}
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.1f);paint.setStrokeJoin(Paint.Join.ROUND);paint.setColor(Color.argb(isFocused()?155:90,151,167,177));
        Path gear=new Path();
        for(int i=0;i<32;i++){double angle=i*Math.PI/16;float radius=(i%4==0||i%4==3)?8:6.3f;
            float x=24+(float)Math.cos(angle)*radius,y=24+(float)Math.sin(angle)*radius;if(i==0)gear.moveTo(x,y);else gear.lineTo(x,y);}
        gear.close();canvas.drawPath(gear,paint);canvas.drawCircle(24,24,2.8f,paint);canvas.restore();
    }
    @Override public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info){super.onInitializeAccessibilityNodeInfo(info);info.setClassName("android.widget.Button");}
}
