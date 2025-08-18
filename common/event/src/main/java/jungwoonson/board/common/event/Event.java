package jungwoonson.board.common.event;

import jungwoonson.board.common.dataserializer.DataSerializer;
import lombok.Getter;

@Getter
public class Event<T extends EventPayload> {

    private Long eventId;
    private EventType type;
    private T payload;

    public static Event<EventPayload> of(Long eventId, EventType type, EventPayload payload) {
        Event<EventPayload> event = new Event<>();
        event.eventId = eventId;
        event.type = type;
        event.payload = payload;
        return event;
    }

    public String toJson() {
        return DataSerializer.serialize(this);
    }

    public static Event<EventPayload> fromJson(String json) {
        EventRow eventRow = DataSerializer.deserialize(json, EventRow.class);
        if (eventRow == null) {
            return null;
        }
        Event<EventPayload> event = new Event<>();
        event.eventId = eventRow.getEventId();
        event.type = EventType.from(eventRow.getType());
        event.payload = DataSerializer.deserialize(eventRow.getPayload(), event.type.getPayloadClass());
        return event;
    }

    @Getter
    private static class EventRow {
        private Long eventId;
        private String type;
        private Object payload;
    }
}
