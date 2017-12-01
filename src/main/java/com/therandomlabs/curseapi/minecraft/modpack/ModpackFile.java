package com.therandomlabs.curseapi.minecraft.modpack;

import java.util.Collection;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;

public class ModpackFile extends CurseFile {
	private final FileType type;
	private final TRLList<String> relatedFiles;
	private final CurseFileList alternatives;

	public ModpackFile(CurseFile file, FileType type, Collection<String> relatedFiles,
			Collection<CurseFile> alternatives) {
		this(file, type, relatedFiles.toArray(new String[0]),
				alternatives.toArray(new CurseFile[0]));
	}

	public ModpackFile(CurseFile file, FileType type, String[] relatedFiles,
			CurseFile[] alternatives) {
		super(file);
		this.type = type;
		this.relatedFiles = new ImmutableList<>(relatedFiles);
		this.alternatives = CurseFileList.ofUnsorted(alternatives).
				sortedByProjectTitle().filterDuplicateProjects();
	}

	public FileType getType() {
		return type;
	}

	public TRLList<String> getRelatedFiles() {
		return relatedFiles;
	}

	public CurseFileList getAlternatives() {
		return alternatives;
	}

	@Override
	public int hashCode() {
		//Differentiate from CurseFile so CurseFile.equals(ModpackFile) is always false
		return id() + 90000000;
	}
}
