/***
 * @Author: Borislav Sabotinov
 * Strictly define allowed scheduler types by enumerating, preventing the use of "Magic numbers"
 * (i.e., using an int 1 directly to compare type or pass to constructor? What is 1?
 * SchedulerType.FCFS is clearer.
 */

enum SchedulerType {
    FCFS(1), SRTF(2), RR(3);

    private final int schedulerType;

    private SchedulerType(int schedulerType) {
        this.schedulerType = schedulerType;
    }

    public int getSchedulerType() {
        return schedulerType;
    }
} // end enum

/**
 * @Author: Borislav Sabotinov
 * Main class definition for what constitutes a scheduling algorithm. Abstract definition of both properties and
 * behavior that all schedulers share.
 */
public abstract class SchedulingAlgorithm implements iPerformanceMetrics {

    private SchedulerType schedulerType;
    ProcessReadyQueue myQueue;

    static double runningTurnaroundSum = 0;
    static double runningBurstTimeSum = 0;
    static double runningWaitTimeSum = 0;

    // default constructor to be overwritten by specialization classes FCFS, SRTF, RR
    SchedulingAlgorithm() {

    }

    // Implement methods from interface as required
    @Override
    public double avgTurnaroundTime(double totalSimTime) {
      return runningTurnaroundSum / 10000;
    }
    @Override
    public double throughput(double totalSimTime) {
      return 10000 / totalSimTime;
    }
    @Override
    public double cpuUtilization(double totalSimTime) {
      return runningBurstTimeSum / totalSimTime;
    }
    @Override
    public double avgProcessesInReadyQueue(int lambda) {
      return lambda * (runningWaitTimeSum/10000);
    }


    public SchedulerType getSchedulerType() {
        return schedulerType;
    }

    public void setSchedulerType(SchedulerType schedulerType) {
        this.schedulerType = schedulerType;
    }

    public Process getNextProcessForCPU() {
        return myQueue.returnAndRemoveHeadProcess();
    }

    public Process safelyPeekAtNextProcess() { return myQueue.peek(); }

    public void addProcessToReadyQueue(Process p) {
        myQueue.insertProcess(p);
    }

} // end class
