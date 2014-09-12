package xavireig.com.pacecalculator;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.math.BigDecimal;


public class main extends Activity {

    private ViewFlipper viewFlipper;
    private float distance;
    private int h, m, s;
    private int unit;       // 0 - Km     1 - Miles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Getting View Flipper from main.xml and assigning to flipper reference variable
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        initializations();
    }

    private void initializations() {
        // Set Time Number Pickers
        NumberPicker npHours = (NumberPicker) findViewById(R.id.npHours);
        NumberPicker npMinutes = (NumberPicker) findViewById(R.id.npMinutes);
        NumberPicker npSeconds = (NumberPicker) findViewById(R.id.npSeconds);

        npHours.setMaxValue(9);
        npHours.setMinValue(0);
        npMinutes.setMaxValue(59);
        npMinutes.setMinValue(0);
        String[] values = new String[12];
        for (int i = 0; i < 12; i++) {
            values[i] = Integer.toString(i*5);
        }
        npSeconds.setMaxValue(values.length-1);
        npSeconds.setMinValue(0);
        npSeconds.setDisplayedValues(values);

        // Set fonts

        String fontPath = "Oswald.otf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        TextView tv = (TextView)findViewById(R.id.lblDistance);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.lblProjectedPace);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.lblSetTime);
        tv.setTypeface(tf);
        RadioButton rb = (RadioButton)findViewById(R.id.rbKm);
        rb.setTypeface(tf);
        rb = (RadioButton)findViewById(R.id.rbMiles);
        rb.setTypeface(tf);
        Button bt = (Button)findViewById(R.id.btCalculate);
        bt.setTypeface(tf);
        bt = (Button)findViewById(R.id.btReturn);
        bt.setTypeface(tf);
        EditText et = (EditText)findViewById(R.id.txtDistance);
        et.setTypeface(tf);
        tv = (TextView)findViewById(R.id.txtPace1Unit);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.txtPacePerUnit);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.txtPace5k);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.txtPace10k);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.txtPaceHalfMarathon);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.txtPaceMarathon);
        tv.setTypeface(tf);
        tv = (TextView)findViewById(R.id.lblProjectedRaces);
        tv.setTypeface(tf);
    }

    public void onCalculateButtonClicked(View view) {
        boolean success = captureValues();
        if (success) {
            calculatePace();
            viewFlipper.setInAnimation(this, R.anim.in_from_right);
            viewFlipper.setOutAnimation(this, R.anim.out_to_left);
            // Show the next Screen
            viewFlipper.showNext();
        }
    }

    public void onReturnButtonClicked(View view) {
        viewFlipper.setInAnimation(this, R.anim.in_from_left);
        viewFlipper.setOutAnimation(this, R.anim.out_to_right);
        // Show the next Screen
        viewFlipper.showPrevious();
    }

    private boolean captureValues() {
        boolean success = true;
        TextView txt = (TextView)findViewById(R.id.txtDistance);
        if (txt.getText().length() == 0) {
            displayErrorMessage("Enter a distance");
            success = false;
        }
        else distance = Float.valueOf(txt.getText().toString());
        NumberPicker np = (NumberPicker)findViewById(R.id.npHours);
        h = np.getValue();
        np = (NumberPicker)findViewById(R.id.npMinutes);
        m = np.getValue();
        np = (NumberPicker)findViewById(R.id.npSeconds);
        s = np.getValue() * 5;
        if (h == 0 && m == 0 && s == 0) {
            displayErrorMessage("Enter a time");
            success = false;
        }
        RadioButton rb = (RadioButton)findViewById(R.id.rbKm);
        if (rb.isChecked()) unit = 1;
        else
        {
            rb = (RadioButton)findViewById(R.id.rbMiles);
            unit = (rb.isChecked()) ? 2 : -1;   // -1 should never be
        }
        return success;
    }

    private void calculatePace()
    {
        int totalSeconds = h*3600 + m*60 + s;
        float pace = distance / totalSeconds;   // km/s or mps
        String unitText = (unit == 1) ? "Km" : "Mile";
        TextView txtPace1Unit = (TextView)findViewById(R.id.txtPace1Unit);
        TextView txtPacePerUnit = (TextView)findViewById(R.id.txtPacePerUnit);

        // Calculate pace such as 4:55 min per km
        float aux = 1 / pace;
        int aux2 = (int)aux / 60;
        int aux3 = (int)(aux % 60);
        if (aux3 < 10) txtPace1Unit.setText( aux2 + ":0" + aux3 + " per " + unitText);
        else txtPace1Unit.setText(aux2 + ":" + aux3 + " per " + unitText);

        // Calculate pace such as 6.5 km/h or 6.5 mph
        aux = pace * 3600;
        BigDecimal result = round(aux,2);
        if (unitText.compareTo("Km") == 0) txtPacePerUnit.setText( result + " " + unitText + "/h");
        else txtPacePerUnit.setText( result + " mph");

        // Calculate race times

        TextView txt = new TextView(this);
        String race = "";
        float[] distances = {5.0f, 10.0f, 21.0975f, 42.195f};
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0: txt = (TextView)findViewById(R.id.txtPace5k);
                    race = "5K   ";
                    break;
                case 1: txt = (TextView)findViewById(R.id.txtPace10k);
                    race = "10K   ";
                    break;
                case 2: txt = (TextView)findViewById(R.id.txtPaceHalfMarathon);
                    race = "Half Marathon   ";
                    break;
                case 3: txt = (TextView)findViewById(R.id.txtPaceMarathon);
                    race = "Marathon   ";
                    break;
            }
            aux = (1 / pace) * distances[i];
            int aux4 = (int) aux / 3600;
            aux2 = (int) aux % 3600;
            aux3 = (aux2 % 60);
            aux2 = aux2 / 60;
            if (aux3 < 10 && aux2 < 10 && aux4 < 10) txt.setText(race + "0" + aux4 + ":0" + aux2 + ":0" + aux3);
            else if (aux3 < 10 && aux2 < 10) txt.setText(race + aux4 + ":0" + aux2 + ":0" + aux3);
            else if (aux3 < 10) txt.setText(race + aux4 + ":" + aux2 + ":0" + aux3);
            else if (aux2 < 10) txt.setText(race + aux4 + ":0" + aux2 + ":" + aux3);
            else txt.setText(race + aux4 + ":" + aux2 + ":" + aux3);
        }

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public void displayErrorMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
}
