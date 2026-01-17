package com.inhouse.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 事件发布与查询控制器。
 */
@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {
    // 内存事件存储
    private final EventStore store;

    public EventController(EventStore store) {
        this.store = store;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event publish(@RequestBody Event event) {
        // 写入事件
        event.setId(UUID.randomUUID().toString());
        event.setCreatedAt(new Date());
        store.getEvents().add(event);
        return event;
    }

    @GetMapping
    public List<Event> list() {
        return new ArrayList<Event>(store.getEvents());
    }
}
