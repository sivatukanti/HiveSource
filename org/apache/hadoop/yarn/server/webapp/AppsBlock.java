// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp;

import java.util.Iterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.yarn.server.webapp.dao.AppInfo;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.HashSet;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.api.ApplicationContext;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class AppsBlock extends HtmlBlock
{
    protected ApplicationContext appContext;
    
    @Inject
    AppsBlock(final ApplicationContext appContext, final ViewContext ctx) {
        super(ctx);
        this.appContext = appContext;
    }
    
    public void render(final Block html) {
        this.setTitle("Applications");
        final Hamlet.TBODY<Hamlet.TABLE<Hamlet>> tbody = html.table("#apps").thead().tr().th(".id", "ID").th(".user", "User").th(".name", "Name").th(".type", "Application Type").th(".queue", "Queue").th(".starttime", "StartTime").th(".finishtime", "FinishTime").th(".state", "State").th(".finalstatus", "FinalStatus").th(".progress", "Progress").th(".ui", "Tracking UI")._()._().tbody();
        Collection<YarnApplicationState> reqAppStates = null;
        final String reqStateString = this.$("app.state");
        if (reqStateString != null && !reqStateString.isEmpty()) {
            final String[] appStateStrings = reqStateString.split(",");
            reqAppStates = new HashSet<YarnApplicationState>(appStateStrings.length);
            for (final String stateString : appStateStrings) {
                reqAppStates.add(YarnApplicationState.valueOf(stateString));
            }
        }
        final UserGroupInformation callerUGI = this.getCallerUGI();
        Collection<ApplicationReport> appReports;
        try {
            if (callerUGI == null) {
                appReports = this.appContext.getAllApplications().values();
            }
            else {
                appReports = callerUGI.doAs((PrivilegedExceptionAction<Collection<ApplicationReport>>)new PrivilegedExceptionAction<Collection<ApplicationReport>>() {
                    @Override
                    public Collection<ApplicationReport> run() throws Exception {
                        return AppsBlock.this.appContext.getAllApplications().values();
                    }
                });
            }
        }
        catch (Exception e) {
            final String message = "Failed to read the applications.";
            AppsBlock.LOG.error(message, e);
            html.p()._(message)._();
            return;
        }
        final StringBuilder appsTableData = new StringBuilder("[\n");
        for (final ApplicationReport appReport : appReports) {
            if (reqAppStates != null && !reqAppStates.contains(appReport.getYarnApplicationState())) {
                continue;
            }
            final AppInfo app = new AppInfo(appReport);
            final String percent = String.format("%.1f", app.getProgress() * 100.0f);
            appsTableData.append("[\"<a href='").append(this.url("app", app.getAppId())).append("'>").append(app.getAppId()).append("</a>\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(app.getUser()))).append("\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(app.getName()))).append("\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(app.getType()))).append("\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(app.getQueue()))).append("\",\"").append(app.getStartedTime()).append("\",\"").append(app.getFinishedTime()).append("\",\"").append(app.getAppState()).append("\",\"").append(app.getFinalAppStatus()).append("\",\"").append("<br title='").append(percent).append("'> <div class='").append(JQueryUI.C_PROGRESSBAR).append("' title='").append(StringHelper.join(percent, '%')).append("'> ").append("<div class='").append(JQueryUI.C_PROGRESSBAR_VALUE).append("' style='").append(StringHelper.join("width:", percent, '%')).append("'> </div> </div>").append("\",\"<a href='");
            final String trackingURL = (app.getTrackingUrl() == null) ? "#" : app.getTrackingUrl();
            appsTableData.append(trackingURL).append("'>").append("History").append("</a>\"],\n");
        }
        if (appsTableData.charAt(appsTableData.length() - 2) == ',') {
            appsTableData.delete(appsTableData.length() - 2, appsTableData.length() - 1);
        }
        appsTableData.append("]");
        html.script().$type("text/javascript")._("var appsTableData=" + (Object)appsTableData)._();
        tbody._()._();
    }
}
