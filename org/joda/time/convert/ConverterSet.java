// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

class ConverterSet
{
    private final Converter[] iConverters;
    private Entry[] iSelectEntries;
    
    ConverterSet(final Converter[] iConverters) {
        this.iConverters = iConverters;
        this.iSelectEntries = new Entry[16];
    }
    
    Converter select(final Class<?> clazz) throws IllegalStateException {
        final Entry[] iSelectEntries = this.iSelectEntries;
        final int length = iSelectEntries.length;
        int n = (clazz == null) ? 0 : (clazz.hashCode() & length - 1);
        Entry entry;
        while ((entry = iSelectEntries[n]) != null) {
            if (entry.iType == clazz) {
                return entry.iConverter;
            }
            if (++n < length) {
                continue;
            }
            n = 0;
        }
        final Converter selectSlow = selectSlow(this, clazz);
        final Entry entry2 = new Entry(clazz, selectSlow);
        final Entry[] iSelectEntries2 = iSelectEntries.clone();
        iSelectEntries2[n] = entry2;
        for (int i = 0; i < length; ++i) {
            if (iSelectEntries2[i] == null) {
                this.iSelectEntries = iSelectEntries2;
                return selectSlow;
            }
        }
        final int n2 = length << 1;
        final Entry[] iSelectEntries3 = new Entry[n2];
        for (final Entry entry3 : iSelectEntries2) {
            final Class<?> iType = entry3.iType;
            int n3;
            for (n3 = ((iType == null) ? 0 : (iType.hashCode() & n2 - 1)); iSelectEntries3[n3] != null; n3 = 0) {
                if (++n3 >= n2) {}
            }
            iSelectEntries3[n3] = entry3;
        }
        this.iSelectEntries = iSelectEntries3;
        return selectSlow;
    }
    
    int size() {
        return this.iConverters.length;
    }
    
    void copyInto(final Converter[] array) {
        System.arraycopy(this.iConverters, 0, array, 0, this.iConverters.length);
    }
    
    ConverterSet add(final Converter converter, final Converter[] array) {
        final Converter[] iConverters = this.iConverters;
        final int length = iConverters.length;
        for (int i = 0; i < length; ++i) {
            final Converter obj = iConverters[i];
            if (converter.equals(obj)) {
                if (array != null) {
                    array[0] = null;
                }
                return this;
            }
            if (converter.getSupportedType() == obj.getSupportedType()) {
                final Converter[] array2 = new Converter[length];
                for (int j = 0; j < length; ++j) {
                    if (j != i) {
                        array2[j] = iConverters[j];
                    }
                    else {
                        array2[j] = converter;
                    }
                }
                if (array != null) {
                    array[0] = obj;
                }
                return new ConverterSet(array2);
            }
        }
        final Converter[] array3 = new Converter[length + 1];
        System.arraycopy(iConverters, 0, array3, 0, length);
        array3[length] = converter;
        if (array != null) {
            array[0] = null;
        }
        return new ConverterSet(array3);
    }
    
    ConverterSet remove(final Converter converter, final Converter[] array) {
        final Converter[] iConverters = this.iConverters;
        for (int length = iConverters.length, i = 0; i < length; ++i) {
            if (converter.equals(iConverters[i])) {
                return this.remove(i, array);
            }
        }
        if (array != null) {
            array[0] = null;
        }
        return this;
    }
    
    ConverterSet remove(final int n, final Converter[] array) {
        final Converter[] iConverters = this.iConverters;
        final int length = iConverters.length;
        if (n >= length) {
            throw new IndexOutOfBoundsException();
        }
        if (array != null) {
            array[0] = iConverters[n];
        }
        final Converter[] array2 = new Converter[length - 1];
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            if (i != n) {
                array2[n2++] = iConverters[i];
            }
        }
        return new ConverterSet(array2);
    }
    
    private static Converter selectSlow(ConverterSet set, final Class<?> clazz) {
        Converter[] array = set.iConverters;
        int length;
        int n = length = array.length;
        while (--length >= 0) {
            final Converter converter = array[length];
            final Class<?> supportedType = converter.getSupportedType();
            if (supportedType == clazz) {
                return converter;
            }
            if (supportedType != null && (clazz == null || supportedType.isAssignableFrom(clazz))) {
                continue;
            }
            set = set.remove(length, null);
            array = set.iConverters;
            n = array.length;
        }
        if (clazz == null || n == 0) {
            return null;
        }
        if (n == 1) {
            return array[0];
        }
        int n2 = n;
        while (--n2 >= 0) {
            final Class<?> supportedType2 = array[n2].getSupportedType();
            int n3 = n;
            while (--n3 >= 0) {
                if (n3 != n2 && array[n3].getSupportedType().isAssignableFrom(supportedType2)) {
                    set = set.remove(n3, null);
                    array = set.iConverters;
                    n = array.length;
                    n2 = n - 1;
                }
            }
        }
        if (n == 1) {
            return array[0];
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unable to find best converter for type \"");
        sb.append(clazz.getName());
        sb.append("\" from remaining set: ");
        for (final Converter converter2 : array) {
            final Class<?> supportedType3 = converter2.getSupportedType();
            sb.append(converter2.getClass().getName());
            sb.append('[');
            sb.append((supportedType3 == null) ? null : supportedType3.getName());
            sb.append("], ");
        }
        throw new IllegalStateException(sb.toString());
    }
    
    static class Entry
    {
        final Class<?> iType;
        final Converter iConverter;
        
        Entry(final Class<?> iType, final Converter iConverter) {
            this.iType = iType;
            this.iConverter = iConverter;
        }
    }
}
