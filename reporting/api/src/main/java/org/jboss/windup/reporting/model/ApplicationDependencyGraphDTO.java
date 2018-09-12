package org.jboss.windup.reporting.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.jboss.windup.graph.model.ProjectModel;

public class ApplicationDependencyGraphDTO {

	/** the filename, e.g. com.acme-1.0.0.jar **/
	private String name;

	/** jar, war, ear, etc. **/
	private String type;

	private Set<String> parents = new HashSet<>();

	public ApplicationDependencyGraphDTO(final ProjectModel projectModel) {
		this.name = projectModel.getRootFileModel().getFileName();
		if (projectModel.getProjectType() == null) {
			//sometimes the type is resolved to null
			this.type = extractTypeFromFileName(projectModel);
		} else {
			this.type = projectModel.getProjectType();
		}
	}
	
	public ApplicationDependencyGraphDTO() {
		this.name = "foo";
		this.type = "bar";
	}


	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Set<String> getParents() {
		return Collections.unmodifiableSet(parents);
	}

	public void addParent(final String parent) {
		parents.add(parent);
	}

	private String extractTypeFromFileName(final ProjectModel projectModel) {
		return FilenameUtils.getExtension(this.name);
	}
}