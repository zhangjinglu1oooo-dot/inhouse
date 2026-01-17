package com.inhouse.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventStore {
    private final List<Event> events = new CopyOnWriteArrayList<Event>();

    public List<Event> getEvents() {
        return events;
    }
}
