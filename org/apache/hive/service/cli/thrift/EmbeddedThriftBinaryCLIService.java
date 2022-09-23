// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.hive.service.cli.ICLIService;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.server.HiveServer2;
import org.apache.hive.service.cli.CLIService;

public class EmbeddedThriftBinaryCLIService extends ThriftBinaryCLIService
{
    public EmbeddedThriftBinaryCLIService() {
        super(new CLIService((HiveServer2)null));
        HiveConf.setLoadHiveServer2Config(this.isEmbedded = true);
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        this.cliService.init(hiveConf);
        this.cliService.start();
        super.init(hiveConf);
    }
    
    public ICLIService getService() {
        return this.cliService;
    }
}
