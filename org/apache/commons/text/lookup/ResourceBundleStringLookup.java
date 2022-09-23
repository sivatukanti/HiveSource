// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.lookup;

import java.util.ResourceBundle;

final class ResourceBundleStringLookup extends AbstractStringLookup
{
    static final ResourceBundleStringLookup INSTANCE;
    
    private ResourceBundleStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split(":");
        final int keyLen = keys.length;
        if (keyLen != 2) {
            throw IllegalArgumentExceptions.format("Bad ResourceBundle key format [%s]. Expected format is BundleName:KeyName.", key);
        }
        final String bundleName = keys[0];
        final String bundleKey = keys[1];
        try {
            return ResourceBundle.getBundle(bundleName).getString(bundleKey);
        }
        catch (Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up ResourceBundle [%s] and key [%s].", bundleName, bundleKey);
        }
    }
    
    static {
        INSTANCE = new ResourceBundleStringLookup();
    }
}
