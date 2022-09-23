// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.extras;

import org.apache.log4j.spi.LoggingEvent;
import java.net.MalformedURLException;
import org.apache.log4j.helpers.LogLog;
import java.applet.Applet;
import java.net.URL;
import java.applet.AudioClip;
import org.apache.log4j.AppenderSkeleton;

public final class SoundAppender extends AppenderSkeleton
{
    private AudioClip clip;
    private String audioURL;
    
    public void activateOptions() {
        try {
            this.clip = Applet.newAudioClip(new URL(this.audioURL));
        }
        catch (MalformedURLException mue) {
            LogLog.error("unable to initialize SoundAppender", mue);
        }
        if (this.clip == null) {
            LogLog.error("Unable to initialize SoundAppender");
        }
    }
    
    public String getAudioURL() {
        return this.audioURL;
    }
    
    public void setAudioURL(final String audioURL) {
        this.audioURL = audioURL;
    }
    
    protected void append(final LoggingEvent event) {
        if (this.clip != null) {
            this.clip.play();
        }
    }
    
    public void close() {
    }
    
    public boolean requiresLayout() {
        return false;
    }
}
