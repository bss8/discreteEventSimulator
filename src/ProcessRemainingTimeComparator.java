import java.util.Comparator;

/***
 * @Author: Borislav Sabotinov
 * Used by SRTF scheduler.
 * Compares processes by their remainingCpuTime, so the process with the least remaining time is first in the queue.
 */
public class ProcessRemainingTimeComparator implements Comparator<Process> {

    /**
     *
     * @param p1
     * @param p2
     * @return
     */
    @Override
    public int compare(Process p1, Process p2) {
        if (p1.getRemainingCpuTime() < p2.getRemainingCpuTime())
        {
            return -1;
        }
        if (p1.getRemainingCpuTime() > p2.getRemainingCpuTime())
        {
            return 1;
        }
        return 0;
    }
}
