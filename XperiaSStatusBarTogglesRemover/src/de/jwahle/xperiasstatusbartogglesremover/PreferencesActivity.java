package de.jwahle.xperiasstatusbartogglesremover;

import static de.jwahle.xperiasstatusbartogglesremover.Constants.EXPAND_LAUNCHER_GRID;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.HIDE_STATUS_BAR_TOGGLES;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.HOME;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.PREFS;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.SYSTEM_UI;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity {

    private Restarter restarter;
    private SharedPreferences preferences;
    
    @Override
    @SuppressWarnings("deprecation")
    @SuppressLint("WorldReadableFiles")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpPreferences();
        restarter = new Restarter(this);
        preferences = getSharedPreferences(PREFS, MODE_WORLD_READABLE);
        preferences.registerOnSharedPreferenceChangeListener(
                new RemoveTogglesPreferenceListener());
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("WorldReadableFiles")
    private void setUpPreferences() {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(PREFS);
        manager.setSharedPreferencesMode(MODE_WORLD_READABLE);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    private final class RemoveTogglesPreferenceListener
    implements OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            if (key.equals(HIDE_STATUS_BAR_TOGGLES))
                restarter.restart(SYSTEM_UI);
            else if (key.equals(EXPAND_LAUNCHER_GRID))
                restarter.restart(HOME);
        }
    }

}
