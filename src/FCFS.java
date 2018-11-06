/***
 * @author Borislav Sabotinov
 * First Come First Served specialization class
 */
public class FCFS extends SchedulingAlgorithm {



    FCFS () {
        this.setSchedulerType(SchedulerType.FCFS);
        myQueue = ProcessReadyQueue.createSingleProcessReadyQueueInstance(SchedulerType.FCFS.getSchedulerType());
    }



} // end class
