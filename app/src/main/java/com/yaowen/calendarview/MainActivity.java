package com.yaowen.calendarview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 日历视图
 *
 * @author YAOWEN
 * @explain
 * @date 2015-10-23
 */
public class MainActivity extends AppCompatActivity {
    private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();
    private Calendar calStartDate = Calendar.getInstance();
    private Calendar calToday = Calendar.getInstance();
    private Calendar calCalendar = Calendar.getInstance();
    private Calendar calSelected = Calendar.getInstance();
    private LinearLayout layContent = null;
    private int iFirstDayOfWeek = Calendar.SUNDAY;
    private int iMonthViewCurrentMonth = 0;
    private int iMonthViewCurrentYear = 0;
    public static final int SELECT_DAY_REQUEST = 111;
    private static final int iDayCellSize = 38;
    private static final int iDayHeaderHeight = 34;
    private static final int iDayCellHeight = 34;
    private TextView yearTextView, monthTextView;
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        iFirstDayOfWeek = Calendar.SUNDAY;
        mYear = calSelected.get(Calendar.YEAR);
        mMonth = calSelected.get(Calendar.MONTH);
        mDay = calSelected.get(Calendar.DAY_OF_MONTH);
        setContentView(generateContentView());
        calStartDate = getCalendarStartDate();
        DateWidgetDayCell daySelected = updateCalendar();
        if (daySelected != null)
            daySelected.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private LinearLayout createLayout(int iOrientation) {
        LinearLayout lay = new LinearLayout(this);
        lay.setLayoutParams(new ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        lay.setOrientation(iOrientation);
        return lay;
    }

    private void generateTopButtons(LinearLayout layTopControls) {
        yearTextView = new TextView(this);
        yearTextView.setText(mYear + "年");
        monthTextView = new TextView(this);
        monthTextView.setText(format(mMonth + 1) + "月");
        Button btnPrevMonth = new Button(this);
        btnPrevMonth.setBackgroundResource(R.mipmap.icon_forword);
        Button btnNextMonth = new Button(this);
        btnNextMonth.setBackgroundResource(R.mipmap.icon_next);
        // set events
        btnPrevMonth.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {
                setPrevMonthViewItem();
            }
        });

