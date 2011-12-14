package com.sabril.biomedis;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GraphActivity extends Activity{
	//private Timer mTimer;
	private TextView timeLabel;
	private long mStartTime;
	private Handler mHandler = new Handler();
	private GraphViewData[] data; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        //mStartTime = 0l;
        
        timeLabel = (TextView)findViewById(R.id.mTimeLabel);
        
        LinearLayout ecg_section = (LinearLayout)findViewById(R.id.ecg_graph);
        LinearLayout ppg_section = (LinearLayout)findViewById(R.id.ppg_graph);
        
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                new GraphViewData(1, 2.0d)
                , new GraphViewData(2, 1.5d)
                , new GraphViewData(3, 2.5d)
                , new GraphViewData(4, 1.0d)
        });
        
        GraphView ecg_view = new LineGraphView(
        	      this // context
        	      , "ECG" // heading
        	);
        GraphView ppg_view = new LineGraphView(
      	      this // context
      	      , "PPG" // heading
      	);
        
		// draw random curve
        int num = 1000;
        data = new GraphViewData[num];
		double v=0;
		for (int i=0; i<num; i++) {
			v += 0.2;
			data[i] = new GraphViewData(i, Math.sin(Math.random()*v));
		}
        
        ecg_view.addSeries(new GraphViewSeries("", Color.rgb(90,250,0),data)); // data
        ecg_view.setViewPort(2, 50);
        ecg_view.setScrollable(true);
        ecg_view.setScalable(true);
        ppg_view.addSeries(exampleSeries); // data
        
        ecg_section.addView(ecg_view);
        ppg_section.addView(ppg_view);
        
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
        	Toast.makeText(this, "Starting..", Toast.LENGTH_LONG).show();
        	//if  (mStartTime == 0L){
        		mStartTime = System.currentTimeMillis();
        	//}
        	mHandler.removeCallbacks(Timer_Tick);
    		mHandler.postDelayed(Timer_Tick, 100);
            return true;
        case R.id.stop:
        	Toast.makeText(this, "Stopping..", Toast.LENGTH_LONG).show();
        	mHandler.removeCallbacks(Timer_Tick);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			final long start = mStartTime;
		    long millis = System.currentTimeMillis() - start;
		    int seconds = (int) (millis / 1000);
		    int minutes = seconds / 60;
		    seconds     = seconds % 60;
		    if (seconds < 10) {
		    	timeLabel.setText("" + minutes + ":0" + seconds);
		    } else {
		    	timeLabel.setText("" + minutes + ":" + seconds);            
		    }
		    
		    
		    
		    
		    mHandler.postDelayed(this, 200);
		}
	};
	
	public boolean resetGraph(){
		return true;
	}
}
