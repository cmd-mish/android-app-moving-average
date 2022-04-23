package fi.arcada.sos_projekt_chart_sma;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class ChartLine {
    // Attribut
    private ArrayList<Double> dataSet;
    private String label;
    private int color;
    private int startOffset;

    // Konstruktorn
    public ChartLine(ArrayList<Double> dataSet, String label, int color, int startOffset) {
        this.dataSet = dataSet;
        this.label = label;
        this.color = color;
        this.startOffset = startOffset;
    }

    // Getters
    public ArrayList<Double> getDataSet() {
        return dataSet;
    }

    public String getLabel() {
        return label;
    }

    public int getColor() {
        return color;
    }

    public int getStartOffset() {
        return startOffset;
    }

    /**
     * Returnerar värderna som användas i grafen
     * @return Entries i List<Entry> format
     */
    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < dataSet.size(); i++) {
            entries.add(new Entry(i + startOffset, dataSet.get(i).floatValue()));
        }
        return entries;
    }
}
