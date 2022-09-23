// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

public class UnresolvedUnionException extends AvroRuntimeException
{
    private Object unresolvedDatum;
    private Schema unionSchema;
    
    public UnresolvedUnionException(final Schema unionSchema, final Object unresolvedDatum) {
        super("Not in union " + unionSchema + ": " + unresolvedDatum);
        this.unionSchema = unionSchema;
        this.unresolvedDatum = unresolvedDatum;
    }
    
    public Object getUnresolvedDatum() {
        return this.unresolvedDatum;
    }
    
    public Schema getUnionSchema() {
        return this.unionSchema;
    }
}
