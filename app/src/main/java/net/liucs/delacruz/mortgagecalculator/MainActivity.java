package net.liucs.delacruz.mortgagecalculator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.text.NumberFormat;
import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;



public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    // private static final String LOG_NAME = "Mortgage";
    private static final double minPrice = 150000;
    private static final double maxPrice = 2500000;
    private static final double maxRate = .15;
    private static final double minRate = .01;
    private static final double maxDown = .50;
    private static final double minDown = .20;
    private static final int minYear = 120; // 10 YEARS
    private static final int maxYear = 600; // 50 YEARS
    private SeekBar priceSeek;
    private SeekBar downSeek;
    private SeekBar rateSeek;
    private SeekBar yearSeek;
    private TextView priceAmount, downAmount, rateAmount, yearAmount,
            monthlyAmount, totalAmount, interestAmount;
    private double price;
    private double rate;
    private double down;
    private double principal;
    private double years;
    private LinearLayout chartContainer;
    private DefaultRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        priceSeek = (SeekBar) findViewById(R.id.priceSeek);
        priceSeek.setOnSeekBarChangeListener(this);
        downSeek = (SeekBar) findViewById(R.id.downSeek);
        downSeek.setOnSeekBarChangeListener(this);
        rateSeek = (SeekBar) findViewById(R.id.rateSeek);
        rateSeek.setOnSeekBarChangeListener(this);
        yearSeek = (SeekBar) findViewById(R.id.yearsSeek);
        yearSeek.setOnSeekBarChangeListener(this);
        priceAmount = (TextView) findViewById(R.id.priceAmount);
        downAmount = (TextView) findViewById(R.id.downAmount);
        rateAmount = (TextView) findViewById(R.id.rateAmount);
        yearAmount = (TextView) findViewById(R.id.yearsAmount);
        monthlyAmount = (TextView) findViewById(R.id.monthlyPay);
        totalAmount = (TextView) findViewById(R.id.totalPay);
        interestAmount = (TextView) findViewById(R.id.interestPaid);
        chartContainer = (LinearLayout) findViewById(R.id.chartContainer);
        int[] colors = { Color.GREEN, Color.YELLOW, Color.RED };
        renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(40);
        for(int c : colors) {
            SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
            ssr.setColor(c);
            ssr.setShowLegendItem(false);
            renderer.addSeriesRenderer(ssr);
        }

        if (savedInstanceState == null) {
            priceSeek.setProgress(priceSeek.getMax()/3);
            rateSeek.setProgress(rateSeek.getMax()/3);
            downSeek.setProgress(downSeek.getMax()/3);
            yearSeek.setProgress(yearSeek.getMax());

        }
    }

    private double downCal() {
        return price * (down);
    }

    private double MonthlyCal() {
    //Monthly Payment Formula M = P [ i(1 + i)^n ] / [ (1 + i)^n – 1]
        principal = price - downCal();
        double monthlyRate = rate / 12;
        return principal * (monthlyRate * Math.pow(1 + monthlyRate, years)) / (Math.pow(1 + monthlyRate, years) - 1);
    }

    private double interestCal() {
        return MonthlyCal() * years - principal;

    }

    private double totalCal() {
        return MonthlyCal() * years + downCal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R
                .menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        // FOR CURRENCY FORMULA - http://tutorials.jenkov.com/java-internationalization/numberformat.html

        NumberFormat percentageFormat = NumberFormat.getPercentInstance();
        percentageFormat.setMinimumFractionDigits(2);
        //FOR PERCENTAGE FORMULA - http://www.avajava.com/tutorials/lessons/how-do-i-use-numberformat-to-format-a-percent.html

        NumberFormat yearFormat = NumberFormat.getInstance();
        yearFormat.setMaximumFractionDigits(0);


        if (seekBar == priceSeek) {
            double m = priceSeek.getMax();
            double r = maxPrice - minPrice;
            double p = (double) progress / m;
            price = p * r + minPrice;
            priceAmount.setText(defaultFormat.format(price));

        }

        if (seekBar == downSeek) {
            double m = downSeek.getMax();
            double r = maxDown - minDown;
            double p = (double) progress / m;
            down = p * r + minDown;

        }
        if (seekBar == rateSeek) {
            double m = rateSeek.getMax();
            double r = maxRate - minRate;
            double p = (double) progress / m;
            rate = p * r + minRate;
            rateAmount.setText(percentageFormat.format(rate));

        }

        if (seekBar == yearSeek) {
            int m = yearSeek.getMax();
            int r = maxYear - minYear;
            double p = (double)progress / m;
            years = p * r + minYear;
            double actualYears = years / 12;
            yearAmount.setText(actualYears + " Years");
            yearAmount.setText(yearFormat.format(actualYears));
        }

        double downPayment = downCal();
        downAmount.setText(percentageFormat.format(down) + " (" + defaultFormat.format(downPayment) + ")");

        double monthlyPayment = MonthlyCal();
        monthlyAmount.setText("Monthly Payment " + defaultFormat.format(monthlyPayment));

        double interestPayment = interestCal();
        interestAmount.setText("Total Interest paid " + defaultFormat.format(interestPayment));

        double totalPayment = totalCal();
        totalAmount.setText("Total Amount paid " + defaultFormat.format(totalPayment));

        CategorySeries data = new CategorySeries("Chart");
        data.add("Downpayment", price);
        data.add("Principal", principal);
        data.add("Interest", interestCal());
        chartContainer.removeAllViews();
        chartContainer.addView(ChartFactory.getPieChartView(this, data, renderer));
}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}