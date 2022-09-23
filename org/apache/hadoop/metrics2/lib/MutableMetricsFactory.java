// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.Method;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.annotation.Metric;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class MutableMetricsFactory
{
    private static final Logger LOG;
    
    MutableMetric newForField(final Field field, final Metric annotation, final MetricsRegistry registry) {
        if (MutableMetricsFactory.LOG.isDebugEnabled()) {
            MutableMetricsFactory.LOG.debug("field " + field + " with annotation " + annotation);
        }
        final MetricsInfo info = this.getInfo(annotation, field);
        final MutableMetric metric = this.newForField(field, annotation);
        if (metric != null) {
            registry.add(info.name(), metric);
            return metric;
        }
        final Class<?> cls = field.getType();
        if (cls == MutableCounterInt.class) {
            return registry.newCounter(info, 0);
        }
        if (cls == MutableCounterLong.class) {
            return registry.newCounter(info, 0L);
        }
        if (cls == MutableGaugeInt.class) {
            return registry.newGauge(info, 0);
        }
        if (cls == MutableGaugeLong.class) {
            return registry.newGauge(info, 0L);
        }
        if (cls == MutableGaugeFloat.class) {
            return registry.newGauge(info, 0.0f);
        }
        if (cls == MutableRate.class) {
            return registry.newRate(info.name(), info.description(), annotation.always());
        }
        if (cls == MutableRates.class) {
            return new MutableRates(registry);
        }
        if (cls == MutableRatesWithAggregation.class) {
            return registry.newRatesWithAggregation(info.name());
        }
        if (cls == MutableStat.class) {
            return registry.newStat(info.name(), info.description(), annotation.sampleName(), annotation.valueName(), annotation.always());
        }
        if (cls == MutableRollingAverages.class) {
            return registry.newMutableRollingAverages(info.name(), annotation.valueName());
        }
        if (cls == MutableQuantiles.class) {
            return registry.newQuantiles(info.name(), annotation.about(), annotation.sampleName(), annotation.valueName(), annotation.interval());
        }
        throw new MetricsException("Unsupported metric field " + field.getName() + " of type " + field.getType().getName());
    }
    
    MutableMetric newForMethod(final Object source, final Method method, final Metric annotation, final MetricsRegistry registry) {
        if (MutableMetricsFactory.LOG.isDebugEnabled()) {
            MutableMetricsFactory.LOG.debug("method " + method + " with annotation " + annotation);
        }
        final MetricsInfo info = this.getInfo(annotation, method);
        MutableMetric metric = this.newForMethod(source, method, annotation);
        metric = ((metric != null) ? metric : new MethodMetric(source, method, info, annotation.type()));
        registry.add(info.name(), metric);
        return metric;
    }
    
    protected MutableMetric newForField(final Field field, final Metric annotation) {
        return null;
    }
    
    protected MutableMetric newForMethod(final Object source, final Method method, final Metric annotation) {
        return null;
    }
    
    protected MetricsInfo getInfo(final Metric annotation, final Field field) {
        return this.getInfo(annotation, this.getName(field));
    }
    
    protected String getName(final Field field) {
        return StringUtils.capitalize(field.getName());
    }
    
    protected MetricsInfo getInfo(final Metric annotation, final Method method) {
        return this.getInfo(annotation, this.getName(method));
    }
    
    protected MetricsInfo getInfo(final Class<?> cls, final Metrics annotation) {
        final String name = annotation.name();
        final String about = annotation.about();
        final String name2 = name.isEmpty() ? cls.getSimpleName() : name;
        return Interns.info(name2, about.isEmpty() ? name2 : about);
    }
    
    protected String getName(final Method method) {
        final String methodName = method.getName();
        if (methodName.startsWith("get")) {
            return StringUtils.capitalize(methodName.substring(3));
        }
        return StringUtils.capitalize(methodName);
    }
    
    protected MetricsInfo getInfo(final Metric annotation, final String defaultName) {
        final String[] value = annotation.value();
        if (value.length == 2) {
            return Interns.info(value[0], value[1]);
        }
        if (value.length == 1) {
            return Interns.info(defaultName, value[0]);
        }
        return Interns.info(defaultName, defaultName);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MutableMetricsFactory.class);
    }
}
