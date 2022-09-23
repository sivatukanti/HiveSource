// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.server.impl.model.HttpHelper;
import java.util.HashSet;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.core.header.QualitySourceMediaType;
import javax.ws.rs.core.Variant;
import java.util.Iterator;
import com.sun.jersey.core.header.QualityFactor;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import com.sun.jersey.core.header.AcceptableToken;
import java.util.Locale;
import com.sun.jersey.core.header.AcceptableLanguageTag;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.AcceptableMediaType;

public final class VariantSelector
{
    private static final DimensionChecker<AcceptableMediaType, MediaType> MEDIA_TYPE_DC;
    private static final DimensionChecker<AcceptableLanguageTag, Locale> LANGUAGE_TAG_DC;
    private static final DimensionChecker<AcceptableToken, String> CHARSET_DC;
    private static final DimensionChecker<AcceptableToken, String> ENCODING_DC;
    
    private VariantSelector() {
    }
    
    private static <T extends QualityFactor, U> LinkedList<VariantHolder> selectVariants(final LinkedList<VariantHolder> vs, final List<T> as, final DimensionChecker<T, U> dc, final Set<String> vary) {
        int cq = 0;
        int cqs = 0;
        final LinkedList<VariantHolder> selected = new LinkedList<VariantHolder>();
        for (final T a : as) {
            final int q = a.getQuality();
            final Iterator<VariantHolder> iv = vs.iterator();
            while (iv.hasNext()) {
                final VariantHolder v = iv.next();
                final U d = dc.getDimension(v);
                if (d != null) {
                    vary.add(dc.getVaryHeaderValue());
                    final int qs = dc.getQualitySource(v, d);
                    if (qs < cqs || !dc.isCompatible(a, d)) {
                        continue;
                    }
                    if (qs > cqs) {
                        cqs = qs;
                        cq = q;
                        selected.clear();
                        selected.add(v);
                    }
                    else if (q > cq) {
                        cq = q;
                        selected.addFirst(v);
                    }
                    else if (q == cq) {
                        selected.add(v);
                    }
                    iv.remove();
                }
            }
        }
        for (final VariantHolder v2 : vs) {
            if (dc.getDimension(v2) == null) {
                selected.add(v2);
            }
        }
        return selected;
    }
    
    private static LinkedList<VariantHolder> getVariantHolderList(final List<Variant> variants) {
        final LinkedList<VariantHolder> l = new LinkedList<VariantHolder>();
        for (final Variant v : variants) {
            final MediaType mt = v.getMediaType();
            if (mt != null) {
                if (mt instanceof QualitySourceMediaType || mt.getParameters().containsKey("qs")) {
                    final int qs = QualitySourceMediaType.getQualitySource(mt);
                    l.add(new VariantHolder(v, qs));
                }
                else {
                    l.add(new VariantHolder(v));
                }
            }
            else {
                l.add(new VariantHolder(v));
            }
        }
        return l;
    }
    
    public static Variant selectVariant(final ContainerRequest r, final List<Variant> variants) {
        LinkedList<VariantHolder> vhs = getVariantHolderList(variants);
        final Set<String> vary = new HashSet<String>();
        vhs = selectVariants(vhs, HttpHelper.getAccept(r), VariantSelector.MEDIA_TYPE_DC, vary);
        vhs = selectVariants(vhs, HttpHelper.getAcceptLanguage(r), VariantSelector.LANGUAGE_TAG_DC, vary);
        vhs = selectVariants(vhs, HttpHelper.getAcceptCharset(r), VariantSelector.CHARSET_DC, vary);
        vhs = selectVariants(vhs, HttpHelper.getAcceptEncoding(r), VariantSelector.ENCODING_DC, vary);
        if (vhs.isEmpty()) {
            return null;
        }
        final StringBuilder varyHeader = new StringBuilder();
        for (final String v : vary) {
            if (varyHeader.length() > 0) {
                varyHeader.append(',');
            }
            varyHeader.append(v);
        }
        r.getProperties().put("Vary", varyHeader.toString());
        return vhs.iterator().next().v;
    }
    
    static {
        MEDIA_TYPE_DC = new DimensionChecker<AcceptableMediaType, MediaType>() {
            @Override
            public MediaType getDimension(final VariantHolder v) {
                return v.v.getMediaType();
            }
            
            @Override
            public boolean isCompatible(final AcceptableMediaType t, final MediaType u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder v, final MediaType u) {
                return v.mediaTypeQs;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept";
            }
        };
        LANGUAGE_TAG_DC = new DimensionChecker<AcceptableLanguageTag, Locale>() {
            @Override
            public Locale getDimension(final VariantHolder v) {
                return v.v.getLanguage();
            }
            
            @Override
            public boolean isCompatible(final AcceptableLanguageTag t, final Locale u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder qsv, final Locale u) {
                return 0;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept-Language";
            }
        };
        CHARSET_DC = new DimensionChecker<AcceptableToken, String>() {
            @Override
            public String getDimension(final VariantHolder v) {
                final MediaType m = v.v.getMediaType();
                return (m != null) ? m.getParameters().get("charset") : null;
            }
            
            @Override
            public boolean isCompatible(final AcceptableToken t, final String u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder qsv, final String u) {
                return 0;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept-Charset";
            }
        };
        ENCODING_DC = new DimensionChecker<AcceptableToken, String>() {
            @Override
            public String getDimension(final VariantHolder v) {
                return v.v.getEncoding();
            }
            
            @Override
            public boolean isCompatible(final AcceptableToken t, final String u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder qsv, final String u) {
                return 0;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept-Encoding";
            }
        };
    }
    
    private static class VariantHolder
    {
        private final Variant v;
        private final int mediaTypeQs;
        
        public VariantHolder(final Variant v) {
            this(v, 1000);
        }
        
        public VariantHolder(final Variant v, final int mediaTypeQs) {
            this.v = v;
            this.mediaTypeQs = mediaTypeQs;
        }
    }
    
    private interface DimensionChecker<T, U>
    {
        U getDimension(final VariantHolder p0);
        
        int getQualitySource(final VariantHolder p0, final U p1);
        
        boolean isCompatible(final T p0, final U p1);
        
        String getVaryHeaderValue();
    }
}
