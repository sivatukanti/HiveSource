// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.util.ConverterUtils;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.api.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class ContainerBlock extends HtmlBlock
{
    private static final Log LOG;
    private final ApplicationContext appContext;
    
    @Inject
    public ContainerBlock(final ApplicationContext appContext, final ViewContext ctx) {
        super(ctx);
        this.appContext = appContext;
    }
    
    @Override
    protected void render(final Block html) {
        final String containerid = this.$("container.id");
        if (containerid.isEmpty()) {
            this.puts("Bad request: requires container ID");
            return;
        }
        ContainerId containerId = null;
        try {
            containerId = ConverterUtils.toContainerId(containerid);
        }
        catch (IllegalArgumentException e2) {
            this.puts("Invalid container ID: " + containerid);
            return;
        }
        final ContainerId containerIdFinal = containerId;
        final UserGroupInformation callerUGI = this.getCallerUGI();
        ContainerReport containerReport;
        try {
            if (callerUGI == null) {
                containerReport = this.appContext.getContainer(containerId);
            }
            else {
                containerReport = callerUGI.doAs((PrivilegedExceptionAction<ContainerReport>)new PrivilegedExceptionAction<ContainerReport>() {
                    @Override
                    public ContainerReport run() throws Exception {
                        return ContainerBlock.this.appContext.getContainer(containerIdFinal);
                    }
                });
            }
        }
        catch (Exception e) {
            final String message = "Failed to read the container " + containerid + ".";
            ContainerBlock.LOG.error(message, e);
            html.p()._(message)._();
            return;
        }
        if (containerReport == null) {
            this.puts("Container not found: " + containerid);
            return;
        }
        final ContainerInfo container = new ContainerInfo(containerReport);
        this.setTitle(StringHelper.join("Container ", containerid));
        this.info("Container Overview")._("State:", container.getContainerState())._("Exit Status:", container.getContainerExitStatus())._("Node:", container.getAssignedNodeId())._("Priority:", container.getPriority())._("Started:", Times.format(container.getStartedTime()))._("Elapsed:", StringUtils.formatTime(Times.elapsed(container.getStartedTime(), container.getFinishedTime())))._("Resource:", container.getAllocatedMB() + " Memory, " + container.getAllocatedVCores() + " VCores")._("Logs:", (container.getLogUrl() == null) ? "#" : container.getLogUrl(), (container.getLogUrl() == null) ? "N/A" : "Logs")._("Diagnostics:", container.getDiagnosticsInfo());
        html._(InfoBlock.class);
    }
    
    static {
        LOG = LogFactory.getLog(ContainerBlock.class);
    }
}
