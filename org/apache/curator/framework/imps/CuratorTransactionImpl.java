// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.zookeeper.OpResult;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import java.util.Collection;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.transaction.OperationType;
import org.apache.zookeeper.Op;
import org.apache.curator.framework.api.transaction.TransactionCheckBuilder;
import org.apache.curator.framework.api.transaction.TransactionSetDataBuilder;
import org.apache.curator.framework.api.transaction.TransactionDeleteBuilder;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.framework.api.transaction.TransactionCreateBuilder;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.CuratorTransaction;

class CuratorTransactionImpl implements CuratorTransaction, CuratorTransactionBridge, CuratorTransactionFinal
{
    private final CuratorFrameworkImpl client;
    private final CuratorMultiTransactionRecord transaction;
    private boolean isCommitted;
    
    CuratorTransactionImpl(final CuratorFrameworkImpl client) {
        this.isCommitted = false;
        this.client = client;
        this.transaction = new CuratorMultiTransactionRecord();
    }
    
    @Override
    public CuratorTransactionFinal and() {
        return this;
    }
    
    @Override
    public TransactionCreateBuilder create() {
        Preconditions.checkState(!this.isCommitted, (Object)"transaction already committed");
        return new CreateBuilderImpl(this.client).asTransactionCreateBuilder(this, this.transaction);
    }
    
    @Override
    public TransactionDeleteBuilder delete() {
        Preconditions.checkState(!this.isCommitted, (Object)"transaction already committed");
        return new DeleteBuilderImpl(this.client).asTransactionDeleteBuilder(this, this.transaction);
    }
    
    @Override
    public TransactionSetDataBuilder setData() {
        Preconditions.checkState(!this.isCommitted, (Object)"transaction already committed");
        return new SetDataBuilderImpl(this.client).asTransactionSetDataBuilder(this, this.transaction);
    }
    
    @Override
    public TransactionCheckBuilder check() {
        Preconditions.checkState(!this.isCommitted, (Object)"transaction already committed");
        return new TransactionCheckBuilder() {
            private int version = -1;
            
            @Override
            public CuratorTransactionBridge forPath(final String path) throws Exception {
                final String fixedPath = CuratorTransactionImpl.this.client.fixForNamespace(path);
                CuratorTransactionImpl.this.transaction.add(Op.check(fixedPath, this.version), OperationType.CHECK, path);
                return CuratorTransactionImpl.this;
            }
            
            @Override
            public Pathable<CuratorTransactionBridge> withVersion(final int version) {
                this.version = version;
                return this;
            }
        };
    }
    
    @Override
    public Collection<CuratorTransactionResult> commit() throws Exception {
        Preconditions.checkState(!this.isCommitted, (Object)"transaction already committed");
        this.isCommitted = true;
        final AtomicBoolean firstTime = new AtomicBoolean(true);
        final List<OpResult> resultList = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<List<OpResult>>)new Callable<List<OpResult>>() {
            @Override
            public List<OpResult> call() throws Exception {
                return CuratorTransactionImpl.this.doOperation(firstTime);
            }
        });
        if (resultList.size() != this.transaction.metadataSize()) {
            throw new IllegalStateException(String.format("Result size (%d) doesn't match input size (%d)", resultList.size(), this.transaction.metadataSize()));
        }
        final ImmutableList.Builder<CuratorTransactionResult> builder = ImmutableList.builder();
        for (int i = 0; i < resultList.size(); ++i) {
            final OpResult opResult = resultList.get(i);
            final CuratorMultiTransactionRecord.TypeAndPath metadata = this.transaction.getMetadata(i);
            final CuratorTransactionResult curatorResult = this.makeCuratorResult(opResult, metadata);
            builder.add(curatorResult);
        }
        return builder.build();
    }
    
    private List<OpResult> doOperation(final AtomicBoolean firstTime) throws Exception {
        final boolean localFirstTime = firstTime.getAndSet(false);
        if (!localFirstTime) {}
        final List<OpResult> opResults = this.client.getZooKeeper().multi(this.transaction);
        if (opResults.size() > 0) {
            final OpResult firstResult = opResults.get(0);
            if (firstResult.getType() == -1) {
                final OpResult.ErrorResult error = (OpResult.ErrorResult)firstResult;
                KeeperException.Code code = KeeperException.Code.get(error.getErr());
                if (code == null) {
                    code = KeeperException.Code.UNIMPLEMENTED;
                }
                throw KeeperException.create(code);
            }
        }
        return opResults;
    }
    
    private CuratorTransactionResult makeCuratorResult(final OpResult opResult, final CuratorMultiTransactionRecord.TypeAndPath metadata) {
        String resultPath = null;
        Stat resultStat = null;
        switch (opResult.getType()) {
            case 1: {
                final OpResult.CreateResult createResult = (OpResult.CreateResult)opResult;
                resultPath = this.client.unfixForNamespace(createResult.getPath());
                break;
            }
            case 5: {
                final OpResult.SetDataResult setDataResult = (OpResult.SetDataResult)opResult;
                resultStat = setDataResult.getStat();
                break;
            }
        }
        return new CuratorTransactionResult(metadata.type, metadata.forPath, resultPath, resultStat);
    }
}
