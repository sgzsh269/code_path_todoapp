package com.example.android.todoapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.todoapp.data.TodoAppDBHelper;
import com.example.android.todoapp.data.TodoItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddEditItemFragment.OnTodoSaveListener, TodoPageFragment.ActivityInterface {


    private TodoAppDBHelper mTodoAppDBHelper;

    private ArrayList<TodoItem> mTodoItems;
    private ArrayList<TodoItem> mCompletedItems;

    private TodoItemAdapter mTodoItemAdapter;
    private CompletedItemAdapter mCompletedItemAdapter;

    private AddEditItemFragment mAddEditFragment;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TodoPageFragmentAdapter mTodoPagerFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.logo_todo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTodoItems = new ArrayList<>();
        mCompletedItems = new ArrayList<>();
        mTodoAppDBHelper = TodoAppDBHelper.getInstance(this);
        mTodoItemAdapter = new TodoItemAdapter(this, mTodoItems);
        mCompletedItemAdapter = new CompletedItemAdapter(this, mCompletedItems);
        mTodoPagerFragmentAdapter = new TodoPageFragmentAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TodoPageFragment todoPageFragment = (TodoPageFragment) mTodoPagerFragmentAdapter.instantiateItem(mViewPager, position);
                todoPageFragment.populateTodoItems();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager.setAdapter(mTodoPagerFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_edit_todo:
                showAddEditDialog(-1, mTodoItemAdapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addUpdateTodoItem(TodoItem todoItem, TodoItemAdapter todoItemAdapter) {
        mTodoAppDBHelper.addOrUpdateTodoItem(todoItem);
        populateTodoItems(todoItemAdapter);
    }

    @Override
    public void deleteTodoItem(int position, TodoItemAdapter todoItemAdapter) {
        TodoItem todoItem = todoItemAdapter.getList().get(position);
        todoItemAdapter.getList().remove(position);
        todoItemAdapter.notifyDataSetChanged();
        mTodoAppDBHelper.deleteTodoItem(todoItem);
    }

    @Override
    public TodoItemAdapter getTodoItemAdapter(boolean isCompletedFragment) {
        if (isCompletedFragment) {
            return mCompletedItemAdapter;
        } else {
            return mTodoItemAdapter;
        }
    }

    @Override
    public void populateTodoItems(TodoItemAdapter todoItemAdapter) {
        todoItemAdapter.getList().clear();
        todoItemAdapter.addAll(mTodoAppDBHelper.getSortedTodoItems(todoItemAdapter.isCompleted()));
        todoItemAdapter.notifyDataSetChanged();
    }

    public void showAddEditDialog(int position, TodoItemAdapter todoItemAdapter) {
        FragmentManager fm = getSupportFragmentManager();
        mAddEditFragment = AddEditItemFragment.newInstance();
        mAddEditFragment.setTodoItemAdapter(todoItemAdapter);
        if (position != -1) {
            TodoItem todoItem = todoItemAdapter.getList().get(position);
            Bundle args = new Bundle();
            args.putParcelable("todoItem", todoItem);
            args.putInt("position", position);
            mAddEditFragment.setArguments(args);
        }
        mAddEditFragment.show(fm, "fragmentAddEditItem");
    }

    @Override
    public void onTodoSave(TodoItem todoItem, TodoItemAdapter todoItemAdapter) {
        addUpdateTodoItem(todoItem, todoItemAdapter);
    }

    public class TodoItemAdapter extends ArrayAdapter<TodoItem> {

        private ArrayList<TodoItem> mTodoItems;

        public TodoItemAdapter(Context context, ArrayList<TodoItem> todoItems) {
            super(context, 0, todoItems);
            mTodoItems = todoItems;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            TodoItem todoItem = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_todo, parent, false);
            }

            TextView lvTvTodoDesc = (TextView) convertView.findViewById(R.id.lv_tv_todo_desc);
            TextView lvTvTodoDueDays = (TextView) convertView.findViewById(R.id.lv_tv_todo_due_days);
            CheckBox lvCheckboxTodoCompleted = (CheckBox) convertView.findViewById(R.id.lv_checkbox_todo_completed);
            lvCheckboxTodoCompleted.setChecked(false);

            lvTvTodoDesc.setText(todoItem.getDesc());
            lvTvTodoDueDays.setText(todoItem.getTimeleft());

            lvCheckboxTodoCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox)v;
                    TodoItem todoItem = mTodoItems.get(position);
                    todoItem.isCompleted(checkBox.isChecked());
                    addUpdateTodoItem(todoItem, TodoItemAdapter.this);
                }
            });

            convertView.setBackgroundColor(TodoItem.Priority.valueOf(todoItem.getPriority()).getColor());

            return convertView;
        }

        public ArrayList<TodoItem> getList() {
            return mTodoItems;
        }

        public boolean isCompleted() {
            return false;
        }

    }

    private class CompletedItemAdapter extends TodoItemAdapter {

        public CompletedItemAdapter(Context context, ArrayList<TodoItem> todoItems) {
            super(context, todoItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TodoItem todoItem = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_todo, parent, false);
            }

            TextView lvTvTodoDesc = (TextView) convertView.findViewById(R.id.lv_tv_todo_desc);
            LinearLayout llTimeLeft = (LinearLayout) convertView.findViewById(R.id.ll_timeleft);
            ((LinearLayout) convertView).removeView(llTimeLeft);
            CheckBox lvCheckboxTodoCompleted = (CheckBox) convertView.findViewById(R.id.lv_checkbox_todo_completed);
            ((LinearLayout)convertView).removeView(lvCheckboxTodoCompleted);

            lvTvTodoDesc.setText(todoItem.getDesc());

            convertView.setBackgroundColor(TodoItem.Priority.valueOf(todoItem.getPriority()).getColor());

            return convertView;
        }

        @Override
        public boolean isCompleted() {
            return true;
        }

    }

    private class TodoPageFragmentAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String TAB_TITLES[] = new String[]{"To do", "Completed"};


        public TodoPageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return TodoPageFragment.newInstance(false);
            } else {
                return TodoPageFragment.newInstance(true);
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }
    }

}
