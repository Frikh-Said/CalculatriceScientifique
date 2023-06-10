package com.example.calculatricescientifique;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class HistoriqueActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    DatabaseHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("History");
        }

        linearLayout = findViewById(R.id.linearLayout);
        dbHelper = new DatabaseHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseHelper.COLUMN_EXPRESSION, DatabaseHelper.COLUMN_RESULT};
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_HISTORY,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String expression = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPRESSION));
            double result = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESULT));

            createTextView(expression, result);
        }

        MaterialButton clearButton = findViewById(R.id.bHistorique);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
                linearLayout.removeAllViews();
            }
        });

        cursor.close();
    }

    private void createTextView(final String expression, double result) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(100)
        );
        params.weight = 1;

        TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(Color.WHITE);
        textView.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        textView.setGravity(Gravity.END);
        textView.setTextColor(Color.parseColor("#A8A8A8"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        String text = expression + " = " + result;
        textView.setText(text);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("expression", expression);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        linearLayout.addView(textView);
    }

    private void clearHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_HISTORY, null, null);
        db.close();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

