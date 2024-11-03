package com.example.sprintproject.viewmodels;

import android.graphics.Color;


import com.example.sprintproject.model.TravelStats;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChartViewModel {
    public ChartViewModel() {

    }


    public void configureChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterTextSize(16f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
    }


    public void updateChartWithStats(PieChart pieChart, TravelStats stats) {
        List<PieEntry> entries = new ArrayList<>();
        float totalValue = stats.getAllottedDays() + stats.getPlannedDays();

        if (totalValue > 0) {
            entries.add(new PieEntry(stats.getAllottedDays(), "Allotted Days"));
            entries.add(new PieEntry(stats.getPlannedDays(), "Planned Days"));
        } else {
            entries.add(new PieEntry(1, "No Travel Days"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Travel Days Distribution");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(64, 89, 128));
        colors.add(Color.rgb(231, 76, 60));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

        String summaryText = createSummaryText(stats, totalValue);
        pieChart.setCenterText(summaryText);

        pieChart.animateY(1400);
        pieChart.invalidate();
    }

    public String createSummaryText(TravelStats stats, float totalValue) {
        if (totalValue > 0) {
            String text = String.format("Allotted: %d days\nPlanned: %d days",
                    stats.getAllottedDays(), stats.getPlannedDays());
            if (stats.getPlannedDays() > stats.getAllottedDays() && stats.getAllottedDays() > 0) {
                text += "\n⚠️ Exceeds allotted time";
            }
            return text;
        }
        return "No travel days recorded";
    }
    public void showEmptyChart(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1, "No Data Available"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColor(Color.GRAY);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.setCenterText("No travel data available");
        pieChart.invalidate();
    }

}
