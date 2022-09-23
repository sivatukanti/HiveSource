// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;
import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.HashMap;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.SystemClock;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class AllocationFileLoaderService extends AbstractService
{
    public static final Log LOG;
    public static final long ALLOC_RELOAD_INTERVAL_MS = 10000L;
    public static final long ALLOC_RELOAD_WAIT_MS = 5000L;
    public static final long THREAD_JOIN_TIMEOUT_MS = 1000L;
    private final Clock clock;
    private long lastSuccessfulReload;
    private boolean lastReloadAttemptFailed;
    private File allocFile;
    private Listener reloadListener;
    @VisibleForTesting
    long reloadIntervalMs;
    private Thread reloadThread;
    private volatile boolean running;
    
    public AllocationFileLoaderService() {
        this(new SystemClock());
    }
    
    public AllocationFileLoaderService(final Clock clock) {
        super(AllocationFileLoaderService.class.getName());
        this.lastReloadAttemptFailed = false;
        this.reloadIntervalMs = 10000L;
        this.running = true;
        this.clock = clock;
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        this.allocFile = this.getAllocationFile(conf);
        if (this.allocFile != null) {
            (this.reloadThread = new Thread() {
                @Override
                public void run() {
                    while (AllocationFileLoaderService.this.running) {
                        final long time = AllocationFileLoaderService.this.clock.getTime();
                        final long lastModified = AllocationFileLoaderService.this.allocFile.lastModified();
                        if (lastModified > AllocationFileLoaderService.this.lastSuccessfulReload && time > lastModified + 5000L) {
                            try {
                                AllocationFileLoaderService.this.reloadAllocations();
                            }
                            catch (Exception ex) {
                                if (!AllocationFileLoaderService.this.lastReloadAttemptFailed) {
                                    AllocationFileLoaderService.LOG.error("Failed to reload fair scheduler config file - will use existing allocations.", ex);
                                }
                                AllocationFileLoaderService.this.lastReloadAttemptFailed = true;
                            }
                        }
                        else if (lastModified == 0L) {
                            if (!AllocationFileLoaderService.this.lastReloadAttemptFailed) {
                                AllocationFileLoaderService.LOG.warn("Failed to reload fair scheduler config file because last modified returned 0. File exists: " + AllocationFileLoaderService.this.allocFile.exists());
                            }
                            AllocationFileLoaderService.this.lastReloadAttemptFailed = true;
                        }
                        try {
                            Thread.sleep(AllocationFileLoaderService.this.reloadIntervalMs);
                        }
                        catch (InterruptedException ex2) {
                            AllocationFileLoaderService.LOG.info("Interrupted while waiting to reload alloc configuration");
                        }
                    }
                }
            }).setName("AllocationFileReloader");
            this.reloadThread.setDaemon(true);
        }
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        if (this.reloadThread != null) {
            this.reloadThread.start();
        }
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        this.running = false;
        if (this.reloadThread != null) {
            this.reloadThread.interrupt();
            try {
                this.reloadThread.join(1000L);
            }
            catch (InterruptedException e) {
                AllocationFileLoaderService.LOG.warn("reloadThread fails to join.");
            }
        }
        super.serviceStop();
    }
    
    public File getAllocationFile(final Configuration conf) {
        final String allocFilePath = conf.get("yarn.scheduler.fair.allocation.file", "fair-scheduler.xml");
        File allocFile = new File(allocFilePath);
        if (!allocFile.isAbsolute()) {
            final URL url = Thread.currentThread().getContextClassLoader().getResource(allocFilePath);
            if (url == null) {
                AllocationFileLoaderService.LOG.warn(allocFilePath + " not found on the classpath.");
                allocFile = null;
            }
            else {
                if (!url.getProtocol().equalsIgnoreCase("file")) {
                    throw new RuntimeException("Allocation file " + url + " found on the classpath is not on the local filesystem.");
                }
                allocFile = new File(url.getPath());
            }
        }
        return allocFile;
    }
    
    public synchronized void setReloadListener(final Listener reloadListener) {
        this.reloadListener = reloadListener;
    }
    
    public synchronized void reloadAllocations() throws IOException, ParserConfigurationException, SAXException, AllocationConfigurationException {
        if (this.allocFile == null) {
            return;
        }
        AllocationFileLoaderService.LOG.info("Loading allocation file " + this.allocFile);
        final Map<String, Resource> minQueueResources = new HashMap<String, Resource>();
        final Map<String, Resource> maxQueueResources = new HashMap<String, Resource>();
        final Map<String, Integer> queueMaxApps = new HashMap<String, Integer>();
        final Map<String, Integer> userMaxApps = new HashMap<String, Integer>();
        final Map<String, Float> queueMaxAMShares = new HashMap<String, Float>();
        final Map<String, ResourceWeights> queueWeights = new HashMap<String, ResourceWeights>();
        final Map<String, SchedulingPolicy> queuePolicies = new HashMap<String, SchedulingPolicy>();
        final Map<String, Long> minSharePreemptionTimeouts = new HashMap<String, Long>();
        final Map<String, Long> fairSharePreemptionTimeouts = new HashMap<String, Long>();
        final Map<String, Float> fairSharePreemptionThresholds = new HashMap<String, Float>();
        final Map<String, Map<QueueACL, AccessControlList>> queueAcls = new HashMap<String, Map<QueueACL, AccessControlList>>();
        int userMaxAppsDefault = Integer.MAX_VALUE;
        int queueMaxAppsDefault = Integer.MAX_VALUE;
        float queueMaxAMShareDefault = 0.5f;
        long defaultFairSharePreemptionTimeout = Long.MAX_VALUE;
        long defaultMinSharePreemptionTimeout = Long.MAX_VALUE;
        float defaultFairSharePreemptionThreshold = 0.5f;
        SchedulingPolicy defaultSchedPolicy = SchedulingPolicy.DEFAULT_POLICY;
        QueuePlacementPolicy newPlacementPolicy = null;
        final Map<FSQueueType, Set<String>> configuredQueues = new HashMap<FSQueueType, Set<String>>();
        for (final FSQueueType queueType : FSQueueType.values()) {
            configuredQueues.put(queueType, new HashSet<String>());
        }
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringComments(true);
        final DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
        final Document doc = builder.parse(this.allocFile);
        final Element root = doc.getDocumentElement();
        if (!"allocations".equals(root.getTagName())) {
            throw new AllocationConfigurationException("Bad fair scheduler config file: top-level element not <allocations>");
        }
        final NodeList elements = root.getChildNodes();
        final List<Element> queueElements = new ArrayList<Element>();
        Element placementPolicyElement = null;
        for (int i = 0; i < elements.getLength(); ++i) {
            final Node node = elements.item(i);
            if (node instanceof Element) {
                final Element element = (Element)node;
                if ("queue".equals(element.getTagName()) || "pool".equals(element.getTagName())) {
                    queueElements.add(element);
                }
                else if ("user".equals(element.getTagName())) {
                    final String userName = element.getAttribute("name");
                    final NodeList fields = element.getChildNodes();
                    for (int j = 0; j < fields.getLength(); ++j) {
                        final Node fieldNode = fields.item(j);
                        if (fieldNode instanceof Element) {
                            final Element field = (Element)fieldNode;
                            if ("maxRunningApps".equals(field.getTagName())) {
                                final String text = ((Text)field.getFirstChild()).getData().trim();
                                final int val = Integer.parseInt(text);
                                userMaxApps.put(userName, val);
                            }
                        }
                    }
                }
                else if ("userMaxAppsDefault".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    final int val2 = userMaxAppsDefault = Integer.parseInt(text2);
                }
                else if ("defaultFairSharePreemptionTimeout".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    final long val3 = defaultFairSharePreemptionTimeout = Long.parseLong(text2) * 1000L;
                }
                else if ("fairSharePreemptionTimeout".equals(element.getTagName())) {
                    if (defaultFairSharePreemptionTimeout == Long.MAX_VALUE) {
                        final String text2 = ((Text)element.getFirstChild()).getData().trim();
                        final long val3 = defaultFairSharePreemptionTimeout = Long.parseLong(text2) * 1000L;
                    }
                }
                else if ("defaultMinSharePreemptionTimeout".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    final long val3 = defaultMinSharePreemptionTimeout = Long.parseLong(text2) * 1000L;
                }
                else if ("defaultFairSharePreemptionThreshold".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    float val4 = Float.parseFloat(text2);
                    val4 = (defaultFairSharePreemptionThreshold = Math.max(Math.min(val4, 1.0f), 0.0f));
                }
                else if ("queueMaxAppsDefault".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    final int val2 = queueMaxAppsDefault = Integer.parseInt(text2);
                }
                else if ("queueMaxAMShareDefault".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    float val4 = Float.parseFloat(text2);
                    val4 = (queueMaxAMShareDefault = Math.min(val4, 1.0f));
                }
                else if ("defaultQueueSchedulingPolicy".equals(element.getTagName()) || "defaultQueueSchedulingMode".equals(element.getTagName())) {
                    final String text2 = ((Text)element.getFirstChild()).getData().trim();
                    defaultSchedPolicy = SchedulingPolicy.parse(text2);
                }
                else if ("queuePlacementPolicy".equals(element.getTagName())) {
                    placementPolicyElement = element;
                }
                else {
                    AllocationFileLoaderService.LOG.warn("Bad element in allocations file: " + element.getTagName());
                }
            }
        }
        for (final Element element2 : queueElements) {
            String parent = "root";
            if (element2.getAttribute("name").equalsIgnoreCase("root")) {
                if (queueElements.size() > 1) {
                    throw new AllocationConfigurationException("If configuring root queue, no other queues can be placed alongside it.");
                }
                parent = null;
            }
            this.loadQueue(parent, element2, minQueueResources, maxQueueResources, queueMaxApps, userMaxApps, queueMaxAMShares, queueWeights, queuePolicies, minSharePreemptionTimeouts, fairSharePreemptionTimeouts, fairSharePreemptionThresholds, queueAcls, configuredQueues);
        }
        final Configuration conf = this.getConfig();
        if (placementPolicyElement != null) {
            newPlacementPolicy = QueuePlacementPolicy.fromXml(placementPolicyElement, configuredQueues, conf);
        }
        else {
            newPlacementPolicy = QueuePlacementPolicy.fromConfiguration(conf, configuredQueues);
        }
        if (!minSharePreemptionTimeouts.containsKey("root")) {
            minSharePreemptionTimeouts.put("root", defaultMinSharePreemptionTimeout);
        }
        if (!fairSharePreemptionTimeouts.containsKey("root")) {
            fairSharePreemptionTimeouts.put("root", defaultFairSharePreemptionTimeout);
        }
        if (!fairSharePreemptionThresholds.containsKey("root")) {
            fairSharePreemptionThresholds.put("root", defaultFairSharePreemptionThreshold);
        }
        final AllocationConfiguration info = new AllocationConfiguration(minQueueResources, maxQueueResources, queueMaxApps, userMaxApps, queueWeights, queueMaxAMShares, userMaxAppsDefault, queueMaxAppsDefault, queueMaxAMShareDefault, queuePolicies, defaultSchedPolicy, minSharePreemptionTimeouts, fairSharePreemptionTimeouts, fairSharePreemptionThresholds, queueAcls, newPlacementPolicy, configuredQueues);
        this.lastSuccessfulReload = this.clock.getTime();
        this.lastReloadAttemptFailed = false;
        this.reloadListener.onReload(info);
    }
    
    private void loadQueue(final String parentName, final Element element, final Map<String, Resource> minQueueResources, final Map<String, Resource> maxQueueResources, final Map<String, Integer> queueMaxApps, final Map<String, Integer> userMaxApps, final Map<String, Float> queueMaxAMShares, final Map<String, ResourceWeights> queueWeights, final Map<String, SchedulingPolicy> queuePolicies, final Map<String, Long> minSharePreemptionTimeouts, final Map<String, Long> fairSharePreemptionTimeouts, final Map<String, Float> fairSharePreemptionThresholds, final Map<String, Map<QueueACL, AccessControlList>> queueAcls, final Map<FSQueueType, Set<String>> configuredQueues) throws AllocationConfigurationException {
        String queueName = element.getAttribute("name");
        if (parentName != null) {
            queueName = parentName + "." + queueName;
        }
        final Map<QueueACL, AccessControlList> acls = new HashMap<QueueACL, AccessControlList>();
        final NodeList fields = element.getChildNodes();
        boolean isLeaf = true;
        for (int j = 0; j < fields.getLength(); ++j) {
            final Node fieldNode = fields.item(j);
            if (fieldNode instanceof Element) {
                final Element field = (Element)fieldNode;
                if ("minResources".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final Resource val = FairSchedulerConfiguration.parseResourceConfigValue(text);
                    minQueueResources.put(queueName, val);
                }
                else if ("maxResources".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final Resource val = FairSchedulerConfiguration.parseResourceConfigValue(text);
                    maxQueueResources.put(queueName, val);
                }
                else if ("maxRunningApps".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final int val2 = Integer.parseInt(text);
                    queueMaxApps.put(queueName, val2);
                }
                else if ("maxAMShare".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    float val3 = Float.parseFloat(text);
                    val3 = Math.min(val3, 1.0f);
                    queueMaxAMShares.put(queueName, val3);
                }
                else if ("weight".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final double val4 = Double.parseDouble(text);
                    queueWeights.put(queueName, new ResourceWeights((float)val4));
                }
                else if ("minSharePreemptionTimeout".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final long val5 = Long.parseLong(text) * 1000L;
                    minSharePreemptionTimeouts.put(queueName, val5);
                }
                else if ("fairSharePreemptionTimeout".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final long val5 = Long.parseLong(text) * 1000L;
                    fairSharePreemptionTimeouts.put(queueName, val5);
                }
                else if ("fairSharePreemptionThreshold".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    float val3 = Float.parseFloat(text);
                    val3 = Math.max(Math.min(val3, 1.0f), 0.0f);
                    fairSharePreemptionThresholds.put(queueName, val3);
                }
                else if ("schedulingPolicy".equals(field.getTagName()) || "schedulingMode".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData().trim();
                    final SchedulingPolicy policy = SchedulingPolicy.parse(text);
                    queuePolicies.put(queueName, policy);
                }
                else if ("aclSubmitApps".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData();
                    acls.put(QueueACL.SUBMIT_APPLICATIONS, new AccessControlList(text));
                }
                else if ("aclAdministerApps".equals(field.getTagName())) {
                    final String text = ((Text)field.getFirstChild()).getData();
                    acls.put(QueueACL.ADMINISTER_QUEUE, new AccessControlList(text));
                }
                else if ("queue".endsWith(field.getTagName()) || "pool".equals(field.getTagName())) {
                    this.loadQueue(queueName, field, minQueueResources, maxQueueResources, queueMaxApps, userMaxApps, queueMaxAMShares, queueWeights, queuePolicies, minSharePreemptionTimeouts, fairSharePreemptionTimeouts, fairSharePreemptionThresholds, queueAcls, configuredQueues);
                    configuredQueues.get(FSQueueType.PARENT).add(queueName);
                    isLeaf = false;
                }
            }
        }
        if (isLeaf) {
            if ("parent".equals(element.getAttribute("type"))) {
                configuredQueues.get(FSQueueType.PARENT).add(queueName);
            }
            else {
                configuredQueues.get(FSQueueType.LEAF).add(queueName);
            }
        }
        queueAcls.put(queueName, acls);
        if (maxQueueResources.containsKey(queueName) && minQueueResources.containsKey(queueName) && !Resources.fitsIn(minQueueResources.get(queueName), maxQueueResources.get(queueName))) {
            AllocationFileLoaderService.LOG.warn(String.format("Queue %s has max resources %s less than min resources %s", queueName, maxQueueResources.get(queueName), minQueueResources.get(queueName)));
        }
    }
    
    static {
        LOG = LogFactory.getLog(AllocationFileLoaderService.class.getName());
    }
    
    public interface Listener
    {
        void onReload(final AllocationConfiguration p0);
    }
}
