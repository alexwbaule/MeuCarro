package com.alexwbaule.flexprofile.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.CalculaCombustivel;
import com.alexwbaule.flexprofile.domain.SQLiteHelper;
import com.alexwbaule.flexprofile.utils.Calculos;
import com.alexwbaule.flexprofile.utils.PreferencesProcessed;
import com.alexwbaule.flexprofile.utils.ReportValueFormatter;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by alex on 09/07/15.
 */
public class Calculator extends BaseFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler {
    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM2 = "EDTID";
    private TextView price_alc;
    private TextView price_gnv;
    private TextView price_gas;
    private TextView price_diesel;
    private TextView label_custo;

    private TextView currency_alc;
    private TextView currency_gnv;
    private TextView currency_gas;
    private TextView currency_diesel;

    private CardView relative_combtv;
    private LinearLayout relative_result;
    private int EditUsed;
    private static boolean[] hasList;
    private static Typeface tflcddot;

    private TextView result_combustivel;
    private TextView result_autonomia;
    private TextView result_custokm;
    private TextView result_tq_cheio;
    private TextView result_calc_type;
    private HorizontalBarChart barChart01;

    private CalculaCombustivel calculoGasolina;
    private CalculaCombustivel calculoEtanol;
    private CalculaCombustivel calculoGNV;
    private CalculaCombustivel calculoDiesel;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private PreferencesProcessed valores;
    private int maxSize = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tflcddot = Typeface.createFromAsset(getActivity().getAssets(), "fonts/digital.ttf");
        if (savedInstanceState != null) {
            EditUsed = savedInstanceState.getInt(ARG_PARAM2);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PARAM2, EditUsed);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        maxSize = graphSize(280);
        // Inflate the layout for this fragment
        View rootView;
        valores = new PreferencesProcessed(getActivity());

