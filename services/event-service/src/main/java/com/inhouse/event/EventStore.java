package com.inhouse.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件内存存储。
 */
public class EventStore {
    // 线程安全事件集合
    private final List<Event> events = new CopyOnWriteArrayList<Event>();

    public List<Event> getEvents() {
        return events;
    }
}
