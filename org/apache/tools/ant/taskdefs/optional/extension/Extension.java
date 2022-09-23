// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.StringTokenizer;
import org.apache.tools.ant.util.StringUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.jar.Manifest;
import org.apache.tools.ant.util.DeweyDecimal;
import java.util.jar.Attributes;

public final class Extension
{
    public static final Attributes.Name EXTENSION_LIST;
    public static final Attributes.Name OPTIONAL_EXTENSION_LIST;
    public static final Attributes.Name EXTENSION_NAME;
    public static final Attributes.Name SPECIFICATION_VERSION;
    public static final Attributes.Name SPECIFICATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_VERSION;
    public static final Attributes.Name IMPLEMENTATION_VENDOR;
    public static final Attributes.Name IMPLEMENTATION_URL;
    public static final Attributes.Name IMPLEMENTATION_VENDOR_ID;
    public static final Compatibility COMPATIBLE;
    public static final Compatibility REQUIRE_SPECIFICATION_UPGRADE;
    public static final Compatibility REQUIRE_VENDOR_SWITCH;
    public static final Compatibility REQUIRE_IMPLEMENTATION_UPGRADE;
    public static final Compatibility INCOMPATIBLE;
    private String extensionName;
    private DeweyDecimal specificationVersion;
    private String specificationVendor;
    private String implementationVendorID;
    private String implementationVendor;
    private DeweyDecimal implementationVersion;
    private String implementationURL;
    
    public static Extension[] getAvailable(final Manifest manifest) {
        if (null == manifest) {
            return new Extension[0];
        }
        final ArrayList results = new ArrayList();
        final Attributes mainAttributes = manifest.getMainAttributes();
        if (null != mainAttributes) {
            final Extension extension = getExtension("", mainAttributes);
            if (null != extension) {
                results.add(extension);
            }
        }
        final Map entries = manifest.getEntries();
        for (final String key : entries.keySet()) {
            final Attributes attributes = entries.get(key);
            final Extension extension2 = getExtension("", attributes);
            if (null != extension2) {
                results.add(extension2);
            }
        }
        return results.toArray(new Extension[results.size()]);
    }
    
    public static Extension[] getRequired(final Manifest manifest) {
        return getListed(manifest, Attributes.Name.EXTENSION_LIST);
    }
    
    public static Extension[] getOptions(final Manifest manifest) {
        return getListed(manifest, Extension.OPTIONAL_EXTENSION_LIST);
    }
    
    public static void addExtension(final Extension extension, final Attributes attributes) {
        addExtension(extension, "", attributes);
    }
    
    public static void addExtension(final Extension extension, final String prefix, final Attributes attributes) {
        attributes.putValue(prefix + Extension.EXTENSION_NAME, extension.getExtensionName());
        final String specificationVendor = extension.getSpecificationVendor();
        if (null != specificationVendor) {
            attributes.putValue(prefix + Extension.SPECIFICATION_VENDOR, specificationVendor);
        }
        final DeweyDecimal specificationVersion = extension.getSpecificationVersion();
        if (null != specificationVersion) {
            attributes.putValue(prefix + Extension.SPECIFICATION_VERSION, specificationVersion.toString());
        }
        final String implementationVendorID = extension.getImplementationVendorID();
        if (null != implementationVendorID) {
            attributes.putValue(prefix + Extension.IMPLEMENTATION_VENDOR_ID, implementationVendorID);
        }
        final String implementationVendor = extension.getImplementationVendor();
        if (null != implementationVendor) {
            attributes.putValue(prefix + Extension.IMPLEMENTATION_VENDOR, implementationVendor);
        }
        final DeweyDecimal implementationVersion = extension.getImplementationVersion();
        if (null != implementationVersion) {
            attributes.putValue(prefix + Extension.IMPLEMENTATION_VERSION, implementationVersion.toString());
        }
        final String implementationURL = extension.getImplementationURL();
        if (null != implementationURL) {
            attributes.putValue(prefix + Extension.IMPLEMENTATION_URL, implementationURL);
        }
    }
    
