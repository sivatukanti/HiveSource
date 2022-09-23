// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import parquet.org.codehaus.jackson.map.SerializationConfig;
import parquet.org.codehaus.jackson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.JsonMappingException;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.List;
import parquet.org.codehaus.jackson.map.ObjectMapper;

public class ParquetMetadata
{
    private static ObjectMapper objectMapper;
    private static ObjectMapper prettyObjectMapper;
    private final FileMetaData fileMetaData;
    private final List<BlockMetaData> blocks;
    
    public static String toJSON(final ParquetMetadata parquetMetaData) {
        return toJSON(parquetMetaData, ParquetMetadata.objectMapper);
    }
    
    public static String toPrettyJSON(final ParquetMetadata parquetMetaData) {
        return toJSON(parquetMetaData, ParquetMetadata.prettyObjectMapper);
    }
    
    private static String toJSON(final ParquetMetadata parquetMetaData, final ObjectMapper mapper) {
        final StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, parquetMetaData);
        }
        catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        }
        catch (JsonMappingException e2) {
            throw new RuntimeException(e2);
        }
        catch (IOException e3) {
            throw new RuntimeException(e3);
        }
        return stringWriter.toString();
    }
    
    public static ParquetMetadata fromJSON(final String json) {
        try {
            return ParquetMetadata.objectMapper.readValue(new StringReader(json), ParquetMetadata.class);
        }
        catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
        catch (JsonMappingException e2) {
            throw new RuntimeException(e2);
        }
        catch (IOException e3) {
            throw new RuntimeException(e3);
        }
    }
    
    public ParquetMetadata(final FileMetaData fileMetaData, final List<BlockMetaData> blocks) {
        this.fileMetaData = fileMetaData;
        this.blocks = blocks;
    }
    
    public List<BlockMetaData> getBlocks() {
        return this.blocks;
    }
    
    public FileMetaData getFileMetaData() {
        return this.fileMetaData;
    }
    
    @Override
    public String toString() {
        return "ParquetMetaData{" + this.fileMetaData + ", blocks: " + this.blocks + "}";
    }
    
    static {
        ParquetMetadata.objectMapper = new ObjectMapper();
        (ParquetMetadata.prettyObjectMapper = new ObjectMapper()).configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
    }
}
