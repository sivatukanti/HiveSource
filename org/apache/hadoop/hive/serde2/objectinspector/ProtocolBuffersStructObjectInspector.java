// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

class ProtocolBuffersStructObjectInspector extends ReflectionStructObjectInspector
{
    @Override
    public boolean shouldIgnoreField(final String name) {
        return name.startsWith("has");
    }
}
