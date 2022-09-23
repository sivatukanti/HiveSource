// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import parquet.column.Encoding;

public class EncodingList implements Iterable<Encoding>
{
    private static Canonicalizer<EncodingList> encodingLists;
    private final List<Encoding> encodings;
    
    public static EncodingList getEncodingList(final List<Encoding> encodings) {
        return EncodingList.encodingLists.canonicalize(new EncodingList(encodings));
    }
    
    private EncodingList(final List<Encoding> encodings) {
        this.encodings = Collections.unmodifiableList((List<? extends Encoding>)encodings);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof EncodingList)) {
            return false;
        }
        final List<Encoding> other = ((EncodingList)obj).encodings;
        final int size = other.size();
        if (size != this.encodings.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!other.get(i).equals(this.encodings.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        for (final Encoding element : this.encodings) {
            result = 31 * result + ((element == null) ? 0 : element.hashCode());
        }
        return result;
    }
    
    public List<Encoding> toList() {
        return this.encodings;
    }
    
    @Override
    public Iterator<Encoding> iterator() {
        return this.encodings.iterator();
    }
    
    public int size() {
        return this.encodings.size();
    }
    
    static {
        EncodingList.encodingLists = new Canonicalizer<EncodingList>();
    }
}
