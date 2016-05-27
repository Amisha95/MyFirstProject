package com.example.m1.myfirstproject;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_layout);
        PreferenceSummary(findPreference("sortby"));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue=newValue.toString();
        if(preference instanceof ListPreference)
        {
            ListPreference listPreference=(ListPreference)preference;
            int pref=listPreference.findIndexOfValue(stringValue);
            if(pref>=0)
            {
                preference.setSummary(listPreference.getEntries()[pref]);
            }
            else
            {
                preference.setSummary(stringValue);
            }
        }
        return true;
    }
    private void PreferenceSummary(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences
                (preference.getContext()).getString(preference.getKey(),""));
    }
}
