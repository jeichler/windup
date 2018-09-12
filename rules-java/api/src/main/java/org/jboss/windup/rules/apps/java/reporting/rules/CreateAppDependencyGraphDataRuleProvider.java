package org.jboss.windup.rules.apps.java.reporting.rules;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.loader.RuleLoaderContext;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.config.phase.ReportRenderingPhase;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.reporting.model.ApplicationDependencyGraphDTO;
import org.jboss.windup.reporting.service.ApplicationDependencyService;
import org.jboss.windup.reporting.service.ReportService;
import org.jboss.windup.util.exception.WindupException;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Generates a .js (javascript) file in the reports directory containing the
 * apps and their dependencies.
 */
@RuleMetadata(phase = ReportRenderingPhase.class)
public class CreateAppDependencyGraphDataRuleProvider extends AbstractRuleProvider {

	public static final String APP_DEPENDENCY_GRAPH_JS = "app_dependencies_graph.js";

	private static final String JS_DATA_FUNCTION_NAME = "app_dependencies";
	private static final String NEWLINE = System.lineSeparator();

	private static final ObjectMapper ITEMS_OBJECTMAPPER = getObjectMapperForItems(ApplicationDependencyGraphDTOItemSerializer.class);
	private static final ObjectMapper RELATIONS_OBJECTMAPPER = getObjectMapperForItems(ApplicationDependencyGraphDTORelationSerializer.class);

	@Override
	public Configuration getConfiguration(RuleLoaderContext ruleLoaderContext) {
		return ConfigurationBuilder.begin().addRule().perform(new GraphOperation() {
			@Override
			public void perform(GraphRewrite event, EvaluationContext context) {
				generateData(event);
			}
		});
	}

	private void generateData(GraphRewrite event) {
		final GraphContext graphContext = event.getGraphContext();
		final ApplicationDependencyService appDependencyService = new ApplicationDependencyService(graphContext);
		final ReportService reportService = new ReportService(graphContext);

		final Map<String, ApplicationDependencyGraphDTO> appDeps = appDependencyService.getDependenciesGraphSet();

		try {
			Path dataDirectory = reportService.getReportDataDirectory();

			final Path appDependencyGraphPath = dataDirectory.resolve(APP_DEPENDENCY_GRAPH_JS);
			try (FileWriter appDependencyGraphDataWriter = new FileWriter(appDependencyGraphPath.toFile())) {

				appDependencyGraphDataWriter.write(JS_DATA_FUNCTION_NAME + "({" + NEWLINE + "\"items\": {");
				// items section
				appDeps.values().forEach(value -> {
					try {
						appDependencyGraphDataWriter.write(ITEMS_OBJECTMAPPER.writeValueAsString(value) + "," + NEWLINE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				// relations section
				appDependencyGraphDataWriter.write("}, \"relations\":[");
				appDeps.values().forEach(value -> {
					try {
						appDependencyGraphDataWriter.write(RELATIONS_OBJECTMAPPER.writeValueAsString(value));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				appDependencyGraphDataWriter.write("]});");
			}
		} catch (Exception e) {
			throw new WindupException("Error serializing app dependency graph json due to: " + e.getMessage(), e);
		}
	}

	private static ObjectMapper getObjectMapperForItems(Class<? extends StdSerializer<ApplicationDependencyGraphDTO>> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addSerializer(ApplicationDependencyGraphDTO.class, clazz.getDeclaredConstructor().newInstance());
			mapper.registerModule(module);
			return mapper;
		} catch (Exception e) {
			// instead of failing we just return a regular mapper.
			// this might corrupt the app graph report, but the analysis shouldn't fail in
			// that case
			return new ObjectMapper();
		}

	}
}