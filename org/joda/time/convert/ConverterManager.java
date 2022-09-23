// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import java.security.Permission;
import org.joda.time.JodaTimePermission;

public final class ConverterManager
{
    private static ConverterManager INSTANCE;
    private ConverterSet iInstantConverters;
    private ConverterSet iPartialConverters;
    private ConverterSet iDurationConverters;
    private ConverterSet iPeriodConverters;
    private ConverterSet iIntervalConverters;
    
    public static ConverterManager getInstance() {
        if (ConverterManager.INSTANCE == null) {
            ConverterManager.INSTANCE = new ConverterManager();
        }
        return ConverterManager.INSTANCE;
    }
    
    protected ConverterManager() {
        this.iInstantConverters = new ConverterSet(new Converter[] { ReadableInstantConverter.INSTANCE, StringConverter.INSTANCE, CalendarConverter.INSTANCE, DateConverter.INSTANCE, LongConverter.INSTANCE, NullConverter.INSTANCE });
        this.iPartialConverters = new ConverterSet(new Converter[] { ReadablePartialConverter.INSTANCE, ReadableInstantConverter.INSTANCE, StringConverter.INSTANCE, CalendarConverter.INSTANCE, DateConverter.INSTANCE, LongConverter.INSTANCE, NullConverter.INSTANCE });
        this.iDurationConverters = new ConverterSet(new Converter[] { ReadableDurationConverter.INSTANCE, ReadableIntervalConverter.INSTANCE, StringConverter.INSTANCE, LongConverter.INSTANCE, NullConverter.INSTANCE });
        this.iPeriodConverters = new ConverterSet(new Converter[] { ReadableDurationConverter.INSTANCE, ReadablePeriodConverter.INSTANCE, ReadableIntervalConverter.INSTANCE, StringConverter.INSTANCE, NullConverter.INSTANCE });
        this.iIntervalConverters = new ConverterSet(new Converter[] { ReadableIntervalConverter.INSTANCE, StringConverter.INSTANCE, NullConverter.INSTANCE });
    }
    
    public InstantConverter getInstantConverter(final Object o) {
        final InstantConverter instantConverter = (InstantConverter)this.iInstantConverters.select((o == null) ? null : o.getClass());
        if (instantConverter != null) {
            return instantConverter;
        }
        throw new IllegalArgumentException("No instant converter found for type: " + ((o == null) ? "null" : o.getClass().getName()));
    }
    
    public InstantConverter[] getInstantConverters() {
        final ConverterSet iInstantConverters = this.iInstantConverters;
        final InstantConverter[] array = new InstantConverter[iInstantConverters.size()];
        iInstantConverters.copyInto(array);
        return array;
    }
    
    public InstantConverter addInstantConverter(final InstantConverter instantConverter) throws SecurityException {
        this.checkAlterInstantConverters();
        if (instantConverter == null) {
            return null;
        }
        final InstantConverter[] array = { null };
        this.iInstantConverters = this.iInstantConverters.add(instantConverter, array);
        return array[0];
    }
    
    public InstantConverter removeInstantConverter(final InstantConverter instantConverter) throws SecurityException {
        this.checkAlterInstantConverters();
        if (instantConverter == null) {
            return null;
        }
        final InstantConverter[] array = { null };
        this.iInstantConverters = this.iInstantConverters.remove(instantConverter, array);
        return array[0];
    }
    
