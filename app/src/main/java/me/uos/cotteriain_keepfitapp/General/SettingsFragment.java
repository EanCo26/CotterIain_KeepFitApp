package me.uos.cotteriain_keepfitapp.General;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import me.uos.cotteriain_keepfitapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }
}
