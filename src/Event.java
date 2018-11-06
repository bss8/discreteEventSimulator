/***
 * @author Borislav Sabotinov
 * Strictly typed events enumerated in enum to prevent the use of "magic numbers" directly
 * (i.e., passing 1, 2, or 3 directly to a constructor - what is 1?)
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
 * @author Borislav Sabotinov
 * Defines what constitutes an event and internalizes the time at which it occurs
 */
public class Event {

    private EventType eventType;
    private Double eventTime;

    Event(EventType eventType, double eventTime) {
        this.eventType = eventType;
        this.eventTime = eventTime;
    }

    double getEventTime() {
        return eventTime;
    }

    public EventType getEventType() {
        return eventType;
    }
    public void setEventTime(double eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return eventType.toString() + " at time " + eventTime.toString();
    }
} // end class
