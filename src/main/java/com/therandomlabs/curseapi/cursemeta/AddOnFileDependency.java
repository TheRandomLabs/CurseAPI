package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.util.Collection;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.RelationType;
import com.therandomlabs.utils.collection.TRLList;

public class AddOnFileDependency implements Cloneable, Serializable {
	private static final long serialVersionUID = 5846432969453933849L;

	public int AddOnId;
	public RelationType Type;

	@Override
	public AddOnFileDependency clone() {
		try {
			final AddOnFileDependency dependency = (AddOnFileDependency) super.clone();

			dependency.AddOnId = AddOnId;
			dependency.Type = Type;

			return dependency;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	public static TRLList<CurseProject> toProjects(Collection<AddOnFileDependency> dependencies)
			throws CurseException {
		if(dependencies == null) {
			return null;
		}

		final TRLList<CurseProject> projects = new TRLList<>(dependencies.size());
		for(AddOnFileDependency dependency : dependencies) {
			projects.add(CurseProject.fromID(dependency.AddOnId));
		}
		return projects;
	}
}
