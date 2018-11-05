/***
 * @Author: Borislav Sabotinov
 *
 */
enum EventType {
    ProcessArrival("ProcessArrival"), ProcessCompletion("ProcessCompletion"),
    TimeSliceOccurrence("TimeSliceOccurrence");

    private final String eventType;

    private EventType(String eventType) {
        this.eventType = eventType;
    }
} // end enum

/***
 * @Author: Borislav Sabotinov
 *
 */
public class Event {

    private EventType eventType;
    private Float eventTime;

    Event(EventType eventType, float eventTime) {
        this.eventType = eventType;
        this.eventTime = eventTime;
    }


    float getEventTime() {
        return eventTime;
    }

    public EventType getEventType() {
        return eventType;
    }
    public void setEventTime(float eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return eventType.toString() + " at time " + eventTime.toString();
    }
} // end class
