package com.example.android.todoapp.data;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TodoItem implements Parcelable {

    private long dbId;
    private String desc;
    private Date dueDate;
    private String timeleft;
    private String priority;
    private Boolean isCompleted;

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TodoItem() {
    }

    public TodoItem(String desc, Date dueDate, String priority, Boolean isCompleted) {
        setDbId(-1);
        setDesc(desc);
        setDueDate(dueDate);
        setPriority(priority);
        isCompleted(isCompleted);

    }

    protected TodoItem(Parcel in) {
        try {
            setDesc(in.readString());
            setDueDate(in.readString());
            setPriority(in.readString());
            isCompleted(Boolean.parseBoolean(in.readString()));
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
}
