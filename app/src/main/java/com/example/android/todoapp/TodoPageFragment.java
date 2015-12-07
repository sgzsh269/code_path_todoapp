package com.example.android.todoapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

public class TodoPageFragment extends Fragment {

    private ListView mLvItems;
    private ActivityInterface mActivity;
    private Boolean mIsCompletedFragment;
    private MainActivity.TodoItemAdapter mTodoItemAdapter;

    public static TodoPageFragment newInstance(Boolean isCompletedFragment) {
        TodoPageFragment todoPageFragment = new TodoPageFragment();
        Bundle args = new Bundle();
        args.putBoolean("isCompletedFragment", isCompletedFragment);
        todoPageFragment.setArguments(args);
        return todoPageFragment;
    }

    public interface ActivityInterface {

        MainActivity.TodoItemAdapter getTodoItemAdapter(boolean isCompletedFragment);

        void populateTodoItems(MainActivity.TodoItemAdapter todoItemAdapter);

        void deleteTodoItem(int position, MainActivity.TodoItemAdapter todoItemAdapter);

        void showAddEditDialog(int position, MainActivity.TodoItemAdapter todoItemAdapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ActivityInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mIsCompletedFragment = getArguments().getBoolean("isCompletedFragment");
        mTodoItemAdapter = mActivity.getTodoItemAdapter(mIsCompletedFragment);
        return inflater.inflate(R.layout.fragment_page_todo_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLvItems = (ListView) view.findViewById(R.id.lvItems);
        mLvItems.setAdapter(mTodoItemAdapter);
        mLvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.deleteTodoItem(position, mTodoItemAdapter);
                return true;
            }
        });
        mLvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox lvCheckboxTodoCompleted = (CheckBox) view.findViewById(R.id.lv_checkbox_todo_completed);
                mActivity.showAddEditDialog(position, mTodoItemAdapter);
            }
        });


        populateTodoItems();
    }

    public void populateTodoItems() {
        mActivity.populateTodoItems(mTodoItemAdapter);
    }
}