    private void checkAlterInstantConverters() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterInstantConverters"));
        }
    }
    
    public PartialConverter getPartialConverter(final Object o) {
        final PartialConverter partialConverter = (PartialConverter)this.iPartialConverters.select((o == null) ? null : o.getClass());
        if (partialConverter != null) {
            return partialConverter;
        }
        throw new IllegalArgumentException("No partial converter found for type: " + ((o == null) ? "null" : o.getClass().getName()));
    }
    
    public PartialConverter[] getPartialConverters() {
        final ConverterSet iPartialConverters = this.iPartialConverters;
        final PartialConverter[] array = new PartialConverter[iPartialConverters.size()];
        iPartialConverters.copyInto(array);
        return array;
    }
    
    public PartialConverter addPartialConverter(final PartialConverter partialConverter) throws SecurityException {
        this.checkAlterPartialConverters();
        if (partialConverter == null) {
            return null;
        }
        final PartialConverter[] array = { null };
        this.iPartialConverters = this.iPartialConverters.add(partialConverter, array);
        return array[0];
    }
    
    public PartialConverter removePartialConverter(final PartialConverter partialConverter) throws SecurityException {
        this.checkAlterPartialConverters();
        if (partialConverter == null) {
            return null;
        }
        final PartialConverter[] array = { null };
        this.iPartialConverters = this.iPartialConverters.remove(partialConverter, array);
        return array[0];
    }
    
    private void checkAlterPartialConverters() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterPartialConverters"));
        }
    }
    
    public DurationConverter getDurationConverter(final Object o) {
        final DurationConverter durationConverter = (DurationConverter)this.iDurationConverters.select((o == null) ? null : o.getClass());
        if (durationConverter != null) {
            return durationConverter;
        }
        throw new IllegalArgumentException("No duration converter found for type: " + ((o == null) ? "null" : o.getClass().getName()));
    }
    
    public DurationConverter[] getDurationConverters() {
        final ConverterSet iDurationConverters = this.iDurationConverters;
        final DurationConverter[] array = new DurationConverter[iDurationConverters.size()];
        iDurationConverters.copyInto(array);
        return array;
    }
    
    public DurationConverter addDurationConverter(final DurationConverter durationConverter) throws SecurityException {
        this.checkAlterDurationConverters();
        if (durationConverter == null) {
            return null;
        }
        final DurationConverter[] array = { null };
        this.iDurationConverters = this.iDurationConverters.add(durationConverter, array);
        return array[0];
    }
    
    public DurationConverter removeDurationConverter(final DurationConverter durationConverter) throws SecurityException {
        this.checkAlterDurationConverters();
        if (durationConverter == null) {
            return null;
        }
        final DurationConverter[] array = { null };
        this.iDurationConverters = this.iDurationConverters.remove(durationConverter, array);
        return array[0];
    }
    
    private void checkAlterDurationConverters() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterDurationConverters"));
        }
    }
    
    public PeriodConverter getPeriodConverter(final Object o) {
        final PeriodConverter periodConverter = (PeriodConverter)this.iPeriodConverters.select((o == null) ? null : o.getClass());
        if (periodConverter != null) {
            return periodConverter;
        }
        throw new IllegalArgumentException("No period converter found for type: " + ((o == null) ? "null" : o.getClass().getName()));
    }
    
    public PeriodConverter[] getPeriodConverters() {
        final ConverterSet iPeriodConverters = this.iPeriodConverters;
        final PeriodConverter[] array = new PeriodConverter[iPeriodConverters.size()];
        iPeriodConverters.copyInto(array);
        return array;
    }
    
    public PeriodConverter addPeriodConverter(final PeriodConverter periodConverter) throws SecurityException {
        this.checkAlterPeriodConverters();
        if (periodConverter == null) {
            return null;
        }
        final PeriodConverter[] array = { null };
        this.iPeriodConverters = this.iPeriodConverters.add(periodConverter, array);
        return array[0];
    }
    
    public PeriodConverter removePeriodConverter(final PeriodConverter periodConverter) throws SecurityException {
        this.checkAlterPeriodConverters();
        if (periodConverter == null) {
            return null;
        }
        final PeriodConverter[] array = { null };
        this.iPeriodConverters = this.iPeriodConverters.remove(periodConverter, array);
        return array[0];
    }
    
    private void checkAlterPeriodConverters() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterPeriodConverters"));
        }
    }
    
    public IntervalConverter getIntervalConverter(final Object o) {
        final IntervalConverter intervalConverter = (IntervalConverter)this.iIntervalConverters.select((o == null) ? null : o.getClass());
        if (intervalConverter != null) {
            return intervalConverter;
        }
        throw new IllegalArgumentException("No interval converter found for type: " + ((o == null) ? "null" : o.getClass().getName()));
    }
    
    public IntervalConverter[] getIntervalConverters() {
        final ConverterSet iIntervalConverters = this.iIntervalConverters;
        final IntervalConverter[] array = new IntervalConverter[iIntervalConverters.size()];
        iIntervalConverters.copyInto(array);
        return array;
    }
    
    public IntervalConverter addIntervalConverter(final IntervalConverter intervalConverter) throws SecurityException {
        this.checkAlterIntervalConverters();
        if (intervalConverter == null) {
            return null;
        }
        final IntervalConverter[] array = { null };
        this.iIntervalConverters = this.iIntervalConverters.add(intervalConverter, array);
        return array[0];
    }
    
    public IntervalConverter removeIntervalConverter(final IntervalConverter intervalConverter) throws SecurityException {
        this.checkAlterIntervalConverters();
        if (intervalConverter == null) {
            return null;
        }
        final IntervalConverter[] array = { null };
        this.iIntervalConverters = this.iIntervalConverters.remove(intervalConverter, array);
        return array[0];
    }
    
    private void checkAlterIntervalConverters() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterIntervalConverters"));
        }
    }
    
    @Override
    public String toString() {
        return "ConverterManager[" + this.iInstantConverters.size() + " instant," + this.iPartialConverters.size() + " partial," + this.iDurationConverters.size() + " duration," + this.iPeriodConverters.size() + " period," + this.iIntervalConverters.size() + " interval]";
    }
}
