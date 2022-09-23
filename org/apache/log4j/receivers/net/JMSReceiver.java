// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import org.apache.log4j.spi.LoggingEvent;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import javax.jms.TopicSubscriber;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import java.util.Hashtable;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import javax.naming.InitialContext;
import org.apache.log4j.component.plugins.Plugin;
import javax.jms.TopicConnection;
import javax.jms.MessageListener;
import org.apache.log4j.component.plugins.Receiver;

public class JMSReceiver extends Receiver implements MessageListener
{
    private boolean active;
    protected String topicFactoryName;
    protected String topicName;
    protected String userId;
    protected String password;
    protected TopicConnection topicConnection;
    protected String jndiPath;
    private String remoteInfo;
    private String providerUrl;
    
    public JMSReceiver() {
        this.active = false;
    }
    
    public JMSReceiver(final String _topicFactoryName, final String _topicName, final String _userId, final String _password, final String _jndiPath) {
        this.active = false;
        this.topicFactoryName = _topicFactoryName;
        this.topicName = _topicName;
        this.userId = _userId;
        this.password = _password;
        this.jndiPath = _jndiPath;
    }
    
    public void setJndiPath(final String _jndiPath) {
        this.jndiPath = _jndiPath;
    }
    
    public String getJndiPath() {
        return this.jndiPath;
    }
    
    public void setTopicFactoryName(final String _topicFactoryName) {
        this.topicFactoryName = _topicFactoryName;
    }
    
    public String getTopicFactoryName() {
        return this.topicFactoryName;
    }
    
    public void setTopicName(final String _topicName) {
        this.topicName = _topicName;
    }
    
    public String getTopicName() {
        return this.topicName;
    }
    
    public void setUserId(final String _userId) {
        this.userId = _userId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setPassword(final String _password) {
        this.password = _password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public boolean isEquivalent(final Plugin testPlugin) {
        if (testPlugin instanceof JMSReceiver) {
            final JMSReceiver receiver = (JMSReceiver)testPlugin;
            return this.topicFactoryName.equals(receiver.getTopicFactoryName()) && (this.jndiPath == null || this.jndiPath.equals(receiver.getJndiPath())) && super.isEquivalent(testPlugin);
        }
        return false;
    }
    
    public synchronized boolean isActive() {
        return this.active;
    }
    
    protected synchronized void setActive(final boolean _active) {
        this.active = _active;
    }
    
    public void activateOptions() {
        if (!this.isActive()) {
            try {
                this.remoteInfo = this.topicFactoryName + ":" + this.topicName;
                Context ctx = null;
                if (this.jndiPath == null || this.jndiPath.equals("")) {
                    ctx = new InitialContext();
                }
                else {
                    final FileInputStream is = new FileInputStream(this.jndiPath);
                    final Properties p = new Properties();
                    p.load(is);
                    is.close();
                    ctx = new InitialContext(p);
                }
                this.providerUrl = (String)ctx.getEnvironment().get("java.naming.provider.url");
                final TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)this.lookup(ctx, this.topicFactoryName);
                if (this.userId != null && this.password != null) {
                    this.topicConnection = topicConnectionFactory.createTopicConnection(this.userId, this.password);
                }
                else {
                    this.topicConnection = topicConnectionFactory.createTopicConnection();
                }
                final TopicSession topicSession = this.topicConnection.createTopicSession(false, 1);
                final Topic topic = (Topic)ctx.lookup(this.topicName);
                final TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
                topicSubscriber.setMessageListener((MessageListener)this);
                this.topicConnection.start();
                this.setActive(true);
            }
            catch (Exception e) {
                this.setActive(false);
                if (this.topicConnection != null) {
                    try {
                        this.topicConnection.close();
                    }
                    catch (Exception ex) {}
                    this.topicConnection = null;
                }
                this.getLogger().error("Could not start JMSReceiver.", e);
            }
        }
    }
    
    public synchronized void shutdown() {
        if (this.isActive()) {
            this.setActive(false);
            if (this.topicConnection != null) {
                try {
                    this.topicConnection.close();
                }
                catch (Exception ex) {}
                this.topicConnection = null;
            }
        }
    }
    
    public void onMessage(final Message message) {
        try {
            if (message instanceof ObjectMessage) {
                final ObjectMessage objectMessage = (ObjectMessage)message;
                final LoggingEvent event = (LoggingEvent)objectMessage.getObject();
                event.setProperty("log4j.remoteSourceInfo", this.remoteInfo);
                event.setProperty("log4j.jmsProviderUrl", this.providerUrl);
                this.doPost(event);
            }
            else {
                this.getLogger().warn("Received message is of type " + message.getJMSType() + ", was expecting ObjectMessage.");
            }
        }
        catch (Exception e) {
            this.getLogger().error("Exception thrown while processing incoming message.", e);
        }
    }
    
    protected Object lookup(final Context ctx, final String name) throws NamingException {
        try {
            return ctx.lookup(name);
        }
        catch (NameNotFoundException e) {
            this.getLogger().error("Could not find name [" + name + "].");
            throw e;
        }
    }
}
