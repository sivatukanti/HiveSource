// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import java.util.Map;
import org.apache.commons.configuration2.reloading.ReloadingDetector;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;
import org.apache.commons.configuration2.FileBasedConfiguration;

public class ReloadingFileBasedConfigurationBuilder<T extends FileBasedConfiguration> extends FileBasedConfigurationBuilder<T> implements ReloadingControllerSupport
{
    private static final ReloadingDetectorFactory DEFAULT_DETECTOR_FACTORY;
    private final ReloadingController reloadingController;
    private volatile ReloadingDetector resultReloadingDetector;
    
    public ReloadingFileBasedConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params) {
        super(resCls, params);
        this.reloadingController = this.createReloadingController();
    }
    
    public ReloadingFileBasedConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params, final boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
        this.reloadingController = this.createReloadingController();
    }
    
    public ReloadingFileBasedConfigurationBuilder(final Class<? extends T> resCls) {
        super(resCls);
        this.reloadingController = this.createReloadingController();
    }
    
    @Override
    public ReloadingController getReloadingController() {
        return this.reloadingController;
    }
    
    @Override
    public ReloadingFileBasedConfigurationBuilder<T> configure(final BuilderParameters... params) {
        super.configure(params);
        return this;
    }
    
    protected ReloadingDetector createReloadingDetector(final FileHandler handler, final FileBasedBuilderParametersImpl fbparams) throws ConfigurationException {
        return fetchDetectorFactory(fbparams).createReloadingDetector(handler, fbparams);
    }
    
    @Override
    protected void initFileHandler(final FileHandler handler) throws ConfigurationException {
        super.initFileHandler(handler);
        this.resultReloadingDetector = this.createReloadingDetector(handler, FileBasedBuilderParametersImpl.fromParameters(this.getParameters(), true));
    }
    
    private ReloadingController createReloadingController() {
        final ReloadingDetector ctrlDetector = this.createReloadingDetectorForController();
        final ReloadingController ctrl = new ReloadingController(ctrlDetector);
        this.connectToReloadingController(ctrl);
        return ctrl;
    }
    
    private ReloadingDetector createReloadingDetectorForController() {
        return new ReloadingDetector() {
            @Override
            public void reloadingPerformed() {
                final ReloadingDetector detector = ReloadingFileBasedConfigurationBuilder.this.resultReloadingDetector;
                if (detector != null) {
                    detector.reloadingPerformed();
                }
            }
            
            @Override
            public boolean isReloadingRequired() {
                final ReloadingDetector detector = ReloadingFileBasedConfigurationBuilder.this.resultReloadingDetector;
                return detector != null && detector.isReloadingRequired();
            }
        };
    }
    
    private static ReloadingDetectorFactory fetchDetectorFactory(final FileBasedBuilderParametersImpl params) {
        final ReloadingDetectorFactory factory = params.getReloadingDetectorFactory();
        return (factory != null) ? factory : ReloadingFileBasedConfigurationBuilder.DEFAULT_DETECTOR_FACTORY;
    }
    
    static {
        DEFAULT_DETECTOR_FACTORY = new DefaultReloadingDetectorFactory();
    }
}
