// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import java.util.PriorityQueue;
import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.google.common.collect.ArrayListMultimap;
import java.util.HashMap;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ListMultimap;
import java.util.Map;
import org.apache.commons.logging.Log;

public class MaxRunningAppsEnforcer
{
    private static final Log LOG;
    private final FairScheduler scheduler;
    private final Map<String, Integer> usersNumRunnableApps;
    @VisibleForTesting
    final ListMultimap<String, FSAppAttempt> usersNonRunnableApps;
    
    public MaxRunningAppsEnforcer(final FairScheduler scheduler) {
        this.scheduler = scheduler;
        this.usersNumRunnableApps = new HashMap<String, Integer>();
        this.usersNonRunnableApps = (ListMultimap<String, FSAppAttempt>)ArrayListMultimap.create();
    }
    
    public boolean canAppBeRunnable(FSQueue queue, final String user) {
        final AllocationConfiguration allocConf = this.scheduler.getAllocationConfiguration();
        Integer userNumRunnable = this.usersNumRunnableApps.get(user);
        if (userNumRunnable == null) {
            userNumRunnable = 0;
        }
        if (userNumRunnable >= allocConf.getUserMaxApps(user)) {
            return false;
        }
        while (queue != null) {
            final int queueMaxApps = allocConf.getQueueMaxApps(queue.getName());
            if (queue.getNumRunnableApps() >= queueMaxApps) {
                return false;
            }
            queue = queue.getParent();
        }
        return true;
    }
    
    public void trackRunnableApp(final FSAppAttempt app) {
        final String user = app.getUser();
        final FSLeafQueue queue = app.getQueue();
        for (FSParentQueue parent = queue.getParent(); parent != null; parent = parent.getParent()) {
            parent.incrementRunnableApps();
        }
        final Integer userNumRunnable = this.usersNumRunnableApps.get(user);
        this.usersNumRunnableApps.put(user, ((userNumRunnable == null) ? 0 : userNumRunnable) + 1);
    }
    
    public void trackNonRunnableApp(final FSAppAttempt app) {
        final String user = app.getUser();
        this.usersNonRunnableApps.put(user, app);
    }
    
