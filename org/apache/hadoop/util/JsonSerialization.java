// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Map;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.FSDataInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.InputStream;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import java.io.EOFException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.base.Preconditions;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class JsonSerialization<T>
{
    private static final Logger LOG;
    private static final String UTF_8 = "UTF-8";
    private final Class<T> classType;
    private final ObjectMapper mapper;
    private static final ObjectWriter WRITER;
    private static final ObjectReader MAP_READER;
    
    public static ObjectWriter writer() {
        return JsonSerialization.WRITER;
    }
    
    public static ObjectReader mapReader() {
        return JsonSerialization.MAP_READER;
    }
    
    public JsonSerialization(final Class<T> classType, final boolean failOnUnknownProperties, final boolean pretty) {
        Preconditions.checkArgument(classType != null, (Object)"null classType");
        this.classType = classType;
        (this.mapper = new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        this.mapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);
    }
    
    public String getName() {
        return this.classType.getSimpleName();
    }
    
    public ObjectMapper getMapper() {
        return this.mapper;
    }
    
    public synchronized T fromJson(final String json) throws IOException, JsonParseException, JsonMappingException {
        if (json.isEmpty()) {
            throw new EOFException("No data");
        }
        try {
            return this.mapper.readValue(json, this.classType);
        }
        catch (IOException e) {
            JsonSerialization.LOG.error("Exception while parsing json : {}\n{}", e, json, e);
            throw e;
        }
    }
    
    public synchronized T fromJsonStream(final InputStream stream) throws IOException {
        return this.mapper.readValue(stream, this.classType);
    }
    
    public synchronized T load(final File jsonFile) throws IOException, JsonParseException, JsonMappingException {
        if (!jsonFile.isFile()) {
            throw new FileNotFoundException("Not a file: " + jsonFile);
        }
        if (jsonFile.length() == 0L) {
            throw new EOFException("File is empty: " + jsonFile);
        }
        try {
            return this.mapper.readValue(jsonFile, this.classType);
        }
        catch (IOException e) {
            JsonSerialization.LOG.error("Exception while parsing json file {}", jsonFile, e);
            throw e;
        }
    }
    
    public void save(final File file, final T instance) throws IOException {
        this.writeJsonAsBytes(instance, new FileOutputStream(file));
    }
    
    public synchronized T fromResource(final String resource) throws IOException, JsonParseException, JsonMappingException {
        try (final InputStream resStream = this.getClass().getResourceAsStream(resource)) {
            if (resStream == null) {
                throw new FileNotFoundException(resource);
            }
            return this.mapper.readValue(resStream, this.classType);
        }
        catch (IOException e) {
            JsonSerialization.LOG.error("Exception while parsing json resource {}", resource, e);
            throw e;
        }
    }
    
    public T fromInstance(final T instance) throws IOException {
        return this.fromJson(this.toJson(instance));
    }
    
    public T load(final FileSystem fs, final Path path) throws IOException {
        try (final FSDataInputStream dataInputStream = fs.open(path)) {
            if (dataInputStream.available() == 0) {
                throw new EOFException("No data in " + path);
            }
            return this.fromJsonStream(dataInputStream);
        }
        catch (JsonProcessingException e) {
            throw new IOException(String.format("Failed to read JSON file \"%s\": %s", path, e), e);
        }
    }
    
    public void save(final FileSystem fs, final Path path, final T instance, final boolean overwrite) throws IOException {
        this.writeJsonAsBytes(instance, fs.create(path, overwrite));
    }
    
    private void writeJsonAsBytes(final T instance, final OutputStream dataOutputStream) throws IOException {
        try {
            dataOutputStream.write(this.toBytes(instance));
        }
        finally {
            dataOutputStream.close();
        }
    }
    
    public byte[] toBytes(final T instance) throws IOException {
        return this.mapper.writeValueAsBytes(instance);
    }
    
    public T fromBytes(final byte[] bytes) throws IOException {
        return this.fromJson(new String(bytes, 0, bytes.length, "UTF-8"));
    }
    
    public synchronized String toJson(final T instance) throws JsonProcessingException {
        return this.mapper.writeValueAsString(instance);
    }
    
    public String toString(final T instance) {
        Preconditions.checkArgument(instance != null, (Object)"Null instance argument");
        try {
            return this.toJson(instance);
        }
        catch (JsonProcessingException e) {
            return "Failed to convert to a string: " + e;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(JsonSerialization.class);
        WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
        MAP_READER = new ObjectMapper().readerFor(Map.class);
    }
}
