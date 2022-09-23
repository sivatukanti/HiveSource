// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import java.lang.reflect.Field;

public interface ClassMetadata extends TypeMetadata
{
    ClassMetadata setPersistenceModifier(final ClassPersistenceModifier p0);
    
    ClassPersistenceModifier getPersistenceModifier();
    
    FieldMetadata newFieldMetadata(final String p0);
    
    FieldMetadata newFieldMetadata(final Field p0);
}
