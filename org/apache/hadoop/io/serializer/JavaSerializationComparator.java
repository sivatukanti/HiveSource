// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class JavaSerializationComparator<T extends Serializable> extends DeserializerComparator<T>
{
    @InterfaceAudience.Private
    public JavaSerializationComparator() throws IOException {
        super(new JavaSerialization.JavaSerializationDeserializer());
    }
    
    @InterfaceAudience.Private
    @Override
    public int compare(final T o1, final T o2) {
        return ((Comparable)o1).compareTo(o2);
    }
}
