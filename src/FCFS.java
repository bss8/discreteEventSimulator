/***
 * @Author: Borislav Sabotinov
 *
 */
public class FCFS extends SchedulingAlgorithm {



    FCFS () {
        this.setSchedulerType(SchedulerType.FCFS);
        myQueue = ProcessReadyQueue.createSingleProcessReadyQueueInstance(SchedulerType.FCFS.getSchedulerType());
    }



} // end class
