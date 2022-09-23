// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.reloading.ReloadingEvent;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;

final class ReloadingBuilderSupportListener implements EventListener<Event>
{
    private final BasicConfigurationBuilder<?> builder;
    private final ReloadingController reloadingController;
    
    private ReloadingBuilderSupportListener(final BasicConfigurationBuilder<?> configBuilder, final ReloadingController controller) {
        this.builder = configBuilder;
        this.reloadingController = controller;
    }
    
    public static ReloadingBuilderSupportListener connect(final BasicConfigurationBuilder<?> configBuilder, final ReloadingController controller) {
        final ReloadingBuilderSupportListener listener = new ReloadingBuilderSupportListener(configBuilder, controller);
        controller.addEventListener(ReloadingEvent.ANY, listener);
        configBuilder.installEventListener(ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, listener);
        return listener;
    }
    
    @Override
    public void onEvent(final Event event) {
        if (ConfigurationBuilderResultCreatedEvent.RESULT_CREATED.equals(event.getEventType())) {
            this.reloadingController.resetReloadingState();
        }
        else {
            this.builder.resetResult();
        }
    }
}
