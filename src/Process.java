/***
 * @Author: Borislav Sabotinov
 * A process's arrival time is the same as the Event ProcessArrival's eventTime.
 * The burst time is obtained by passing 1/avgServiceTime as the lambda in genexp(lambda)
 * The remainingCpuTime is initialized to burst time and is then used to track the process's progress on the CPU
 * and whether we may consider it complete or not.
 */
public class Process {
    private double arrivalTime;      // = eventTIme
    private double burstTime;        // clock + genexp(1/avgSvcTime)
    private double completionTime;   //
    private double waitingTime;      //
    private double turnaroundTime;   // = completionTime - startTime
    private double startTime;        // = clock when given to CPU
    private double remainingCpuTime; //
    private boolean isReturning;    //

    public Process() {
        this.isReturning = false;
    }


    /* Getters and Setters */
    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(double burstTime) {
        this.burstTime = burstTime;
    }

    public double getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(double turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getRemainingCpuTime() {
        return remainingCpuTime;
    }

    public void setRemainingCpuTime(double remainingCpuTime) {
        this.remainingCpuTime = remainingCpuTime;
    }

    public boolean isReturning() {
        return isReturning;
    }

    public void setReturning(boolean returning) {
        isReturning = returning;
    }
} // end class
