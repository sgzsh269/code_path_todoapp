package com.example.android.todoapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class TodoAppDBHelper extends SQLiteOpenHelper {

    private static TodoAppDBHelper sTodoAppDBHelper;
    private static final String DATABASE_NAME = "todoDB";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_TODO = "todo";

    private static final String KEY_TODO_ID = "id";
    private static final String KEY_DESC = "desc";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_IS_COMPLETED = "is_completed";
    private static final String KEY_NEEDS_REMINDER = "needs_reminder";
    private static final String KEY_REMINDER_DATE = "reminder_date";

    private TodoAppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static synchronized TodoAppDBHelper getInstance(Context context) {
        if (sTodoAppDBHelper == null) {
            sTodoAppDBHelper = new TodoAppDBHelper(context.getApplicationContext());
        }
        return sTodoAppDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "" +
                "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," +
                KEY_DESC + " TEXT," +
                KEY_DUE_DATE + " TEXT," +
                KEY_PRIORITY + " TEXT," +
                KEY_IS_COMPLETED + " INTEGER," +
                KEY_NEEDS_REMINDER + " INTEGER," +
                KEY_REMINDER_DATE + " TEXT" +
                ")";

        db.execSQL(CREATE_TODO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(db);
        }
    }

    public void addOrUpdateTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_DESC, todoItem.getDesc());
            values.put(KEY_DUE_DATE, todoItem.getStrDueDate());
            values.put(KEY_PRIORITY, todoItem.getPriority());
            values.put(KEY_IS_COMPLETED, todoItem.isCompleted());
            values.put(KEY_NEEDS_REMINDER, todoItem.needsReminder());
            values.put(KEY_REMINDER_DATE, todoItem.getStrReminderDate());

            if (todoItem.getDbId() == -1) {
                long dbId = db.insertOrThrow(TABLE_TODO, null, values);
                todoItem.setDbId(dbId);
            } else {
                int rows = db.update(TABLE_TODO, values, KEY_TODO_ID + "= ?", new String[]{String.valueOf(todoItem.getDbId())});
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO, KEY_TODO_ID + "= ?", new String[]{String.valueOf(todoItem.getDbId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<TodoItem> getSortedTodoItems(Boolean isCompleted) {
        ArrayList<TodoItem> todoItems = new ArrayList<>();

        String TODO_ITEMS_QUERY =
                String.format("SELECT * FROM %s " +
                                "WHERE %s = %d " +
                                "ORDER BY " +
                                "CASE " +
                                "WHEN %s = 'HIGH' " +
                                "THEN 0 " +
                                "ELSE " +
                                "(CASE WHEN %s = 'MEDIUM' THEN 1 ELSE 2 END)" +
                                "END ASC," +
                                "%s ASC",
                        TABLE_TODO,
                        KEY_IS_COMPLETED,
                        isCompleted ? 1 : 0,
                        KEY_PRIORITY,
                        KEY_PRIORITY,
                        KEY_DUE_DATE);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_ITEMS_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    TodoItem todoItem = new TodoItem();
                    todoItem.setDbId(cursor.getLong(cursor.getColumnIndex(KEY_TODO_ID)));
                    todoItem.setDesc(cursor.getString(cursor.getColumnIndex(KEY_DESC)));
                    todoItem.setDueDate(cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE)));
                    todoItem.setPriority(cursor.getString(cursor.getColumnIndex(KEY_PRIORITY)));
                    todoItem.isCompleted(cursor.getInt(cursor.getColumnIndex(KEY_IS_COMPLETED)));
                    todoItem.needsReminder(cursor.getInt(cursor.getColumnIndex(KEY_NEEDS_REMINDER)));
                    todoItem.setReminderDate(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_DATE)));

                    todoItems.add(todoItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todoItems;
    }

    public ArrayList<TodoItem> getReminderItems() {
        ArrayList<TodoItem> todoItems = new ArrayList<>();

        String currDate = TodoItem.simpleDateFormat.format(new Date());

        String TODO_ITEMS_QUERY =
                String.format("SELECT * FROM %s " +
                                "WHERE %s = 0 AND %s = 1 AND %s = '%s'" +
                                "ORDER BY " +
                                "CASE " +
                                "WHEN %s = 'HIGH' " +
                                "THEN 0 " +
                                "ELSE " +
                                "(CASE WHEN %s = 'MEDIUM' THEN 1 ELSE 2 END)" +
                                "END DESC," +
                                "%s DESC",
                        TABLE_TODO,
                        KEY_IS_COMPLETED,
                        KEY_NEEDS_REMINDER,
                        KEY_REMINDER_DATE,
                        currDate,
                        KEY_PRIORITY,
                        KEY_PRIORITY,
                        KEY_DUE_DATE);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_ITEMS_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    TodoItem todoItem = new TodoItem();
                    todoItem.setDbId(cursor.getLong(cursor.getColumnIndex(KEY_TODO_ID)));
                    todoItem.setDesc(cursor.getString(cursor.getColumnIndex(KEY_DESC)));
                    todoItem.setDueDate(cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE)));
                    todoItem.setPriority(cursor.getString(cursor.getColumnIndex(KEY_PRIORITY)));
                    todoItem.isCompleted(cursor.getInt(cursor.getColumnIndex(KEY_IS_COMPLETED)));
                    todoItem.needsReminder(cursor.getInt(cursor.getColumnIndex(KEY_NEEDS_REMINDER)));
                    todoItem.setReminderDate(cursor.getString(cursor.getColumnIndex(KEY_REMINDER_DATE)));

                    todoItems.add(todoItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todoItems;
    }

}
