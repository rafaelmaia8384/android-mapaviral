package br.com.smarttoolsapps.mapaviral;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import br.com.smarttoolsapps.mapaviral.R;

public class EstatisticasActivity extends BaseActivity {

    private ModelEstatistica estatistica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estatistica = gson.fromJson(getIntent().getStringExtra("estatisticas"), ModelEstatistica.class);

        setChart();
    }

    private void setChart() {

        PieChart chart = findViewById(R.id.chart);

        Legend l = chart.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);

        chart.setCenterText("Sintomas (%)");
        chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(false);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(40,0,0,0);
        chart.setDragDecelerationFrictionCoef(0.9f);
        chart.setTransparentCircleRadius(60f);
        chart.animateY(1000, Easing.EaseInOutCubic);
        chart.setEntryLabelColor(Color.parseColor("#FF000000"));

        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(estatistica.getS1(),"Febre (" + estatistica.getS1() + ")"));
        values.add(new PieEntry(estatistica.getS2(),"Cansaço (" + estatistica.getS2() + ")"));
        values.add(new PieEntry(estatistica.getS3(),"Tosse (" + estatistica.getS3() + ")"));
        values.add(new PieEntry(estatistica.getS4(),"Espirros (" + estatistica.getS4() + ")"));
        values.add(new PieEntry(estatistica.getS5(),"Dores no corpo e mal-estar (" + estatistica.getS5() + ")"));
        values.add(new PieEntry(estatistica.getS6(),"Coriza ou nariz entupido (" + estatistica.getS6() + ")"));
        values.add(new PieEntry(estatistica.getS7(),"Dor de garganta (" + estatistica.getS7() + ")"));
        values.add(new PieEntry(estatistica.getS8(),"Diarreia (" + estatistica.getS8() + ")"));
        values.add(new PieEntry(estatistica.getS9(),"Dor de cabeça (" + estatistica.getS9() + ")"));
        values.add(new PieEntry(estatistica.getS10(),"Falta de ar (" + estatistica.getS10() + ")"));
        values.add(new PieEntry(estatistica.getS11(),"Perda de paladar ou olfato (" + estatistica.getS11() + ")"));

        PieDataSet dataSet = new PieDataSet(values, null);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(colors);

        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);

        ((TextView)findViewById(R.id.textUsuariosTotais)).setText(Integer.toString(estatistica.getUsuariosTotais()));
        ((TextView)findViewById(R.id.textUsuariosAssintomaticos)).setText(Integer.toString(estatistica.getUsuariosAssintomaticos()));
        ((TextView)findViewById(R.id.textUsuariosTiveramContato)).setText(Integer.toString(estatistica.getUsuariosTiveramContato()));
        ((TextView)findViewById(R.id.textUsuariosCadastros48h)).setText(Integer.toString(estatistica.getUsuariosCadastros48h()));
        ((TextView)findViewById(R.id.textUsuariosDeslocamento48h)).setText(Integer.toString(estatistica.getUsuariosDeslocamento48h()));
        ((TextView)findViewById(R.id.textUsuariosDeletado48h)).setText(Integer.toString(estatistica.getUsuariosDeletado48h()));
        ((TextView)findViewById(R.id.textUsuariosSintomasAgressivos)).setText(Integer.toString(estatistica.getUsuariosSintomasAgressivos()));

        chart.setData(pieData);
    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();

        return true;
    }
}
