package com.therandomlabs.curseapi.cursemeta;

import com.therandomlabs.curseapi.RelationType;

public class AddOnFileDependency implements Cloneable {
	public int AddOnId;
	public RelationType Type;

	@Override
	public AddOnFileDependency clone() {
		try {
			return (AddOnFileDependency) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id=" + AddOnId + ",type=" + Type + "]";
	}
}