    public Extension(final String extensionName, final String specificationVersion, final String specificationVendor, final String implementationVersion, final String implementationVendor, final String implementationVendorId, final String implementationURL) {
        this.extensionName = extensionName;
        this.specificationVendor = specificationVendor;
        if (null != specificationVersion) {
            try {
                this.specificationVersion = new DeweyDecimal(specificationVersion);
            }
            catch (NumberFormatException nfe) {
                final String error = "Bad specification version format '" + specificationVersion + "' in '" + extensionName + "'. (Reason: " + nfe + ")";
                throw new IllegalArgumentException(error);
            }
        }
        this.implementationURL = implementationURL;
        this.implementationVendor = implementationVendor;
        this.implementationVendorID = implementationVendorId;
        if (null != implementationVersion) {
            try {
                this.implementationVersion = new DeweyDecimal(implementationVersion);
            }
            catch (NumberFormatException nfe) {
                final String error = "Bad implementation version format '" + implementationVersion + "' in '" + extensionName + "'. (Reason: " + nfe + ")";
                throw new IllegalArgumentException(error);
            }
        }
        if (null == this.extensionName) {
            throw new NullPointerException("extensionName property is null");
        }
    }
    
    public String getExtensionName() {
        return this.extensionName;
    }
    
    public String getSpecificationVendor() {
        return this.specificationVendor;
    }
    
    public DeweyDecimal getSpecificationVersion() {
        return this.specificationVersion;
    }
    
    public String getImplementationURL() {
        return this.implementationURL;
    }
    
    public String getImplementationVendor() {
        return this.implementationVendor;
    }
    
    public String getImplementationVendorID() {
        return this.implementationVendorID;
    }
    
    public DeweyDecimal getImplementationVersion() {
        return this.implementationVersion;
    }
    
    public Compatibility getCompatibilityWith(final Extension required) {
        if (!this.extensionName.equals(required.getExtensionName())) {
            return Extension.INCOMPATIBLE;
        }
        final DeweyDecimal requiredSpecificationVersion = required.getSpecificationVersion();
        if (null != requiredSpecificationVersion && (null == this.specificationVersion || !this.isCompatible(this.specificationVersion, requiredSpecificationVersion))) {
            return Extension.REQUIRE_SPECIFICATION_UPGRADE;
        }
        final String requiredImplementationVendorID = required.getImplementationVendorID();
        if (null != requiredImplementationVendorID && (null == this.implementationVendorID || !this.implementationVendorID.equals(requiredImplementationVendorID))) {
            return Extension.REQUIRE_VENDOR_SWITCH;
        }
        final DeweyDecimal requiredImplementationVersion = required.getImplementationVersion();
        if (null != requiredImplementationVersion && (null == this.implementationVersion || !this.isCompatible(this.implementationVersion, requiredImplementationVersion))) {
            return Extension.REQUIRE_IMPLEMENTATION_UPGRADE;
        }
        return Extension.COMPATIBLE;
    }
    
    public boolean isCompatibleWith(final Extension required) {
        return Extension.COMPATIBLE == this.getCompatibilityWith(required);
    }
    
    @Override
    public String toString() {
        final String brace = ": ";
        final StringBuffer sb = new StringBuffer(Extension.EXTENSION_NAME.toString());
        sb.append(": ");
        sb.append(this.extensionName);
        sb.append(StringUtils.LINE_SEP);
        if (null != this.specificationVersion) {
            sb.append(Extension.SPECIFICATION_VERSION);
            sb.append(": ");
            sb.append(this.specificationVersion);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.specificationVendor) {
            sb.append(Extension.SPECIFICATION_VENDOR);
            sb.append(": ");
            sb.append(this.specificationVendor);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationVersion) {
            sb.append(Extension.IMPLEMENTATION_VERSION);
            sb.append(": ");
            sb.append(this.implementationVersion);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationVendorID) {
            sb.append(Extension.IMPLEMENTATION_VENDOR_ID);
            sb.append(": ");
            sb.append(this.implementationVendorID);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationVendor) {
            sb.append(Extension.IMPLEMENTATION_VENDOR);
            sb.append(": ");
            sb.append(this.implementationVendor);
            sb.append(StringUtils.LINE_SEP);
        }
        if (null != this.implementationURL) {
            sb.append(Extension.IMPLEMENTATION_URL);
            sb.append(": ");
            sb.append(this.implementationURL);
            sb.append(StringUtils.LINE_SEP);
        }
        return sb.toString();
    }
    
