package com.nearsky.tv;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.view.*;
import android.widget.*;

public final class MainActivity extends Activity {
    private FlightView display;
    private LinearLayout controls;
    private boolean demo, resumed;
    private Button demoButton;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        demo = state == null ? getIntent().getBooleanExtra("demo", false) : state.getBoolean("demo");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        FrameLayout root = new FrameLayout(this);
        display = new FlightView(this); root.addView(display, new FrameLayout.LayoutParams(-1, -1));
        controls = new LinearLayout(this); controls.setOrientation(LinearLayout.VERTICAL); controls.setPadding(dp(24), dp(20), dp(24), dp(20)); controls.setBackgroundColor(Color.rgb(13,22,29));
        TextView title = new TextView(this); title.setText("NEARSKY"); title.setTextSize(20); title.setTextColor(Color.rgb(196,229,201)); controls.addView(title);
        TextView hint = new TextView(this); hint.setText("Your nearby sky.\nThree aircraft, live."); hint.setTextSize(14); hint.setPadding(0,dp(8),0,dp(18)); controls.addView(hint);
        button("Full-screen preview", () -> { controls.setVisibility(View.GONE); display.requestFocus(); });
        button("Location & radius", this::showLocation);
        demoButton = button(demo ? "Switch to live" : "Try sample flights", () -> { demo = !demo; demoButton.setText(demo ? "Switch to live" : "Try sample flights"); display.start(demo); });
        button("Set as screensaver", this::openScreensaverSettings);
        button("Sources & photo credits", this::showSources);
        TextView back = new TextView(this); back.setText("Back opens this menu\nin full-screen preview."); back.setTextSize(12); back.setPadding(0,dp(16),0,0); controls.addView(back);
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(dp(320), -2, Gravity.CENTER);
        root.addView(controls, menuParams); setContentView(root);
        if (getIntent().getBooleanExtra("preview", false)) controls.setVisibility(View.GONE);
    }
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    private Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setTextSize(13); b.setAllCaps(false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(48)); lp.topMargin = dp(6); controls.addView(b, lp);
        b.setOnClickListener(v -> action.run()); return b;
    }
    @Override protected void onResume() { super.onResume(); resumed = true; display.start(demo); }
    @Override protected void onPause() { resumed = false; display.stop(); super.onPause(); }
    @Override protected void onSaveInstanceState(Bundle state) { state.putBoolean("demo", demo); super.onSaveInstanceState(state); }
    @Override public boolean onKeyDown(int code, KeyEvent event) {
        if (code == KeyEvent.KEYCODE_BACK && controls.getVisibility() != View.VISIBLE) { controls.setVisibility(View.VISIBLE); controls.getChildAt(2).requestFocus(); return true; }
        if (code == KeyEvent.KEYCODE_MENU) { showLocation(); return true; }
        return super.onKeyDown(code, event);
    }
    private EditText field(LinearLayout form, String label, String value, boolean numeric) {
        TextView caption = new TextView(this); caption.setText(label); caption.setTextSize(14); caption.setPadding(0,dp(10),0,0); form.addView(caption);
        EditText field = new EditText(this); field.setText(value); field.setSingleLine(true); field.setTextSize(18); field.setSelectAllOnFocus(true);
        field.setInputType(numeric ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        field.setFilters(new InputFilter[]{new InputFilter.LengthFilter(numeric ? 16 : 32)});
        field.setContentDescription(label); form.addView(field, new LinearLayout.LayoutParams(-1,dp(52))); return field;
    }
    private void showLocation() {
        com.nearsky.tv.Settings s = com.nearsky.tv.Settings.load(this);
        ScrollView scroll = new ScrollView(this); LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(24),dp(8),dp(24),dp(16)); scroll.addView(form);
        TextView help = new TextView(this); help.setText("Choose a city or enter its coordinates. The name is a display label. Coordinates are the center of your search."); help.setTextSize(14); form.addView(help);
        Button preset = new Button(this); preset.setText("Choose a city"); preset.setAllCaps(false); form.addView(preset);
        EditText name = field(form, "Location name", s.name, false);
        EditText lat = field(form, "Latitude (−90 to 90)", Double.toString(s.lat), true);
        EditText lon = field(form, "Longitude (−180 to 180)", Double.toString(s.lon), true);
        EditText radius = field(form, "Radius in kilometers (1–200)", Double.toString(s.radiusKm), true);
        Switch photos = new Switch(this); photos.setText("Prefer photos instead of custom SVG liveries"); photos.setChecked(s.photos); photos.setPadding(0,dp(18),0,dp(8)); form.addView(photos);
        preset.setOnClickListener(v -> {
            String[] cities = {"Ghaziabad", "New Delhi", "Mumbai", "Bengaluru", "London", "New York"};
            double[][] coordinates = {{28.6692,77.4538},{28.6139,77.2090},{19.0760,72.8777},{12.9716,77.5946},{51.5074,-.1278},{40.7128,-74.0060}};
            new AlertDialog.Builder(this).setTitle("Choose a city center").setItems(cities, (d,i) -> {
                name.setText(cities[i]); lat.setText(Double.toString(coordinates[i][0])); lon.setText(Double.toString(coordinates[i][1]));
            }).setNegativeButton("Cancel", null).show();
        });
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Location & radius").setView(scroll).setNegativeButton("Cancel",null).setPositiveButton("Save",null).create();
        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                try {
                    new com.nearsky.tv.Settings(name.getText().toString(), Double.parseDouble(lat.getText().toString().trim()), Double.parseDouble(lon.getText().toString().trim()), Double.parseDouble(radius.getText().toString().trim()), photos.isChecked()).save(this);
                    dialog.dismiss(); if (resumed) display.start(demo);
                } catch (IllegalArgumentException e) {
                    new AlertDialog.Builder(this).setTitle("Check your location").setMessage("Enter a location name, latitude from −90 to 90, longitude from −180 to 180, and radius from 1 to 200 km.").setPositiveButton("OK",null).show();
                }
            });
            preset.requestFocus();
        });
        dialog.show(); dialog.getWindow().setLayout(dp(560), -1);
    }
    private void openScreensaverSettings() {
        try { startActivity(new Intent(Settings.ACTION_DREAM_SETTINGS)); }
        catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(this).setTitle("Select NearSky on your TV")
                .setMessage("Open TV Settings → Screen saver / Ambient Mode and select NearSky. Some Google TVs hide third-party selection; the included setup guide has the ADB commands. You can always use Full-screen preview here.")
                .setPositiveButton("OK",null).show();
        }
    }
    private void showSources() {
        String credits = "Live positions: https://adsb.fi\nAPI: https://github.com/adsbfi/opendata\n\nRoutes, airline names, aircraft metadata & photo lookup: https://adsbdb.com\nPhotos: Airport-data.com, copyright their respective photographers. Images are reference photos of the registered aircraft, not live photographs.\n\nSVG liveries: hand-redrawn standard airline schemes for 24 carriers, with enlarged English lettering and simplified tail motifs. These are representative illustrations, not official brand master assets or confirmation of the paint on a particular aircraft. Other operator codes retain labeled concept artwork. SVG rendering: AndroidSVG 1.4, Apache 2.0; lettering outlines use Liberation fonts; bundled license notices accompany the source.\n\nLocation stays on this device and its coordinates are sent to the flight provider to search nearby aircraft. Flight callsigns are sent to adsbdb for reference routes; missing routes are never inferred. No account or GPS permission is required.\n\nCurrent image sources:\n" + String.join("\n\n", display.photoSources());
        TextView text = new TextView(this); text.setAutoLinkMask(android.text.util.Linkify.WEB_URLS); text.setText(credits); text.setTextSize(16); text.setMovementMethod(android.text.method.LinkMovementMethod.getInstance()); text.setPadding(dp(24),dp(12),dp(24),dp(16));
        ScrollView scroll = new ScrollView(this); scroll.addView(text);
        new AlertDialog.Builder(this).setTitle("Sources & photo credits").setView(scroll).setPositiveButton("Close",null).show();
    }
}
