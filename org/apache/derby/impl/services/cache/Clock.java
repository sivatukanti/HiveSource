// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.util.Iterator;
import java.util.Collection;
import org.apache.derby.iapi.util.Operator;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.cache.SizedCacheable;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import java.util.ArrayList;
import org.apache.derby.iapi.services.daemon.DaemonService;
import java.util.HashMap;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.services.cache.CacheManager;

final class Clock implements CacheManager, Serviceable
{
    private final CacheStat stat;
    private final HashMap cache_;
    private DaemonService cleaner;
    private final ArrayList holders;
    private int validItemCount;
    private long maximumSize;
    private boolean useByteCount;
    private long currentByteCount;
    private static final int ITEM_OVERHEAD;
    private final CacheableFactory holderFactory;
    private boolean active;
    private String name;
    private int clockHand;
    private int myClientNumber;
    private boolean wokenToClean;
    private boolean cleanerRunning;
    private boolean needService;
    private int trimRequests;
    
    Clock(final CacheableFactory holderFactory, final String name, final int n, final long n2, final boolean useByteCount) {
        this.validItemCount = 0;
        this.currentByteCount = 0L;
        this.trimRequests = 0;
        this.cache_ = new HashMap(n, 0.95f);
        this.maximumSize = n2;
        this.holderFactory = holderFactory;
        this.useByteCount = useByteCount;
        this.holders = new ArrayList(n);
        this.name = name;
        this.active = true;
        this.stat = new CacheStat();
        this.stat.initialSize = n;
        this.stat.maxSize = n2;
    }
    
    public Cacheable find(final Object key) throws StandardException {
        while (true) {
            boolean b = false;
            CachedItem freeItem;
            synchronized (this) {
                if (!this.active) {
                    return null;
                }
                freeItem = this.cache_.get(key);
                if (freeItem != null) {
                    freeItem.keepAfterSearch();
                    final CacheStat stat = this.stat;
                    ++stat.findHit;
                }
            }
            if (freeItem == null) {
                freeItem = this.findFreeItem();
                final CacheStat stat2 = this.stat;
                ++stat2.findMiss;
                synchronized (this) {
                    final CachedItem cachedItem = this.cache_.get(key);
                    if (cachedItem != null) {
                        freeItem.unkeepForCreate();
                        freeItem = cachedItem;
                        freeItem.keepAfterSearch();
                    }
                    else {
                        this.cache_.put(key, freeItem);
                        b = true;
                    }
                }
            }
            if (b) {
                final CacheStat stat3 = this.stat;
                ++stat3.findFault;
                return this.addEntry(freeItem, key, false, null);
            }
            final Cacheable use = freeItem.use();
            if (use != null) {
                return use;
            }
            synchronized (this) {
                freeItem.unkeep();
            }
        }
    }
    
    public Cacheable findCached(final Object key) throws StandardException {
        final CachedItem cachedItem;
        synchronized (this) {
            if (!this.active) {
                return null;
            }
            cachedItem = this.cache_.get(key);
            if (cachedItem == null) {
                final CacheStat stat = this.stat;
                ++stat.findCachedMiss;
                return null;
            }
            final CacheStat stat2 = this.stat;
            ++stat2.findCachedHit;
            cachedItem.keepAfterSearch();
        }
        final Cacheable use = cachedItem.use();
        if (use == null) {
            synchronized (this) {
                cachedItem.unkeep();
            }
        }
        return use;
    }
    
    public void setUsed(final Object[] array) {
        int i = 0;
        while (i < array.length) {
            synchronized (this) {
                if (!this.active) {
                    return;
                }
                int length = i + 32;
                if (length > array.length) {
                    length = array.length;
                }
                while (i < length) {
                    if (array[i] == null) {
                        return;
                    }
                    final CachedItem cachedItem = this.cache_.get(array[i]);
                    if (null != cachedItem) {
                        cachedItem.setUsed(true);
                    }
                    ++i;
                }
            }
        }
    }
    
