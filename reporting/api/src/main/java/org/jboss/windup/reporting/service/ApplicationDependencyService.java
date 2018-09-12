package org.jboss.windup.reporting.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ArchiveModel;
import org.jboss.windup.graph.model.ProjectModel;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.graph.service.WindupConfigurationService;
import org.jboss.windup.reporting.model.ApplicationDependencyGraphDTO;

public class ApplicationDependencyService extends GraphService<ArchiveModel> {

	public ApplicationDependencyService(GraphContext context) {
		super(context, ArchiveModel.class);
	}

	public Map<String, ApplicationDependencyGraphDTO> getDependenciesGraphSet() {
		final Map<String, ApplicationDependencyGraphDTO> appDeps = new HashMap<>();

		for (FileModel inputPath : WindupConfigurationService.getConfigurationModel(getGraphContext())
				.getInputPaths()) {
			final ProjectModel rootProjectModel = inputPath.getProjectModel();
			appDeps.computeIfAbsent(rootProjectModel.getRootFileModel().getFileName(), value -> new ApplicationDependencyGraphDTO(rootProjectModel));
			rootProjectModel.getAllProjectModels().forEach(item -> addChildren(item, appDeps));
		}
		return appDeps;
	}

	private void addChildren(ProjectModel projectModel, final Map<String, ApplicationDependencyGraphDTO> appDeps) {
		List<ProjectModel> children = projectModel.getChildProjects();
		if (CollectionUtils.isEmpty(children)) {
			return;
		}
		children.forEach(item -> {
			final ApplicationDependencyGraphDTO dto = appDeps.computeIfAbsent(item.getRootFileModel().getFileName(),
					value -> new ApplicationDependencyGraphDTO(item));
			dto.addParent(projectModel.getRootFileModel().getFileName());
			addChildren(item, appDeps);
		});
	}

	public String getRelationsAsJson() {
		return null;
	}


}
