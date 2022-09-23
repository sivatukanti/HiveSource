// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.Iterator;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsTag;
import javax.management.MBeanInfo;
import org.apache.hadoop.metrics2.MetricsInfo;
import com.google.common.collect.Lists;
import javax.management.MBeanAttributeInfo;
import java.util.List;
import org.apache.hadoop.metrics2.MetricsVisitor;

class MBeanInfoBuilder implements MetricsVisitor
{
    private final String name;
    private final String description;
    private List<MBeanAttributeInfo> attrs;
    private Iterable<MetricsRecordImpl> recs;
    private int curRecNo;
    
    MBeanInfoBuilder(final String name, final String desc) {
        this.name = name;
        this.description = desc;
        this.attrs = (List<MBeanAttributeInfo>)Lists.newArrayList();
    }
    
    MBeanInfoBuilder reset(final Iterable<MetricsRecordImpl> recs) {
        this.recs = recs;
        this.attrs.clear();
        return this;
    }
    
    MBeanAttributeInfo newAttrInfo(final String name, final String desc, final String type) {
        return new MBeanAttributeInfo(this.getAttrName(name), type, desc, true, false, false);
    }
    
    MBeanAttributeInfo newAttrInfo(final MetricsInfo info, final String type) {
        return this.newAttrInfo(info.name(), info.description(), type);
    }
    
    @Override
    public void gauge(final MetricsInfo info, final int value) {
        this.attrs.add(this.newAttrInfo(info, "java.lang.Integer"));
    }
    
    @Override
    public void gauge(final MetricsInfo info, final long value) {
        this.attrs.add(this.newAttrInfo(info, "java.lang.Long"));
    }
    
    @Override
    public void gauge(final MetricsInfo info, final float value) {
        this.attrs.add(this.newAttrInfo(info, "java.lang.Float"));
    }
    
    @Override
    public void gauge(final MetricsInfo info, final double value) {
        this.attrs.add(this.newAttrInfo(info, "java.lang.Double"));
    }
    
    @Override
    public void counter(final MetricsInfo info, final int value) {
        this.attrs.add(this.newAttrInfo(info, "java.lang.Integer"));
    }
    
    @Override
    public void counter(final MetricsInfo info, final long value) {
        this.attrs.add(this.newAttrInfo(info, "java.lang.Long"));
    }
    
    String getAttrName(final String name) {
        return (this.curRecNo > 0) ? (name + "." + this.curRecNo) : name;
    }
    
    MBeanInfo get() {
        this.curRecNo = 0;
        for (final MetricsRecordImpl rec : this.recs) {
            for (final MetricsTag t : rec.tags()) {
                this.attrs.add(this.newAttrInfo("tag." + t.name(), t.description(), "java.lang.String"));
            }
            for (final AbstractMetric m : rec.metrics()) {
                m.visit(this);
            }
            ++this.curRecNo;
        }
        MetricsSystemImpl.LOG.debug(this.attrs.toString());
        final MBeanAttributeInfo[] attrsArray = new MBeanAttributeInfo[this.attrs.size()];
        return new MBeanInfo(this.name, this.description, this.attrs.toArray(attrsArray), null, null, null);
    }
}