    public Cacheable create(final Object o, final Object o2) throws StandardException {
        final CachedItem freeItem = this.findFreeItem();
        final CacheStat stat = this.stat;
        ++stat.create;
        synchronized (this) {
            if (!this.active) {
                return null;
            }
            if (this.cache_.get(o) != null) {
                freeItem.unkeepForCreate();
                throw StandardException.newException("XBCA0.S", this.name, o);
            }
            this.cache_.put(o, freeItem);
        }
        return this.addEntry(freeItem, o, true, o2);
    }
    
    public void release(final Cacheable cacheable) {
        long shrinkSize = 0L;
        final CachedItem cachedItem;
        final boolean unkeep;
        synchronized (this) {
            cachedItem = this.cache_.get(cacheable.getIdentity());
            unkeep = cachedItem.unkeep();
            if (unkeep) {
                this.cache_.remove(cacheable.getIdentity());
                cachedItem.keepForClean();
            }
            if (this.cleaner == null) {
                shrinkSize = this.shrinkSize(this.getCurrentSizeNoSync());
            }
        }
        if (unkeep) {
            cachedItem.notifyRemover();
        }
        if (shrinkSize > 0L) {
            this.performWork(true);
        }
    }
    
    private void release(final CachedItem cachedItem) {
        final boolean unkeep;
        synchronized (this) {
            unkeep = cachedItem.unkeep();
            if (unkeep) {
                this.cache_.remove(cachedItem.getEntry().getIdentity());
                cachedItem.keepForClean();
            }
        }
        if (unkeep) {
            cachedItem.notifyRemover();
        }
    }
    
    public void remove(final Cacheable cacheable) throws StandardException {
        long n = 0L;
        final CacheStat stat = this.stat;
        ++stat.remove;
        final CachedItem cachedItem;
        final boolean unkeep;
        synchronized (this) {
            cachedItem = this.cache_.get(cacheable.getIdentity());
            if (this.useByteCount) {
                n = this.getItemSize(cachedItem);
            }
            cachedItem.setRemoveState();
            unkeep = cachedItem.unkeep();
            if (unkeep) {
                this.cache_.remove(cacheable.getIdentity());
                cachedItem.keepForClean();
            }
        }
        try {
            cachedItem.remove(unkeep);
        }
        finally {
            synchronized (this) {
                cachedItem.unkeep();
                cachedItem.setValidState(false);
                --this.validItemCount;
                cachedItem.getEntry().clearIdentity();
                if (this.useByteCount) {
                    this.currentByteCount += this.getItemSize(cachedItem) - n;
                }
            }
        }
    }
    
    public void cleanAll() throws StandardException {
        final CacheStat stat = this.stat;
        ++stat.cleanAll;
        this.cleanCache(null);
    }
    
    public void clean(final Matchable matchable) throws StandardException {
        this.cleanCache(matchable);
    }
    
    public void ageOut() {
        final CacheStat stat = this.stat;
        ++stat.ageOut;
        synchronized (this) {
            final int size = this.holders.size();
            long shrinkSize = this.shrinkSize(this.getCurrentSizeNoSync());
            boolean b = false;
            for (int i = 0; i < size; ++i) {
                final CachedItem cachedItem = this.holders.get(i);
                if (!cachedItem.isKept()) {
                    if (cachedItem.isValid()) {
                        if (!cachedItem.getEntry().isDirty()) {
                            final long removeIdentity = this.removeIdentity(cachedItem);
                            if (shrinkSize > 0L) {
                                shrinkSize -= removeIdentity;
                                b = true;
                            }
                        }
                    }
                }
            }
            if (b) {
                this.trimToSize();
            }
        }
    }
    
    public void shutdown() throws StandardException {
        if (this.cleaner != null) {
            this.cleaner.unsubscribe(this.myClientNumber);
            this.cleaner = null;
        }
        synchronized (this) {
            this.active = false;
        }
        this.ageOut();
        this.cleanAll();
        this.ageOut();
    }
    
