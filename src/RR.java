/***
 * @author Borislav Sabotinov
 * Round Robin specialization class that inherits from abstract Scheduling Algorithm
 */
public class RR extends SchedulingAlgorithm {

    RR() {
        this.setSchedulerType(SchedulerType.RR);
        myQueue = ProcessReadyQueue.createSingleProcessReadyQueueInstance(SchedulerType.RR.getSchedulerType());
    }

} // end class