    public void updateRunnabilityOnAppRemoval(final FSAppAttempt app, final FSLeafQueue queue) {
        final AllocationConfiguration allocConf = this.scheduler.getAllocationConfiguration();
        FSQueue highestQueueWithAppsNowRunnable = (queue.getNumRunnableApps() == allocConf.getQueueMaxApps(queue.getName()) - 1) ? queue : null;
        for (FSParentQueue parent = queue.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getNumRunnableApps() == allocConf.getQueueMaxApps(parent.getName()) - 1) {
                highestQueueWithAppsNowRunnable = parent;
            }
        }
        final List<List<FSAppAttempt>> appsNowMaybeRunnable = new ArrayList<List<FSAppAttempt>>();
        if (highestQueueWithAppsNowRunnable != null) {
            this.gatherPossiblyRunnableAppLists(highestQueueWithAppsNowRunnable, appsNowMaybeRunnable);
        }
        final String user = app.getUser();
        Integer userNumRunning = this.usersNumRunnableApps.get(user);
        if (userNumRunning == null) {
            userNumRunning = 0;
        }
        if (userNumRunning == allocConf.getUserMaxApps(user) - 1) {
            final List<FSAppAttempt> userWaitingApps = this.usersNonRunnableApps.get(user);
            if (userWaitingApps != null) {
                appsNowMaybeRunnable.add(userWaitingApps);
            }
        }
        final Iterator<FSAppAttempt> iter = new MultiListStartTimeIterator(appsNowMaybeRunnable);
        FSAppAttempt prev = null;
        final List<FSAppAttempt> noLongerPendingApps = new ArrayList<FSAppAttempt>();
        while (iter.hasNext()) {
            final FSAppAttempt next = iter.next();
            if (next == prev) {
                continue;
            }
            if (this.canAppBeRunnable(next.getQueue(), next.getUser())) {
                this.trackRunnableApp(next);
                final FSAppAttempt appSched = next;
                next.getQueue().getRunnableAppSchedulables().add(appSched);
                noLongerPendingApps.add(appSched);
                if (noLongerPendingApps.size() >= appsNowMaybeRunnable.size()) {
                    break;
                }
            }
            prev = next;
        }
        final Iterator i$ = noLongerPendingApps.iterator();
        while (i$.hasNext()) {
            final FSAppAttempt appSched = i$.next();
            if (!appSched.getQueue().getNonRunnableAppSchedulables().remove(appSched)) {
                MaxRunningAppsEnforcer.LOG.error("Can't make app runnable that does not already exist in queue as non-runnable: " + appSched + ". This should never happen.");
            }
            if (!this.usersNonRunnableApps.remove(appSched.getUser(), appSched)) {
                MaxRunningAppsEnforcer.LOG.error("Waiting app " + appSched + " expected to be in " + "usersNonRunnableApps, but was not. This should never happen.");
            }
        }
    }
    
    public void untrackRunnableApp(final FSAppAttempt app) {
        final String user = app.getUser();
        final int newUserNumRunning = this.usersNumRunnableApps.get(user) - 1;
        if (newUserNumRunning == 0) {
            this.usersNumRunnableApps.remove(user);
        }
        else {
            this.usersNumRunnableApps.put(user, newUserNumRunning);
        }
        final FSLeafQueue queue = app.getQueue();
        for (FSParentQueue parent = queue.getParent(); parent != null; parent = parent.getParent()) {
            parent.decrementRunnableApps();
        }
    }
    
    public void untrackNonRunnableApp(final FSAppAttempt app) {
        this.usersNonRunnableApps.remove(app.getUser(), app);
    }
    
    private void gatherPossiblyRunnableAppLists(final FSQueue queue, final List<List<FSAppAttempt>> appLists) {
        if (queue.getNumRunnableApps() < this.scheduler.getAllocationConfiguration().getQueueMaxApps(queue.getName())) {
            if (queue instanceof FSLeafQueue) {
                appLists.add(((FSLeafQueue)queue).getNonRunnableAppSchedulables());
            }
            else {
                for (final FSQueue child : queue.getChildQueues()) {
                    this.gatherPossiblyRunnableAppLists(child, appLists);
                }
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(FairScheduler.class);
    }
    
    static class MultiListStartTimeIterator implements Iterator<FSAppAttempt>
    {
        private List<FSAppAttempt>[] appLists;
        private int[] curPositionsInAppLists;
        private PriorityQueue<IndexAndTime> appListsByCurStartTime;
        
        public MultiListStartTimeIterator(final List<List<FSAppAttempt>> appListList) {
            this.appLists = appListList.toArray(new List[appListList.size()]);
            this.curPositionsInAppLists = new int[this.appLists.length];
            this.appListsByCurStartTime = new PriorityQueue<IndexAndTime>();
            for (int i = 0; i < this.appLists.length; ++i) {
                final long time = this.appLists[i].isEmpty() ? Long.MAX_VALUE : this.appLists[i].get(0).getStartTime();
                this.appListsByCurStartTime.add(new IndexAndTime(i, time));
            }
        }
        
        @Override
        public boolean hasNext() {
            return !this.appListsByCurStartTime.isEmpty() && this.appListsByCurStartTime.peek().time != Long.MAX_VALUE;
        }
        
        @Override
        public FSAppAttempt next() {
            final IndexAndTime indexAndTime = this.appListsByCurStartTime.remove();
            final int nextListIndex = indexAndTime.index;
            final FSAppAttempt next = this.appLists[nextListIndex].get(this.curPositionsInAppLists[nextListIndex]);
            final int[] curPositionsInAppLists = this.curPositionsInAppLists;
            final int n = nextListIndex;
            ++curPositionsInAppLists[n];
            if (this.curPositionsInAppLists[nextListIndex] < this.appLists[nextListIndex].size()) {
                indexAndTime.time = this.appLists[nextListIndex].get(this.curPositionsInAppLists[nextListIndex]).getStartTime();
            }
            else {
                indexAndTime.time = Long.MAX_VALUE;
            }
            this.appListsByCurStartTime.add(indexAndTime);
            return next;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
        
        private static class IndexAndTime implements Comparable<IndexAndTime>
        {
            public int index;
            public long time;
            
            public IndexAndTime(final int index, final long time) {
                this.index = index;
                this.time = time;
            }
            
            @Override
            public int compareTo(final IndexAndTime o) {
                return (this.time < o.time) ? -1 : ((this.time > o.time) ? 1 : 0);
            }
            
            @Override
            public boolean equals(final Object o) {
                if (!(o instanceof IndexAndTime)) {
                    return false;
                }
                final IndexAndTime other = (IndexAndTime)o;
                return other.time == this.time;
            }
            
            @Override
            public int hashCode() {
                return (int)this.time;
            }
        }
    }
}
