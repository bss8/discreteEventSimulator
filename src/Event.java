/***
 * @Author: Borislav Sabotinov
 *
 */
enum EventType {
    ProcessArrival("ProcessArrival"), ProcessCompletion("ProcessCompletion"),
    TimeSliceOccurrence("TimeSliceOccurrence"), Preemption("Preemption");

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
