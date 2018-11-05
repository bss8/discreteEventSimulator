/***
 * @Author: Borislav Sabotinov
 *
 */
public class RR extends SchedulingAlgorithm {

    RR() {
        this.setSchedulerType(SchedulerType.RR);
        myQueue = ProcessReadyQueue.createSingleProcessReadyQueueInstance(SchedulerType.RR.getSchedulerType());
    }

} // end class
