package com.example.android.todoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    private EditText etEditItem;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etEditItem = (EditText) findViewById(R.id.etEditItem);

        String listItem = getIntent().getStringExtra("listItem");
        etEditItem.setText(listItem);
        etEditItem.requestFocus();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void saveItem(View view) {
        imm.hideSoftInputFromWindow(etEditItem.getWindowToken(), 0);
        Intent data = new Intent();
        data.putExtra("editedItem", etEditItem.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }
}
