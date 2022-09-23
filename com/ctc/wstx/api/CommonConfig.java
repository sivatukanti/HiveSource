// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.api;

import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ArgUtil;
import java.util.HashMap;
import org.codehaus.stax2.XMLStreamProperties;

abstract class CommonConfig implements XMLStreamProperties
{
    protected static final String IMPL_NAME = "woodstox";
    protected static final String IMPL_VERSION = "5.0";
    static final int PROP_IMPL_NAME = 1;
    static final int PROP_IMPL_VERSION = 2;
    static final int PROP_SUPPORTS_XML11 = 3;
    static final int PROP_SUPPORT_XMLID = 4;
    static final int PROP_RETURN_NULL_FOR_DEFAULT_NAMESPACE = 5;
    static final HashMap<String, Integer> sStdProperties;
    protected boolean mReturnNullForDefaultNamespace;
    
    protected CommonConfig(final CommonConfig base) {
        this.mReturnNullForDefaultNamespace = ((base == null) ? Boolean.getBoolean("com.ctc.wstx.returnNullForDefaultNamespace") : base.mReturnNullForDefaultNamespace);
    }
    
    public Object getProperty(final String propName) {
        int id = this.findPropertyId(propName);
        if (id >= 0) {
            return this.getProperty(id);
        }
        id = this.findStdPropertyId(propName);
        if (id < 0) {
            this.reportUnknownProperty(propName);
            return null;
        }
        return this.getStdProperty(id);
    }
    
    public boolean isPropertySupported(final String propName) {
        return this.findPropertyId(propName) >= 0 || this.findStdPropertyId(propName) >= 0;
    }
    
    public boolean setProperty(final String propName, final Object value) {
        int id = this.findPropertyId(propName);
        if (id >= 0) {
            return this.setProperty(propName, id, value);
        }
        id = this.findStdPropertyId(propName);
        if (id < 0) {
            this.reportUnknownProperty(propName);
            return false;
        }
        return this.setStdProperty(propName, id, value);
    }
    
    protected void reportUnknownProperty(final String propName) {
        throw new IllegalArgumentException("Unrecognized property '" + propName + "'");
    }
    
    public final Object safeGetProperty(final String propName) {
        int id = this.findPropertyId(propName);
        if (id >= 0) {
            return this.getProperty(id);
        }
        id = this.findStdPropertyId(propName);
        if (id < 0) {
            return null;
        }
        return this.getStdProperty(id);
    }
    
    public static String getImplName() {
        return "woodstox";
    }
    
    public static String getImplVersion() {
        return "5.0";
    }
    
    protected abstract int findPropertyId(final String p0);
    
    public boolean doesSupportXml11() {
        return true;
    }
    
    public boolean doesSupportXmlId() {
        return true;
    }
    
    public boolean returnNullForDefaultNamespace() {
        return this.mReturnNullForDefaultNamespace;
    }
    
    protected abstract Object getProperty(final int p0);
    
    protected abstract boolean setProperty(final String p0, final int p1, final Object p2);
    
    protected int findStdPropertyId(final String propName) {
        final Integer I = CommonConfig.sStdProperties.get(propName);
        return (I == null) ? -1 : I;
    }
    
    protected boolean setStdProperty(final String propName, final int id, final Object value) {
        switch (id) {
            case 5: {
                this.mReturnNullForDefaultNamespace = ArgUtil.convertToBoolean(propName, value);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    protected Object getStdProperty(final int id) {
        switch (id) {
            case 1: {
                return "woodstox";
            }
            case 2: {
                return "5.0";
            }
            case 3: {
                return this.doesSupportXml11() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 4: {
                return this.doesSupportXmlId() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 5: {
                return this.returnNullForDefaultNamespace() ? Boolean.TRUE : Boolean.FALSE;
            }
            default: {
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            }
        }
    }
    
    static {
        (sStdProperties = new HashMap<String, Integer>(16)).put("org.codehaus.stax2.implName", DataUtil.Integer(1));
        CommonConfig.sStdProperties.put("org.codehaus.stax2.implVersion", DataUtil.Integer(2));
        CommonConfig.sStdProperties.put("org.codehaus.stax2.supportsXml11", DataUtil.Integer(3));
        CommonConfig.sStdProperties.put("org.codehaus.stax2.supportXmlId", DataUtil.Integer(4));
        CommonConfig.sStdProperties.put("com.ctc.wstx.returnNullForDefaultNamespace", DataUtil.Integer(5));
        CommonConfig.sStdProperties.put("http://java.sun.com/xml/stream/properties/implementation-name", DataUtil.Integer(1));
    }
}
