// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.hadoop.yarn.server.webapp.dao.ContainersInfo;
import org.apache.hadoop.yarn.server.webapp.dao.ContainerInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptsInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppsInfo;
import org.apache.hadoop.yarn.server.webapp.dao.AppInfo;
import java.util.Set;
import javax.ws.rs.ext.Provider;
import com.google.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.ws.rs.ext.ContextResolver;

@Singleton
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext>
{
    private JAXBContext context;
    private final Set<Class> types;
    private final Class[] cTypes;
    
    public JAXBContextResolver() throws Exception {
        this.cTypes = new Class[] { AppInfo.class, AppsInfo.class, AppAttemptInfo.class, AppAttemptsInfo.class, ContainerInfo.class, ContainersInfo.class };
        this.types = new HashSet<Class>(Arrays.asList((Class[])this.cTypes));
        this.context = new JSONJAXBContext(JSONConfiguration.natural().rootUnwrapping(false).build(), this.cTypes);
    }
    
    @Override
    public JAXBContext getContext(final Class<?> objectType) {
        return this.types.contains(objectType) ? this.context : null;
    }
}
