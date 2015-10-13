package com.example.android.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> todoItems;
    private ArrayAdapter<String> todoAdapter;
    private ListView lvItems;
    private EditText etText;
    private int editedPosition = 0;

    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        populateArrayItem();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(todoAdapter);
        etText = (EditText) findViewById(R.id.etText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("listItem", todoItems.get(position));
                editedPosition = position;
                startActivityForResult(i, REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String editedItem = data.getExtras().getString("editedItem");
            todoItems.set(editedPosition, editedItem);
            todoAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    public void populateArrayItem() {
        readItems();
        todoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    private void readItems() {
        File fileDir = getFilesDir();
        File file = new File(fileDir, "todo.txt");
        try {
            todoItems = new ArrayList<String>(FileUtils.readLines(file));
        } catch (IOException e) {
            // No file throws an IO Exception, thus have to instantiate the ArrayList in the catch
            todoItems = new ArrayList<String>();
        }

    }

    private void writeItems() {
        File fileDir = getFilesDir();
        File file = new File(fileDir, "todo.txt");
        try {
            FileUtils.writeLines(file, todoItems);
        } catch (IOException e) {

        }

    }

    public void addItem(View view) {
        todoAdapter.add(etText.getText().toString());
        etText.setText("");
        writeItems();
    }
}