    public void useDaemonService(final DaemonService cleaner) {
        if (this.cleaner != null) {
            this.cleaner.unsubscribe(this.myClientNumber);
        }
        this.cleaner = cleaner;
        this.myClientNumber = this.cleaner.subscribe(this, true);
    }
    
    public boolean discard(final Matchable matchable) {
        boolean b = true;
        synchronized (this) {
            final int size = this.holders.size();
            long shrinkSize = this.shrinkSize(this.getCurrentSizeNoSync());
            boolean b2 = false;
            for (int i = 0; i < size; ++i) {
                final CachedItem cachedItem = this.holders.get(i);
                if (cachedItem.isValid()) {
                    final Object identity = cachedItem.getEntry().getIdentity();
                    if (matchable == null || matchable.match(identity)) {
                        if (cachedItem.isKept()) {
                            b = false;
                        }
                        else {
                            final long removeIdentity = this.removeIdentity(cachedItem);
                            if (shrinkSize > 0L) {
                                shrinkSize -= removeIdentity;
                                b2 = true;
                            }
                        }
                    }
                }
            }
            if (b2) {
                this.trimToSize();
            }
        }
        return b;
    }
    
    private Cacheable addEntry(final CachedItem value, final Object key, final boolean b, final Object o) throws StandardException {
        Cacheable takeOnIdentity = null;
        long n = 0L;
        if (this.useByteCount) {
            n = this.getItemSize(value);
        }
        try {
            takeOnIdentity = value.takeOnIdentity(this, this.holderFactory, key, b, o);
        }
        finally {
            boolean kept;
            synchronized (this) {
                this.cache_.remove(key);
                if (takeOnIdentity != null) {
                    this.cache_.put(takeOnIdentity.getIdentity(), value);
                    if (this.useByteCount) {
                        this.currentByteCount += ((SizedCacheable)takeOnIdentity).getSize() - n;
                    }
                    value.setValidState(true);
                    ++this.validItemCount;
                    kept = true;
                }
                else {
                    value.unkeep();
                    kept = value.isKept();
                }
            }
            if (kept) {
                value.settingIdentityComplete();
            }
        }
        return takeOnIdentity;
    }
    
    private CachedItem findFreeItem() throws StandardException {
        if (this.getCurrentSize() >= this.maximumSize) {
            final CachedItem rotateClock = this.rotateClock(0.2f);
            if (rotateClock != null) {
                return rotateClock;
            }
        }
        if (this.validItemCount < this.holders.size()) {
            synchronized (this) {
                for (int n = this.holders.size() - this.validItemCount, index = this.holders.size() - 1; n > 0 && index >= 0; --index) {
                    final CachedItem cachedItem = this.holders.get(index);
                    if (cachedItem.isKept()) {
                        if (!cachedItem.isValid()) {
                            --n;
                        }
                    }
                    else if (!cachedItem.isValid()) {
                        cachedItem.keepForCreate();
                        return cachedItem;
                    }
                }
            }
        }
        return this.growCache();
    }
    
