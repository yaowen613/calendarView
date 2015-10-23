package com.yaowen.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import java.util.Calendar;


/**
 * WeekHead
 * @author YAOWEN
 * @explain 
 * @date 2015-10-23
 */
public class DateWidgetDayHeader extends View {
	// fields
	private final static int	iDayHeaderFontSize	= 13;

	// fields
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private int iWeekDay = -1;
	private boolean bHoliday = false;

	// methods
	public DateWidgetDayHeader(Context context, int iWidth, int iHeight) {
		super(context);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
	}

	public void setData(int iWeekDay) {
		this.iWeekDay = iWeekDay;
		this.bHoliday = false;
		if ((iWeekDay == Calendar.SATURDAY) || (iWeekDay == Calendar.SUNDAY))
			this.bHoliday = true;
	}

	private void drawDayHeader(Canvas canvas) {
		if (iWeekDay != -1) {
			// background
			pt.setColor(DayStyle.getColorFrameHeader(bHoliday));
			canvas.drawRect(rect, pt);

			// text
			pt.setTypeface(null);
			pt.setTextSize(iDayHeaderFontSize);
			pt.setAntiAlias(true);
			pt.setFakeBoldText(true);
			pt.setColor(DayStyle.getColorTextHeader(bHoliday,iWeekDay));

			final int iTextPosY = getTextHeight();
			final String sDayName = DayStyle.getWeekDayName(iWeekDay);

			// draw day name
			final int iDayNamePosX = (int) rect.left
					+ ((int) rect.width() >> 1)
					- ((int) pt.measureText(sDayName) >> 1);
			canvas.drawText(sDayName, iDayNamePosX, rect.top + iTextPosY + 2,
					pt);
		}
		//this.setBackgroundResource(R.drawable.calendar_bg);
	}

	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// init rectangles
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);

		// drawing
		drawDayHeader(canvas);
	}

}
