/**
 * Demo app for Android Wear, for JavaMagazine
 * measuring heartrate and then show a card fragment with average heartrate
 * Rik Vegt, Ordina, februari 2015
 */

package nl.javamagazine.heartrate;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.widget.TextView;

public class MyDisplayActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        String message = "Je hartslag is gemiddeld " + getIntent().getIntExtra(MeasurementActivity.EXTRA_MSG, 0) + " bpm.";
        getFragmentManager().beginTransaction().add(R.id.frame_layout, cardFragment(R.string.card_title, message, R.drawable.ic_launcher)).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Fragment cardFragment(int titleRes, String message, int iconRes) {
        Resources res = this.getResources();
        CardFragment fragment = CardFragment.create(res.getText(titleRes), message, iconRes);
        return fragment;
    }
}