        if (rootapp.getHasCars() == 0) {
            rootView = inflater.inflate(R.layout.empty_calculator, container, false);
            final FloatingActionButton fab = rootView.findViewById(R.id.fab_vehicle);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(new VehicleAdd());
                }
            });
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
        //onCoachMark();
        hasList = rootapp.getHasCombustiveis();

        barChart01 = rootView.findViewById(R.id.chart_result01);

        label_custo = rootView.findViewById(R.id.label_custo_km);
        label_custo.setText(getString(R.string.custo_km, valores.getOdometro()));

        price_alc = rootView.findViewById(R.id.price_Etanol);
        price_gnv = rootView.findViewById(R.id.price_gnv);
        price_gas = rootView.findViewById(R.id.price_gasolina);
        price_diesel = rootView.findViewById(R.id.price_diesel);

        currency_alc = rootView.findViewById(R.id.price_Etanol_currency);
        currency_gnv = rootView.findViewById(R.id.price_gnv_currency);
        currency_gas = rootView.findViewById(R.id.price_gasolina_currency);
        currency_diesel = rootView.findViewById(R.id.price_diesel_currency);

        NumberFormat format = new DecimalFormat("0.000");
        currency_alc.setText(format.getCurrency().getSymbol());
        currency_gnv.setText(format.getCurrency().getSymbol());
        currency_gas.setText(format.getCurrency().getSymbol());
        currency_diesel.setText(format.getCurrency().getSymbol());

        setTextStyle(price_alc);
        setTextStyle(price_gnv);
        setTextStyle(price_gas);
        setTextStyle(price_diesel);


        setTextStyle(currency_alc);
        setTextStyle(currency_gnv);
        setTextStyle(currency_gas);
        setTextStyle(currency_diesel);

        double[] values = rootapp.getPrices();

        price_gas.setText(format.format(values[0]));
        price_alc.setText(format.format(values[1]));
        price_gnv.setText(format.format(values[2]));
        price_diesel.setText(format.format(values[3]));

        price_alc.setOnClickListener(meuclick);
        price_gnv.setOnClickListener(meuclick);
        price_gas.setOnClickListener(meuclick);
        price_diesel.setOnClickListener(meuclick);

        if (!hasList[0]) {
            relative_combtv = rootView.findViewById(R.id.include_gasolina);
            relative_combtv.setVisibility(View.GONE);
            maxSize -= graphSize(65);
        }
        if (!hasList[1]) {
            relative_combtv = rootView.findViewById(R.id.include_Etanol);
            relative_combtv.setVisibility(View.GONE);
            maxSize -= graphSize(65);
        }
        if (!hasList[2]) {
            relative_combtv = rootView.findViewById(R.id.include_gnv);
            relative_combtv.setVisibility(View.GONE);
            maxSize -= graphSize(65);
        }
        if (!hasList[3]) {
            relative_combtv = rootView.findViewById(R.id.include_diesel);
            relative_combtv.setVisibility(View.GONE);
            maxSize -= graphSize(65);
        }

        if (values[0] != 0.000 || values[1] != 0.000 || values[2] != 0.000 || values[3] != 0.000) {
            updateResult(rootView);
        }
        barChart01.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxSize));

        return rootView;
    }

    View.OnClickListener meuclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            NumberFormat format = new DecimalFormat();
            NumberPickerBuilder dpb = new NumberPickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setLabelText(format.getCurrency().getSymbol())
                    .setTargetFragment(Calculator.this)
                    .setPlusMinusVisibility(View.GONE)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            dpb.show();
        }
    };

    @Override
    public void onDialogNumberSet(int i, int i2, double v, boolean b, double v2) {
        NumberFormat format = new DecimalFormat("0.000");
        TextView updatetxt = getActivity().findViewById(EditUsed);
        updatetxt.setText(format.format(v2));
        updateResult(getView());
    }

    public void setTextStyle(TextView v) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        v.setTypeface(tflcddot);
    }

    public void updateResult(View view) {

        double[] DadosCarro = rootapp.getDataDefaultCar();

        Calculos calc = new Calculos(getActivity());

        if (hasList[0]) {
            price_gas = view.findViewById(R.id.price_gasolina);
            calculoGasolina = calc.CalculaCombustivel(DadosCarro[2], DadosCarro[0], price_gas.getText().toString());
            rootapp.setPrices(SQLiteHelper.LAST_GAS, price_gas.getText().toString());
        } else {
            calculoGasolina = new CalculaCombustivel();
        }
        if (hasList[1]) {
            price_alc = view.findViewById(R.id.price_Etanol);
            calculoEtanol = calc.CalculaCombustivel(DadosCarro[2], DadosCarro[1], price_alc.getText().toString());
            rootapp.setPrices(SQLiteHelper.LAST_ALC, price_alc.getText().toString());
        } else {
            calculoEtanol = new CalculaCombustivel();
        }
        if (hasList[2]) {
            price_gnv = view.findViewById(R.id.price_gnv);
            calculoGNV = calc.CalculaCombustivel(DadosCarro[4], DadosCarro[3], price_gnv.getText().toString());
            rootapp.setPrices(SQLiteHelper.LAST_GNV, price_gnv.getText().toString());
        } else {
            calculoGNV = new CalculaCombustivel();
        }
        if (hasList[3]) {
            price_diesel = view.findViewById(R.id.price_diesel);
            calculoDiesel = calc.CalculaCombustivel(DadosCarro[2], DadosCarro[5], price_diesel.getText().toString());
            rootapp.setPrices(SQLiteHelper.LAST_DIE, price_diesel.getText().toString());
        } else {
            calculoDiesel = new CalculaCombustivel();
        }

        result_combustivel = view.findViewById(R.id.result_combustivel);
        result_autonomia = view.findViewById(R.id.fuel_km_label);
        result_custokm = view.findViewById(R.id.fuel_volum_label);
        result_tq_cheio = view.findViewById(R.id.result_tq_cheio);
        result_calc_type = view.findViewById(R.id.result_calc_type);

        Log.v(getClass().getName(), "CalculoGasolina => " + calculoGasolina.custoKM + " CalculoEtanol => " + calculoEtanol.custoKM);
        Log.v(getClass().getName(), "CalculoGNV => " + calculoGNV.custoKM + " CalculoDiesel => " + calculoDiesel.custoKM);

        result_calc_type.setText(calc.getcalccusto());

        barChart01 = view.findViewById(R.id.chart_result01);
        barChart01.animateY(1000);
        barChart01.setDescription(null);
        barChart01.setNoDataText(getString(R.string.nao_ha_graficos));
        barChart01.setPinchZoom(false);
        barChart01.setScaleYEnabled(false);
        barChart01.setScaleXEnabled(false);
        barChart01.setDoubleTapToZoomEnabled(false);

        barChart01.getLegend().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        barChart01.getXAxis().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        barChart01.getAxisLeft().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        barChart01.getAxisRight().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));


        XAxis xAxis = barChart01.getXAxis();
        xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        barChart01.getAxisRight().setEnabled(false);
        barChart01.getAxisLeft().setSpaceTop(30f);
        barChart01.setData(buildGraph(calculoGasolina, calculoEtanol, calculoGNV, calculoDiesel));
        /* CUSTO */
        if (calc.getcalccusto_id() == 1) {
            if ((calculoGasolina.custoKM < calculoEtanol.custoKM) &&
                    (calculoGasolina.custoKM < calculoGNV.custoKM) &&
                    (calculoGasolina.custoKM < calculoDiesel.custoKM)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.gasolina));
                result_combustivel.setText(R.string.gasolina);
                result_autonomia.setText(calculoGasolina.autonomia);
                result_custokm.setText(calculoGasolina.custoKMRodado);
                result_tq_cheio.setText(calculoGasolina.tanqueCheioValor);
            } else if ((calculoEtanol.custoKM < calculoGasolina.custoKM) &&
                    (calculoEtanol.custoKM < calculoGNV.custoKM) &&
                    (calculoEtanol.custoKM < calculoDiesel.custoKM)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.Etanol));
                result_combustivel.setText(R.string.Etanol);
                result_autonomia.setText(calculoEtanol.autonomia);
                result_custokm.setText(calculoEtanol.custoKMRodado);
                result_tq_cheio.setText(calculoEtanol.tanqueCheioValor);
            } else if ((calculoGNV.custoKM < calculoGasolina.custoKM) &&
                    (calculoGNV.custoKM < calculoEtanol.custoKM) &&
                    (calculoGNV.custoKM < calculoDiesel.custoKM)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.gnv));
                result_combustivel.setText(R.string.gnv);
                result_autonomia.setText(calculoGNV.autonomia);
                result_custokm.setText(calculoGNV.custoKMRodado);
                result_tq_cheio.setText(calculoGNV.tanqueCheioValor);
            } else if ((calculoDiesel.custoKM < calculoGasolina.custoKM) &&
                    (calculoDiesel.custoKM < calculoEtanol.custoKM) &&
                    (calculoDiesel.custoKM < calculoGNV.custoKM)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.diesel));
                result_combustivel.setText(R.string.diesel);
                result_autonomia.setText(calculoDiesel.autonomia);
                result_custokm.setText(calculoDiesel.custoKMRodado);
                result_tq_cheio.setText(calculoDiesel.tanqueCheioValor);
            }
            /* AUTONOMIA */
        } else if (calc.getcalccusto_id() == 2) {
            if ((calculoGasolina.custoAuto > calculoEtanol.custoAuto) &&
                    (calculoGasolina.custoAuto > calculoGNV.custoAuto) &&
                    (calculoGasolina.custoAuto > calculoDiesel.custoAuto)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.gasolina));
                result_combustivel.setText(R.string.gasolina);
                result_autonomia.setText(calculoGasolina.autonomia);
                result_custokm.setText(calculoGasolina.custoKMRodado);
                result_tq_cheio.setText(calculoGasolina.tanqueCheioValor);
            } else if ((calculoEtanol.custoAuto > calculoGasolina.custoAuto) &&
                    (calculoEtanol.custoAuto > calculoGNV.custoAuto) &&
                    (calculoEtanol.custoAuto > calculoDiesel.custoAuto)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.Etanol));
                result_combustivel.setText(R.string.Etanol);
                result_autonomia.setText(calculoEtanol.autonomia);
                result_custokm.setText(calculoEtanol.custoKMRodado);
                result_tq_cheio.setText(calculoEtanol.tanqueCheioValor);
            } else if ((calculoGNV.custoAuto > calculoGasolina.custoAuto) &&
                    (calculoGNV.custoAuto > calculoEtanol.custoAuto) &&
                    (calculoGNV.custoAuto > calculoDiesel.custoAuto)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.gnv));
                result_combustivel.setText(R.string.gnv);
                result_autonomia.setText(calculoGNV.autonomia);
                result_custokm.setText(calculoGNV.custoKMRodado);
                result_tq_cheio.setText(calculoGNV.tanqueCheioValor);
            } else if ((calculoDiesel.custoAuto > calculoGasolina.custoAuto) &&
                    (calculoDiesel.custoAuto > calculoEtanol.custoAuto) &&
                    (calculoDiesel.custoAuto > calculoGNV.custoAuto)) {
                result_combustivel.setTextColor(ContextCompat.getColor(getActivity(), R.color.diesel));
                result_combustivel.setText(R.string.diesel);
                result_autonomia.setText(calculoDiesel.autonomia);
                result_custokm.setText(calculoDiesel.custoKMRodado);
                result_tq_cheio.setText(calculoDiesel.tanqueCheioValor);
            }
        }
    }

    protected BarData buildGraph(CalculaCombustivel gas, CalculaCombustivel alc, CalculaCombustivel gnv, CalculaCombustivel dies) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> v1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> v2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> v3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> v4 = new ArrayList<BarEntry>();
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        if (gas.custoAuto != -1) {
            xVals.add(getString(R.string.gasolina));
            v1.add(new BarEntry((float) gas.vAuto, 0));
            v2.add(new BarEntry((float) gas.custoKM, 0));
            v3.add(new BarEntry((float) gas.tqcheio, 0));
        }
        if (alc.custoAuto != -1) {
            xVals.add(getString(R.string.Etanol));
            v1.add(new BarEntry((float) alc.vAuto, 1));
            v2.add(new BarEntry((float) alc.custoKM, 1));
            v3.add(new BarEntry((float) alc.tqcheio, 1));
        }
        if (gnv.custoAuto != -1) {
            xVals.add(getString(R.string.gnv));
            v1.add(new BarEntry((float) gnv.vAuto, 2));
            v2.add(new BarEntry((float) gnv.custoKM, 2));
            v3.add(new BarEntry((float) gnv.tqcheio, 2));
        }
        if (dies.custoAuto != -1) {
            xVals.add(getString(R.string.diesel));
            v1.add(new BarEntry((float) dies.vAuto, 3));
            v2.add(new BarEntry((float) dies.custoKM, 3));
            v3.add(new BarEntry((float) dies.tqcheio, 3));
        }

        if (!v1.isEmpty()) {
            BarDataSet set1 = new BarDataSet(v1, getString(R.string.autonomia));
            set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.DISTANCE));
            set1.setColor(ContextCompat.getColor(getActivity(), R.color.autonomia));
            dataSets.add(set1);
        }
        if (!v2.isEmpty()) {
            BarDataSet set1 = new BarDataSet(v2, getString(R.string.custo_km, valores.getOdometro()));
            set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY_CALC));
            set1.setColor(ContextCompat.getColor(getActivity(), R.color.custo));
            dataSets.add(set1);
        }
        if (!v3.isEmpty()) {
            BarDataSet set1 = new BarDataSet(v3, getString(R.string.tanque_cheio));
            set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY));
            set1.setColor(ContextCompat.getColor(getActivity(), R.color.tqcheio));
            dataSets.add(set1);
        }

        BarData data = new BarData(xVals, dataSets);

        data.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        data.setValueTextSize(11f);
        data.setGroupSpace(40f);
        return data;
    }

    protected int graphSize(int size) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }
}
