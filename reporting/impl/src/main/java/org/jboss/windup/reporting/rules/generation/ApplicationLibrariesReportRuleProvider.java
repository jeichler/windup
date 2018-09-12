package org.jboss.windup.reporting.rules.generation;

import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.loader.RuleLoaderContext;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.config.phase.ReportGenerationPhase;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ProjectModel;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.graph.service.WindupConfigurationService;
import org.jboss.windup.reporting.model.ApplicationReportModel;
import org.jboss.windup.reporting.model.MigrationIssuesReportModel;
import org.jboss.windup.reporting.model.TemplateType;
import org.jboss.windup.reporting.service.ApplicationReportService;
import org.jboss.windup.reporting.service.ReportService;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;

import com.google.common.collect.Iterables;
import org.jboss.windup.config.metadata.RuleMetadata;

@RuleMetadata(phase = ReportGenerationPhase.class)
public class ApplicationLibrariesReportRuleProvider extends AbstractRuleProvider
{
    public static final String TEMPLATE_PATH = "/reports/templates/application-libraries.ftl";
    public static final String REPORT_DESCRIPTION = "TODO";

    @Override
    public Configuration getConfiguration(RuleLoaderContext ruleLoaderContext)
    {
        return ConfigurationBuilder.begin()
                    .addRule()
                    .perform(new CreateMigrationIssueReportOperation());
    }

    private class CreateMigrationIssueReportOperation extends GraphOperation
    {
        private static final String ALL_MIGRATION_ISSUES_REPORT_NAME = "Application Graph";
        private static final String MIGRATION_ISSUES_REPORT_NAME = "App Graph";

        @Override
        public void perform(GraphRewrite event, EvaluationContext context)
        {
//            int inputApplicationCount = Iterables.size(WindupConfigurationService.getConfigurationModel(event.getGraphContext()).getInputPaths());
//            if (inputApplicationCount > 1)
//            {
                createGlobalAppDependencyGraphReport(event.getGraphContext());
//            }

//            for (FileModel inputPath : WindupConfigurationService.getConfigurationModel(event.getGraphContext()).getInputPaths())
//            {
//                ApplicationReportModel report = createSingleAppDependencyGraphReport(event.getGraphContext(), inputPath.getProjectModel());
//                report.setMainApplicationReport(false);
//            }
        }

        private ApplicationReportModel createMigrationIssuesReportBase(GraphContext context)
        {
            ApplicationReportService applicationReportService = new ApplicationReportService(context);
            ApplicationReportModel report = applicationReportService.create();
            report.setReportPriority(102);
            report.setReportIconClass("glyphicon glyphicon-warning-sign");
            report.setTemplatePath(TEMPLATE_PATH);
            report.setTemplateType(TemplateType.FREEMARKER);
            report.setDisplayInApplicationReportIndex(true);
            report.setDescription(REPORT_DESCRIPTION);

            new GraphService<>(context, MigrationIssuesReportModel.class).addTypeToModel(report);

            return report;
        }

        private ApplicationReportModel createSingleAppDependencyGraphReport(GraphContext context, ProjectModel projectModel)
        {
            ReportService reportService = new ReportService(context);
            ApplicationReportModel report = createMigrationIssuesReportBase(context);
            report.setReportName(MIGRATION_ISSUES_REPORT_NAME);
            report.setProjectModel(projectModel);
            reportService.setUniqueFilename(report, "application_graph", "html");
            return report;
        }

        private ApplicationReportModel createGlobalAppDependencyGraphReport(GraphContext context)
        {
            ReportService reportService = new ReportService(context);
            ApplicationReportModel report = createMigrationIssuesReportBase(context);
            report.setReportName(ALL_MIGRATION_ISSUES_REPORT_NAME);
            report.setDisplayInGlobalApplicationIndex(true);
            reportService.setUniqueFilename(report, "application_graph", "html");
            return report;
        }
    }
}
