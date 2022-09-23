// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.util.ListIterator;
import org.eclipse.jetty.util.annotation.Name;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.server.Server;
import java.util.ArrayList;

public interface Configuration
{
    public static final String ATTR = "org.eclipse.jetty.webapp.configuration";
    
    void preConfigure(final WebAppContext p0) throws Exception;
    
    void configure(final WebAppContext p0) throws Exception;
    
    void postConfigure(final WebAppContext p0) throws Exception;
    
    void deconfigure(final WebAppContext p0) throws Exception;
    
    void destroy(final WebAppContext p0) throws Exception;
    
    void cloneConfigure(final WebAppContext p0, final WebAppContext p1) throws Exception;
    
    public static class ClassList extends ArrayList<String>
    {
        public static ClassList setServerDefault(final Server server) {
            ClassList cl = server.getBean(ClassList.class);
            if (cl != null) {
                return cl;
            }
            cl = serverDefault(server);
            server.addBean(cl);
            server.setAttribute("org.eclipse.jetty.webapp.configuration", null);
            return cl;
        }
        
        public static ClassList serverDefault(final Server server) {
            ClassList cl = null;
            if (server != null) {
                cl = server.getBean(ClassList.class);
                if (cl != null) {
                    return new ClassList(cl);
                }
                final Object attr = server.getAttribute("org.eclipse.jetty.webapp.configuration");
                if (attr instanceof ClassList) {
                    return new ClassList((List<String>)attr);
                }
                if (attr instanceof String[]) {
                    return new ClassList((String[])attr);
                }
            }
            return new ClassList();
        }
        
        public ClassList() {
            this(WebAppContext.DEFAULT_CONFIGURATION_CLASSES);
        }
        
        public ClassList(final String[] classes) {
            this.addAll(Arrays.asList(classes));
        }
        
        public ClassList(final List<String> classes) {
            this.addAll(classes);
        }
        
        public void addAfter(@Name("afterClass") final String afterClass, @Name("configClass") final String... configClass) {
            if (configClass != null && afterClass != null) {
                final ListIterator<String> iter = this.listIterator();
                while (iter.hasNext()) {
                    final String cc = iter.next();
                    if (afterClass.equals(cc)) {
                        for (int i = 0; i < configClass.length; ++i) {
                            iter.add(configClass[i]);
                        }
                        return;
                    }
                }
            }
            throw new IllegalArgumentException("afterClass '" + afterClass + "' not found in " + this);
        }
        
        public void addBefore(@Name("beforeClass") final String beforeClass, @Name("configClass") final String... configClass) {
            if (configClass != null && beforeClass != null) {
                final ListIterator<String> iter = this.listIterator();
                while (iter.hasNext()) {
                    final String cc = iter.next();
                    if (beforeClass.equals(cc)) {
                        iter.previous();
                        for (int i = 0; i < configClass.length; ++i) {
                            iter.add(configClass[i]);
                        }
                        return;
                    }
                }
            }
            throw new IllegalArgumentException("beforeClass '" + beforeClass + "' not found in " + this);
        }
    }
}
