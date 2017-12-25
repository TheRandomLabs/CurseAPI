package com.therandomlabs.curseapi.widget;

public final class DateInfo implements Cloneable {
	public String date;
	//https://stackoverflow.com/questions/17694894/different-timezone-types-on-datetime-object/
	//17711005#17711005
	public int timezone_type;
	public String timezone;

	@Override
	public DateInfo clone() {
		final DateInfo info = new DateInfo();

		info.date = date;
		info.timezone_type = timezone_type;
		info.timezone = timezone;

		return info;
	}
}
