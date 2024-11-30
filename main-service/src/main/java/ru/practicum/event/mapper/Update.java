package ru.practicum.event.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.event.model.Event;


import java.time.LocalDateTime;

public class Update {
    public static Event updateEvent(Event event, EventUpdate eventUpdated, Category category) {

        if (eventUpdated.getId() != null) {
            event.setId(eventUpdated.getId());
        }

        if (eventUpdated.getAnnotation() != null) {
            event.setAnnotation(eventUpdated.getAnnotation());
        }

        if (eventUpdated.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventUpdated.getDescription() != null) {
            event.setDescription(eventUpdated.getDescription());
        }

        if (eventUpdated.getLocation() != null) {
            event.setLocation(eventUpdated.getLocation());
        }

        if (eventUpdated.getPaid() != null) {
            event.setPaid(eventUpdated.getPaid());
        }

        if (eventUpdated.getParticipantLimit() != 0) {
            event.setLimit(eventUpdated.getParticipantLimit());
        }

        if (eventUpdated.getRequestModeration() != null) {
            event.setModeration(eventUpdated.getRequestModeration());
        }

        if (eventUpdated.getTitle() != null) {
            event.setTitle(eventUpdated.getTitle());
        }

        if (eventUpdated.getCreatedOn() != null) {
            event.setCreated(eventUpdated.getCreatedOn());
        }

        if (eventUpdated.getStateAction() != null) {
            if (eventUpdated.getStateAction().equals("PUBLISH_EVENT")) {
                event.setState("PUBLISHED");
                event.setPublishDate(LocalDateTime.now());
            }

            if ((eventUpdated.getStateAction().equals("REJECT_EVENT"))
                    || (eventUpdated.getStateAction().equals("CANCEL_REVIEW"))) {
                event.setState("CANCELED");
            }

            if (eventUpdated.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState("PENDING");
            }

        }
        return event;
    }
}
