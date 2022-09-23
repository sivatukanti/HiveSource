// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

public class DefaultConverter implements TypeConverter
{
    public Object convertToJSONPrimitive(final String text) {
        Object primitive = null;
        try {
            primitive = Long.valueOf(text);
        }
        catch (Exception ex) {}
        if (primitive == null) {
            try {
                primitive = Double.valueOf(text);
            }
            catch (Exception ex2) {}
        }
        if (primitive == null && (text.trim().equalsIgnoreCase("true") || text.trim().equalsIgnoreCase("false"))) {
            primitive = Boolean.valueOf(text);
        }
        if (primitive == null || !primitive.toString().equals(text)) {
            primitive = text;
        }
        return primitive;
    }
}
