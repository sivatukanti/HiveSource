// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Arrays;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import java.io.FileFilter;
import java.io.File;
import javax.xml.stream.events.Characters;
import java.util.Map;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.XMLInputFactory;
import java.util.Stack;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import java.util.List;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class ConfTest
{
    private static final String USAGE = "Usage: hadoop conftest [-conffile <path>|-h|--help]\n  Options:\n  \n  -conffile <path>\n    If not specified, the files in ${HADOOP_CONF_DIR}\n    whose name end with .xml will be verified.\n    If specified, that path will be verified.\n    You can specify either a file or directory, and\n    if a directory specified, the files in that directory\n    whose name end with .xml will be verified.\n    You can specify this option multiple times.\n  -h, --help       Print this help";
    private static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
    
    protected ConfTest() {
    }
    
    private static List<NodeInfo> parseConf(final InputStream in) throws XMLStreamException {
        final QName configuration = new QName("configuration");
        final QName property = new QName("property");
        final List<NodeInfo> nodes = new ArrayList<NodeInfo>();
        final Stack<NodeInfo> parsed = new Stack<NodeInfo>();
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLEventReader reader = factory.createXMLEventReader(in);
        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                final StartElement currentElement = event.asStartElement();
                final NodeInfo currentNode = new NodeInfo(currentElement);
                if (parsed.isEmpty()) {
                    if (!currentElement.getName().equals(configuration)) {
                        return null;
                    }
                }
                else {
                    final NodeInfo parentNode = parsed.peek();
                    final QName parentName = parentNode.getStartElement().getName();
                    if (parentName.equals(configuration) && currentNode.getStartElement().getName().equals(property)) {
                        final Iterator<Attribute> it = (Iterator<Attribute>)currentElement.getAttributes();
                        while (it.hasNext()) {
                            currentNode.addAttribute(it.next());
                        }
                    }
                    else if (parentName.equals(property)) {
                        parentNode.addElement(currentElement);
                    }
                }
                parsed.push(currentNode);
            }
            else if (event.isEndElement()) {
                final NodeInfo node = parsed.pop();
                if (parsed.size() != 1) {
                    continue;
                }
                nodes.add(node);
            }
            else {
                if (!event.isCharacters() || 2 >= parsed.size()) {
                    continue;
                }
                final NodeInfo parentNode2 = parsed.pop();
                final StartElement parentElement = parentNode2.getStartElement();
                final NodeInfo grandparentNode = parsed.peek();
                if (grandparentNode.getElement(parentElement) == null) {
                    grandparentNode.setElement(parentElement, event.asCharacters());
                }
                parsed.push(parentNode2);
            }
        }
        return nodes;
    }
    
    public static List<String> checkConf(final InputStream in) {
        List<NodeInfo> nodes = null;
        final List<String> errors = new ArrayList<String>();
        try {
            nodes = parseConf(in);
            if (nodes == null) {
                errors.add("bad conf file: top-level element not <configuration>");
            }
        }
        catch (XMLStreamException e) {
            errors.add("bad conf file: " + e.getMessage());
        }
        if (!errors.isEmpty()) {
            return errors;
        }
        final Map<String, List<Integer>> duplicatedProperties = new HashMap<String, List<Integer>>();
        for (final NodeInfo node : nodes) {
            final StartElement element = node.getStartElement();
            final int line = element.getLocation().getLineNumber();
            if (!element.getName().equals(new QName("property"))) {
                errors.add(String.format("Line %d: element not <property>", line));
            }
            else {
                List<XMLEvent> events = node.getXMLEventsForQName(new QName("name"));
                if (events == null) {
                    errors.add(String.format("Line %d: <property> has no <name>", line));
                }
                else {
                    String v = null;
                    for (final XMLEvent event : events) {
                        if (event.isAttribute()) {
                            v = ((Attribute)event).getValue();
                        }
                        else {
                            final Characters c = node.getElement(event.asStartElement());
                            if (c != null) {
                                v = c.getData();
                            }
                        }
                        if (v == null || v.isEmpty()) {
                            errors.add(String.format("Line %d: <property> has an empty <name>", line));
                        }
                    }
                    if (v != null && !v.isEmpty()) {
                        List<Integer> lines = duplicatedProperties.get(v);
                        if (lines == null) {
                            lines = new ArrayList<Integer>();
                            duplicatedProperties.put(v, lines);
                        }
                        lines.add(node.getStartElement().getLocation().getLineNumber());
                    }
                }
                events = node.getXMLEventsForQName(new QName("value"));
                if (events == null) {
                    errors.add(String.format("Line %d: <property> has no <value>", line));
                }
                for (final QName qName : node.getDuplicatedQNames()) {
                    if (!qName.equals(new QName("source"))) {
                        errors.add(String.format("Line %d: <property> has duplicated <%s>s", line, qName));
                    }
                }
            }
        }
        for (final Map.Entry<String, List<Integer>> e2 : duplicatedProperties.entrySet()) {
            final List<Integer> lines2 = e2.getValue();
            if (1 < lines2.size()) {
                errors.add(String.format("Line %s: duplicated <property>s for %s", StringUtils.join(", ", lines2), e2.getKey()));
            }
        }
        return errors;
    }
    
    private static File[] listFiles(final File dir) {
        return dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isFile() && file.getName().endsWith(".xml");
            }
        });
    }
    
    public static void main(final String[] args) throws IOException {
        final GenericOptionsParser genericParser = new GenericOptionsParser(args);
        final String[] remainingArgs = genericParser.getRemainingArgs();
        OptionBuilder.hasArg();
        final Option conf = OptionBuilder.create("conffile");
        OptionBuilder.withLongOpt("help");
        final Option help = OptionBuilder.create('h');
        final Options opts = new Options().addOption(conf).addOption(help);
        final CommandLineParser specificParser = new GnuParser();
        CommandLine cmd = null;
        try {
            cmd = specificParser.parse(opts, remainingArgs);
        }
        catch (MissingArgumentException e) {
            terminate(1, "No argument specified for -conffile option");
        }
        catch (ParseException e2) {
            terminate(1, "Usage: hadoop conftest [-conffile <path>|-h|--help]\n  Options:\n  \n  -conffile <path>\n    If not specified, the files in ${HADOOP_CONF_DIR}\n    whose name end with .xml will be verified.\n    If specified, that path will be verified.\n    You can specify either a file or directory, and\n    if a directory specified, the files in that directory\n    whose name end with .xml will be verified.\n    You can specify this option multiple times.\n  -h, --help       Print this help");
        }
        if (cmd == null) {
            terminate(1, "Failed to parse options");
        }
        if (cmd.hasOption('h')) {
            terminate(0, "Usage: hadoop conftest [-conffile <path>|-h|--help]\n  Options:\n  \n  -conffile <path>\n    If not specified, the files in ${HADOOP_CONF_DIR}\n    whose name end with .xml will be verified.\n    If specified, that path will be verified.\n    You can specify either a file or directory, and\n    if a directory specified, the files in that directory\n    whose name end with .xml will be verified.\n    You can specify this option multiple times.\n  -h, --help       Print this help");
        }
        List<File> files = new ArrayList<File>();
        if (cmd.hasOption("conffile")) {
            final String[] optionValues;
            final String[] values = optionValues = cmd.getOptionValues("conffile");
            for (final String value : optionValues) {
                final File confFile = new File(value);
                if (confFile.isFile()) {
                    files.add(confFile);
                }
                else if (confFile.isDirectory()) {
                    files.addAll(Arrays.asList(listFiles(confFile)));
                }
                else {
                    terminate(1, confFile.getAbsolutePath() + " is neither a file nor directory");
                }
            }
        }
        else {
            final String confDirName = System.getenv("HADOOP_CONF_DIR");
            if (confDirName == null) {
                terminate(1, "HADOOP_CONF_DIR is not defined");
            }
            final File confDir = new File(confDirName);
            if (!confDir.isDirectory()) {
                terminate(1, "HADOOP_CONF_DIR is not a directory");
            }
            files = Arrays.asList(listFiles(confDir));
        }
        if (files.isEmpty()) {
            terminate(1, "No input file to validate");
        }
        boolean ok = true;
        for (final File file : files) {
            final String path = file.getAbsolutePath();
            final List<String> errors = checkConf(new FileInputStream(file));
            if (errors.isEmpty()) {
                System.out.println(path + ": valid");
            }
            else {
                ok = false;
                System.err.println(path + ":");
                for (final String error : errors) {
                    System.err.println("\t" + error);
                }
            }
        }
        if (ok) {
            System.out.println("OK");
        }
        else {
            terminate(1, "Invalid file exists");
        }
    }
    
    private static void terminate(final int status, final String msg) {
        System.err.println(msg);
        System.exit(status);
    }
}
