// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.io.Text;
import java.util.List;

public interface LazyObjectInspectorParameters
{
    boolean isEscaped();
    
    byte getEscapeChar();
    
    boolean isExtendedBooleanLiteral();
    
    List<String> getTimestampFormats();
    
    byte[] getSeparators();
    
    Text getNullSequence();
    
    boolean isLastColumnTakesRest();
}
