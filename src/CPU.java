/***
 * @author Borislav Sabotinov
 * CPU receives a process and: sets the start time if this is the first time the process has been serviced,
 * setting process' boolean flag isReturning from false to true, so the next time it would be ID'd as a returning process.
 * If CPU is working on a process, it sets its boolean flag isBusy to true.
 */
public class CPU {

    private boolean isBusy;
    private Process myProcess;

    CPU() {
        isBusy = false;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public void tick() {
        this.myProcess.setRemainingCpuTime(this.myProcess.getRemainingCpuTime() - 0.01f);
    }

    public Process getMyProcess() {
        return myProcess;
    }

    public void setMyProcess(Process myProcess) {
        this.myProcess = myProcess;
    }
} // end class
