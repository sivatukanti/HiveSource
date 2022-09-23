// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.util.Iterator;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Comparator;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Map;

public class MediaType
{
    private String type;
    private String subtype;
    private Map<String, String> parameters;
    private static final Map<String, String> emptyMap;
    private static final RuntimeDelegate.HeaderDelegate<MediaType> delegate;
    public static final String MEDIA_TYPE_WILDCARD = "*";
    public static final String WILDCARD = "*/*";
    public static final MediaType WILDCARD_TYPE;
    public static final String APPLICATION_XML = "application/xml";
    public static final MediaType APPLICATION_XML_TYPE;
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final MediaType APPLICATION_ATOM_XML_TYPE;
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final MediaType APPLICATION_XHTML_XML_TYPE;
    public static final String APPLICATION_SVG_XML = "application/svg+xml";
    public static final MediaType APPLICATION_SVG_XML_TYPE;
    public static final String APPLICATION_JSON = "application/json";
    public static final MediaType APPLICATION_JSON_TYPE;
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final MediaType APPLICATION_FORM_URLENCODED_TYPE;
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final MediaType MULTIPART_FORM_DATA_TYPE;
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final MediaType APPLICATION_OCTET_STREAM_TYPE;
    public static final String TEXT_PLAIN = "text/plain";
    public static final MediaType TEXT_PLAIN_TYPE;
    public static final String TEXT_XML = "text/xml";
    public static final MediaType TEXT_XML_TYPE;
    public static final String TEXT_HTML = "text/html";
    public static final MediaType TEXT_HTML_TYPE;
    
    public static MediaType valueOf(final String type) throws IllegalArgumentException {
        return MediaType.delegate.fromString(type);
    }
    
    public MediaType(final String type, final String subtype, final Map<String, String> parameters) {
        this.type = ((type == null) ? "*" : type);
        this.subtype = ((subtype == null) ? "*" : subtype);
        if (parameters == null) {
            this.parameters = MediaType.emptyMap;
        }
        else {
            final Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
                public int compare(final String o1, final String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            for (final Map.Entry<String, String> e : parameters.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
            this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
        }
    }
    
    public MediaType(final String type, final String subtype) {
        this(type, subtype, MediaType.emptyMap);
    }
    
    public MediaType() {
        this("*", "*");
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean isWildcardType() {
        return this.getType().equals("*");
    }
    
    public String getSubtype() {
        return this.subtype;
    }
    
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals("*");
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public boolean isCompatible(final MediaType other) {
        return other != null && (this.type.equals("*") || other.type.equals("*") || (this.type.equalsIgnoreCase(other.type) && (this.subtype.equals("*") || other.subtype.equals("*"))) || (this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype)));
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MediaType)) {
            return false;
        }
        final MediaType other = (MediaType)obj;
        return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype) && this.parameters.equals(other.parameters);
    }
    
    @Override
    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
    }
    
    @Override
    public String toString() {
        return MediaType.delegate.toString(this);
    }
    
    static {
        emptyMap = Collections.emptyMap();
        delegate = RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class);
        WILDCARD_TYPE = new MediaType();
        APPLICATION_XML_TYPE = new MediaType("application", "xml");
        APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");
        APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");
        APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");
        APPLICATION_JSON_TYPE = new MediaType("application", "json");
        APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");
        MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");
        APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");
        TEXT_PLAIN_TYPE = new MediaType("text", "plain");
        TEXT_XML_TYPE = new MediaType("text", "xml");
        TEXT_HTML_TYPE = new MediaType("text", "html");
    }
}
