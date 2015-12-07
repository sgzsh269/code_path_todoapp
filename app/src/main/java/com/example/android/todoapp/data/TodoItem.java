package com.example.android.todoapp.data;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TodoItem implements Parcelable {

    private long dbId;
    private String desc;
    private Date dueDate;
    private String timeleft;
    private String priority;
    private Boolean isCompleted;
    private Boolean needsReminder;
    private int reminderDays;
    private Date reminderDate;

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TodoItem() {
    }

    public TodoItem(String desc, Date dueDate, String priority, Boolean isCompleted, Boolean needsReminder, int reminderDays) {
        setDbId(-1);
        setDesc(desc);
        setDueDate(dueDate);
        setPriority(priority);
        isCompleted(isCompleted);
        needsReminder(needsReminder);
        setReminderDays(reminderDays);
    }

    protected TodoItem(Parcel in) {
        try {
            setDesc(in.readString());
            setDueDate(in.readString());
            setPriority(in.readString());
            isCompleted(Boolean.parseBoolean(in.readString()));
            needsReminder(Boolean.parseBoolean(in.readString()));
            setReminderDays(Integer.parseInt(in.readString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Priority {
        HIGH(0, Color.parseColor("#E57373")),
        MEDIUM(1, Color.parseColor("#FFEE58")),
        LOW(2, Color.TRANSPARENT);

        private int index;
        private int color;

        Priority(int index, int color) {
            this.index = index;
            this.color = color;
        }

        public int getIndex() {
            return index;
        }

        public int getColor() {
            return color;
        }

    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    private void calculateTimeleft() {
        Date currDate = new Date();
        long diff = dueDate.getTime() - currDate.getTime() + (1000 * 60 * 60 * 24);
        long timeleftDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        long timeleftHours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
        String timeleft;
        if (timeleftDays > 0) {
            timeleft = String.format("%sd", timeleftDays);
        } else {
            timeleft = String.format("%sh", timeleftHours);
        }
        this.timeleft = timeleft;
    }

    private void calculateReminderDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        cal.add(Calendar.DATE, -reminderDays);
        reminderDate = cal.getTime();
    }

    private void calculateReminderDays() {
        long diff = dueDate.getTime() - reminderDate.getTime();
        reminderDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(desc);
        String strDueDate = getStrDueDate();
        dest.writeString(strDueDate);
        dest.writeString(priority);
        dest.writeString(isCompleted.toString());
        dest.writeString(needsReminder.toString());
        dest.writeString(String.valueOf(reminderDays));
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getStrDueDate() {
        return simpleDateFormat.format(dueDate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        calculateTimeleft();
    }

    public void setDueDate(String dueDate) throws ParseException {
        setDueDate(simpleDateFormat.parse(dueDate));
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void isCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void isCompleted(int isCompleted) {
        isCompleted(isCompleted == 1);
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String getTimeleft() {
        return timeleft;
    }

    public void setReminderDays(int reminderDays) {
        this.reminderDays = reminderDays;
        calculateReminderDate();
    }

    public int getReminderDays() {
        return reminderDays;
    }

    public String getStrReminderDate() {
        return simpleDateFormat.format(reminderDate);

    }

    public Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) throws ParseException {
        this.reminderDate = simpleDateFormat.parse(reminderDate);
        calculateReminderDays();
    }

    public Boolean needsReminder() {
        return needsReminder;
    }

    public void needsReminder(Boolean needsReminder) {
        this.needsReminder = needsReminder;
    }

    public void needsReminder(int needsReminder) {
        needsReminder(needsReminder == 1);
    }
}
