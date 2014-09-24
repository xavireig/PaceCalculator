package xavireig.com.pacecalculator;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.math.BigDecimal;


public class main extends Activity {

    private ViewFlipper viewFlipper;
    private float distance;
    private int h, m, s;
    private int unit;       // 0 - Km     1 - Miles
    private final String TAPPX_KEY = "/120940746/Pub-2167-Android-6569";
    private com.google.android.gms.ads.doubleclick.PublisherAdView adBanner = null;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Tappx ad banner creation
        adBanner = com.tappx.TAPPXAdBanner.ConfigureAndShowAtBottom(this, adBanner, TAPPX_KEY);

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

    @Override
    public void onPause() {
        com.tappx.TAPPXAdBanner.Pause(adBanner);
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        com.tappx.TAPPXAdBanner.Resume(adBanner);
    }
    @Override protected void onDestroy() {
        com.tappx.TAPPXAdBanner.Destroy(adBanner);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (exit)
            this.finish();
        else {
            Toast.makeText(this, "Press again to quit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
