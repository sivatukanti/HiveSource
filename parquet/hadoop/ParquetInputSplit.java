// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.util.Arrays;
import java.io.IOException;
import java.util.Iterator;
import parquet.schema.MessageType;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.schema.MessageTypeParser;
import java.util.Map;
import parquet.hadoop.metadata.BlockMetaData;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

@InterfaceAudience.Private
public class ParquetInputSplit extends FileSplit implements Writable
{
    private long end;
    private long[] rowGroupOffsets;
    
    public ParquetInputSplit() {
        super((Path)null, 0L, 0L, new String[0]);
    }
    
    @Deprecated
    public ParquetInputSplit(final Path path, final long start, final long length, final String[] hosts, final List<BlockMetaData> blocks, final String requestedSchema, final String fileSchema, final Map<String, String> extraMetadata, final Map<String, String> readSupportMetadata) {
        this(path, start, length, end(blocks, requestedSchema), hosts, offsets(blocks));
    }
    
    private static long end(final List<BlockMetaData> blocks, final String requestedSchema) {
        final MessageType requested = MessageTypeParser.parseMessageType(requestedSchema);
        long length = 0L;
        for (final BlockMetaData block : blocks) {
            final List<ColumnChunkMetaData> columns = block.getColumns();
            for (final ColumnChunkMetaData column : columns) {
                if (requested.containsPath(column.getPath().toArray())) {
                    length += column.getTotalSize();
                }
            }
        }
        return length;
    }
    
    private static long[] offsets(final List<BlockMetaData> blocks) {
        final long[] offsets = new long[blocks.size()];
        for (int i = 0; i < offsets.length; ++i) {
            offsets[i] = blocks.get(i).getStartingPos();
        }
        return offsets;
    }
    
    @Deprecated
    public List<BlockMetaData> getBlocks() {
        throw new UnsupportedOperationException("Splits no longer have row group metadata, see PARQUET-234");
    }
    
    static ParquetInputSplit from(final FileSplit split) throws IOException {
        return new ParquetInputSplit(split.getPath(), split.getStart(), split.getStart() + split.getLength(), split.getLength(), split.getLocations(), null);
    }
    
    static ParquetInputSplit from(final org.apache.hadoop.mapred.FileSplit split) throws IOException {
        return new ParquetInputSplit(split.getPath(), split.getStart(), split.getStart() + split.getLength(), split.getLength(), split.getLocations(), null);
    }
    
    public ParquetInputSplit(final Path file, final long start, final long end, final long length, final String[] hosts, final long[] rowGroupOffsets) {
        super(file, start, length, hosts);
        this.end = end;
        this.rowGroupOffsets = rowGroupOffsets;
    }
    
    @Deprecated
    String getRequestedSchema() {
        throw new UnsupportedOperationException("Splits no longer have the requested schema, see PARQUET-234");
    }
    
    @Deprecated
    public String getFileSchema() {
        throw new UnsupportedOperationException("Splits no longer have the file schema, see PARQUET-234");
    }
    
    public long getEnd() {
        return this.end;
    }
    
    @Deprecated
    public Map<String, String> getExtraMetadata() {
        throw new UnsupportedOperationException("Splits no longer have file metadata, see PARQUET-234");
    }
    
    @Deprecated
    Map<String, String> getReadSupportMetadata() {
        throw new UnsupportedOperationException("Splits no longer have read-support metadata, see PARQUET-234");
    }
    
    public long[] getRowGroupOffsets() {
        return this.rowGroupOffsets;
    }
    
    public String toString() {
        String hosts;
        try {
            hosts = Arrays.toString(this.getLocations());
        }
        catch (Exception e) {
            hosts = "(" + e + ")";
        }
        return this.getClass().getSimpleName() + "{" + "part: " + this.getPath() + " start: " + this.getStart() + " end: " + this.getEnd() + " length: " + this.getLength() + " hosts: " + hosts + ((this.rowGroupOffsets == null) ? "" : (" row groups: " + Arrays.toString(this.rowGroupOffsets))) + "}";
    }
    
    public void readFields(final DataInput hin) throws IOException {
        final byte[] bytes = readArray(hin);
        final DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)));
        super.readFields((DataInput)in);
        this.end = in.readLong();
        if (in.readBoolean()) {
            this.rowGroupOffsets = new long[in.readInt()];
            for (int i = 0; i < this.rowGroupOffsets.length; ++i) {
                this.rowGroupOffsets[i] = in.readLong();
            }
        }
        in.close();
    }
    
    public void write(final DataOutput hout) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(new GZIPOutputStream(baos));
        super.write((DataOutput)out);
        out.writeLong(this.end);
        out.writeBoolean(this.rowGroupOffsets != null);
        if (this.rowGroupOffsets != null) {
            out.writeInt(this.rowGroupOffsets.length);
            for (final long o : this.rowGroupOffsets) {
                out.writeLong(o);
            }
        }
        out.close();
        writeArray(hout, baos.toByteArray());
    }
    
    private static void writeArray(final DataOutput out, final byte[] bytes) throws IOException {
        out.writeInt(bytes.length);
        out.write(bytes, 0, bytes.length);
    }
    
    private static byte[] readArray(final DataInput in) throws IOException {
        final int len = in.readInt();
        final byte[] bytes = new byte[len];
        in.readFully(bytes);
        return bytes;
    }
}
