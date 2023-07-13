package com.bleta.calendarevents.controller;

import com.bleta.calendarevents.model.Event;
import com.bleta.calendarevents.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

	private final EventService eventService;

	@Autowired
	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping
	public ResponseEntity<List<Event>> getAllEvents() {
		List<Event> events = eventService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getEventById(@PathVariable Long id) {
		Event event = eventService.getEventById(id);
		if (event == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
		}

		return ResponseEntity.ok(event);
	}

	@PostMapping
	public ResponseEntity<Object> createEvent(@RequestBody Event event) {
		try {
			Event createdEvent = eventService.createEvent(event);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateEvent(@PathVariable Long id, @RequestBody Event event) {
		try {
			Event updatedEvent = eventService.updateEvent(id, event);
			if (updatedEvent == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
			}

			return ResponseEntity.ok(updatedEvent);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteEvent(@PathVariable Long id) {
		eventService.deleteEvent(id);
		return ResponseEntity.noContent().build();
	}
}