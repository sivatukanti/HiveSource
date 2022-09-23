// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.lookup;

import org.apache.commons.lang3.StringUtils;
import java.util.Locale;

final class JavaPlatformStringLookup extends AbstractStringLookup
{
    static final JavaPlatformStringLookup INSTANCE;
    
    private JavaPlatformStringLookup() {
    }
    
    String getHardware() {
        return "processors: " + Runtime.getRuntime().availableProcessors() + ", architecture: " + this.getSystemProperty("os.arch") + this.getSystemProperty("-", "sun.arch.data.model") + this.getSystemProperty(", instruction sets: ", "sun.cpu.isalist");
    }
    
    String getLocale() {
        return "default locale: " + Locale.getDefault() + ", platform encoding: " + this.getSystemProperty("file.encoding");
    }
    
    String getOperatingSystem() {
        return this.getSystemProperty("os.name") + " " + this.getSystemProperty("os.version") + this.getSystemProperty(" ", "sun.os.patch.level") + ", architecture: " + this.getSystemProperty("os.arch") + this.getSystemProperty("-", "sun.arch.data.model");
    }
    
    String getRuntime() {
        return this.getSystemProperty("java.runtime.name") + " (build " + this.getSystemProperty("java.runtime.version") + ") from " + this.getSystemProperty("java.vendor");
    }
    
    private String getSystemProperty(final String name) {
        return SystemPropertyStringLookup.INSTANCE.lookup(name);
    }
    
    private String getSystemProperty(final String prefix, final String name) {
        final String value = this.getSystemProperty(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return prefix + value;
    }
    
    String getVirtualMachine() {
        return this.getSystemProperty("java.vm.name") + " (build " + this.getSystemProperty("java.vm.version") + ", " + this.getSystemProperty("java.vm.info") + ")";
    }
    
    @Override
    public String lookup(final String key) {
        switch (key) {
            case "version": {
                return "Java version " + this.getSystemProperty("java.version");
            }
            case "runtime": {
                return this.getRuntime();
            }
            case "vm": {
                return this.getVirtualMachine();
            }
            case "os": {
                return this.getOperatingSystem();
            }
            case "hardware": {
                return this.getHardware();
            }
            case "locale": {
                return this.getLocale();
            }
            default: {
                throw new IllegalArgumentException(key);
            }
        }
    }
    
    static {
        INSTANCE = new JavaPlatformStringLookup();
    }
}
