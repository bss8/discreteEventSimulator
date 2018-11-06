import java.util.Comparator;
import java.util.PriorityQueue;

/***
 * @Author: Borislav Sabotinov
 *
 */
public class EventQueue {
    private static PriorityQueue<Event> priorityQueue;

    EventQueue() {
        Comparator<Event> comparator = new EventTimeComparator();
        priorityQueue = new PriorityQueue<>(10, comparator);
    }

    void insertEvent(Event e) {
        priorityQueue.add(e);
    }

    // retrieve and remove head of queue
    Event returnAndRemoveHeadEvent() {
        return priorityQueue.poll();
    }

    Event safelyPeekAtNextEvent() {
        return priorityQueue.peek();
    }

    double getSystemTimeFromHead() {
        return priorityQueue.peek().getEventTime();
    }

    @Override
    public String toString() {
        String s = "";
        for (Event e : priorityQueue) {
            s = s + e.toString() + " | ";
        }
        return s;
    }

} // end class
