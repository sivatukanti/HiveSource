// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.metrics;

import javax.management.MBeanParameterInfo;
import java.io.IOException;
import javax.management.InvalidAttributeValueException;
import java.util.Iterator;
import javax.management.MBeanInfo;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import java.util.HashMap;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import java.util.Map;

public class MetricsMBeanImpl implements MetricsMBean
{
    private final Map<String, Object> metricsMap;
    private MBeanAttributeInfo[] attributeInfos;
    private boolean dirtyAttributeInfoCache;
    private static final MBeanConstructorInfo[] ctors;
    private static final MBeanOperationInfo[] ops;
    private static final MBeanNotificationInfo[] notifs;
    
    public MetricsMBeanImpl() {
        this.metricsMap = new HashMap<String, Object>();
        this.dirtyAttributeInfoCache = true;
    }
    
    @Override
    public Object getAttribute(final String arg0) throws AttributeNotFoundException, MBeanException, ReflectionException {
        synchronized (this.metricsMap) {
            if (this.metricsMap.containsKey(arg0)) {
                return this.metricsMap.get(arg0);
            }
            throw new AttributeNotFoundException("Key [" + arg0 + "] not found/tracked");
        }
    }
    
    @Override
    public AttributeList getAttributes(final String[] arg0) {
        final AttributeList results = new AttributeList();
        synchronized (this.metricsMap) {
            for (final String key : arg0) {
                results.add(new Attribute(key, this.metricsMap.get(key)));
            }
        }
        return results;
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        if (this.dirtyAttributeInfoCache) {
            synchronized (this.metricsMap) {
                this.attributeInfos = new MBeanAttributeInfo[this.metricsMap.size()];
                int i = 0;
                for (final String key : this.metricsMap.keySet()) {
                    this.attributeInfos[i] = new MBeanAttributeInfo(key, this.metricsMap.get(key).getClass().getName(), key, true, true, false);
                    ++i;
                }
                this.dirtyAttributeInfoCache = false;
            }
        }
        return new MBeanInfo(this.getClass().getName(), "metrics information", this.attributeInfos, MetricsMBeanImpl.ctors, MetricsMBeanImpl.ops, MetricsMBeanImpl.notifs);
    }
    
    @Override
    public Object invoke(final String name, final Object[] args, final String[] signature) throws MBeanException, ReflectionException {
        if (name.equals("reset")) {
            this.reset();
            return null;
        }
        throw new ReflectionException(new NoSuchMethodException(name));
    }
    
    @Override
    public void setAttribute(final Attribute attr) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        try {
            this.put(attr.getName(), attr.getValue());
        }
        catch (Exception e) {
            throw new MBeanException(e);
        }
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList arg0) {
        final AttributeList attributesSet = new AttributeList();
        for (final Attribute attr : arg0.asList()) {
            try {
                this.setAttribute(attr);
                attributesSet.add(attr);
            }
            catch (AttributeNotFoundException ex) {}
            catch (InvalidAttributeValueException ex2) {}
            catch (MBeanException ex3) {}
            catch (ReflectionException ex4) {}
        }
        return attributesSet;
    }
    
    @Override
    public boolean hasKey(final String name) {
        synchronized (this.metricsMap) {
            return this.metricsMap.containsKey(name);
        }
    }
    
    @Override
    public void put(final String name, final Object value) throws IOException {
        synchronized (this.metricsMap) {
            if (!this.metricsMap.containsKey(name)) {
                this.dirtyAttributeInfoCache = true;
            }
            this.metricsMap.put(name, value);
        }
    }
    
    @Override
    public Object get(final String name) throws IOException {
        try {
            return this.getAttribute(name);
        }
        catch (AttributeNotFoundException e) {
            throw new IOException(e);
        }
        catch (MBeanException e2) {
            throw new IOException(e2);
        }
        catch (ReflectionException e3) {
            throw new IOException(e3);
        }
    }
    
    public void reset() {
        synchronized (this.metricsMap) {
            for (final String key : this.metricsMap.keySet()) {
                this.metricsMap.put(key, 0L);
            }
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.metricsMap) {
            this.attributeInfos = null;
            this.dirtyAttributeInfoCache = true;
            this.metricsMap.clear();
        }
    }
    
    static {
        ctors = null;
        ops = new MBeanOperationInfo[] { new MBeanOperationInfo("reset", "Sets the values of all Attributes to 0", null, "void", 1) };
        notifs = null;
    }
}
