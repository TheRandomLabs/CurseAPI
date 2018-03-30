package com.therandomlabs.curseapi.widget;

import java.io.Serializable;

public final class DateInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 3437565908465622016L;

	public String date;
	//https://stackoverflow.com/questions/17694894/different-timezone-types-on-datetime-object/
	//17711005#17711005
	public int timezone_type;
	public String timezone;

	@Override
	public int hashCode() {
		return date.hashCode() + timezone_type + timezone.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof DateInfo && object.hashCode() == hashCode();
	}

	@Override
	public DateInfo clone() {
		try {
			return (DateInfo) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[date=\"" + date + "\",timezone_type=" + timezone_type + ",timezone=\"" +
				timezone +
				"\"]";
	}
}
