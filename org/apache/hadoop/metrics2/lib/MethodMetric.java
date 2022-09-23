// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.util.Contracts;
import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.lang.reflect.Method;
import org.slf4j.Logger;

class MethodMetric extends MutableMetric
{
    private static final Logger LOG;
    private final Object obj;
    private final Method method;
    private final MetricsInfo info;
    private final MutableMetric impl;
    
    MethodMetric(final Object obj, final Method method, final MetricsInfo info, final Metric.Type type) {
        this.obj = Preconditions.checkNotNull(obj, (Object)"object");
        (this.method = Contracts.checkArg(method, method.getParameterTypes().length == 0, "Metric method should have no arguments")).setAccessible(true);
        this.info = Preconditions.checkNotNull(info, (Object)"info");
        this.impl = this.newImpl(Preconditions.checkNotNull(type, (Object)"metric type"));
    }
    
    private MutableMetric newImpl(final Metric.Type metricType) {
        final Class<?> resType = this.method.getReturnType();
        switch (metricType) {
            case COUNTER: {
                return this.newCounter(resType);
            }
            case GAUGE: {
                return this.newGauge(resType);
            }
            case DEFAULT: {
                return (resType == String.class) ? this.newTag(resType) : this.newGauge(resType);
            }
            case TAG: {
                return this.newTag(resType);
            }
            default: {
                Contracts.checkArg(metricType, false, "unsupported metric type");
                return null;
            }
        }
    }
    
    MutableMetric newCounter(final Class<?> type) {
        if (isInt(type) || isLong(type)) {
            return new MutableMetric() {
                @Override
                public void snapshot(final MetricsRecordBuilder rb, final boolean all) {
                    try {
                        final Object ret = MethodMetric.this.method.invoke(MethodMetric.this.obj, (Object[])null);
                        if (MethodMetric.isInt(type)) {
                            rb.addCounter(MethodMetric.this.info, (int)ret);
                        }
                        else {
                            rb.addCounter(MethodMetric.this.info, (long)ret);
                        }
                    }
                    catch (Exception ex) {
                        MethodMetric.LOG.error("Error invoking method " + MethodMetric.this.method.getName(), ex);
                    }
                }
            };
        }
        throw new MetricsException("Unsupported counter type: " + type.getName());
    }
    
    static boolean isInt(final Class<?> type) {
        final boolean ret = type == Integer.TYPE || type == Integer.class;
        return ret;
    }
    
    static boolean isLong(final Class<?> type) {
        return type == Long.TYPE || type == Long.class;
    }
    
    static boolean isFloat(final Class<?> type) {
        return type == Float.TYPE || type == Float.class;
    }
    
    static boolean isDouble(final Class<?> type) {
        return type == Double.TYPE || type == Double.class;
    }
    
    MutableMetric newGauge(final Class<?> t) {
        if (isInt(t) || isLong(t) || isFloat(t) || isDouble(t)) {
            return new MutableMetric() {
                @Override
                public void snapshot(final MetricsRecordBuilder rb, final boolean all) {
                    try {
                        final Object ret = MethodMetric.this.method.invoke(MethodMetric.this.obj, (Object[])null);
                        if (MethodMetric.isInt(t)) {
                            rb.addGauge(MethodMetric.this.info, (int)ret);
                        }
                        else if (MethodMetric.isLong(t)) {
                            rb.addGauge(MethodMetric.this.info, (long)ret);
                        }
                        else if (MethodMetric.isFloat(t)) {
                            rb.addGauge(MethodMetric.this.info, (float)ret);
                        }
                        else {
                            rb.addGauge(MethodMetric.this.info, (double)ret);
                        }
                    }
                    catch (Exception ex) {
                        MethodMetric.LOG.error("Error invoking method " + MethodMetric.this.method.getName(), ex);
                    }
                }
            };
        }
        throw new MetricsException("Unsupported gauge type: " + t.getName());
    }
    
    MutableMetric newTag(final Class<?> resType) {
        if (resType == String.class) {
            return new MutableMetric() {
                @Override
                public void snapshot(final MetricsRecordBuilder rb, final boolean all) {
                    try {
                        final Object ret = MethodMetric.this.method.invoke(MethodMetric.this.obj, (Object[])null);
                        rb.tag(MethodMetric.this.info, (String)ret);
                    }
                    catch (Exception ex) {
                        MethodMetric.LOG.error("Error invoking method " + MethodMetric.this.method.getName(), ex);
                    }
                }
            };
        }
        throw new MetricsException("Unsupported tag type: " + resType.getName());
    }
    
    @Override
    public void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        this.impl.snapshot(builder, all);
    }
    
    static MetricsInfo metricInfo(final Method method) {
        return Interns.info(nameFrom(method), "Metric for " + method.getName());
    }
    
    static String nameFrom(final Method method) {
        final String methodName = method.getName();
        if (methodName.startsWith("get")) {
            return StringUtils.capitalize(methodName.substring(3));
        }
        return StringUtils.capitalize(methodName);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MethodMetric.class);
    }
}
