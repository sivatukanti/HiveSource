// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api.transaction;

import com.google.common.base.Predicate;
import org.apache.zookeeper.data.Stat;

public class CuratorTransactionResult
{
    private final OperationType type;
    private final String forPath;
    private final String resultPath;
    private final Stat resultStat;
    
    public static Predicate<CuratorTransactionResult> ofTypeAndPath(final OperationType type, final String forPath) {
        return new Predicate<CuratorTransactionResult>() {
            @Override
            public boolean apply(final CuratorTransactionResult result) {
                return result.getType() == type && result.getForPath().equals(forPath);
            }
        };
    }
    
    public CuratorTransactionResult(final OperationType type, final String forPath, final String resultPath, final Stat resultStat) {
        this.forPath = forPath;
        this.resultPath = resultPath;
        this.resultStat = resultStat;
        this.type = type;
    }
    
    public OperationType getType() {
        return this.type;
    }
    
    public String getForPath() {
        return this.forPath;
    }
    
    public String getResultPath() {
        return this.resultPath;
    }
    
    public Stat getResultStat() {
        return this.resultStat;
    }
}
