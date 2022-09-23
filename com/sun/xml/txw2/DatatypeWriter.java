// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.txw2;

import java.util.Collections;
import javax.xml.namespace.QName;
import java.util.AbstractList;
import java.util.List;

public interface DatatypeWriter<DT>
{
    public static final List<DatatypeWriter<?>> BUILTIN = Collections.unmodifiableList((List<? extends DatatypeWriter<?>>)new AbstractList() {
        private DatatypeWriter<?>[] BUILTIN_ARRAY = { new DatatypeWriter<String>() {
                public Class<String> getType() {
                    return String.class;
                }
                
                public void print(final String s, final NamespaceResolver resolver, final StringBuilder buf) {
                    buf.append(s);
                }
            }, new DatatypeWriter<Integer>() {
                public Class<Integer> getType() {
                    return Integer.class;
                }
                
                public void print(final Integer i, final NamespaceResolver resolver, final StringBuilder buf) {
                    buf.append(i);
                }
            }, new DatatypeWriter<Float>() {
                public Class<Float> getType() {
                    return Float.class;
                }
                
                public void print(final Float f, final NamespaceResolver resolver, final StringBuilder buf) {
                    buf.append(f);
                }
            }, new DatatypeWriter<Double>() {
                public Class<Double> getType() {
                    return Double.class;
                }
                
                public void print(final Double d, final NamespaceResolver resolver, final StringBuilder buf) {
                    buf.append(d);
                }
            }, new DatatypeWriter<QName>() {
                public Class<QName> getType() {
                    return QName.class;
                }
                
                public void print(final QName qn, final NamespaceResolver resolver, final StringBuilder buf) {
                    final String p = resolver.getPrefix(qn.getNamespaceURI());
                    if (p.length() != 0) {
                        buf.append(p).append(':');
                    }
                    buf.append(qn.getLocalPart());
                }
            } };
        
        @Override
        public DatatypeWriter<?> get(final int n) {
            return this.BUILTIN_ARRAY[n];
        }
        
        @Override
        public int size() {
            return this.BUILTIN_ARRAY.length;
        }
    });
    
    Class<DT> getType();
    
    void print(final DT p0, final NamespaceResolver p1, final StringBuilder p2);
}
