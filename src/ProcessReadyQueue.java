import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/***
 * @Author: Borislav Sabotinov
 * This class behaves as static - declared final to prevent extension of the class
 * Private constructor prevents instantiation by client code as we do not want to instantiate
 * Make all the members and functions of the class static - since the class cannot be instantiated
 * no instance methods can be called or instance fields accessed.
 * The process ready queue is implemented in different way depending on the scheduler.
 * 1) If FCFS - use arrival time to sort queue
 * 2) If SRTF - use remaining time to sort queue
 * 3) If RR - starts off with arrival time but an early arriving process with a long burst time may end up at the back
 * of the line, so need a simple list to maintain order. A pre-empted process will go to the tail. After quantum
 * expires, next process is obtained from the head.
 */
public final class ProcessReadyQueue {

    // serves as single instance of PRQ to be used throughout simulation
    private static ProcessReadyQueue obj = null;

    private static Queue<Process> schedulerPriorityQueue;

    //Private Constructor will prevent the instantiation of this class directly
    private ProcessReadyQueue(int schedulerType) {
        createProcessReadyQueue(schedulerType);
    }

    public static ProcessReadyQueue createSingleProcessReadyQueueInstance(int schedulerType) {

        //This logic will ensure that no more than one object can be created at a time
        if (obj == null) {
            obj = new ProcessReadyQueue(schedulerType);
        }
        return obj;
    }

    private void createProcessReadyQueue(int schedulerType) {
        // FCFS
        if(schedulerType == SchedulerType.FCFS.getSchedulerType()) {
            // TODO: use ProcessArrivalTimeComparator for priorityQueue
            ProcessArrivalTimeComparator fcfsComparator = new ProcessArrivalTimeComparator();
            schedulerPriorityQueue = new PriorityQueue<>(10, fcfsComparator);
        }
        // SRTF
        else if (schedulerType == 2) {
            // TODO: use ProcessRemainingTimeComparator for priorityQueue
            ProcessRemainingTimeComparator srtfComparator = new ProcessRemainingTimeComparator();
            schedulerPriorityQueue = new PriorityQueue<>(10, srtfComparator);
        }
        // RR
        else if (schedulerType == 3) {
            // TODO: use LinkedList, insert new processes at the tail, remove from the head.
            schedulerPriorityQueue = new LinkedList<>();
        }
    }

    public void insertProcess(Process p) {
        //adds to queue or if List, to end of list
        schedulerPriorityQueue.add(p);
    }

    public Process returnAndRemoveHeadProcess() {
        //retrieve and remove head
        return schedulerPriorityQueue.poll();
    }

    public boolean isEmpty() {
        return schedulerPriorityQueue.isEmpty();
    }

    public Process peek() {
        return schedulerPriorityQueue.peek();
    }

} // end class
