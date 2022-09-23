// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.HashMap;
import java.util.Collection;
import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.util.ArrayList;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import javax.ws.rs.core.MediaType;

public class MediaTypes
{
    public static final String WADL_STRING = "application/vnd.sun.wadl+xml";
    public static final MediaType WADL;
    public static final String WADL_JSON_STRING = "application/vnd.sun.wadl+json";
    public static final MediaType WADL_JSON;
    public static final MediaType FAST_INFOSET;
    public static final Comparator<MediaType> MEDIA_TYPE_COMPARATOR;
    public static final Comparator<List<? extends MediaType>> MEDIA_TYPE_LIST_COMPARATOR;
    public static final MediaType GENERAL_MEDIA_TYPE;
    public static final List<MediaType> GENERAL_MEDIA_TYPE_LIST;
    public static final AcceptableMediaType GENERAL_ACCEPT_MEDIA_TYPE;
    public static final List<AcceptableMediaType> GENERAL_ACCEPT_MEDIA_TYPE_LIST;
    public static final Comparator<QualitySourceMediaType> QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR;
    public static final List<MediaType> GENERAL_QUALITY_SOURCE_MEDIA_TYPE_LIST;
    private static Map<String, MediaType> mediaTypeCache;
    
    private MediaTypes() {
    }
    
    public static final boolean typeEquals(final MediaType m1, final MediaType m2) {
        return m1 != null && m2 != null && m1.getSubtype().equalsIgnoreCase(m2.getSubtype()) && m1.getType().equalsIgnoreCase(m2.getType());
    }
    
    public static final boolean intersects(final List<? extends MediaType> ml1, final List<? extends MediaType> ml2) {
        for (final MediaType m1 : ml1) {
            for (final MediaType m2 : ml2) {
                if (typeEquals(m1, m2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final MediaType mostSpecific(final MediaType m1, final MediaType m2) {
        if (m1.isWildcardSubtype() && !m2.isWildcardSubtype()) {
            return m2;
        }
        if (m1.isWildcardType() && !m2.isWildcardType()) {
            return m2;
        }
        return m1;
    }
    
    private static List<MediaType> createMediaTypeList() {
        return Collections.singletonList(MediaTypes.GENERAL_MEDIA_TYPE);
    }
    
    private static List<AcceptableMediaType> createAcceptMediaTypeList() {
        return Collections.singletonList(MediaTypes.GENERAL_ACCEPT_MEDIA_TYPE);
    }
    
    public static List<MediaType> createMediaTypes(final Consumes mime) {
        if (mime == null) {
            return MediaTypes.GENERAL_MEDIA_TYPE_LIST;
        }
        return createMediaTypes(mime.value());
    }
    
    public static List<MediaType> createMediaTypes(final Produces mime) {
        if (mime == null) {
            return MediaTypes.GENERAL_MEDIA_TYPE_LIST;
        }
        return createMediaTypes(mime.value());
    }
    
    public static List<MediaType> createMediaTypes(final String[] mediaTypes) {
        final List<MediaType> l = new ArrayList<MediaType>();
        try {
            for (final String mediaType : mediaTypes) {
                HttpHeaderReader.readMediaTypes(l, mediaType);
            }
            Collections.sort(l, MediaTypes.MEDIA_TYPE_COMPARATOR);
            return l;
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private static List<MediaType> createQualitySourceMediaTypeList() {
        return (List<MediaType>)Collections.singletonList(new QualitySourceMediaType("*", "*"));
    }
    
    public static List<MediaType> createQualitySourceMediaTypes(final Produces mime) {
        if (mime == null || mime.value().length == 0) {
            return MediaTypes.GENERAL_QUALITY_SOURCE_MEDIA_TYPE_LIST;
        }
        return new ArrayList<MediaType>(createQualitySourceMediaTypes(mime.value()));
    }
    
    public static List<QualitySourceMediaType> createQualitySourceMediaTypes(final String[] mediaTypes) {
        try {
            return HttpHeaderReader.readQualitySourceMediaType(mediaTypes);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public static MediaType getTypeWildCart(final MediaType mediaType) {
        MediaType mt = MediaTypes.mediaTypeCache.get(mediaType.getType());
        if (mt == null) {
            mt = new MediaType(mediaType.getType(), "*");
        }
        return mt;
    }
    
    static {
        WADL = MediaType.valueOf("application/vnd.sun.wadl+xml");
        WADL_JSON = MediaType.valueOf("application/vnd.sun.wadl+json");
        FAST_INFOSET = MediaType.valueOf("application/fastinfoset");
        MEDIA_TYPE_COMPARATOR = new Comparator<MediaType>() {
            @Override
            public int compare(final MediaType o1, final MediaType o2) {
                if (o1.getType().equals("*") && !o2.getType().equals("*")) {
                    return 1;
                }
                if (o2.getType().equals("*") && !o1.getType().equals("*")) {
                    return -1;
                }
                if (o1.getSubtype().equals("*") && !o2.getSubtype().equals("*")) {
                    return 1;
                }
                if (o2.getSubtype().equals("*") && !o1.getSubtype().equals("*")) {
                    return -1;
                }
                return 0;
            }
        };
        MEDIA_TYPE_LIST_COMPARATOR = new Comparator<List<? extends MediaType>>() {
            @Override
            public int compare(final List<? extends MediaType> o1, final List<? extends MediaType> o2) {
                return MediaTypes.MEDIA_TYPE_COMPARATOR.compare(this.getLeastSpecific(o1), this.getLeastSpecific(o2));
            }
            
            public MediaType getLeastSpecific(final List<? extends MediaType> l) {
                return (MediaType)l.get(l.size() - 1);
            }
        };
        GENERAL_MEDIA_TYPE = new MediaType("*", "*");
        GENERAL_MEDIA_TYPE_LIST = createMediaTypeList();
        GENERAL_ACCEPT_MEDIA_TYPE = new AcceptableMediaType("*", "*");
        GENERAL_ACCEPT_MEDIA_TYPE_LIST = createAcceptMediaTypeList();
        QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR = new Comparator<QualitySourceMediaType>() {
            @Override
            public int compare(final QualitySourceMediaType o1, final QualitySourceMediaType o2) {
                final int i = o2.getQualitySource() - o1.getQualitySource();
                if (i != 0) {
                    return i;
                }
                return MediaTypes.MEDIA_TYPE_COMPARATOR.compare(o1, o2);
            }
        };
        GENERAL_QUALITY_SOURCE_MEDIA_TYPE_LIST = createQualitySourceMediaTypeList();
        MediaTypes.mediaTypeCache = new HashMap<String, MediaType>() {
            {
                this.put("application", new MediaType("application", "*"));
                this.put("multipart", new MediaType("multipart", "*"));
                this.put("text", new MediaType("text", "*"));
            }
        };
    }
}
