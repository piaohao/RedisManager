package org.piaohao.redisManager;

import lombok.Data;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

@Data
public abstract class RealTimeChartFactory implements Runnable {

    private TimeSeries timeSeries;
    private JFreeChart jFreeChart;

    public RealTimeChartFactory(String chartContent, String title, String yAxisName) {
        timeSeries = new TimeSeries(chartContent);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "时间(秒)", yAxisName, timeseriescollection, true, true, false);

        XYPlot xyPlot = jfreechart.getXYPlot();
        NumberAxis yAxis = ((NumberAxis) xyPlot.getRangeAxis());
        NumberFormat numFormat = new DecimalFormat("#.##");
        yAxis.setNumberFormatOverride(numFormat);

        ValueAxis xAxis = xyPlot.getDomainAxis();
        xAxis.setAutoRange(true);
        xAxis.setFixedAutoRange(TimeUnit.MINUTES.toMillis(2));
        this.jFreeChart = jfreechart;
    }

    protected abstract void refresh();

    @Override
    public void run() {
        while (true) {
            this.refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }
}