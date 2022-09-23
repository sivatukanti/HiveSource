// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import java.util.Collections;
import java.util.HashSet;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Set;

public class SubTypeValidator
{
    protected static final String PREFIX_SPRING = "org.springframework.";
    protected static final String PREFIX_C3P0 = "com.mchange.v2.c3p0.";
    protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
    protected Set<String> _cfgIllegalClassNames;
    private static final SubTypeValidator instance;
    
    protected SubTypeValidator() {
        this._cfgIllegalClassNames = SubTypeValidator.DEFAULT_NO_DESER_CLASS_NAMES;
    }
    
    public static SubTypeValidator instance() {
        return SubTypeValidator.instance;
    }
    
    public void validateSubType(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> raw = type.getRawClass();
        final String full = raw.getName();
        Label_0134: {
            if (!this._cfgIllegalClassNames.contains(full)) {
                if (!raw.isInterface()) {
                    if (full.startsWith("org.springframework.")) {
                        for (Class<?> cls = raw; cls != null && cls != Object.class; cls = cls.getSuperclass()) {
                            final String name = cls.getSimpleName();
                            if ("AbstractPointcutAdvisor".equals(name)) {
                                break Label_0134;
                            }
                            if ("AbstractApplicationContext".equals(name)) {
                                break Label_0134;
                            }
                        }
                    }
                    else if (full.startsWith("com.mchange.v2.c3p0.") && full.endsWith("DataSource")) {
                        break Label_0134;
                    }
                }
                return;
            }
        }
        ctxt.reportBadTypeDefinition(beanDesc, "Illegal type (%s) to deserialize: prevented for security reasons", full);
    }
    
    static {
        final Set<String> s = new HashSet<String>();
        s.add("org.apache.commons.collections.functors.InvokerTransformer");
        s.add("org.apache.commons.collections.functors.InstantiateTransformer");
        s.add("org.apache.commons.collections4.functors.InvokerTransformer");
        s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
        s.add("org.codehaus.groovy.runtime.ConvertedClosure");
        s.add("org.codehaus.groovy.runtime.MethodClosure");
        s.add("org.springframework.beans.factory.ObjectFactory");
        s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
        s.add("com.sun.rowset.JdbcRowSetImpl");
        s.add("java.util.logging.FileHandler");
        s.add("java.rmi.server.UnicastRemoteObject");
        s.add("org.springframework.beans.factory.config.PropertyPathFactoryBean");
        s.add("org.apache.tomcat.dbcp.dbcp2.BasicDataSource");
        s.add("com.sun.org.apache.bcel.internal.util.ClassLoader");
        s.add("org.hibernate.jmx.StatisticsService");
        s.add("org.apache.ibatis.datasource.jndi.JndiDataSourceFactory");
        DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet((Set<? extends String>)s);
        instance = new SubTypeValidator();
    }
}