    private CachedItem rotateClock(final float n) throws StandardException {
        CachedItem cachedItem = null;
        boolean b = false;
        try {
            final int size = this.holders.size();
            int i;
            if (size < 20) {
                i = 2 * size;
            }
            else {
                i = (int)(size * n);
            }
            long shrinkSize = this.shrinkSize(this.getCurrentSize());
            while (i > 0) {
                CachedItem cachedItem2 = null;
                synchronized (this) {
                    final int size2 = this.holders.size();
                    while (i > 0) {
                        if (this.clockHand >= size2) {
                            if (size2 == 0) {
                                break;
                            }
                            this.clockHand = 0;
                        }
                        cachedItem2 = this.holders.get(this.clockHand);
                        Label_0472: {
                            if (!cachedItem2.isKept()) {
                                if (!cachedItem2.isValid()) {
                                    if (null == cachedItem) {
                                        cachedItem2.keepForCreate();
                                        if (!this.useByteCount || this.getCurrentSizeNoSync() <= this.maximumSize) {
                                            this.incrClockHand();
                                            return cachedItem2;
                                        }
                                        cachedItem = cachedItem2;
                                    }
                                }
                                else if (cachedItem2.recentlyUsed()) {
                                    cachedItem2.setUsed(false);
                                }
                                else {
                                    if (shrinkSize > 0L && !this.cleanerRunning) {
                                        b = true;
                                        this.cleanerRunning = true;
                                        this.needService = true;
                                    }
                                    if (cachedItem2.getEntry().isDirty()) {
                                        if (this.cleaner != null && !this.cleanerRunning) {
                                            b = true;
                                            this.wokenToClean = true;
                                            this.cleanerRunning = true;
                                        }
                                        cachedItem2.keepForClean();
                                        break;
                                    }
                                    final long removeIdentity = this.removeIdentity(cachedItem2);
                                    if (this.useByteCount) {
                                        shrinkSize -= removeIdentity;
                                        if (this.getCurrentSizeNoSync() > this.maximumSize && 0L < shrinkSize) {
                                            if (null == cachedItem) {
                                                cachedItem2.keepForCreate();
                                                cachedItem = cachedItem2;
                                            }
                                            break Label_0472;
                                        }
                                    }
                                    this.incrClockHand();
                                    if (null != cachedItem) {
                                        return cachedItem;
                                    }
                                    cachedItem2.keepForCreate();
                                    return cachedItem2;
                                }
                            }
                        }
                        cachedItem2 = null;
                        --i;
                        this.incrClockHand();
                    }
                    if (cachedItem2 == null) {
                        return cachedItem;
                    }
                }
                try {
                    cachedItem2.clean(false);
                }
                finally {
                    this.release(cachedItem2);
                }
            }
            return cachedItem;
        }
        finally {
            if (b && this.cleaner != null) {
                this.cleaner.serviceNow(this.myClientNumber);
            }
        }
    }
    
    private int incrClockHand() {
        if (++this.clockHand >= this.holders.size()) {
            this.clockHand = 0;
        }
        return this.clockHand;
    }
    
    public int performWork(final ContextManager contextManager) {
        final int performWork = this.performWork(false);
        synchronized (this) {
            this.cleanerRunning = false;
        }
        return performWork;
    }
    
    public boolean serviceASAP() {
        return this.needService;
    }
    
    public boolean serviceImmediately() {
        return false;
    }
    
