/***
 * @Author: Borislav Sabotinov
 * A process's arrival time is the same as the Event ProcessArrival's eventTime.
 * The burst time is obtained by passing 1/avgServiceTime as the lambda in genexp(lambda)
 * The remainingCpuTime is initialized to burst time and is then used to track the process's progress on the CPU
 * and whether we may consider it complete or not.
 */
public class Process {
    private float arrivalTime;      // = eventTIme
    private float burstTime;        // clock + genexp(1/avgSvcTime)
    private float completionTime;   //
    private float waitingTime;      //
    private float turnaroundTime;   // = completionTime - startTime
    private float startTime;        // = clock when given to CPU
    private float remainingCpuTime; //
    private boolean isReturning;    //

    public Process() {
        this.isReturning = false;
    }


    /* Getters and Setters */
    public float getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(float arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public float getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(float burstTime) {
        this.burstTime = burstTime;
    }

    public float getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(float completionTime) {
        this.completionTime = completionTime;
    }

    public float getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(float waitingTime) {
        this.waitingTime = waitingTime;
    }

    public float getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(float turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getRemainingCpuTime() {
        return remainingCpuTime;
    }

    public void setRemainingCpuTime(float remainingCpuTime) {
        this.remainingCpuTime = remainingCpuTime;
    }

    public boolean isReturning() {
        return isReturning;
    }

    public void setReturning(boolean returning) {
        isReturning = returning;
    }
} // end class
