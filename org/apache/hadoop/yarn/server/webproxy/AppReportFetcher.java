// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;

public class AppReportFetcher
{
    private static final Log LOG;
    private final Configuration conf;
    private final ApplicationClientProtocol applicationsManager;
    private final RecordFactory recordFactory;
    
    public AppReportFetcher(final Configuration conf) {
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.conf = conf;
        try {
            this.applicationsManager = ClientRMProxy.createRMProxy(conf, ApplicationClientProtocol.class);
        }
        catch (IOException e) {
            throw new YarnRuntimeException(e);
        }
    }
    
    public AppReportFetcher(final Configuration conf, final ApplicationClientProtocol applicationsManager) {
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.conf = conf;
        this.applicationsManager = applicationsManager;
    }
    
    public ApplicationReport getApplicationReport(final ApplicationId appId) throws YarnException, IOException {
        final GetApplicationReportRequest request = this.recordFactory.newRecordInstance(GetApplicationReportRequest.class);
        request.setApplicationId(appId);
        final GetApplicationReportResponse response = this.applicationsManager.getApplicationReport(request);
        return response.getApplicationReport();
    }
    
    public void stop() {
        if (this.applicationsManager != null) {
            RPC.stopProxy(this.applicationsManager);
        }
    }
    
    static {
        LOG = LogFactory.getLog(AppReportFetcher.class);
    }
}
