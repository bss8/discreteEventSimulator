/***
 * @Author: Borislav Sabotinov
 * Provides a contract for the type of metrics all schedulers will need to calculate. Requires continuously updating
 * certain intermediate variables (using values obtained as each Process is dealt with) throughout the simulation.
 */
public interface iPerformanceMetrics {

    float avgTurnaroundTime(float totalSimTime);

    float throughput(float totalSimTime);

    float cpuUtilization(float totalSimTime);

    float avgProcessesInReadyQueue(int lambda);
} // end interface
