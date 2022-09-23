// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.FileHandlerReloadingDetector;
import org.apache.commons.configuration2.reloading.ReloadingDetector;
import org.apache.commons.configuration2.io.FileHandler;

public class DefaultReloadingDetectorFactory implements ReloadingDetectorFactory
{
    @Override
    public ReloadingDetector createReloadingDetector(final FileHandler handler, final FileBasedBuilderParametersImpl params) throws ConfigurationException {
        final Long refreshDelay = params.getReloadingRefreshDelay();
        return (refreshDelay != null) ? new FileHandlerReloadingDetector(handler, refreshDelay) : new FileHandlerReloadingDetector(handler);
    }
}
