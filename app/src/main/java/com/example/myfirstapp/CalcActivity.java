package com.example.myfirstapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utils.MathUtils;


public class CalcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        EditText etNumber1 = findViewById(R.id.etNumber1);
        EditText etNumber2 = findViewById(R.id.etNumber2);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvResult);

        btnCalculate.setOnClickListener(v -> {
            String n1Str = etNumber1.getText().toString();
            String n2Str = etNumber2.getText().toString();

            if (n1Str.isEmpty() || n2Str.isEmpty()) {
                tvResult.setText("Введите оба числа");
                return;
            }

            int n1 = Integer.parseInt(n1Str);
            int n2 = Integer.parseInt(n2Str);

            int sum = MathUtils.add(n1, n2);

            tvResult.setText("Результат: " + sum);
        });
    }
}
