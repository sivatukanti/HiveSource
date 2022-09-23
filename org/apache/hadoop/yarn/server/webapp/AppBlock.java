// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp;

import java.util.Iterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import java.util.Collection;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.server.webapp.dao.AppInfo;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.util.Apps;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.api.ApplicationContext;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class AppBlock extends HtmlBlock
{
    protected ApplicationContext appContext;
    
    @Inject
    AppBlock(final ApplicationContext appContext, final ViewContext ctx) {
        super(ctx);
        this.appContext = appContext;
    }
    
    @Override
    protected void render(final Block html) {
        final String aid = this.$("app.id");
        if (aid.isEmpty()) {
            this.puts("Bad request: requires Application ID");
            return;
        }
        ApplicationId appID = null;
        try {
            appID = Apps.toAppID(aid);
        }
        catch (Exception e4) {
            this.puts("Invalid Application ID: " + aid);
            return;
        }
        final ApplicationId appIDFinal = appID;
        final UserGroupInformation callerUGI = this.getCallerUGI();
        ApplicationReport appReport;
        try {
            if (callerUGI == null) {
                appReport = this.appContext.getApplication(appID);
            }
            else {
                appReport = callerUGI.doAs((PrivilegedExceptionAction<ApplicationReport>)new PrivilegedExceptionAction<ApplicationReport>() {
                    @Override
                    public ApplicationReport run() throws Exception {
                        return AppBlock.this.appContext.getApplication(appIDFinal);
                    }
                });
            }
        }
        catch (Exception e) {
            final String message = "Failed to read the application " + appID + ".";
            AppBlock.LOG.error(message, e);
            html.p()._(message)._();
            return;
        }
        if (appReport == null) {
            this.puts("Application not found: " + aid);
            return;
        }
        final AppInfo app = new AppInfo(appReport);
        this.setTitle(StringHelper.join("Application ", aid));
        this.info("Application Overview")._("User:", app.getUser())._("Name:", app.getName())._("Application Type:", app.getType())._("State:", app.getAppState())._("FinalStatus:", app.getFinalAppStatus())._("Started:", Times.format(app.getStartedTime()))._("Elapsed:", StringUtils.formatTime(Times.elapsed(app.getStartedTime(), app.getFinishedTime())))._("Tracking URL:", (app.getTrackingUrl() == null) ? "#" : this.root_url(app.getTrackingUrl()), "History")._("Diagnostics:", app.getDiagnosticsInfo());
        html._(InfoBlock.class);
        Collection<ApplicationAttemptReport> attempts;
        try {
            if (callerUGI == null) {
                attempts = this.appContext.getApplicationAttempts(appID).values();
            }
            else {
                attempts = callerUGI.doAs((PrivilegedExceptionAction<Collection<ApplicationAttemptReport>>)new PrivilegedExceptionAction<Collection<ApplicationAttemptReport>>() {
                    @Override
                    public Collection<ApplicationAttemptReport> run() throws Exception {
                        return AppBlock.this.appContext.getApplicationAttempts(appIDFinal).values();
                    }
                });
            }
        }
        catch (Exception e2) {
            final String message2 = "Failed to read the attempts of the application " + appID + ".";
            AppBlock.LOG.error(message2, e2);
            html.p()._(message2)._();
            return;
        }
        final Hamlet.TBODY<Hamlet.TABLE<Hamlet>> tbody = html.table("#attempts").thead().tr().th(".id", "Attempt ID").th(".started", "Started").th(".node", "Node").th(".logs", "Logs")._()._().tbody();
        final StringBuilder attemptsTableData = new StringBuilder("[\n");
        for (final ApplicationAttemptReport appAttemptReport : attempts) {
            final AppAttemptInfo appAttempt = new AppAttemptInfo(appAttemptReport);
            ContainerReport containerReport;
            try {
                if (callerUGI == null) {
                    containerReport = this.appContext.getAMContainer(appAttemptReport.getApplicationAttemptId());
                }
                else {
                    containerReport = callerUGI.doAs((PrivilegedExceptionAction<ContainerReport>)new PrivilegedExceptionAction<ContainerReport>() {
                        @Override
                        public ContainerReport run() throws Exception {
                            return AppBlock.this.appContext.getAMContainer(appAttemptReport.getApplicationAttemptId());
                        }
                    });
                }
            }
            catch (Exception e3) {
                final String message3 = "Failed to read the AM container of the application attempt " + appAttemptReport.getApplicationAttemptId() + ".";
                AppBlock.LOG.error(message3, e3);
                html.p()._(message3)._();
                return;
            }
            long startTime = Long.MAX_VALUE;
            String logsLink = null;
            if (containerReport != null) {
                final ContainerInfo container = new ContainerInfo(containerReport);
                startTime = container.getStartedTime();
                logsLink = containerReport.getLogUrl();
            }
            String nodeLink = null;
            if (appAttempt.getHost() != null && appAttempt.getRpcPort() >= 0 && appAttempt.getRpcPort() < 65536) {
                nodeLink = appAttempt.getHost() + ":" + appAttempt.getRpcPort();
            }
            attemptsTableData.append("[\"<a href='").append(this.url("appattempt", appAttempt.getAppAttemptId())).append("'>").append(appAttempt.getAppAttemptId()).append("</a>\",\"").append(startTime).append("\",\"<a href='").append((nodeLink == null) ? "#" : this.url("//", nodeLink)).append("'>").append((nodeLink == null) ? "N/A" : StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(nodeLink))).append("</a>\",\"<a href='").append((logsLink == null) ? "#" : logsLink).append("'>").append((logsLink == null) ? "N/A" : "Logs").append("</a>\"],\n");
        }
        if (attemptsTableData.charAt(attemptsTableData.length() - 2) == ',') {
            attemptsTableData.delete(attemptsTableData.length() - 2, attemptsTableData.length() - 1);
        }
        attemptsTableData.append("]");
        html.script().$type("text/javascript")._("var attemptsTableData=" + (Object)attemptsTableData)._();
        tbody._()._();
    }
}
