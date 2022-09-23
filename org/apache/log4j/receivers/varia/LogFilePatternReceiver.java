// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.varia;

import org.apache.log4j.component.ULogger;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import org.apache.log4j.Category;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.Logger;
import java.util.Hashtable;
import org.apache.log4j.Level;
import java.util.StringTokenizer;
import org.apache.log4j.rule.ExpressionRule;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.MatchResult;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Iterator;
import org.apache.log4j.spi.LoggingEvent;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.Reader;
import java.util.Map;
import org.apache.log4j.rule.Rule;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.log4j.component.plugins.Receiver;

public class LogFilePatternReceiver extends Receiver
{
    private final List keywords;
    private static final String PROP_START = "PROP(";
    private static final String PROP_END = ")";
    private static final String LOGGER = "LOGGER";
    private static final String MESSAGE = "MESSAGE";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String NDC = "NDC";
    private static final String LEVEL = "LEVEL";
    private static final String THREAD = "THREAD";
    private static final String CLASS = "CLASS";
    private static final String FILE = "FILE";
    private static final String LINE = "LINE";
    private static final String METHOD = "METHOD";
    private static final String DEFAULT_HOST = "file";
    private static final String EXCEPTION_PATTERN = "^\\s+at.*";
    private static final String REGEXP_DEFAULT_WILDCARD = ".*?";
    private static final String REGEXP_GREEDY_WILDCARD = ".*";
    private static final String PATTERN_WILDCARD = "*";
    private static final String NOSPACE_GROUP = "(\\S*\\s*?)";
    private static final String DEFAULT_GROUP = "(.*?)";
    private static final String GREEDY_GROUP = "(.*)";
    private static final String MULTIPLE_SPACES_REGEXP = "[ ]+";
    private final String newLine;
    private final String[] emptyException;
    private SimpleDateFormat dateFormat;
    private String timestampFormat;
    private String logFormat;
    private String customLevelDefinitions;
    private String fileURL;
    private String host;
    private String path;
    private boolean tailing;
    private String filterExpression;
    private long waitMillis;
    private static final String VALID_DATEFORMAT_CHARS = "GyMwWDdFEaHkKhmsSzZ";
    private static final String VALID_DATEFORMAT_CHAR_PATTERN = "[GyMwWDdFEaHkKhmsSzZ]";
    private Rule expressionRule;
    private Map currentMap;
    private List additionalLines;
    private List matchingKeywords;
    private String regexp;
    private Reader reader;
    private Pattern regexpPattern;
    private Pattern exceptionPattern;
    private String timestampPatternText;
    private boolean useCurrentThread;
    public static final int MISSING_FILE_RETRY_MILLIS = 10000;
    private boolean appendNonMatches;
    private final Map customLevelDefinitionMap;
    
    public LogFilePatternReceiver() {
        this.keywords = new ArrayList();
        this.newLine = System.getProperty("line.separator");
        this.emptyException = new String[] { "" };
        this.timestampFormat = "yyyy-MM-d HH:mm:ss,SSS";
        this.waitMillis = 2000L;
        this.customLevelDefinitionMap = new HashMap();
        this.keywords.add("TIMESTAMP");
        this.keywords.add("LOGGER");
        this.keywords.add("LEVEL");
        this.keywords.add("THREAD");
        this.keywords.add("CLASS");
        this.keywords.add("FILE");
        this.keywords.add("LINE");
        this.keywords.add("METHOD");
        this.keywords.add("MESSAGE");
        this.keywords.add("NDC");
        try {
            this.exceptionPattern = Pattern.compile("^\\s+at.*");
        }
        catch (PatternSyntaxException ex) {}
    }
    
    public String getFileURL() {
        return this.fileURL;
    }
    
    public void setFileURL(final String fileURL) {
        this.fileURL = fileURL;
    }
    
    public void setCustomLevelDefinitions(final String customLevelDefinitions) {
        this.customLevelDefinitions = customLevelDefinitions;
    }
    
    public String getCustomLevelDefinitions() {
        return this.customLevelDefinitions;
    }
    
    public boolean isAppendNonMatches() {
        return this.appendNonMatches;
    }
    
    public void setAppendNonMatches(final boolean appendNonMatches) {
        this.appendNonMatches = appendNonMatches;
    }
    
    public String getFilterExpression() {
        return this.filterExpression;
    }
    