    private boolean isCompatible(final DeweyDecimal first, final DeweyDecimal second) {
        return first.isGreaterThanOrEqual(second);
    }
    
    private static Extension[] getListed(final Manifest manifest, final Attributes.Name listKey) {
        final ArrayList results = new ArrayList();
        final Attributes mainAttributes = manifest.getMainAttributes();
        if (null != mainAttributes) {
            getExtension(mainAttributes, results, listKey);
        }
        final Map entries = manifest.getEntries();
        for (final String key : entries.keySet()) {
            final Attributes attributes = entries.get(key);
            getExtension(attributes, results, listKey);
        }
        return results.toArray(new Extension[results.size()]);
    }
    
    private static void getExtension(final Attributes attributes, final ArrayList required, final Attributes.Name listKey) {
        final String names = attributes.getValue(listKey);
        if (null == names) {
            return;
        }
        final String[] extentions = split(names, " ");
        for (int i = 0; i < extentions.length; ++i) {
            final String prefix = extentions[i] + "-";
            final Extension extension = getExtension(prefix, attributes);
            if (null != extension) {
                required.add(extension);
            }
        }
    }
    
    private static String[] split(final String string, final String onToken) {
        final StringTokenizer tokenizer = new StringTokenizer(string, onToken);
        final String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }
    
    private static Extension getExtension(final String prefix, final Attributes attributes) {
        final String nameKey = prefix + Extension.EXTENSION_NAME;
        final String name = getTrimmedString(attributes.getValue(nameKey));
        if (null == name) {
            return null;
        }
        final String specVendorKey = prefix + Extension.SPECIFICATION_VENDOR;
        final String specVendor = getTrimmedString(attributes.getValue(specVendorKey));
        final String specVersionKey = prefix + Extension.SPECIFICATION_VERSION;
        final String specVersion = getTrimmedString(attributes.getValue(specVersionKey));
        final String impVersionKey = prefix + Extension.IMPLEMENTATION_VERSION;
        final String impVersion = getTrimmedString(attributes.getValue(impVersionKey));
        final String impVendorKey = prefix + Extension.IMPLEMENTATION_VENDOR;
        final String impVendor = getTrimmedString(attributes.getValue(impVendorKey));
        final String impVendorIDKey = prefix + Extension.IMPLEMENTATION_VENDOR_ID;
        final String impVendorId = getTrimmedString(attributes.getValue(impVendorIDKey));
        final String impURLKey = prefix + Extension.IMPLEMENTATION_URL;
        final String impURL = getTrimmedString(attributes.getValue(impURLKey));
        return new Extension(name, specVersion, specVendor, impVersion, impVendor, impVendorId, impURL);
    }
    
    private static String getTrimmedString(final String value) {
        return (null == value) ? null : value.trim();
    }
    
    static {
        EXTENSION_LIST = new Attributes.Name("Extension-List");
        OPTIONAL_EXTENSION_LIST = new Attributes.Name("Optional-Extension-List");
        EXTENSION_NAME = new Attributes.Name("Extension-Name");
        SPECIFICATION_VERSION = Attributes.Name.SPECIFICATION_VERSION;
        SPECIFICATION_VENDOR = Attributes.Name.SPECIFICATION_VENDOR;
        IMPLEMENTATION_VERSION = Attributes.Name.IMPLEMENTATION_VERSION;
        IMPLEMENTATION_VENDOR = Attributes.Name.IMPLEMENTATION_VENDOR;
        IMPLEMENTATION_URL = new Attributes.Name("Implementation-URL");
        IMPLEMENTATION_VENDOR_ID = new Attributes.Name("Implementation-Vendor-Id");
        COMPATIBLE = new Compatibility("COMPATIBLE");
        REQUIRE_SPECIFICATION_UPGRADE = new Compatibility("REQUIRE_SPECIFICATION_UPGRADE");
        REQUIRE_VENDOR_SWITCH = new Compatibility("REQUIRE_VENDOR_SWITCH");
        REQUIRE_IMPLEMENTATION_UPGRADE = new Compatibility("REQUIRE_IMPLEMENTATION_UPGRADE");
        INCOMPATIBLE = new Compatibility("INCOMPATIBLE");
    }
}
