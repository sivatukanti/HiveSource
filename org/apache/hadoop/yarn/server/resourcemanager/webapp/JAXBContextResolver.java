// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.DelegationToken;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LocalResourceInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ContainerLaunchContextInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NewApplication;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.StatisticsItemInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationStatisticsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.UserInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.UsersInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerQueueInfoList;
import org.apache.hadoop.yarn.webapp.RemoteExceptionData;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodesInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.SchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterMetricsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.UserMetricsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.SchedulerTypeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FifoSchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerQueueInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppInfo;
import java.util.Map;
import javax.ws.rs.ext.Provider;
import com.google.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.ws.rs.ext.ContextResolver;

@Singleton
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext>
{
    private final Map<Class, JAXBContext> typesContextMap;
    
    public JAXBContextResolver() throws Exception {
        final Class[] cTypes = { AppInfo.class, AppAttemptInfo.class, AppAttemptsInfo.class, ClusterInfo.class, CapacitySchedulerQueueInfo.class, FifoSchedulerInfo.class, SchedulerTypeInfo.class, NodeInfo.class, UserMetricsInfo.class, CapacitySchedulerInfo.class, ClusterMetricsInfo.class, SchedulerInfo.class, AppsInfo.class, NodesInfo.class, RemoteExceptionData.class, CapacitySchedulerQueueInfoList.class, ResourceInfo.class, UsersInfo.class, UserInfo.class, ApplicationStatisticsInfo.class, StatisticsItemInfo.class };
        final Class[] rootUnwrappedTypes = { NewApplication.class, ApplicationSubmissionContextInfo.class, ContainerLaunchContextInfo.class, LocalResourceInfo.class, DelegationToken.class };
        this.typesContextMap = new HashMap<Class, JAXBContext>();
        final JAXBContext context = new JSONJAXBContext(JSONConfiguration.natural().rootUnwrapping(false).build(), cTypes);
        final JAXBContext unWrappedRootContext = new JSONJAXBContext(JSONConfiguration.natural().rootUnwrapping(true).build(), rootUnwrappedTypes);
        for (final Class type : cTypes) {
            this.typesContextMap.put(type, context);
        }
        for (final Class type : rootUnwrappedTypes) {
            this.typesContextMap.put(type, unWrappedRootContext);
        }
    }
    
    @Override
    public JAXBContext getContext(final Class<?> objectType) {
        return this.typesContextMap.get(objectType);
    }
}
