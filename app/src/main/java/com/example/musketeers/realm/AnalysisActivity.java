package com.example.musketeers.realm;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class AnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        //P I E C H A R T

        final PieChart pieChart = (PieChart)findViewById(R.id.chart1
        );
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(18.5f, "Green"));
        entries.add(new PieEntry(26.7f, "Yellow"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));
        PieDataSet set = new PieDataSet(entries, "CONSUMPTION");
        PieData data = new PieData(set);
        //set.setColors((new int[] { Color.rgb(255,255,0),Color.rgb(255,255,123),Color.rgb(255,0,255),Color.rgb(0,255,255) }), this);
        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(255,0,0), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)};
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        set.setColors(colors);

        pieChart.setData(data);
        pieChart.animateY(1000);

        pieChart.invalidate(); // refresh


        //L I N E C H A R T
        final LineChart chart = (LineChart) findViewById(R.id.chart);


        chart.setBackgroundColor(Color.rgb(255,255,255));
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setEnabled(false);

        YAxis left = chart.getAxisLeft();
        left.setEnabled(false);
        YAxis right = chart.getAxisRight();
        right.setEnabled(false);

        chart.animateXY(900,4000);

        Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInBounce);


        List<Entry> entriess = new ArrayList<Entry>();
        entriess.add(new Entry(8f,0));
        entriess.add(new Entry(10f,1));
        entriess.add(new Entry(15f,2));
        entriess.add(new Entry(18f,3));
        entriess.add(new Entry(22f,4));
        LineDataSet dataSet = new LineDataSet(entriess, "Label");

        LineData lineData = new LineData(dataSet);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setCircleColor(Color.rgb(37,95,210));
        dataSet.setDrawCircleHole(false);
        chart.setData(lineData);
        dataSet.setLineWidth(5f);
        dataSet.setDrawFilled(false);
        dataSet.setColor(Color.rgb(84,134,230));
        //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        //dataSet.setFillColor(Color.rgb(255,116,112));
        //dataSet.setFillAlpha(1500);
        dataSet.setValueTextColor(Color.rgb(34,40,49));

        chart.invalidate(); // refresh


        CardView a = (CardView) findViewById(R.id.card1);

        final TextView title = (TextView)findViewById(R.id.textView2);
        title.setTextColor(Color.rgb(0,0,0));

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("WATER MOTOR");
                //new ReColor(MainActivity.this).setCardViewColor(L , "#F06292", "BA68C8", 600);
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
                // pieChart.animateY(1000);
                chart.animateXY(900,4000);

            }
        });






        CardView  b = (CardView) findViewById(R.id.card2);
        //final TextView title = (TextView)findViewById(R.id.textView2);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("IRON BOX");
                //new ReColor(MainActivity.this).setCardViewColor(L ,  "BA68C8","9575CD" ,600);
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
                // pieChart.animateY(1000);
                chart.animateXY(900,4000);

            }
        });


        CardView  c = (CardView) findViewById(R.id.card3);
        //  final TextView title = (TextView)findViewById(R.id.textView2);

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("OUT LAMP");
                //new ReColor(MainActivity.this).setCardViewColor(L ,"9575CD" ,"7986CB",600);
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
                // pieChart.animateY(1000);
                chart.animateXY(900,4000);

            }
        });


        CardView  d = (CardView) findViewById(R.id.card4);
        // final TextView title = (TextView)findViewById(R.id.textView2);

        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("BEDROOM LAMP");
                //  new ReColor(MainActivity.this).setCardViewColor(L ,"7986CB","64B5F6",600);
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
                //   pieChart.animateY(1000);
                chart.animateXY(900,4000);

            }
        });


        CardView  e = (CardView) findViewById(R.id.card5);
        //final TextView title = (TextView)findViewById(R.id.textView2);

        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("FAN");
                // new ReColor(MainActivity.this).setCardViewColor(L ,"64B5F6","4FC3F7",600);
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
                // pieChart.animateY(1000);
                chart.animateXY(900,4000);

            }
        });



        CardView  f = (CardView) findViewById(R.id.card6);
        //final TextView title = (TextView)findViewById(R.id.textView2);

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("WASHING MACHINE");

                //  new ReColor(MainActivity.this).setCardViewColor(L ,"4FC3F7","4DD0E1",600);
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
                // pieChart.animateY(1000);
                chart.animateXY(900,4000);

            }
        });



        CardView  g = (CardView) findViewById(R.id.card7);
        //final TextView title = (TextView)findViewById(R.id.textView2);

        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("WATER HEATER");
                //   new ReColor(MainActivity.this).setCardViewColor(L ,"4DD0E1","4DB6AC",600);
                chart.setVisibility(View.VISIBLE);
                // pieChart.setVisibility(View.INVISIBLE);

                chart.animateXY(900,4000);

            }
        });




    }
}


