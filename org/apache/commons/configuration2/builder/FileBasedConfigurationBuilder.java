// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.XMLPropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.configuration2.io.FileHandler;
import java.util.Map;
import org.apache.commons.configuration2.FileBasedConfiguration;

public class FileBasedConfigurationBuilder<T extends FileBasedConfiguration> extends BasicConfigurationBuilder<T>
{
    private static final Map<Class<?>, String> DEFAULT_ENCODINGS;
    private FileHandler currentFileHandler;
    private AutoSaveListener autoSaveListener;
    private boolean resetParameters;
    
    public FileBasedConfigurationBuilder(final Class<? extends T> resCls) {
        super(resCls);
    }
    
    public FileBasedConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params) {
        super(resCls, params);
    }
    
    public FileBasedConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params, final boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
    }
    
    public static String getDefaultEncoding(final Class<?> configClass) {
        String enc = FileBasedConfigurationBuilder.DEFAULT_ENCODINGS.get(configClass);
        if (enc != null || configClass == null) {
            return enc;
        }
        final List<Class<?>> superclasses = ClassUtils.getAllSuperclasses(configClass);
        for (final Class<?> cls : superclasses) {
            enc = FileBasedConfigurationBuilder.DEFAULT_ENCODINGS.get(cls);
            if (enc != null) {
                return enc;
            }
        }
        final List<Class<?>> interfaces = ClassUtils.getAllInterfaces(configClass);
        for (final Class<?> cls2 : interfaces) {
            enc = FileBasedConfigurationBuilder.DEFAULT_ENCODINGS.get(cls2);
            if (enc != null) {
                return enc;
            }
        }
        return null;
    }
    
    public static void setDefaultEncoding(final Class<?> configClass, final String encoding) {
        if (configClass == null) {
            throw new IllegalArgumentException("Configuration class must not be null!");
        }
        if (encoding == null) {
            FileBasedConfigurationBuilder.DEFAULT_ENCODINGS.remove(configClass);
        }
        else {
            FileBasedConfigurationBuilder.DEFAULT_ENCODINGS.put(configClass, encoding);
        }
    }
    
    @Override
    public FileBasedConfigurationBuilder<T> configure(final BuilderParameters... params) {
        super.configure(params);
        return this;
    }
    
    public synchronized FileHandler getFileHandler() {
        return (this.currentFileHandler != null) ? this.currentFileHandler : this.fetchFileHandlerFromParameters();
    }
    
    @Override
    public synchronized BasicConfigurationBuilder<T> setParameters(final Map<String, Object> params) {
        super.setParameters(params);
        this.resetParameters = true;
        return this;
    }
    
    public void save() throws ConfigurationException {
        this.getFileHandler().save();
    }
    
    public synchronized boolean isAutoSave() {
        return this.autoSaveListener != null;
    }
    
    public synchronized void setAutoSave(final boolean enabled) {
        if (enabled) {
            this.installAutoSaveListener();
        }
        else {
            this.removeAutoSaveListener();
        }
    }
    
    @Override
    protected void initResultInstance(final T obj) throws ConfigurationException {
        super.initResultInstance(obj);
        final FileHandler srcHandler = (this.currentFileHandler != null && !this.resetParameters) ? this.currentFileHandler : this.fetchFileHandlerFromParameters();
        this.currentFileHandler = new FileHandler(obj, srcHandler);
        if (this.autoSaveListener != null) {
            this.autoSaveListener.updateFileHandler(this.currentFileHandler);
        }
        this.initFileHandler(this.currentFileHandler);
        this.resetParameters = false;
    }
    
    protected void initFileHandler(final FileHandler handler) throws ConfigurationException {
        this.initEncoding(handler);
        if (handler.isLocationDefined()) {
            handler.locate();
            handler.load();
        }
    }
    
    private FileHandler fetchFileHandlerFromParameters() {
        FileBasedBuilderParametersImpl fileParams = FileBasedBuilderParametersImpl.fromParameters(this.getParameters(), false);
        if (fileParams == null) {
            fileParams = new FileBasedBuilderParametersImpl();
            this.addParameters(fileParams.getParameters());
        }
        return fileParams.getFileHandler();
    }
    
    private void installAutoSaveListener() {
        if (this.autoSaveListener == null) {
            this.autoSaveListener = new AutoSaveListener(this);
            this.addEventListener(ConfigurationEvent.ANY, this.autoSaveListener);
            this.autoSaveListener.updateFileHandler(this.getFileHandler());
        }
    }
    
    private void removeAutoSaveListener() {
        if (this.autoSaveListener != null) {
            this.removeEventListener(ConfigurationEvent.ANY, this.autoSaveListener);
            this.autoSaveListener.updateFileHandler(null);
            this.autoSaveListener = null;
        }
    }
    
    private void initEncoding(final FileHandler handler) {
        if (StringUtils.isEmpty(handler.getEncoding())) {
            final String encoding = getDefaultEncoding(this.getResultClass());
            if (encoding != null) {
                handler.setEncoding(encoding);
            }
        }
    }
    
    private static Map<Class<?>, String> initializeDefaultEncodings() {
        final Map<Class<?>, String> enc = new ConcurrentHashMap<Class<?>, String>();
        enc.put(PropertiesConfiguration.class, "ISO-8859-1");
        enc.put(XMLPropertiesConfiguration.class, "UTF-8");
        return enc;
    }
    
    static {
        DEFAULT_ENCODINGS = initializeDefaultEncodings();
    }
}
