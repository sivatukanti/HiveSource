// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.ReloadingDetector;
import org.apache.commons.configuration2.io.FileHandler;

public interface ReloadingDetectorFactory
{
    ReloadingDetector createReloadingDetector(final FileHandler p0, final FileBasedBuilderParametersImpl p1) throws ConfigurationException;
}
