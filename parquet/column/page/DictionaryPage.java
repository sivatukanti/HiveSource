// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

import java.io.IOException;
import parquet.Preconditions;
import parquet.Ints;
import parquet.column.Encoding;
import parquet.bytes.BytesInput;

public class DictionaryPage extends Page
{
    private final BytesInput bytes;
    private final int dictionarySize;
    private final Encoding encoding;
    
    public DictionaryPage(final BytesInput bytes, final int dictionarySize, final Encoding encoding) {
        this(bytes, (int)bytes.size(), dictionarySize, encoding);
    }
    
    public DictionaryPage(final BytesInput bytes, final int uncompressedSize, final int dictionarySize, final Encoding encoding) {
        super(Ints.checkedCast(bytes.size()), uncompressedSize);
        this.bytes = Preconditions.checkNotNull(bytes, "bytes");
        this.dictionarySize = dictionarySize;
        this.encoding = Preconditions.checkNotNull(encoding, "encoding");
    }
    
    public BytesInput getBytes() {
        return this.bytes;
    }
    
    public int getDictionarySize() {
        return this.dictionarySize;
    }
    
    public Encoding getEncoding() {
        return this.encoding;
    }
    
    public DictionaryPage copy() throws IOException {
        return new DictionaryPage(BytesInput.copy(this.bytes), this.getUncompressedSize(), this.dictionarySize, this.encoding);
    }
    
    @Override
    public String toString() {
        return "Page [bytes.size=" + this.bytes.size() + ", entryCount=" + this.dictionarySize + ", uncompressedSize=" + this.getUncompressedSize() + ", encoding=" + this.encoding + "]";
    }
}
