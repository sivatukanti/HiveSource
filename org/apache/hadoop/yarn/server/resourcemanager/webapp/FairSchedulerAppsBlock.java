// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Collection;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppInfo;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.HashSet;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class FairSchedulerAppsBlock extends HtmlBlock
{
    final ConcurrentMap<ApplicationId, RMApp> apps;
    final FairSchedulerInfo fsinfo;
    final Configuration conf;
    
    @Inject
    public FairSchedulerAppsBlock(final RMContext rmContext, final ResourceManager rm, final ViewContext ctx, final Configuration conf) {
        super(ctx);
        final FairScheduler scheduler = (FairScheduler)rm.getResourceScheduler();
        this.fsinfo = new FairSchedulerInfo(scheduler);
        this.apps = new ConcurrentHashMap<ApplicationId, RMApp>();
        for (final Map.Entry<ApplicationId, RMApp> entry : rmContext.getRMApps().entrySet()) {
            if (!RMAppState.NEW.equals(entry.getValue().getState()) && !RMAppState.NEW_SAVING.equals(entry.getValue().getState()) && !RMAppState.SUBMITTED.equals(entry.getValue().getState())) {
                this.apps.put(entry.getKey(), entry.getValue());
            }
        }
        this.conf = conf;
    }
    
    public void render(final Block html) {
        final Hamlet.TBODY<Hamlet.TABLE<Hamlet>> tbody = html.table("#apps").thead().tr().th(".id", "ID").th(".user", "User").th(".name", "Name").th(".type", "Application Type").th(".queue", "Queue").th(".fairshare", "Fair Share").th(".starttime", "StartTime").th(".finishtime", "FinishTime").th(".state", "State").th(".finalstatus", "FinalStatus").th(".progress", "Progress").th(".ui", "Tracking UI")._()._().tbody();
        Collection<YarnApplicationState> reqAppStates = null;
        final String reqStateString = this.$("app.state");
        if (reqStateString != null && !reqStateString.isEmpty()) {
            final String[] appStateStrings = reqStateString.split(",");
            reqAppStates = new HashSet<YarnApplicationState>(appStateStrings.length);
            for (final String stateString : appStateStrings) {
                reqAppStates.add(YarnApplicationState.valueOf(stateString));
            }
        }
        final StringBuilder appsTableData = new StringBuilder("[\n");
        for (final RMApp app : this.apps.values()) {
            if (reqAppStates != null && !reqAppStates.contains(app.createApplicationState())) {
                continue;
            }
            final AppInfo appInfo = new AppInfo(app, true, WebAppUtils.getHttpSchemePrefix(this.conf));
            final String percent = String.format("%.1f", appInfo.getProgress());
            final ApplicationAttemptId attemptId = app.getCurrentAppAttempt().getAppAttemptId();
            final int fairShare = this.fsinfo.getAppFairShare(attemptId);
            if (fairShare == -1) {
                continue;
            }
            appsTableData.append("[\"<a href='").append(this.url("app", appInfo.getAppId())).append("'>").append(appInfo.getAppId()).append("</a>\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(appInfo.getUser()))).append("\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(appInfo.getName()))).append("\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(appInfo.getApplicationType()))).append("\",\"").append(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(appInfo.getQueue()))).append("\",\"").append(fairShare).append("\",\"").append(appInfo.getStartTime()).append("\",\"").append(appInfo.getFinishTime()).append("\",\"").append(appInfo.getState()).append("\",\"").append(appInfo.getFinalStatus()).append("\",\"").append("<br title='").append(percent).append("'> <div class='").append(JQueryUI.C_PROGRESSBAR).append("' title='").append(StringHelper.join(percent, '%')).append("'> ").append("<div class='").append(JQueryUI.C_PROGRESSBAR_VALUE).append("' style='").append(StringHelper.join("width:", percent, '%')).append("'> </div> </div>").append("\",\"<a href='");
            final String trackingURL = appInfo.isTrackingUrlReady() ? appInfo.getTrackingUrlPretty() : "#";
            appsTableData.append(trackingURL).append("'>").append(appInfo.getTrackingUI()).append("</a>\"],\n");
        }
        if (appsTableData.charAt(appsTableData.length() - 2) == ',') {
            appsTableData.delete(appsTableData.length() - 2, appsTableData.length() - 1);
        }
        appsTableData.append("]");
        html.script().$type("text/javascript")._("var appsTableData=" + (Object)appsTableData)._();
        tbody._()._();
    }
}
