package com.bleta.calendarevents.service.impl;


import com.bleta.calendarevents.model.Event;
import com.bleta.calendarevents.repository.EventRepository;
import com.bleta.calendarevents.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;

	@Autowired
	public EventServiceImpl(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Override
	public List<Event> getAllEvents() {
		return eventRepository.findAll();
	}

	@Override
	public Event getEventById(Long id) {
		return eventRepository.findById(id).orElse(null);
	}

	@Override
	public Event createEvent(Event event) {
		if (isEventOverlap(event)) {
			throw new IllegalArgumentException("Event overlaps with existing events");
		}
		return eventRepository.save(event);
	}

	@Override
	public Event updateEvent(Long id, Event event) {
		Event existingEvent = eventRepository.findById(id).orElse(null);
		if (existingEvent == null) {
			return null;
		}

		// Check for overlap only if the start and end time are modified
		if (!event.getStartTime().equals(existingEvent.getStartTime()) || !event.getEndTime()
		  .equals(existingEvent.getEndTime())) {
			if (isEventOverlap(event)) {
				throw new IllegalArgumentException("Event overlaps with existing events");
			}
		}

		existingEvent.setTitle(event.getTitle());
		existingEvent.setStartTime(event.getStartTime());
		existingEvent.setEndTime(event.getEndTime());

		return eventRepository.save(existingEvent);
	}

	@Override
	public void deleteEvent(Long id) {
		eventRepository.deleteById(id);
	}

	// CCheck if the event overlaps with existing events
	private boolean isEventOverlap(Event event) {
		List<Event> existingEvents = eventRepository.findAll();

		return existingEvents.stream()
		  .anyMatch(existingEvent -> isTimeOverlap(existingEvent.getStartTime(), existingEvent.getEndTime(),
			event.getStartTime(), event.getEndTime()));
	}

	// Check if two time intervals overlap
	private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
		return start1.isBefore(end2) && start2.isBefore(end1);
	}
}
