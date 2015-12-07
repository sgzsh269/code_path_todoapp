package com.example.android.todoapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.android.todoapp.data.TodoItem;

import java.text.ParseException;
import java.util.Date;

public class AddEditItemFragment extends DialogFragment {

    private EditText mEtTodoDesc;
    private Spinner mSpinnerTodoPriority;
    private ImageView mIvIconExit;
    private ImageView mIvIconSave;
    private DatePicker mDatePickerTodoDate;
    private CheckBox mCheckboxTodoCompleted;
    private ToggleButton mTBtnReminder;
    private LinearLayout mLlSetReminder;
    private EditText mEtReminderDays;

    private TodoItem mTodoItem;

    private OnTodoSaveListener mOnTodoSaveListener;

    private MainActivity.TodoItemAdapter mTodoItemAdapter;

    public AddEditItemFragment() {
    }

    public interface OnTodoSaveListener {

        void onTodoSave(TodoItem todoItem, MainActivity.TodoItemAdapter todoItemAdapter);
    }

    public static AddEditItemFragment newInstance() {
        return new AddEditItemFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnTodoSaveListener = (OnTodoSaveListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_add_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatePickerTodoDate = (DatePicker) view.findViewById(R.id.datepicker_todo_date);
        mSpinnerTodoPriority = (Spinner) view.findViewById(R.id.spinner_todo_priority);
        mEtTodoDesc = (EditText) view.findViewById(R.id.et_todo_desc);
        mIvIconSave = (ImageView) view.findViewById(R.id.iv_icon_save);
        mIvIconExit = (ImageView) view.findViewById(R.id.iv_icon_exit);
        mCheckboxTodoCompleted = (CheckBox) view.findViewById(R.id.checkbox_todo_completed);
        mTBtnReminder = (ToggleButton) view.findViewById(R.id.tbtn_reminder);
        mLlSetReminder = (LinearLayout) view.findViewById(R.id.ll_set_reminder);
        mEtReminderDays = (EditText) view.findViewById(R.id.et_todo_reminder_days);

        mEtTodoDesc.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        mIvIconExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        mIvIconSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoDesc = mEtTodoDesc.getText().toString();
                Boolean isCompleted = mCheckboxTodoCompleted.isChecked();
                int dayOfMonth = mDatePickerTodoDate.getDayOfMonth();
                int month = mDatePickerTodoDate.getMonth() + 1;
                int year = mDatePickerTodoDate.getYear();
                String strDueDate = String.format("%s-%s-%s", year, month, dayOfMonth);
                String priority = mSpinnerTodoPriority.getSelectedItem().toString();
                Date dueDate = null;
                try {
                    dueDate = TodoItem.simpleDateFormat.parse(strDueDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Boolean needsReminder = mTBtnReminder.isChecked();
                int reminderDays = Integer.parseInt(mEtReminderDays.getText().toString());

                if (mTodoItem == null) {
                    mTodoItem = new TodoItem(todoDesc, dueDate, priority, isCompleted, needsReminder, reminderDays);
                } else {
                    mTodoItem.setDesc(todoDesc);
                    mTodoItem.setDueDate(dueDate);
                    mTodoItem.setPriority(priority);
                    mTodoItem.isCompleted(isCompleted);
                    mTodoItem.needsReminder(needsReminder);
                    mTodoItem.setReminderDays(reminderDays);
                }
                mOnTodoSaveListener.onTodoSave(mTodoItem, mTodoItemAdapter);
                dismiss();
            }
        });

        mTBtnReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLlSetReminder.setVisibility(View.VISIBLE);
                } else {
                    mLlSetReminder.setVisibility(View.INVISIBLE);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTodoPriority.setAdapter(adapter);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("todoItem")) {
            mTodoItem = getArguments().getParcelable("todoItem");
            mEtTodoDesc.setText(mTodoItem.getDesc());
            mDatePickerTodoDate.updateDate(mTodoItem.getDueDate().getYear() + 1900, mTodoItem.getDueDate().getMonth(), mTodoItem.getDueDate().getDate());
            mSpinnerTodoPriority.setSelection(TodoItem.Priority.valueOf(mTodoItem.getPriority()).getIndex());
            mCheckboxTodoCompleted.setChecked(mTodoItem.isCompleted());
            mTBtnReminder.setChecked(mTodoItem.needsReminder());
            mEtReminderDays.setText(String.valueOf(mTodoItem.getReminderDays()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void setTodoItemAdapter(MainActivity.TodoItemAdapter todoItemAdapter) {
        mTodoItemAdapter = todoItemAdapter;
    }


}
