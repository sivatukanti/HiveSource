// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain;

import java.io.PrintStream;
import org.json.JSONObject;

public interface JsonParser
{
    void print(final JSONObject p0, final PrintStream p1) throws Exception;
}
