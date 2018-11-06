/***
 * @Author: Borislav Sabotinov
 *
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


public abstract class SchedulingAlgorithm implements iPerformanceMetrics {

    private SchedulerType schedulerType;
    ProcessReadyQueue myQueue;

    static double runningTurnaroundSum = 0f;
    static double runningBurstTimeSum = 0f;
    static double runningWaitTimeSum = 0f;

    SchedulingAlgorithm() {

    }
    @Override
    public double avgTurnaroundTime() {
      return runningTurnaroundSum / 10000;
    }
    @Override
    public double throughput(double totalSimTime) {
      return 10000f / totalSimTime;
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
