package com.baconworx.smsflash.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.text.Spanned;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.activities.Filters;
import com.baconworx.smsflash.activities.ImportPackage;
import com.baconworx.smsflash.db.ConfigDatabase;

public class MainSettings extends PreferenceFragment {
    public static final String PREFS_NAME = "com.baconworx.smsflash.prefs";
    private static final int IMPORT_REQUEST = 1;
    public static int DEFAULT_TIMEOUT = 5000;
    private Context context = null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.main_preferences);

        Preference preference = this.findPreference("editFilters");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent settingsIntent = new Intent(preference.getContext(), Filters.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingsIntent);
                return true;
            }
        });

        preference = this.findPreference("addPackage");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent addPackageIntent = new Intent(preference.getContext(), ImportPackage.class);
                addPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(addPackageIntent, IMPORT_REQUEST);
                return true;
            }
        });

        preference = this.findPreference("makeConfig");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ConfigDatabase configDatabase = new ConfigDatabase(preference.getContext(), true);
                configDatabase.open();
                configDatabase.close();
                return true;
            }
        });

        preference = this.findPreference("pref_timeout");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putInt("timeout", Integer.parseInt((String) newValue));
                editor.commit();

                return true;
            }
        });

        // only allow timeout values from 1 to 99 seconds
        ((EditTextPreference) preference).getEditText().setFilters(new InputFilter[]{
                new InputFilter() {
                    private static final int min = 1, max = 99;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        try {
                            int input = Integer.parseInt(dest.toString() + source.toString());
                            if (isInRange(min, max, input))
                                return null;
                        } catch (NumberFormatException ignored) {
                        }

                        return "";
                    }

                    private boolean isInRange(int a, int b, int c) {
                        return b > a ? c >= a && c <= b : c >= b && c <= a;
                    }
                }
        });

        SharedPreferences settings = context.getSharedPreferences(MainSettings.PREFS_NAME, 0);
        int timeout = settings.getInt("timeout", DEFAULT_TIMEOUT);
        preference.setDefaultValue(timeout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMPORT_REQUEST:
                // after importing package, only if called from here!
                break;
        }
    }
}