    public void setFilterExpression(final String filterExpression) {
        this.filterExpression = filterExpression;
    }
    
    public boolean isTailing() {
        return this.tailing;
    }
    
    public void setTailing(final boolean tailing) {
        this.tailing = tailing;
    }
    
    public final boolean isUseCurrentThread() {
        return this.useCurrentThread;
    }
    
    public final void setUseCurrentThread(final boolean useCurrentThread) {
        this.useCurrentThread = useCurrentThread;
    }
    
    public String getLogFormat() {
        return this.logFormat;
    }
    
    public void setLogFormat(final String logFormat) {
        this.logFormat = logFormat;
    }
    
    public void setTimestampFormat(final String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }
    
    public String getTimestampFormat() {
        return this.timestampFormat;
    }
    
    public long getWaitMillis() {
        return this.waitMillis;
    }
    
    public void setWaitMillis(final long waitMillis) {
        this.waitMillis = waitMillis;
    }
    
    private int getExceptionLine() {
        for (int i = 0; i < this.additionalLines.size(); ++i) {
            final Matcher exceptionMatcher = this.exceptionPattern.matcher(this.additionalLines.get(i));
            if (exceptionMatcher.matches()) {
                return i;
            }
        }
        return -1;
    }
    
    private String buildMessage(final String firstMessageLine, final int exceptionLine) {
        if (this.additionalLines.size() == 0) {
            return firstMessageLine;
        }
        final StringBuffer message = new StringBuffer();
        if (firstMessageLine != null) {
            message.append(firstMessageLine);
        }
        for (int linesToProcess = (exceptionLine == -1) ? this.additionalLines.size() : exceptionLine, i = 0; i < linesToProcess; ++i) {
            message.append(this.newLine);
            message.append(this.additionalLines.get(i));
        }
        return message.toString();
    }
    
    private String[] buildException(final int exceptionLine) {
        if (exceptionLine == -1) {
            return this.emptyException;
        }
        final String[] exception = new String[this.additionalLines.size() - exceptionLine - 1];
        for (int i = 0; i < exception.length; ++i) {
            exception[i] = this.additionalLines.get(i + exceptionLine);
        }
        return exception;
    }
    
    private LoggingEvent buildEvent() {
        if (this.currentMap.size() == 0) {
            if (this.additionalLines.size() > 0) {
                final Iterator iter = this.additionalLines.iterator();
                while (iter.hasNext()) {
                    this.getLogger().info("found non-matching line: " + iter.next());
                }
            }
            this.additionalLines.clear();
            return null;
        }
        final int exceptionLine = this.getExceptionLine();
        final String[] exception = this.buildException(exceptionLine);
        if (this.additionalLines.size() > 0 && exception.length > 0) {
            this.currentMap.put("MESSAGE", this.buildMessage(this.currentMap.get("MESSAGE"), exceptionLine));
        }
        final LoggingEvent event = this.convertToEvent(this.currentMap, exception);
        this.currentMap.clear();
        this.additionalLines.clear();
        return event;
    }
    
