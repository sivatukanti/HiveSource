// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import java.util.Collection;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.util.ConverterUtils;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.server.api.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class AppAttemptBlock extends HtmlBlock
{
    private static final Log LOG;
    private final ApplicationContext appContext;
    
    @Inject
    public AppAttemptBlock(final ApplicationContext appContext) {
        this.appContext = appContext;
    }
    
    @Override
    protected void render(final Block html) {
        final String attemptid = this.$("appattempt.id");
        if (attemptid.isEmpty()) {
            this.puts("Bad request: requires application attempt ID");
            return;
        }
        ApplicationAttemptId appAttemptId = null;
        try {
            appAttemptId = ConverterUtils.toApplicationAttemptId(attemptid);
        }
        catch (IllegalArgumentException e2) {
            this.puts("Invalid application attempt ID: " + attemptid);
            return;
        }
        final ApplicationAttemptId appAttemptIdFinal = appAttemptId;
        final UserGroupInformation callerUGI = this.getCallerUGI();
        ApplicationAttemptReport appAttemptReport;
        try {
            if (callerUGI == null) {
                appAttemptReport = this.appContext.getApplicationAttempt(appAttemptId);
            }
            else {
                appAttemptReport = callerUGI.doAs((PrivilegedExceptionAction<ApplicationAttemptReport>)new PrivilegedExceptionAction<ApplicationAttemptReport>() {
                    @Override
                    public ApplicationAttemptReport run() throws Exception {
                        return AppAttemptBlock.this.appContext.getApplicationAttempt(appAttemptIdFinal);
                    }
                });
            }
        }
        catch (Exception e) {
            final String message = "Failed to read the application attempt " + appAttemptId + ".";
            AppAttemptBlock.LOG.error(message, e);
            html.p()._(message)._();
            return;
        }
        if (appAttemptReport == null) {
            this.puts("Application Attempt not found: " + attemptid);
            return;
        }
        final AppAttemptInfo appAttempt = new AppAttemptInfo(appAttemptReport);
        this.setTitle(StringHelper.join("Application Attempt ", attemptid));
        String node = "N/A";
        if (appAttempt.getHost() != null && appAttempt.getRpcPort() >= 0 && appAttempt.getRpcPort() < 65536) {
            node = appAttempt.getHost() + ":" + appAttempt.getRpcPort();
        }
        this.info("Application Attempt Overview")._("State", appAttempt.getAppAttemptState())._("Master Container", (appAttempt.getAmContainerId() == null) ? "#" : this.root_url("container", appAttempt.getAmContainerId()), String.valueOf(appAttempt.getAmContainerId()))._("Node:", node)._("Tracking URL:", (appAttempt.getTrackingUrl() == null) ? "#" : this.root_url(appAttempt.getTrackingUrl()), "History")._("Diagnostics Info:", appAttempt.getDiagnosticsInfo());
        html._(InfoBlock.class);
        Collection<ContainerReport> containers;
        try {
            if (callerUGI == null) {
                containers = this.appContext.getContainers(appAttemptId).values();
            }
            else {
                containers = callerUGI.doAs((PrivilegedExceptionAction<Collection<ContainerReport>>)new PrivilegedExceptionAction<Collection<ContainerReport>>() {
                    @Override
                    public Collection<ContainerReport> run() throws Exception {
                        return AppAttemptBlock.this.appContext.getContainers(appAttemptIdFinal).values();
                    }
                });
            }
        }
        catch (RuntimeException e3) {
            html.p()._("Sorry, Failed to get containers for application attempt" + attemptid + ".")._();
            return;
        }
        catch (Exception e4) {
            html.p()._("Sorry, Failed to get containers for application attempt" + attemptid + ".")._();
            return;
        }
        final Hamlet.TBODY<Hamlet.TABLE<Hamlet>> tbody = html.table("#containers").thead().tr().th(".id", "Container ID").th(".node", "Node").th(".exitstatus", "Container Exit Status").th(".logs", "Logs")._()._().tbody();
        final StringBuilder containersTableData = new StringBuilder("[\n");
        for (final ContainerReport containerReport : containers) {
            final ContainerInfo container = new ContainerInfo(containerReport);
            containersTableData.append("[\"<a href='").append(this.url("container", container.getContainerId())).append("'>").append(container.getContainerId()).append("</a>\",\"<a href='").append(container.getAssignedNodeId()).append("'>").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(container.getAssignedNodeId()))).append("</a>\",\"").append(container.getContainerExitStatus()).append("\",\"<a href='").append((container.getLogUrl() == null) ? "#" : container.getLogUrl()).append("'>").append((container.getLogUrl() == null) ? "N/A" : "Logs").append("</a>\"],\n");
        }
        if (containersTableData.charAt(containersTableData.length() - 2) == ',') {
            containersTableData.delete(containersTableData.length() - 2, containersTableData.length() - 1);
        }
        containersTableData.append("]");
        html.script().$type("text/javascript")._("var containersTableData=" + (Object)containersTableData)._();
        tbody._()._();
    }
    
    static {
        LOG = LogFactory.getLog(AppAttemptBlock.class);
    }
}
