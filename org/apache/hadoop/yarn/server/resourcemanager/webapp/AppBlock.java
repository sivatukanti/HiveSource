// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppMetrics;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppInfo;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.util.Apps;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class AppBlock extends HtmlBlock
{
    private ApplicationACLsManager aclsManager;
    private QueueACLsManager queueACLsManager;
    private final Configuration conf;
    
    @Inject
    AppBlock(final ResourceManager rm, final ViewContext ctx, final ApplicationACLsManager aclsManager, final QueueACLsManager queueACLsManager, final Configuration conf) {
        super(ctx);
        this.aclsManager = aclsManager;
        this.queueACLsManager = queueACLsManager;
        this.conf = conf;
    }
    
    @Override
    protected void render(final Block html) {
        final String aid = this.$("app.id");
        if (aid.isEmpty()) {
            this.puts("Bad request: requires application ID");
            return;
        }
        ApplicationId appID = null;
        try {
            appID = Apps.toAppID(aid);
        }
        catch (Exception e) {
            this.puts("Invalid Application ID: " + aid);
            return;
        }
        final RMContext context = this.getInstance(RMContext.class);
        final RMApp rmApp = context.getRMApps().get(appID);
        if (rmApp == null) {
            this.puts("Application not found: " + aid);
            return;
        }
        final AppInfo app = new AppInfo(rmApp, true, WebAppUtils.getHttpSchemePrefix(this.conf));
        final String remoteUser = this.request().getRemoteUser();
        UserGroupInformation callerUGI = null;
        if (remoteUser != null) {
            callerUGI = UserGroupInformation.createRemoteUser(remoteUser);
        }
        if (callerUGI != null && !this.aclsManager.checkAccess(callerUGI, ApplicationAccessType.VIEW_APP, app.getUser(), appID) && !this.queueACLsManager.checkAccess(callerUGI, QueueACL.ADMINISTER_QUEUE, app.getQueue())) {
            this.puts("You (User " + remoteUser + ") are not authorized to view application " + appID);
            return;
        }
        this.setTitle(StringHelper.join("Application ", aid));
        final RMAppMetrics appMerics = rmApp.getRMAppMetrics();
        final RMAppAttemptMetrics attemptMetrics = rmApp.getCurrentAppAttempt().getRMAppAttemptMetrics();
        this.info("Application Overview")._("User:", app.getUser())._("Name:", app.getName())._("Application Type:", app.getApplicationType())._("Application Tags:", app.getApplicationTags())._("State:", app.getState())._("FinalStatus:", app.getFinalStatus())._("Started:", Times.format(app.getStartTime()))._("Elapsed:", StringUtils.formatTime(Times.elapsed(app.getStartTime(), app.getFinishTime())))._("Tracking URL:", app.isTrackingUrlReady() ? app.getTrackingUrlPretty() : "#", app.getTrackingUI())._("Diagnostics:", app.getNote());
        final Hamlet.DIV<Hamlet> pdiv = html._(InfoBlock.class).div(".info-wrap.ui-widget-content.ui-corner-bottom");
        this.info("Application Overview").clear();
        this.info("Application Metrics")._("Total Resource Preempted:", appMerics.getResourcePreempted())._("Total Number of Non-AM Containers Preempted:", String.valueOf(appMerics.getNumNonAMContainersPreempted()))._("Total Number of AM Containers Preempted:", String.valueOf(appMerics.getNumAMContainersPreempted()))._("Resource Preempted from Current Attempt:", attemptMetrics.getResourcePreempted())._("Number of Non-AM Containers Preempted from Current Attempt:", String.valueOf(attemptMetrics.getNumNonAMContainersPreempted()))._("Aggregate Resource Allocation:", String.format("%d MB-seconds, %d vcore-seconds", appMerics.getMemorySeconds(), appMerics.getVcoreSeconds()));
        pdiv._();
        final Collection<RMAppAttempt> attempts = rmApp.getAppAttempts().values();
        final String amString = (attempts.size() == 1) ? "ApplicationMaster" : "ApplicationMasters";
        final Hamlet.DIV<Hamlet> div = html._(InfoBlock.class).div(".info-wrap.ui-widget-content.ui-corner-bottom");
        final Hamlet.TABLE<Hamlet.DIV<Hamlet>> table = div.table("#app");
        table.tr().th(amString)._().tr().th(".ui-state-default", "Attempt Number").th(".ui-state-default", "Start Time").th(".ui-state-default", "Node").th(".ui-state-default", "Logs")._();
        boolean odd = false;
        for (final RMAppAttempt attempt : attempts) {
            final AppAttemptInfo attemptInfo = new AppAttemptInfo(attempt, app.getUser());
            ((Hamlet.TR)table.tr((odd = (odd ? false : true)) ? ".odd" : ".even").td(String.valueOf(attemptInfo.getAttemptId())).td(Times.format(attemptInfo.getStartTime())).td().a(".nodelink", this.url("//", attemptInfo.getNodeHttpAddress()), attemptInfo.getNodeHttpAddress())._().td().a(".logslink", this.url(attemptInfo.getLogsLink()), "logs")._())._();
        }
        table._();
        div._();
    }
}
