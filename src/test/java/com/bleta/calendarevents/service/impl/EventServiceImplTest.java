package com.bleta.calendarevents.service.impl;

import com.bleta.calendarevents.model.Event;
import com.bleta.calendarevents.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceImplTest {

	@Mock
	private EventRepository eventRepository;

	private EventServiceImpl eventService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		eventService = new EventServiceImpl(eventRepository);
	}

	@Test
	void getAllEvents_ReturnsListOfEvents() {
		// Arrange
		List<Event> expectedEvents = Arrays.asList(
		  new Event(1L, "Event 1", LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
		  new Event(2L, "Event 2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
		);
		when(eventRepository.findAll()).thenReturn(expectedEvents);

		// Act
		List<Event> actualEvents = eventService.getAllEvents();

		// Assert
		assertEquals(expectedEvents, actualEvents);
		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void getEventById_ExistingEventId_ReturnsEvent() {
		// Arrange
		Long eventId = 1L;
		Event expectedEvent = new Event(eventId, "Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(expectedEvent));

		// Act
		Event actualEvent = eventService.getEventById(eventId);

		// Assert
		assertEquals(expectedEvent, actualEvent);
		verify(eventRepository, times(1)).findById(eventId);
	}

	@Test
	void getEventById_NonExistingEventId_ReturnsNull() {
		// Arrange
		Long eventId = 1L;
		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		// Act
		Event actualEvent = eventService.getEventById(eventId);

		// Assert
		assertNull(actualEvent);
		verify(eventRepository, times(1)).findById(eventId);
	}

	@Test
	void createEvent_NonOverlappingEvent_ReturnsCreatedEvent() {
		// Arrange
		Event event = new Event(1L, "New Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		when(eventRepository.findAll()).thenReturn(Collections.emptyList());
		when(eventRepository.save(event)).thenReturn(event);

		// Act
		Event createdEvent = eventService.createEvent(event);

		// Assert
		assertEquals(event, createdEvent);
		verify(eventRepository, times(1)).findAll();
		verify(eventRepository, times(1)).save(event);
	}

	@Test
	void createEvent_OverlappingEvent_ThrowsIllegalArgumentException() {
		// Arrange
		Event event = new Event(1L, "Overlapping Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		Event existingEvent = new Event(2L, "Existing Event", LocalDateTime.now(), LocalDateTime.now().plusHours(2));
		when(eventRepository.findAll()).thenReturn(List.of(existingEvent));

		// Act and Assert
		assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(event));
		verify(eventRepository, times(1)).findAll();
		verify(eventRepository, never()).save(event);
	}

	@Test
	void updateEvent_ExistingEvent_ReturnsUpdatedEvent() {
		// Arrange
		Long eventId = 1L;
		Event existingEvent = new Event(eventId, "Existing Event", LocalDateTime.now(),
		  LocalDateTime.now().plusHours(1));
		Event updatedEvent = new Event(eventId, "Updated Event", LocalDateTime.now().plusHours(2),
		  LocalDateTime.now().plusHours(3));

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
		when(eventRepository.save(existingEvent)).thenReturn(updatedEvent);
		when(eventRepository.findAll()).thenReturn(List.of(existingEvent));

		// Act
		Event actualEvent = eventService.updateEvent(eventId, updatedEvent);

		// Assert
		assertEquals(updatedEvent, actualEvent);
		verify(eventRepository, times(1)).findById(eventId);
		verify(eventRepository, times(1)).save(existingEvent);
		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void updateEvent_NonExistingEvent_ReturnsNull() {
		// Arrange
		Long eventId = 1L;
		Event updatedEvent = new Event(eventId, "Updated Event", LocalDateTime.now().plusMinutes(30),
		  LocalDateTime.now().plusHours(2));

		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		// Act
		Event actualEvent = eventService.updateEvent(eventId, updatedEvent);

		// Assert
		assertNull(actualEvent);
		verify(eventRepository, times(1)).findById(eventId);
		verify(eventRepository, never()).save(any(Event.class));
		verify(eventRepository, never()).findAll();
	}

	@Test
	void updateEvent_OverlappingEvent_ThrowsIllegalArgumentException() {
		// Arrange
		Long eventId = 1L;
		Event existingEvent = new Event(eventId, "Existing Event", LocalDateTime.now(),
		  LocalDateTime.now().plusHours(2));
		Event updatedEvent = new Event(eventId, "Updated Event", LocalDateTime.now().plusMinutes(30),
		  LocalDateTime.now().plusHours(3));

		when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
		when(eventRepository.findAll()).thenReturn(List.of(existingEvent));

		// Act and Assert
		assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(eventId, updatedEvent));
		verify(eventRepository, times(1)).findById(eventId);
		verify(eventRepository, never()).save(any(Event.class));
		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void deleteEvent_ExistingEvent_DeletesEvent() {
		// Arrange
		Long eventId = 1L;

		// Act
		eventService.deleteEvent(eventId);

		// Assert
		verify(eventRepository, times(1)).deleteById(eventId);
	}
}
