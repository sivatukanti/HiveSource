// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.sync.Synchronizer;
import java.util.Collection;
import org.apache.commons.configuration2.interpol.Lookup;
import java.util.Map;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.io.ConfigurationLogger;

public interface BasicBuilderProperties<T>
{
    T setLogger(final ConfigurationLogger p0);
    
    T setThrowExceptionOnMissing(final boolean p0);
    
    T setListDelimiterHandler(final ListDelimiterHandler p0);
    
    T setInterpolator(final ConfigurationInterpolator p0);
    
    T setPrefixLookups(final Map<String, ? extends Lookup> p0);
    
    T setDefaultLookups(final Collection<? extends Lookup> p0);
    
    T setParentInterpolator(final ConfigurationInterpolator p0);
    
    T setSynchronizer(final Synchronizer p0);
    
    T setConversionHandler(final ConversionHandler p0);
    
    T setConfigurationDecoder(final ConfigurationDecoder p0);
    
    T setBeanHelper(final BeanHelper p0);
}
