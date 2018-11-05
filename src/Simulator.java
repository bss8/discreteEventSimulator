import java.util.Random;

import static java.lang.Math.log;


/***
* @Author: Borislav Sabotinov
* This class drives the simulation of a single instance of a scheduler as specified by the user through
* command line arguments.
 ***/

public class Simulator {


    public static void main(String[] args) {

        if (args.length == 0 || args[0].toLowerCase().equals("help")) {
            printProgramInstructions();
        } else {
            // initialize system state variables
            final int algorithmType = Integer.parseInt(args[0]);
            final int lambda = Integer.parseInt(args[1]);  // average rate of arrival
            final float avgServiceTime = Float.parseFloat(args[2]);
            final float quantumForRR = Float.parseFloat(args[3]);

            // initialize simulation clock to 0
            Clock simulationClock = new Clock();
            simulationClock.setSimulationTime(0f);
//System.out.println("System time at start is: " + simulationClock.getSimulationTime());
            // schedule an initial event - create Event and add to EventQueue
            //Event initialEvent = new Event(EventType.ProcessArrival, genexp(lambda));
            EventQueue eventQueue = new EventQueue();
            //eventQueue.insertEvent(initialEvent);

            // create the scheduling algorithm and the CPU to handle processes
            SchedulingAlgorithm schedulingAlgorithm = createSchedulingAlgorithm(algorithmType);
            CPU simulationCPU = new CPU();

            int numProcessesHandled = 0;


            // generate 10k processes up front
            for (int i = 0; i < 10000; i++) {
                // Create new ProcessArrival Event and add to queue
                Event nextArrivalEvent = new Event(EventType.ProcessArrival, genexp(lambda));
                eventQueue.insertEvent(nextArrivalEvent);
            } // end for
//System.out.println("Event queue: " + eventQueue.toString());
            // while we have not processed N Processes to completion,
            // keep going and handle events in the EventQueue as needed
            while (numProcessesHandled < 10000) {
                // 1) Set Clock to EventTime
                simulationClock.setSimulationTime(eventQueue.getSystemTimeFromHead());

//System.out.println(">>> System time is now at: " + eventQueue.getSystemTimeFromHead());

                // 3) Do/process next event and remove from EventQueue
                Event eventToProcess = eventQueue.returnAndRemoveHeadEvent();
                EventType eventToProcessType = eventToProcess.getEventType();

                /* If event is:
                * 1) an arrival: create a process and add it to the scheduler's queue
                * 2) a completion: update the intermediate numbers needed for statistics
                * have scheduler start executing next process in ReadyQueue if available
                * and schedule completion event in the future if FCFS or RR because we know
                * the completion times. FCFS is start time + burst time. RR is start time + quantum.
                */
                if (eventToProcessType == EventType.ProcessArrival) {
//System.out.println("---WE HAVE AN ARRIVAL---");
                    // create the "arriving" process
                    Process p = new Process();
                    p.setArrivalTime(eventToProcess.getEventTime());  // processArrivalTime = eventTime
//System.out.println("Process arrived at: " + p.getArrivalTime());
                    p.setBurstTime(genexp(1/avgServiceTime));
                    p.setRemainingCpuTime(p.getBurstTime());
//System.out.println("Process burst time is: " + p.getBurstTime());

                    // add new process to scheduler's ready queue
                    schedulingAlgorithm.addProcessToReadyQueue(p);


                    if (algorithmType == SchedulerType.FCFS.getSchedulerType()) {
                        // Give CPU a process from the ready queue and set busy = true
                        // since this is FCFS, an arriving process' completion time is known in "advance"
                        // so schedule a completion event

                        if(simulationCPU.isBusy()) {
//System.out.println("CPU is busy and arriving process must wait in queue. Completion time cannot be determined now.");
                            continue;
                        } else {
//System.out.println("CPU is IDLE and arriving process can start. Completion time can be determined now!");
                            //remove process from ready queue, give it to CPU
                            simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                            simulationCPU.setBusy(true);
                            p.setStartTime(simulationClock.getSimulationTime());  // start process time
                            p.setWaitingTime(p.getStartTime() - p.getArrivalTime());

                            Event knownCompletion = new Event(EventType.ProcessCompletion,
                                    p.getStartTime() + p.getBurstTime());
                            eventQueue.insertEvent(knownCompletion);
//System.out.println("Event queue: " + eventQueue.toString());
//System.out.println("Process completion will be: " + knownCompletion.getEventTime() + "; " +
//                                        "current time is: " + simulationClock.getSimulationTime());
                        }
//System.out.println("Process start time: " + p.getStartTime());
//System.out.println("Process waited for: " + p.getWaitingTime());

                    } // end FCFS arrival handling
                    else if (algorithmType == SchedulerType.SRTF.getSchedulerType()) {

                    }
                    else if (algorithmType == SchedulerType.RR.getSchedulerType()) {

                    }
                    //TODO: if SRTF, check process running on CPU against Ready Queue
                    //TODO: if queue has process w/ remainingCpuTime less than that of the running process, preempt
                } // end if to handle Process Arrivals
                else if (eventToProcessType == EventType.ProcessCompletion) {
//System.out.println("---WE HAVE A COMPLETION---");

                    /* When an event completes, set its remainingCpuTime to zero
                     * increment numProcessesHandled counter.
                     * Also the CPU is free to work on another process, so we must give it one
                     */
                    numProcessesHandled++;

                    if(algorithmType == SchedulerType.FCFS.getSchedulerType()) {

                        simulationCPU.getMyProcess().setRemainingCpuTime(0f); // process is done
                        simulationCPU.getMyProcess().setCompletionTime(simulationClock.getSimulationTime());
                        simulationCPU.getMyProcess().setTurnaroundTime(simulationCPU.getMyProcess().getCompletionTime()
                                - simulationCPU.getMyProcess().getArrivalTime());

                        // now that a process is complete, update runningSums that we will use to calculate statistics
                        SchedulingAlgorithm.runningBurstTimeSum += simulationCPU.getMyProcess().getBurstTime();
                        SchedulingAlgorithm.runningTurnaroundSum += simulationCPU.getMyProcess().getTurnaroundTime();
                        SchedulingAlgorithm.runningWaitTimeSum += simulationCPU.getMyProcess().getWaitingTime();

//System.out.println("Process completed at: " + simulationCPU.getMyProcess().getCompletionTime());

                        simulationCPU.setBusy(false);
                        if (!schedulingAlgorithm.myQueue.isEmpty()) {
                            simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                            simulationCPU.getMyProcess().setStartTime(simulationClock.getSimulationTime());
                            simulationCPU.getMyProcess().setWaitingTime(simulationCPU.getMyProcess().getStartTime() - simulationCPU.getMyProcess().getArrivalTime());

                            Event knownCompletion = new Event(EventType.ProcessCompletion,
                                    simulationCPU.getMyProcess().getStartTime() + simulationCPU.getMyProcess().getBurstTime());
                            eventQueue.insertEvent(knownCompletion);
//System.out.println("New process can start now at " + simulationCPU.getMyProcess().getStartTime());
//System.out.println("Waiting time was: " + simulationCPU.getMyProcess().getWaitingTime());
                        } else {
                            continue;
                        }




                    }
                    else if (algorithmType == SchedulerType.SRTF.getSchedulerType()) {

                    }
                    else if (algorithmType == SchedulerType.RR.getSchedulerType()) {

                    }
                } // end else-if to handle Process Completions
                else if (eventToProcessType == EventType.TimeSliceOccurrence) {
                    // TODO: logic to handle Round Robin Time slice occurrence event
                }

            } // end while

            //TODO: 4) Generate and display statistical report
            System.out.println("Total sim time: " + simulationClock.getSimulationTime());
            calculateStatistics(schedulingAlgorithm, simulationClock.getSimulationTime(), lambda);





        } // end if-else args.length validation

    } // end main

