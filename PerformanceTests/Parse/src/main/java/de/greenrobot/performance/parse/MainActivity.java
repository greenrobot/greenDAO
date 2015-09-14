package de.greenrobot.performance.parse;

import android.app.Activity;
import android.os.Bundle;

import com.parse.ParseAnalytics;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        try {
            new ParsePerfTest().testPerformance();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
