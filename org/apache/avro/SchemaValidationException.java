// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

public class SchemaValidationException extends Exception
{
    public SchemaValidationException(final Schema reader, final Schema writer) {
        super(getMessage(reader, writer));
    }
    
    public SchemaValidationException(final Schema reader, final Schema writer, final Throwable cause) {
        super(getMessage(reader, writer), cause);
    }
    
    private static String getMessage(final Schema reader, final Schema writer) {
        return "Unable to read schema: \n" + writer.toString(true) + "\nusing schema:\n" + reader.toString(true);
    }
}