    private static SchedulingAlgorithm createSchedulingAlgorithm(int algorithmType) {
        SchedulingAlgorithm schedulingAlgorithm;//validate that algorithmType is in range (1,3)
        if (algorithmType > 0 && algorithmType < 4) {

            // create scheduler based on user defined type
            // the scheduler will internally set its type and create its specific Process Ready Queue
            if (algorithmType == SchedulerType.FCFS.getSchedulerType()) {  // FCFS
                schedulingAlgorithm = new FCFS();
            }
            else if (algorithmType == SchedulerType.SRTF.getSchedulerType()) {  // SRTF
                schedulingAlgorithm = new SRTF();
            }
            else {  // RR
                schedulingAlgorithm = new RR();
            }
        } else {
            System.out.print("Please enter a valid value for the algorithm type, in range (1,3) inclusive.");
            return null;
        }

        return  schedulingAlgorithm;
    }

    /***
     * This method prints usage instructions to the command line if the user does not specify any
     * command line arguments or types the word 'help'
     */
    private static void printProgramInstructions() {
        System.out.println("Discrete event simulator for 3 scheduling algorithms. ");
        System.out.println("Author: Borislav Sabotinov");
        System.out.println("java -jar DiscreteEventSimulator.jar <scheduler_type> <lambda> <avg. svc time> <quantum>");
        System.out.println("[scheduler_type] : value can be in the range (1,3) inclusive.");
        System.out.println("\t1 - First Come First Served (FCFS) Scheduler");
        System.out.println("\t2 - Shortest Remaining Time First (SRTF) Scheduler");
        System.out.println("\t3 - Round Robin (RR) Scheduler - requires 4th argument defining a quantum value.");
        System.out.println("[lambda] : average rate lambda that follows a Poisson process, to ensure exponential inter-arrival times.");
        System.out.println("[avg. svc time] : the service time is chosen according to an exponential distribution with an average service time of this third argument");
        System.out.println("[quantum] : optional argument only required for Round Robin (scheduler_type = 3). Defines the length of the quantum time slice.");
    } // end printProgramInstructions

    private static float urand() {
        Random rand = new Random();
        float value = rand.nextFloat();
        //value = (float) (Math.round(value * 100.0f)/100.0f);
        return value;
    } // end urand

    private static float genexp(float lambda) {
        float u, x;
        x = 0f;
        while (x == 0) {
            u = urand();
            x = (float) ((-1f/lambda) * log(u));
        }
        return x;
    } // end genexp

    private static void calculateStatistics(SchedulingAlgorithm s, float totalSimTime, int lambda) {
        System.out.println("Avg turnaround time: " + s.avgTurnaroundTime(totalSimTime));
        System.out.println("Total throughput: " + s.throughput(totalSimTime));
        System.out.println("CPU utilization: " + s.cpuUtilization(totalSimTime));
        System.out.println("Avg # of processes in ready queue: " + s.avgProcessesInReadyQueue(lambda));
    }

} // end class
