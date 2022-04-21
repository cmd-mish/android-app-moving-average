package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String currency, datefrom, dateto;
    LineChart chart;
    ArrayList<ChartLine> lines;
    ArrayList<Double> currencyValues;
    CheckBox checkBoxSMA1, checkBoxSMA2;
    int sma1, sma2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBoxSMA1 = findViewById(R.id.checkBoxSMA1);
        checkBoxSMA2 = findViewById(R.id.checkBoxSMA2);
        sma1 = 3;
        sma2 = 10;

        chart = (LineChart) findViewById(R.id.chart);
        lines = new ArrayList<>();

        // TEMPORÄRA VÄRDEN
        currency = "USD";
        datefrom = "2022-01-01";
        dateto = "2022-02-01";

        // Hämta växelkurser från API
        currencyValues = getCurrencyValues(currency, datefrom, dateto);
        // Skriv ut dem i konsolen
        System.out.println(currencyValues.toString());

        lines.add(new ChartLine(currencyValues, "Values", Color.BLUE, 0));
        // lines.add(new ChartLine(Statistics.movingAverage(currencyValues, 3), "SMA3", Color.GREEN, 3));

        createGraph(lines);
    }

    public void createGraph(ArrayList<ChartLine> chartLines) {
        // Dataseries med linjerna
        List<ILineDataSet> dataSeries = new ArrayList<>();

        for (ChartLine chartLine: chartLines) {
            LineDataSet lineDataSet = new LineDataSet(chartLine.getEntries(), chartLine.getLabel());

            lineDataSet.setColor(chartLine.getColor());
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            dataSeries.add(lineDataSet);
        }

        LineData lineData = new LineData(dataSeries);
        chart.setData(lineData);
        chart.invalidate();
    }

    public void addLine(ArrayList<Double> values, String label, int color, int offset) {
        lines.add(new ChartLine(values, label, color, offset));
        createGraph(lines);
    }

    public void removeLine(String label) {
        ChartLine line = lines.stream()
                .filter(obj -> label.equals(obj.getLabel()))
                .findAny()
                .orElse(null);
        lines.remove(line);
        createGraph(lines);
    }

    public void checkBoxClickSMA1(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        String label = ((CheckBox) view).getText().toString();

        if (checked) {
            addLine(Statistics.movingAverage(currencyValues, sma1), label, Color.GREEN, sma1);
            // Toast.makeText(this, label, Toast.LENGTH_SHORT).show();
        } else {
            removeLine(label);
        }
    }

    public void checkBoxClickSMA2(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        String label = ((CheckBox) view).getText().toString();

        if (checked) {
            addLine(Statistics.movingAverage(currencyValues, sma2), label, Color.RED, sma2);
            // Toast.makeText(this, label, Toast.LENGTH_SHORT).show();
        } else {
            removeLine(label);
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


/*    // Enkel graf
    public void createGraph(ArrayList<Double> dataSet) {
        List<Entry> entries1 = new ArrayList<Entry>();

        for (int i = 0; i < dataSet.size(); i++) {
            entries1.add(new Entry(i, dataSet.get(i).floatValue()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries1, "Värden");
        LineData lineData = new LineData(lineDataSet);

        chart.setData(lineData);
        chart.invalidate();
    }*/


    // Färdig metod som hämtar växelkursdata
    public ArrayList<Double> getCurrencyValues(String currency, String from, String to) {

        CurrencyApi api = new CurrencyApi();
        ArrayList<Double> currencyData = null;

        String urlString = String.format("https://api.exchangerate.host/timeseries?start_date=%s&end_date=%s&symbols=%s",
                from.trim(),
                to.trim(),
                currency.trim());

        try {
            String jsonData = api.execute(urlString).get();

            if (jsonData != null) {
                currencyData = api.getCurrencyData(jsonData, currency.trim());
                Toast.makeText(getApplicationContext(), String.format("Hämtade %s valutakursvärden från servern", currencyData.size()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Kunde inte hämta växelkursdata från servern: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return currencyData;
    }
}