package com.edison.EventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Vengat G on 1/4/2019.
 */

public class EventMessage {

    private String notification;

    public EventMessage(String notification)
    {
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

}