    protected void process(final BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            final Matcher eventMatcher = this.regexpPattern.matcher(line);
            if (line.trim().equals("")) {
                continue;
            }
            final Matcher exceptionMatcher = this.exceptionPattern.matcher(line);
            if (eventMatcher.matches()) {
                final LoggingEvent event = this.buildEvent();
                if (event != null && this.passesExpression(event)) {
                    this.doPost(event);
                }
                this.currentMap.putAll(this.processEvent(eventMatcher.toMatchResult()));
            }
            else if (exceptionMatcher.matches()) {
                this.additionalLines.add(line);
            }
            else if (this.appendNonMatches) {
                final String lastTime = this.currentMap.get("TIMESTAMP");
                if (this.currentMap.size() > 0) {
                    final LoggingEvent event2 = this.buildEvent();
                    if (event2 != null && this.passesExpression(event2)) {
                        this.doPost(event2);
                    }
                }
                if (lastTime != null) {
                    this.currentMap.put("TIMESTAMP", lastTime);
                }
                this.currentMap.put("MESSAGE", line);
            }
            else {
                this.additionalLines.add(line);
            }
        }
        final LoggingEvent event = this.buildEvent();
        if (event != null && this.passesExpression(event)) {
            this.doPost(event);
        }
    }
    
    protected void createPattern() {
        this.regexpPattern = Pattern.compile(this.regexp);
    }
    
    private boolean passesExpression(final LoggingEvent event) {
        return event == null || this.expressionRule == null || this.expressionRule.evaluate(event, null);
    }
    
    private Map processEvent(final MatchResult result) {
        final Map map = new HashMap();
        for (int i = 1; i < result.groupCount() + 1; ++i) {
            final Object key = this.matchingKeywords.get(i - 1);
            final Object value = result.group(i);
            map.put(key, value);
        }
        return map;
    }
    
    private String convertTimestamp() {
        String result = this.timestampFormat.replaceAll("[GyMwWDdFEaHkKhmsSzZ]+", "\\\\S+");
        result = result.replaceAll(Pattern.quote("."), "\\\\.");
        return result;
    }
    
    protected void setHost(final String host) {
        this.host = host;
    }
    
    protected void setPath(final String path) {
        this.path = path;
    }
    
    public String getPath() {
        return this.path;
    }
    
    protected void initialize() {
        if (this.host == null && this.path == null) {
            try {
                final URL url = new URL(this.fileURL);
                this.host = url.getHost();
                this.path = url.getPath();
            }
            catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        if (this.host == null || this.host.trim().equals("")) {
            this.host = "file";
        }
        if (this.path == null || this.path.trim().equals("")) {
            this.path = this.fileURL;
        }
        this.currentMap = new HashMap();
        this.additionalLines = new ArrayList();
        this.matchingKeywords = new ArrayList();
        if (this.timestampFormat != null) {
            this.dateFormat = new SimpleDateFormat(this.quoteTimeStampChars(this.timestampFormat));
            this.timestampPatternText = this.convertTimestamp();
        }
        this.updateCustomLevelDefinitionMap();
        try {
            if (this.filterExpression != null) {
                this.expressionRule = ExpressionRule.getRule(this.filterExpression);
            }
        }
        catch (Exception e2) {
            this.getLogger().warn("Invalid filter expression: " + this.filterExpression, e2);
        }
        final List buildingKeywords = new ArrayList();
        String newPattern = this.logFormat;
        int index = 0;
        String current = newPattern;
        final List propertyNames = new ArrayList();
        while (index > -1) {
            if (current.indexOf("PROP(") > -1 && current.indexOf(")") > -1) {
                index = current.indexOf("PROP(");
                final String longPropertyName = current.substring(current.indexOf("PROP("), current.indexOf(")") + 1);
                final String shortProp = this.getShortPropertyName(longPropertyName);
                buildingKeywords.add(shortProp);
                propertyNames.add(longPropertyName);
                current = current.substring(longPropertyName.length() + 1 + index);
                newPattern = this.singleReplace(newPattern, longPropertyName, new Integer(buildingKeywords.size() - 1).toString());
            }
            else {
                index = -1;
            }
        }
        for (final String keyword : this.keywords) {
            final int index2 = newPattern.indexOf(keyword);
            if (index2 > -1) {
                buildingKeywords.add(keyword);
                newPattern = this.singleReplace(newPattern, keyword, new Integer(buildingKeywords.size() - 1).toString());
            }
        }
        String buildingInt = "";
        for (int i = 0; i < newPattern.length(); ++i) {
            final String thisValue = String.valueOf(newPattern.substring(i, i + 1));
            if (this.isInteger(thisValue)) {
                buildingInt += thisValue;
            }
            else {
                if (this.isInteger(buildingInt)) {
                    this.matchingKeywords.add(buildingKeywords.get(Integer.parseInt(buildingInt)));
                }
                buildingInt = "";
            }
        }
        if (this.isInteger(buildingInt)) {
            this.matchingKeywords.add(buildingKeywords.get(Integer.parseInt(buildingInt)));
        }
        newPattern = this.replaceMetaChars(newPattern);
        newPattern = newPattern.replaceAll("[ ]+", "[ ]+");
        newPattern = newPattern.replaceAll(Pattern.quote("*"), ".*?");
        for (int i = 0; i < buildingKeywords.size(); ++i) {
            final String keyword2 = buildingKeywords.get(i);
            if (i == buildingKeywords.size() - 1) {
                newPattern = this.singleReplace(newPattern, String.valueOf(i), "(.*)");
            }
            else if ("TIMESTAMP".equals(keyword2)) {
                newPattern = this.singleReplace(newPattern, String.valueOf(i), "(" + this.timestampPatternText + ")");
            }
            else if ("LOGGER".equals(keyword2) || "LEVEL".equals(keyword2)) {
                newPattern = this.singleReplace(newPattern, String.valueOf(i), "(\\S*\\s*?)");
            }
            else {
                newPattern = this.singleReplace(newPattern, String.valueOf(i), "(.*?)");
            }
        }
        this.regexp = newPattern;
        this.getLogger().debug("regexp is " + this.regexp);
    }
    
    private void updateCustomLevelDefinitionMap() {
        if (this.customLevelDefinitions != null) {
            final StringTokenizer entryTokenizer = new StringTokenizer(this.customLevelDefinitions, ",");
            this.customLevelDefinitionMap.clear();
            while (entryTokenizer.hasMoreTokens()) {
                final StringTokenizer innerTokenizer = new StringTokenizer(entryTokenizer.nextToken(), "=");
                this.customLevelDefinitionMap.put(innerTokenizer.nextToken(), Level.toLevel(innerTokenizer.nextToken()));
            }
        }
    }
    
    private boolean isInteger(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    private String quoteTimeStampChars(final String input) {
        final StringBuffer result = new StringBuffer();
        boolean lastCharIsDateFormat = false;
        for (int i = 0; i < input.length(); ++i) {
            final String thisVal = input.substring(i, i + 1);
            final boolean thisCharIsDateFormat = "GyMwWDdFEaHkKhmsSzZ".contains(thisVal);
            if (!thisCharIsDateFormat && (i == 0 || lastCharIsDateFormat)) {
                result.append("'");
            }
            if (thisCharIsDateFormat && i > 0 && !lastCharIsDateFormat) {
                result.append("'");
            }
            lastCharIsDateFormat = thisCharIsDateFormat;
            result.append(thisVal);
        }
        if (!lastCharIsDateFormat) {
            result.append("'");
        }
        return result.toString();
    }
    
    private String singleReplace(String inputString, final String oldString, final String newString) {
        final int propLength = oldString.length();
        final int startPos = inputString.indexOf(oldString);
        if (startPos == -1) {
            this.getLogger().info("string: " + oldString + " not found in input: " + inputString + " - returning input");
            return inputString;
        }
        if (startPos == 0) {
            inputString = inputString.substring(propLength);
            inputString = newString + inputString;
        }
        else {
            inputString = inputString.substring(0, startPos) + newString + inputString.substring(startPos + propLength);
        }
        return inputString;
    }
    
    private String getShortPropertyName(final String longPropertyName) {
        final String currentProp = longPropertyName.substring(longPropertyName.indexOf("PROP("));
        final String prop = currentProp.substring(0, currentProp.indexOf(")") + 1);
        final String shortProp = prop.substring("PROP(".length(), prop.length() - 1);
        return shortProp;
    }
    
    private String replaceMetaChars(String input) {
        input = input.replaceAll("\\\\", "\\\\\\");
        input = input.replaceAll(Pattern.quote("]"), "\\\\]");
        input = input.replaceAll(Pattern.quote("["), "\\\\[");
        input = input.replaceAll(Pattern.quote("^"), "\\\\^");
        input = input.replaceAll(Pattern.quote("$"), "\\\\$");
        input = input.replaceAll(Pattern.quote("."), "\\\\.");
        input = input.replaceAll(Pattern.quote("|"), "\\\\|");
        input = input.replaceAll(Pattern.quote("?"), "\\\\?");
        input = input.replaceAll(Pattern.quote("+"), "\\\\+");
        input = input.replaceAll(Pattern.quote("("), "\\\\(");
        input = input.replaceAll(Pattern.quote(")"), "\\\\)");
        input = input.replaceAll(Pattern.quote("-"), "\\\\-");
        input = input.replaceAll(Pattern.quote("{"), "\\\\{");
        input = input.replaceAll(Pattern.quote("}"), "\\\\}");
        input = input.replaceAll(Pattern.quote("#"), "\\\\#");
        return input;
    }
    
    private LoggingEvent convertToEvent(final Map fieldMap, String[] exception) {
        if (fieldMap == null) {
            return null;
        }
        if (!fieldMap.containsKey("LOGGER")) {
            fieldMap.put("LOGGER", "Unknown");
        }
        if (exception == null) {
            exception = this.emptyException;
        }
        Logger logger = null;
        long timeStamp = 0L;
        String level = null;
        String threadName = null;
        Object message = null;
        String ndc = null;
        String className = null;
        String methodName = null;
        String eventFileName = null;
        String lineNumber = null;
        final Hashtable properties = new Hashtable();
        logger = Logger.getLogger(fieldMap.remove("LOGGER"));
        if (this.dateFormat != null && fieldMap.containsKey("TIMESTAMP")) {
            try {
                timeStamp = this.dateFormat.parse(fieldMap.remove("TIMESTAMP")).getTime();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (timeStamp == 0L) {
            timeStamp = System.currentTimeMillis();
        }
        message = fieldMap.remove("MESSAGE");
        if (message == null) {
            message = "";
        }
        level = fieldMap.remove("LEVEL");
        Level levelImpl;
        if (level == null) {
            levelImpl = Level.DEBUG;
        }
        else {
            levelImpl = this.customLevelDefinitionMap.get(level);
            if (levelImpl == null) {
                levelImpl = Level.toLevel(level.trim());
                if (!level.equals(levelImpl.toString()) && levelImpl == null) {
                    levelImpl = Level.DEBUG;
                    this.getLogger().debug("found unexpected level: " + level + ", logger: " + logger.getName() + ", msg: " + message);
                    message = level + " " + message;
                }
            }
        }
        threadName = fieldMap.remove("THREAD");
        ndc = fieldMap.remove("NDC");
        className = fieldMap.remove("CLASS");
        methodName = fieldMap.remove("METHOD");
        eventFileName = fieldMap.remove("FILE");
        lineNumber = fieldMap.remove("LINE");
        properties.put("hostname", this.host);
        properties.put("application", this.path);
        properties.put("receiver", this.getName());
        properties.putAll(fieldMap);
        LocationInfo info = null;
        if (eventFileName != null || className != null || methodName != null || lineNumber != null) {
            info = new LocationInfo(eventFileName, className, methodName, lineNumber);
        }
        else {
            info = LocationInfo.NA_LOCATION_INFO;
        }
        final LoggingEvent event = new LoggingEvent(null, logger, timeStamp, levelImpl, message, threadName, new ThrowableInformation(exception), ndc, info, properties);
        return event;
    }
    
    public void shutdown() {
        this.getLogger().info(this.getPath() + " shutdown");
        this.active = false;
        try {
            if (this.reader != null) {
                this.reader.close();
                this.reader = null;
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void activateOptions() {
        this.getLogger().info("activateOptions");
        this.active = true;
        final Runnable runnable = new Runnable() {
            public void run() {
                LogFilePatternReceiver.this.initialize();
                while (LogFilePatternReceiver.this.reader == null) {
                    ComponentBase.this.getLogger().info("attempting to load file: " + LogFilePatternReceiver.this.getFileURL());
                    try {
                        LogFilePatternReceiver.this.reader = new InputStreamReader(new URL(LogFilePatternReceiver.this.getFileURL()).openStream());
                        continue;
                    }
                    catch (FileNotFoundException fnfe) {
                        ComponentBase.this.getLogger().info("file not available - will try again");
                        synchronized (this) {
                            try {
                                this.wait(10000L);
                            }
                            catch (InterruptedException ex) {}
                        }
                        continue;
                    }
                    catch (IOException ioe) {
                        ComponentBase.this.getLogger().warn("unable to load file", ioe);
                        return;
                    }
                    break;
                }
                try {
                    final BufferedReader bufferedReader = new BufferedReader(LogFilePatternReceiver.this.reader);
                    LogFilePatternReceiver.this.createPattern();
                    do {
                        LogFilePatternReceiver.this.process(bufferedReader);
                        try {
                            synchronized (this) {
                                this.wait(LogFilePatternReceiver.this.waitMillis);
                            }
                        }
                        catch (InterruptedException ex2) {}
                        if (LogFilePatternReceiver.this.tailing) {
                            ComponentBase.this.getLogger().debug("tailing file");
                        }
                    } while (LogFilePatternReceiver.this.tailing);
                }
                catch (IOException ioe) {
                    ComponentBase.this.getLogger().info("stream closed");
                }
                ComponentBase.this.getLogger().debug("processing " + LogFilePatternReceiver.this.path + " complete");
                LogFilePatternReceiver.this.shutdown();
            }
        };
        if (this.useCurrentThread) {
            runnable.run();
        }
        else {
            new Thread(runnable, "LogFilePatternReceiver-" + this.getName()).start();
        }
    }
}
