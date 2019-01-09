package com.therandomlabs.curseapi.cursemeta;

import com.therandomlabs.curseapi.RelationType;

public final class CMDependency implements Cloneable {
	public int addonId;
	public int type;

	@Override
	public CMDependency clone() {
		try {
			return (CMDependency) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	public RelationType type() {
		switch(type) {
		case 1:
			return RelationType.REQUIRED_LIBRARY;
		case 2:
			return RelationType.OPTIONAL_LIBRARY;
		default:
			return RelationType.UNKNOWN;
		}
	}
}
