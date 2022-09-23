// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import java.util.Iterator;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.builder.BuilderParameters;
import java.util.Collection;

public class FileExtensionConfigurationBuilderProvider extends BaseConfigurationBuilderProvider
{
    private static final char EXT_SEPARATOR = '.';
    private final String matchingConfigurationClass;
    private final String extension;
    
    public FileExtensionConfigurationBuilderProvider(final String bldrCls, final String reloadBldrCls, final String matchingConfigCls, final String defConfigClass, final String ext, final Collection<String> paramCls) {
        super(bldrCls, reloadBldrCls, defConfigClass, paramCls);
        if (matchingConfigCls == null) {
            throw new IllegalArgumentException("Matching configuration class must not be null!");
        }
        if (ext == null) {
            throw new IllegalArgumentException("File extension must not be null!");
        }
        this.matchingConfigurationClass = matchingConfigCls;
        this.extension = ext;
    }
    
    public String getMatchingConfigurationClass() {
        return this.matchingConfigurationClass;
    }
    
    public String getExtension() {
        return this.extension;
    }
    
    @Override
    protected String determineConfigurationClass(final ConfigurationDeclaration decl, final Collection<BuilderParameters> params) throws ConfigurationException {
        final String currentExt = extractExtension(fetchCurrentFileName(params));
        return this.getExtension().equalsIgnoreCase(currentExt) ? this.getMatchingConfigurationClass() : this.getConfigurationClass();
    }
    
    private static String fetchCurrentFileName(final Collection<BuilderParameters> params) {
        for (final BuilderParameters p : params) {
            if (p instanceof FileBasedBuilderParametersImpl) {
                final FileBasedBuilderParametersImpl fp = (FileBasedBuilderParametersImpl)p;
                return fp.getFileHandler().getFileName();
            }
        }
        return null;
    }
    
    private static String extractExtension(final String fileName) {
        if (fileName == null) {
            return null;
        }
        final int pos = fileName.lastIndexOf(46);
        return (pos < 0) ? null : fileName.substring(pos + 1);
    }
}