        btnNextMonth.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {
                setNextMonthViewItem();
            }
        });
        layTopControls.addView(btnPrevMonth);
        layTopControls.addView(yearTextView);
        layTopControls.addView(monthTextView);
        layTopControls.addView(btnNextMonth);
    }

    private View generateContentView() {
        LinearLayout layMain = createLayout(LinearLayout.VERTICAL);
        layMain.setPadding(8, 8, 8, 8);
        layMain.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
        layTopControls.setLayoutParams(new ViewGroup.LayoutParams(iDayCellSize * 7, ViewGroup.LayoutParams.WRAP_CONTENT));
        layTopControls.setGravity(Gravity.CENTER);

        layContent = createLayout(LinearLayout.VERTICAL);
        layContent.setLayoutParams(new ViewGroup.LayoutParams(iDayCellSize * 7, ViewGroup.LayoutParams.WRAP_CONTENT));
        generateTopButtons(layTopControls);
        generateCalendar(layContent);
        layMain.addView(layTopControls);
        layMain.addView(layContent);
        layMain.setBackgroundColor(Color.WHITE);
        return layMain;
    }

    private View generateCalendarRow() {
        LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
        for (int iDay = 0; iDay < 7; iDay++) {
            DateWidgetDayCell dayCell = new DateWidgetDayCell(this,
                    iDayCellSize, iDayCellHeight);
            dayCell.setItemClick(mOnDayCellClick);
            days.add(dayCell);
            layRow.addView(dayCell);
        }
        return layRow;
    }

    private View generateCalendarHeader() {
        LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
        for (int iDay = 0; iDay < 7; iDay++) {
            DateWidgetDayHeader day = new DateWidgetDayHeader(this,
                    iDayCellSize, iDayHeaderHeight);
            final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
            day.setData(iWeekDay);
            layRow.addView(day);
        }
        return layRow;
    }

    private void generateCalendar(LinearLayout layContent) {
        layContent.addView(generateCalendarHeader());
        days.clear();
        for (int iRow = 0; iRow < 6; iRow++) {
            layContent.addView(generateCalendarRow());
        }
    }

    private Calendar getCalendarStartDate() {
        calToday.setTimeInMillis(System.currentTimeMillis());
        calToday.setFirstDayOfWeek(iFirstDayOfWeek);

        if (calSelected.getTimeInMillis() == 0) {
            calStartDate.setTimeInMillis(System.currentTimeMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        } else {
            calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        }
        updateStartDateForMonth();

        return calStartDate;
    }

    private DateWidgetDayCell updateCalendar() {
        DateWidgetDayCell daySelected = null;
        boolean bSelected = false;
        final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
        final int iSelectedYear = calSelected.get(Calendar.YEAR);
        final int iSelectedMonth = calSelected.get(Calendar.MONTH);
        final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
        calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
        for (int i = 0; i < days.size(); i++) {
            final int iYear = calCalendar.get(Calendar.YEAR);
            final int iMonth = calCalendar.get(Calendar.MONTH);
            final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
            final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
            DateWidgetDayCell dayCell = days.get(i);
            // check today
            boolean bToday = false;
            if (calToday.get(Calendar.YEAR) == iYear)
                if (calToday.get(Calendar.MONTH) == iMonth)
                    if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
                        bToday = true;
            // check holiday
            boolean bHoliday = false;
            if ((iDayOfWeek == Calendar.SATURDAY)
                    || (iDayOfWeek == Calendar.SUNDAY))
                bHoliday = true;
            if ((iMonth == Calendar.JANUARY) && (iDay == 1))
                bHoliday = true;

            dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
                    iMonthViewCurrentMonth, iDayOfWeek);
            bSelected = false;
            if (bIsSelection)
                if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
                        && (iSelectedYear == iYear)) {
                    bSelected = true;
                }
            dayCell.setSelected(bSelected);
            if (bSelected)
                daySelected = dayCell;
            calCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        layContent.invalidate();
        return daySelected;
    }

    private void updateStartDateForMonth() {
        iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
        iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        UpdateCurrentMonthDisplay();
        // update days for week
        int iDay = 0;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
    }


    private void UpdateCurrentMonthDisplay() {
        mYear = calCalendar.get(Calendar.YEAR);
    }


    private void setPrevMonthViewItem() {
        iMonthViewCurrentMonth--;
        if (iMonthViewCurrentMonth == -1) {
            iMonthViewCurrentMonth = 11;
            iMonthViewCurrentYear--;
        }
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
        updateDate();
        updateCenterTextView(iMonthViewCurrentMonth, iMonthViewCurrentYear);
    }

    private void setNextMonthViewItem() {
        iMonthViewCurrentMonth++;
        if (iMonthViewCurrentMonth == 12) {
            iMonthViewCurrentMonth = 0;
            iMonthViewCurrentYear++;
        }
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
        updateDate();
        updateCenterTextView(iMonthViewCurrentMonth, iMonthViewCurrentYear);
    }

    private void setPrevYearViewItem() {
        iMonthViewCurrentYear--;
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
        updateDate();
        updateCenterTextView(iMonthViewCurrentMonth, iMonthViewCurrentYear);
    }

    private void setNextYearViewItem() {
        iMonthViewCurrentYear++;
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
        updateDate();
        updateCenterTextView(iMonthViewCurrentMonth, iMonthViewCurrentYear);
    }

    private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {

        public void OnClick(DateWidgetDayCell item) {
            calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
            item.setSelected(true);
            updateCalendar();


            Intent ret = new Intent();
            ret.putExtra("year", calSelected.get(Calendar.YEAR));
            ret.putExtra("month", calSelected.getTime().getMonth());
            ret.putExtra("day", calSelected.getTime().getDate());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Toast.makeText(MainActivity.this, format.format(calSelected.getTime()),
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void updateCenterTextView(int iMonthViewCurrentMonth, int iMonthViewCurrentYear) {
        yearTextView.setText(iMonthViewCurrentYear + "年");
        monthTextView.setText(format(iMonthViewCurrentMonth + 1) + "月");
    }


    private void updateDate() {
        updateStartDateForMonth();
        updateCalendar();
    }


    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }
}
