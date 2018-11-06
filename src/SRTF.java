/***
 * @Author: Borislav Sabotinov
 * Shortest Remaining Time First specialization class
 */
public class SRTF extends SchedulingAlgorithm {

    SRTF() {
        this.setSchedulerType(SchedulerType.SRTF);
        myQueue = ProcessReadyQueue.createSingleProcessReadyQueueInstance(SchedulerType.SRTF.getSchedulerType());
    }

} // end class
