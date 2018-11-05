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

    static float runningTurnaroundSum = 0f;
    static float runningBurstTimeSum = 0f;
    static float runningWaitTimeSum = 0f;

    SchedulingAlgorithm() {

    }
    @Override
    public float avgTurnaroundTime(float totalSimTime) {
      return runningTurnaroundSum / 10000f;
    }
    @Override
    public float throughput(float totalSimTime) {
      return 10000f / totalSimTime;
    }
    @Override
    public float cpuUtilization(float totalSimTime) {
      return runningBurstTimeSum / totalSimTime;
    }
    @Override
    public float avgProcessesInReadyQueue(int lambda) {
      return lambda * (runningWaitTimeSum/10000f);
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

    public void addProcessToReadyQueue(Process p) {
        myQueue.insertProcess(p);
    }

} // end class
