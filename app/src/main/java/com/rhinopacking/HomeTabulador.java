package com.rhinopacking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rhinopacking.adapters.TabuladorListAdapter;
import com.rhinopacking.includes.Toolbar;
import com.rhinopacking.models.Tabulador;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeTabulador extends AppCompatActivity {

    private List<Tabulador> listTabulador;
    Tabulador mTabulador;
    Button btnAdd;

    EditText etN1, etN2, etN3, etDolar;

    TextView tvFecha, tvTotalPulgadas, tvTotalDividido, tvTotalPrecio;

    TabuladorListAdapter tabuladorListAdapter;

    private RecyclerView mRecyclerTabulador;

    long suma;

    double totalDividido;

    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tabulador);
        Toolbar.show(this, true, "Tabulador");

        declaration();
        fecha();
        listener();

    }

    @SuppressLint("SetTextI18n")
    private void fecha() {
        Calendar c1 = Calendar.getInstance();
        tvFecha.setText(c1.get(Calendar.DAY_OF_MONTH) + "/" +
                (c1.get(Calendar.MONTH) +1) + "/"
        +c1.get(Calendar.YEAR));
    }

    @SuppressLint("SetTextI18n")
    private void listener() {

        btnAdd.setOnClickListener(view -> {
            if(etN1.getText().toString().equals("") || etN2.getText().toString().equals("") || etN3.getText().toString().equals(""))
            {
                Toast.makeText(this, "Hay campos vacíos", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mTabulador = new Tabulador(Integer.parseInt(etN1.getText().toString()), Integer.parseInt(etN2.getText().toString()), Integer.parseInt(etN3.getText().toString()));
                listTabulador.add(mTabulador);

                tabuladorListAdapter = new TabuladorListAdapter(listTabulador, this);

                mRecyclerTabulador.setHasFixedSize(true);
                mRecyclerTabulador.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerTabulador.setAdapter(tabuladorListAdapter);

                tabuladorListAdapter.setOnItemClickListener(position -> removeItem(position));


                calcular();


                etN1.setText("");
                etN2.setText("");
                etN3.setText("");
                etN1.requestFocus();
                UIUtil.hideKeyboard(HomeTabulador.this); //ESCONDER TECLADO
            }
        });



        etDolar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calcularUSD();
            }
        });
    }



    private void declaration() {
        btnAdd = findViewById(R.id.btnAdd);
        etN1 = findViewById(R.id.etN1);
        etN2 = findViewById(R.id.etN2);
        etN3 = findViewById(R.id.etN3);
        tvTotalPulgadas = findViewById(R.id.tvTotalPulgadas);
        tvTotalDividido = findViewById(R.id.tvTotalDividido);
        etDolar = findViewById(R.id.etDolar);
        tvTotalPrecio = findViewById(R.id.tvTotalPrecio);
        tvFecha = findViewById(R.id.tvFecha);

        suma = 0;

        mRecyclerTabulador = findViewById(R.id.mRecyclerTabulador);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerTabulador.setLayoutManager(linearLayoutManager);

        listTabulador = new ArrayList<>();

        spinner = findViewById(R.id.spinner);

        List<String> formula = new ArrayList<>();

        formula.add("120,000");
        formula.add("135,744");

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String>adaptador1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formula);
        spinner.setAdapter(adaptador1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    posicion = position;
                    calcularDividido();
                }

                else if (position == 1)
                {
                    posicion = position;
                    calcularDividido();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    int posicion=0;

    @SuppressLint("SetTextI18n")
    public void removeItem(int position) {
        listTabulador.remove(position);
        tabuladorListAdapter.notifyItemRemoved(position);

        calcular();

        mRecyclerTabulador.setHasFixedSize(true);
    }



    private void calcular()
    {
        long suma=0;
        for (int i=0;i<listTabulador.size();i++){

            suma = suma + listTabulador.get(i).getR();

        }
        tvTotalPulgadas.setText(""+suma);


        calcularDividido();

    }

    private void calcularUSD(){
        if(!tvTotalPulgadas.getText().toString().equals("0") && !etDolar.getText().toString().equals("") && !etDolar.getText().toString().equals("0"))
        {
            double totalDolar=totalDividido*Double.parseDouble(etDolar.getText().toString());
            tvTotalPrecio.setText(totalDolar + "");
        }
        else
            tvTotalPrecio.setText("0");
    }

    private void calcularDividido() {
        if(!tvTotalPulgadas.getText().toString().equals("0")){
            totalDividido=0;
            if(posicion==0)
                totalDividido = Double.parseDouble(tvTotalPulgadas.getText().toString())/120000;
            else if(posicion==1)
                totalDividido = Double.parseDouble(tvTotalPulgadas.getText().toString())/135744;

            tvTotalDividido.setText("" + totalDividido);
        }
        else if(tvTotalPulgadas.getText().toString().equals("0"))
            tvTotalDividido.setText("0");

        calcularUSD();
    }


    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        finish();
    }
    //BACK PRESS
}