package com.therandomlabs.curseapi.widget;

public final class DateInfo implements Cloneable {
	public String date;
	//https://stackoverflow.com/a/17711005/5076824
	public int timezone_type;
	public String timezone;

	@Override
	public int hashCode() {
		return date.hashCode() + timezone_type + timezone.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

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
		return "[date=\"" + date + "\",timezone_type=" + timezone_type + ",timezone=\"" + timezone +
				"\"]";
	}
}