    public synchronized int getNumberInUse() {
        final int size = this.holders.size();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            if (((CachedItem)this.holders.get(i)).isValid()) {
                ++n;
            }
        }
        return n;
    }
    
    private CachedItem growCache() {
        final CachedItem e = new CachedItem();
        e.keepForCreate();
        synchronized (this) {
            this.holders.add(e);
        }
        return e;
    }
    
    private long removeIdentity(final CachedItem cachedItem) {
        long n = 1L;
        if (this.useByteCount) {
            n = ((SizedCacheable)cachedItem.getEntry()).getSize();
        }
        this.cache_.remove(cachedItem.getEntry().getIdentity());
        cachedItem.setValidState(false);
        --this.validItemCount;
        cachedItem.getEntry().clearIdentity();
        if (this.useByteCount) {
            n -= ((SizedCacheable)cachedItem.getEntry()).getSize();
            this.currentByteCount -= n;
        }
        return n;
    }
    
    private void cleanCache(final Matchable matchable) throws StandardException {
        int i;
        synchronized (this) {
            i = this.holders.size() - 1;
        }
        while (true) {
            CachedItem cachedItem = null;
            synchronized (this) {
                final int size = this.holders.size();
                if (i >= size) {
                    i = size - 1;
                }
                while (i >= 0) {
                    cachedItem = this.holders.get(i);
                    if (cachedItem.isValid()) {
                        if (cachedItem.getEntry().isDirty()) {
                            if (matchable == null || matchable.match(cachedItem.getEntry().getIdentity())) {
                                cachedItem.keepForClean();
                                break;
                            }
                        }
                    }
                    --i;
                    cachedItem = null;
                }
            }
            if (i < 0) {
                break;
            }
            try {
                cachedItem.clean(false);
            }
            finally {
                this.release(cachedItem);
            }
            --i;
        }
    }
    
    private long shrinkSize(final long n) {
        final long maximumSize = this.getMaximumSize();
        final long n2 = n - maximumSize;
        if (n2 <= 0L) {
            return 0L;
        }
        long n3 = maximumSize / 10L;
        if (n3 == 0L) {
            n3 = 2L;
        }
        if (n2 < n3) {
            return n2;
        }
        return n3;
    }
    
    private int performWork(final boolean b) {
        final long n;
        long n2;
        int n3;
        synchronized (this) {
            if (!this.active) {
                this.needService = false;
                return 1;
            }
            final long currentSizeNoSync = this.getCurrentSizeNoSync();
            n = currentSizeNoSync / 20L;
            n2 = (this.wokenToClean ? 0L : this.shrinkSize(currentSizeNoSync));
            if (n == 0L) {
                this.wokenToClean = false;
                this.needService = false;
                return 1;
            }
            if (!this.wokenToClean && n2 <= 0L) {
                this.needService = false;
                return 1;
            }
            n3 = (this.useByteCount ? (this.holders.size() / 10) : ((int)(n * 2L)));
        }
        long n4 = 0L;
        CachedItem cachedItem = null;
        synchronized (this) {
            int size = this.holders.size();
            int clockHand = this.clockHand;
            boolean b2 = false;
            long n5 = this.getCurrentSizeNoSync();
            while (true) {
                if (b) {
                    if (n5 <= this.maximumSize || n2 <= 0L) {
                        break;
                    }
                }
                else if (n4 >= n) {
                    break;
                }
                if (++clockHand >= size) {
                    if (size == 0) {
                        break;
                    }
                    clockHand = 0;
                }
                if (n3-- <= 0) {
                    break;
                }
                cachedItem = (CachedItem)this.holders.get(clockHand);
                if (!cachedItem.isKept()) {
                    if (!cachedItem.isValid()) {
                        if (n2 > 0L) {
                            final long n6 = n2 - n5;
                            this.holders.remove(clockHand);
                            if (this.useByteCount) {
                                this.currentByteCount -= this.getItemSize(cachedItem);
                            }
                            n5 = this.getCurrentSizeNoSync();
                            n2 = n6 + n5;
                            --size;
                            --clockHand;
                            b2 = true;
                        }
                    }
                    else if (!cachedItem.recentlyUsed()) {
                        n4 += this.getItemSize(cachedItem);
                        if (!cachedItem.getEntry().isDirty()) {
                            if (n2 > 0L) {
                                final long n7 = n2 - n5;
                                this.removeIdentity(cachedItem);
                                this.holders.remove(clockHand);
                                if (this.useByteCount) {
                                    this.currentByteCount -= this.getItemSize(cachedItem);
                                }
                                n5 = this.getCurrentSizeNoSync();
                                n2 = n7 + n5;
                                --size;
                                b2 = true;
                                --clockHand;
                            }
                        }
                        else if (!b) {
                            cachedItem.keepForClean();
                            break;
                        }
                    }
                }
                cachedItem = null;
            }
            if (b2) {
                this.trimToSize();
            }
            if (cachedItem == null) {
                this.wokenToClean = false;
                this.needService = false;
                return 1;
            }
        }
        try {
            cachedItem.clean(false);
        }
        catch (StandardException ex) {}
        finally {
            this.release(cachedItem);
        }
        this.needService = true;
        return 2;
    }
    
    private int getItemSize(final CachedItem cachedItem) {
        if (!this.useByteCount) {
            return 1;
        }
        final SizedCacheable sizedCacheable = (SizedCacheable)cachedItem.getEntry();
        if (null == sizedCacheable) {
            return 0;
        }
        return sizedCacheable.getSize();
    }
    
    public synchronized long[] getCacheStats() {
        this.stat.currentSize = this.getCurrentSizeNoSync();
        return this.stat.getStats();
    }
    
    public void resetCacheStats() {
        this.stat.reset();
    }
    
    public synchronized long getMaximumSize() {
        return this.maximumSize;
    }
    
    public void resize(final long maximumSize) throws StandardException {
        final boolean b;
        synchronized (this) {
            this.maximumSize = maximumSize;
            this.stat.maxSize = this.maximumSize;
            b = (this.shrinkSize(this.getCurrentSizeNoSync()) > 0L);
        }
        if (b) {
            this.performWork(true);
            if (this.shrinkSize(this.getCurrentSize()) > 0L) {
                final CachedItem rotateClock = this.rotateClock(2.0f);
                if (rotateClock != null) {
                    rotateClock.unkeepForCreate();
                }
            }
        }
    }
    
    private synchronized long getCurrentSize() {
        return this.getCurrentSizeNoSync();
    }
    
    private long getCurrentSizeNoSync() {
        if (!this.useByteCount) {
            return this.holders.size();
        }
        return this.currentByteCount + this.holders.size() * Clock.ITEM_OVERHEAD;
    }
    
    public void scan(final Matchable matchable, final Operator operator) {
        Object use = null;
        CachedItem cachedItem = null;
        int i = 0;
        while (true) {
            synchronized (this) {
                if (null != cachedItem) {
                    this.release(cachedItem);
                    cachedItem = null;
                }
                while (i < this.holders.size()) {
                    cachedItem = (CachedItem)this.holders.get(i);
                    Label_0108: {
                        if (null != cachedItem) {
                            try {
                                use = cachedItem.use();
                            }
                            catch (StandardException ex) {
                                break Label_0108;
                            }
                            if (null != use && (null == matchable || matchable.match(use))) {
                                cachedItem.keepForClean();
                                break;
                            }
                        }
                    }
                    ++i;
                }
                if (i >= this.holders.size()) {
                    return;
                }
            }
            operator.operate(use);
            ++i;
        }
    }
    
    private void trimToSize() {
        final int size = this.holders.size();
        ++this.trimRequests;
        if (this.trimRequests < size / 8) {
            return;
        }
        this.trimRequests = 0;
        int i = size - 1;
        int n = 0;
        for (int j = 0; j <= i; ++j) {
            final CachedItem element = this.holders.get(j);
            if (!element.isKept()) {
                if (!element.isValid()) {
                    ++n;
                    while (i > j) {
                        final CachedItem element2 = this.holders.get(i);
                        if (element2.isValid()) {
                            this.holders.set(j, element2);
                            this.holders.set(i, element);
                            --i;
                            break;
                        }
                        --i;
                    }
                }
            }
        }
        if (size < 32) {
            return;
        }
        final int n2 = size - n;
        if (n2 > 3 * size / 4) {
            return;
        }
        final int n3 = n2 + n2 / 10;
        if (n3 >= size) {
            return;
        }
        for (int k = size - 1; k > n3; --k) {
            final CachedItem cachedItem = this.holders.get(k);
            if (!cachedItem.isKept()) {
                if (!cachedItem.isValid()) {
                    if (this.useByteCount) {
                        this.currentByteCount -= this.getItemSize(cachedItem);
                    }
                    this.holders.remove(k);
                }
            }
        }
        this.holders.trimToSize();
        this.clockHand = n2 + 1;
    }
    
    public synchronized boolean containsKey(final Object key) {
        return this.cache_.containsKey(key);
    }
    
    public synchronized Collection values() {
        final ArrayList<Cacheable> list = new ArrayList<Cacheable>();
        final Iterator<CachedItem> iterator = this.cache_.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getEntry());
        }
        return list;
    }
    
    static {
        ITEM_OVERHEAD = ClassSize.estimateBaseFromCatalog(CachedItem.class) + ClassSize.getRefSize() + ClassSize.estimateHashEntrySize();
    }
}
