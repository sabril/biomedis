package com.sabril.biomedis;

import java.util.Arrays;
import java.util.LinkedList;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class GraphActivity extends Activity{
	private Biomedis app;
	private TextView timeLabel;
	private String timeString;
	public WaveformView mWaveform = null;
	private CommClient mCommClient = null;
	private XYPlot mEcgPlot = null;
	private XYPlot mPpgPlot = null;
	
    public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";
	
    private static final int MAX_SAMPLES = 640;
    
    // receive data 
	private Number[] ecg_series = new Number[MAX_SAMPLES/2];
	private Number[] ppg_series = new Number[MAX_SAMPLES/2];
	private LinkedList<Number> ecg_history, ppg_history;
	
	private SimpleXYSeries series1, series2;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        app = (Biomedis)getApplicationContext();
        timeLabel = (TextView)findViewById(R.id.mTimeLabel);
        ecg_history = new LinkedList<Number>();
        ppg_history = new LinkedList<Number>();
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	Toast.makeText(this, "Start..", Toast.LENGTH_LONG).show();
    	
    	mCommClient = new CommClient(this, mHandler);
    	mEcgPlot = (XYPlot)findViewById(R.id.ecg_plotter);
    	mPpgPlot = (XYPlot)findViewById(R.id.ppg_plotter);
         series1 = new SimpleXYSeries(
                Arrays.asList(ecg_series),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "");                             // Set the display title of the series
         series2 = new SimpleXYSeries(
                 Arrays.asList(ppg_series),          // SimpleXYSeries takes a List so turn our array into a List
                 SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                 "");     
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                null,                   // point color
                null);              // fill color (optional)
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series2Format = new LineAndPointFormatter(
                Color.rgb(200, 200, 0),                   // line color
                null,                   // point color
                null);              // fill color (optional)
        mEcgPlot.addSeries(series1, series1Format);
    	//mEcgPlot.setTicksPerRangeLabel(3);
        mEcgPlot.setBackgroundColor(Color.BLACK);
    	mEcgPlot.disableAllMarkup();
    	mEcgPlot.setRangeLabel("mV");
    	mEcgPlot.setDomainLabel("time");
    	
    	mEcgPlot.setDomainBoundaries(0, MAX_SAMPLES/2, BoundaryMode.GROW);
    	mEcgPlot.setBorderStyle(XYPlot.BorderStyle.SQUARE, null, null);
    	mEcgPlot.setPlotMargins(0, 0, 0, 0);
    	mEcgPlot.setPlotPadding(0, 10, 10, 0);
    	
    	mPpgPlot.addSeries(series2, series2Format);
    	//mPpgPlot.setTicksPerRangeLabel(3);
    	mEcgPlot.setBackgroundColor(Color.BLACK);
    	mPpgPlot.disableAllMarkup();
    	mPpgPlot.setRangeLabel("mV");
    	mPpgPlot.setDomainLabel("time");
    	
    	mPpgPlot.setDomainBoundaries(0, MAX_SAMPLES/2, BoundaryMode.AUTO);
    	mPpgPlot.setBorderStyle(XYPlot.BorderStyle.SQUARE, null, null);
    	mPpgPlot.setPlotMargins(0, 0, 0, 0);
    	mPpgPlot.setPlotPadding(0, 10, 10, 0);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.start:
        	mCommClient.connect(app);
        	mHandler.removeCallbacks(Timer_Tick);
        	mHandler.postDelayed(Timer_Tick, 500);
            return true;
        case R.id.stop:
        	mHandler.removeCallbacks(Timer_Tick);
        	mCommClient.stop();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private final Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg){
    		switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				
				break;
			case MESSAGE_READ:
				String read = (String) msg.obj;
				String[] tmp = read.split(" ");
				if (tmp.length > 2){
					timeString = tmp[2];
					if (ecg_history.size() > MAX_SAMPLES/2){
						ecg_history.removeFirst();
					}
					ecg_history.addLast(Integer.parseInt(tmp[0]));
					series1.setModel(ecg_history, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
					mEcgPlot.redraw();
					if (ppg_history.size() > MAX_SAMPLES/2){
						ppg_history.removeFirst();
					}
					ppg_history.addLast(Integer.parseInt(tmp[1]));
					series2.setModel(ppg_history, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
					mPpgPlot.redraw();
				}
				//mWaveform.set_data(ch1_data, ch2_data);
				break;
			case MESSAGE_WRITE:
				break;
			case MESSAGE_DEVICE_NAME:
				Toast.makeText(getApplicationContext(), "Connected to: "
                        + app.getIpAddress(), Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                        Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
    	}
    };
    
	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			timeLabel.setText(timeString);
			mHandler.postDelayed(this, 3000);
		}
	};
}
