// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain;

import org.apache.hadoop.hive.common.jsonexplain.tez.TezJsonParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;

public class JsonParserFactory
{
    private JsonParserFactory() {
    }
    
    public static JsonParser getParser(final HiveConf conf) {
        if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVE_EXECUTION_ENGINE).equals("tez")) {
            return new TezJsonParser();
        }
        return null;
    }
}
