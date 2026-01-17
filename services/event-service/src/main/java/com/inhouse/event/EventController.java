package com.inhouse.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventStore store;

    public EventController(EventStore store) {
        this.store = store;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event publish(@RequestBody Event event) {
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
