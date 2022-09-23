// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.annotation.Metric;
import java.lang.annotation.Annotation;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.MetricsSource;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.apache.hadoop.util.ReflectionUtils;
import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class MetricsSourceBuilder
{
    private static final Logger LOG;
    private final Object source;
    private final MutableMetricsFactory factory;
    private final MetricsRegistry registry;
    private MetricsInfo info;
    private boolean hasAtMetric;
    private boolean hasRegistry;
    
    MetricsSourceBuilder(final Object source, final MutableMetricsFactory factory) {
        this.hasAtMetric = false;
        this.hasRegistry = false;
        this.source = Preconditions.checkNotNull(source, (Object)"source");
        this.factory = Preconditions.checkNotNull(factory, (Object)"mutable metrics factory");
        final Class<?> cls = source.getClass();
        this.registry = this.initRegistry(source);
        for (final Field field : ReflectionUtils.getDeclaredFieldsIncludingInherited(cls)) {
            this.add(source, field);
        }
        for (final Method method : ReflectionUtils.getDeclaredMethodsIncludingInherited(cls)) {
            this.add(source, method);
        }
    }
    
    public MetricsSource build() {
        if (this.source instanceof MetricsSource) {
            if (this.hasAtMetric && !this.hasRegistry) {
                throw new MetricsException("Hybrid metrics: registry required.");
            }
            return (MetricsSource)this.source;
        }
        else {
            if (!this.hasAtMetric) {
                throw new MetricsException("No valid @Metric annotation found.");
            }
            return new MetricsSource() {
                @Override
                public void getMetrics(final MetricsCollector builder, final boolean all) {
                    MetricsSourceBuilder.this.registry.snapshot(builder.addRecord(MetricsSourceBuilder.this.registry.info()), all);
                }
            };
        }
    }
    
    public MetricsInfo info() {
        return this.info;
    }
    
    private MetricsRegistry initRegistry(final Object source) {
        final Class<?> cls = source.getClass();
        MetricsRegistry r = null;
        for (final Field field : ReflectionUtils.getDeclaredFieldsIncludingInherited(cls)) {
            if (field.getType() != MetricsRegistry.class) {
                continue;
            }
            try {
                field.setAccessible(true);
                r = (MetricsRegistry)field.get(source);
                this.hasRegistry = (r != null);
            }
            catch (Exception e) {
                MetricsSourceBuilder.LOG.warn("Error accessing field " + field, e);
                continue;
            }
            break;
        }
        for (final Annotation annotation : cls.getAnnotations()) {
            if (annotation instanceof Metrics) {
                final Metrics ma = (Metrics)annotation;
                this.info = this.factory.getInfo(cls, ma);
                if (r == null) {
                    r = new MetricsRegistry(this.info);
                }
                r.setContext(ma.context());
            }
        }
        if (r == null) {
            return new MetricsRegistry(cls.getSimpleName());
        }
        return r;
    }
    
    private void add(final Object source, final Field field) {
        for (final Annotation annotation : field.getAnnotations()) {
            Label_0179: {
                if (annotation instanceof Metric) {
                    try {
                        field.setAccessible(true);
                        if (field.get(source) != null) {
                            break Label_0179;
                        }
                    }
                    catch (Exception e) {
                        MetricsSourceBuilder.LOG.warn("Error accessing field " + field + " annotated with" + annotation, e);
                        break Label_0179;
                    }
                    final MutableMetric mutable = this.factory.newForField(field, (Metric)annotation, this.registry);
                    if (mutable != null) {
                        try {
                            field.set(source, mutable);
                            this.hasAtMetric = true;
                        }
                        catch (Exception e2) {
                            throw new MetricsException("Error setting field " + field + " annotated with " + annotation, e2);
                        }
                    }
                }
            }
        }
    }
    
    private void add(final Object source, final Method method) {
        for (final Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof Metric) {
                this.factory.newForMethod(source, method, (Metric)annotation, this.registry);
                this.hasAtMetric = true;
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsSourceBuilder.class);
    }
}
