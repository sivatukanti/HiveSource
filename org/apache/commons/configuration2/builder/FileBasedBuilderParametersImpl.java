// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystem;
import java.net.URL;
import java.io.File;
import java.util.Map;
import org.apache.commons.configuration2.io.FileHandler;

public class FileBasedBuilderParametersImpl extends BasicBuilderParameters implements FileBasedBuilderProperties<FileBasedBuilderParametersImpl>
{
    private static final String PARAM_KEY = "config-fileBased";
    private static final String PROP_REFRESH_DELAY = "reloadingRefreshDelay";
    private static final String PROP_DETECTOR_FACTORY = "reloadingDetectorFactory";
    private FileHandler fileHandler;
    private ReloadingDetectorFactory reloadingDetectorFactory;
    private Long reloadingRefreshDelay;
    
    public FileBasedBuilderParametersImpl() {
        this(null);
    }
    
    public FileBasedBuilderParametersImpl(final FileHandler handler) {
        this.fileHandler = ((handler != null) ? handler : new FileHandler());
    }
    
    public static FileBasedBuilderParametersImpl fromParameters(final Map<String, ?> params) {
        return fromParameters(params, false);
    }
    
    public static FileBasedBuilderParametersImpl fromParameters(final Map<String, ?> params, final boolean createIfMissing) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null!");
        }
        FileBasedBuilderParametersImpl instance = (FileBasedBuilderParametersImpl)params.get("config-fileBased");
        if (instance == null && createIfMissing) {
            instance = new FileBasedBuilderParametersImpl();
        }
        return instance;
    }
    
    public static FileBasedBuilderParametersImpl fromMap(final Map<String, ?> map) {
        final FileBasedBuilderParametersImpl params = new FileBasedBuilderParametersImpl(FileHandler.fromMap(map));
        if (map != null) {
            params.setReloadingRefreshDelay((Long)map.get("reloadingRefreshDelay"));
            params.setReloadingDetectorFactory((ReloadingDetectorFactory)map.get("reloadingDetectorFactory"));
        }
        return params;
    }
    
    @Override
    public void inheritFrom(final Map<String, ?> source) {
        super.inheritFrom(source);
        final FileBasedBuilderParametersImpl srcParams = fromParameters(source);
        if (srcParams != null) {
            this.setFileSystem(srcParams.getFileHandler().getFileSystem());
            this.setLocationStrategy(srcParams.getFileHandler().getLocationStrategy());
            if (srcParams.getFileHandler().getEncoding() != null) {
                this.setEncoding(srcParams.getFileHandler().getEncoding());
            }
            if (srcParams.getReloadingDetectorFactory() != null) {
                this.setReloadingDetectorFactory(srcParams.getReloadingDetectorFactory());
            }
            if (srcParams.getReloadingRefreshDelay() != null) {
                this.setReloadingRefreshDelay(srcParams.getReloadingRefreshDelay());
            }
        }
    }
    
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }
    
    public Long getReloadingRefreshDelay() {
        return this.reloadingRefreshDelay;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setReloadingRefreshDelay(final Long reloadingRefreshDelay) {
        this.reloadingRefreshDelay = reloadingRefreshDelay;
        return this;
    }
    
    public ReloadingDetectorFactory getReloadingDetectorFactory() {
        return this.reloadingDetectorFactory;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setReloadingDetectorFactory(final ReloadingDetectorFactory reloadingDetectorFactory) {
        this.reloadingDetectorFactory = reloadingDetectorFactory;
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setFile(final File file) {
        this.getFileHandler().setFile(file);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setURL(final URL url) {
        this.getFileHandler().setURL(url);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setPath(final String path) {
        this.getFileHandler().setPath(path);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setFileName(final String name) {
        this.getFileHandler().setFileName(name);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setBasePath(final String path) {
        this.getFileHandler().setBasePath(path);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setFileSystem(final FileSystem fs) {
        this.getFileHandler().setFileSystem(fs);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setLocationStrategy(final FileLocationStrategy strategy) {
        this.getFileHandler().setLocationStrategy(strategy);
        return this;
    }
    
    @Override
    public FileBasedBuilderParametersImpl setEncoding(final String enc) {
        this.getFileHandler().setEncoding(enc);
        return this;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        final Map<String, Object> params = super.getParameters();
        params.put("config-fileBased", this);
        return params;
    }
    
    @Override
    public FileBasedBuilderParametersImpl clone() {
        final FileBasedBuilderParametersImpl copy = (FileBasedBuilderParametersImpl)super.clone();
        copy.fileHandler = new FileHandler(this.fileHandler.getContent(), this.fileHandler);
        return copy;
    }
}
