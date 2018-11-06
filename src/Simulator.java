import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.log;


/***
* @Author: Borislav Sabotinov
* This class drives the simulation of a single instance of a scheduler as specified by the user through
* command line arguments.
 ***/
public class Simulator {

    public static void main(String[] args) throws IOException {

        if (args.length == 0 || args[0].toLowerCase().equals("help")) {
            printProgramInstructions();
        } else {
            // initialize system state variables
            final int algorithmType = Integer.parseInt(args[0]);
            final int lambda = Integer.parseInt(args[1]);  // average rate of arrival
            final double avgServiceTime = Double.parseDouble(args[2]);
            final double quantumForRR = Double.parseDouble(args[3]);

            // initialize simulation clock to 0
            Clock simulationClock = new Clock();
            simulationClock.setSimulationTime(0f);

            EventQueue eventQueue = new EventQueue();

            Event initialEvent = new Event(EventType.ProcessArrival, 0);
            eventQueue.insertEvent(initialEvent);

            // create the scheduling algorithm and the CPU to handle processes
            SchedulingAlgorithm schedulingAlgorithm = createSchedulingAlgorithm(algorithmType);
            CPU simulationCPU = new CPU();

            int numProcessesHandled = 0;

            /*
             * I experimented with generating all 10k processes up-front but this caused issues in the distribution
             * and calculated statistics values. Generating new arrivals as we go is a preferred approach.
             */

//            // generate 10k processes up front
//            for (int i = 0; i < 10000; i++) {
//                // Create new ProcessArrival Event and add to queue
//                Event nextArrivalEvent = new Event(EventType.ProcessArrival, genexp(lambda));
//                eventQueue.insertEvent(nextArrivalEvent);
//            } // end for

            // while we have not processed N Processes to completion,
            // keep going and handle events in the EventQueue as needed
            while (numProcessesHandled < 10000) {
                // 1) Set Clock to EventTime
                simulationClock.setSimulationTime(eventQueue.getSystemTimeFromHead());

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
                    // routine to unconditionally create new arrival event
                    unconditionallyCreateNewArrival(lambda, simulationClock, eventQueue);
                    // create the "arriving" process
                    Process p = new Process();
                    p.setArrivalTime(simulationClock.getSimulationTime());  // processArrivalTime = eventTime
                    p.setBurstTime(genexp(1/avgServiceTime));
                    p.setRemainingCpuTime(p.getBurstTime());

                    // add new process to scheduler's ready queue unconditionally
                    // only always use a process from the queue, not p directly
                    schedulingAlgorithm.addProcessToReadyQueue(p);

                    if (algorithmType == SchedulerType.FCFS.getSchedulerType()) {
                        // Give CPU a process from the ready queue and set busy = true
                        // since this is FCFS, an arriving process' completion time is known in "advance"
                        // so schedule a completion event

                        if(simulationCPU.isBusy()) {
                            continue;
                        } else {
                            //remove process from ready queue, give it to CPU
                            fcfsGiveProcessToCpuAndScheduleCompletion(simulationClock, eventQueue, schedulingAlgorithm, simulationCPU);
                        } // end cpu idle fcfs
                    } // end FCFS arrival handling
                    else if (algorithmType == SchedulerType.SRTF.getSchedulerType()) {
                        // CPU not busy, give it a process from queue, no preemption possible in this case but may have completion
                        if (!simulationCPU.isBusy()) {
                            simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                            simulationCPU.setBusy(true);

                            checkIfReturningAndSetTimes(simulationClock, simulationCPU);

                            if (eventQueue.safelyPeekAtNextEvent().getEventType() == EventType.ProcessArrival) {
                                if ((simulationClock.getSimulationTime() + simulationCPU.getMyProcess().getRemainingCpuTime())
                                        <= eventQueue.safelyPeekAtNextEvent().getEventTime()) {
                                    Event knownCompletion = new Event(EventType.ProcessCompletion,
                                            simulationCPU.getMyProcess().getRestartTime() + simulationCPU.getMyProcess().getRemainingCpuTime());
                                    eventQueue.insertEvent(knownCompletion);
                                }
                            }
                        } // end cpu idle
                        //else CPU is busy and we may have to preempt if conditions are met
                        else {
                            //System.out.println("arrival, cpu busy");
                            //process ready queue sorted by remTime, NOT arrival, so we are not guaranteed sequential processes
                            //so, check system time for current time instead
                            double elapsedTime = simulationClock.getSimulationTime() - simulationCPU.getMyProcess().getRestartTime();
                            double oldRemTime = simulationCPU.getMyProcess().getRemainingCpuTime();
                            double newRemTime = oldRemTime - elapsedTime;
                            //if head process remainingTime >= runningProcess remTime, do nothing, let it run
                            //checkIfReturningAndSetTimes(simulationClock, simulationCPU);


                            if (newRemTime <= 0) {
                                Event knownCompletion = new Event(EventType.ProcessCompletion,
                                        simulationClock.getSimulationTime() + oldRemTime);
                                eventQueue.insertEvent(knownCompletion);
                            }
                            else if (schedulingAlgorithm.safelyPeekAtNextProcess().getRemainingCpuTime() >= newRemTime) {
                                simulationCPU.getMyProcess().setRemainingCpuTime(newRemTime);
                            }
                            // else head process has a shorter remTime and we need to PREEMPT
                            // no speical event type because preemption happens at the current system time
                            else {
                                simulationCPU.getMyProcess().setRemainingCpuTime(newRemTime);
                                Process tempProcess = simulationCPU.getMyProcess();
                                simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                                checkIfReturningAndSetTimes(simulationClock, simulationCPU);
                                schedulingAlgorithm.addProcessToReadyQueue(tempProcess);
                            }
                        } // end cpu busy
                    } // end srtf arrival handling
                    else if (algorithmType == SchedulerType.RR.getSchedulerType()) {
                        if (simulationCPU.isBusy()) {

                        } else {
                            simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                            simulationCPU.setBusy(true);
                            // completion if remTime - quantum <=0
                            if (p.getRemainingCpuTime() - quantumForRR <= 0) {
                                Event completionEvent = new Event(EventType.ProcessCompletion,
                                        simulationClock.getSimulationTime() + p.getRemainingCpuTime());
                                eventQueue.insertEvent(completionEvent);
                            }
                            // time slice interrupt if remTime - quantum > 0
                            else if (p.getRemainingCpuTime() - quantumForRR > 0) {
                                Event interrupt = new Event(EventType.TimeSliceOccurrence,
                                        simulationClock.getSimulationTime() + quantumForRR);
                                eventQueue.insertEvent(interrupt);
                            }
                        } // end else CPU is idle
                    } // end rr arrival handling
                } // end if to handle Process Arrivals
                else if (eventToProcessType == EventType.ProcessCompletion) {
                    /* When an event completes, set its remainingCpuTime to zero
                     * increment numProcessesHandled counter.
                     * Also the CPU is free to work on another process, so we must give it one
                     */
                    numProcessesHandled++;
                    //System.out.println(numProcessesHandled);
                    if (numProcessesHandled == 10000) {
                        System.out.println("10000th process completing now");
                    }

                    if(algorithmType == SchedulerType.FCFS.getSchedulerType()) {

                        simulationCPU.getMyProcess().setRemainingCpuTime(0); // process is done
                        simulationCPU.getMyProcess().setCompletionTime(simulationClock.getSimulationTime());
                        simulationCPU.getMyProcess().setTurnaroundTime(simulationCPU.getMyProcess().getCompletionTime()
                                - simulationCPU.getMyProcess().getArrivalTime());

                        // now that a process is complete, update runningSums that we will use to calculate statistics
                        SchedulingAlgorithm.runningBurstTimeSum += simulationCPU.getMyProcess().getBurstTime();
                        SchedulingAlgorithm.runningTurnaroundSum += simulationCPU.getMyProcess().getTurnaroundTime();
                        SchedulingAlgorithm.runningWaitTimeSum += simulationCPU.getMyProcess().getWaitingTime();

                        simulationCPU.setBusy(false);

                        if (!schedulingAlgorithm.myQueue.isEmpty()) {
                            fcfsGiveProcessToCpuAndScheduleCompletion(simulationClock, eventQueue, schedulingAlgorithm, simulationCPU);
                        } else {
                            continue;
                        }
                    } // end fcfs completion
                    else if (algorithmType == SchedulerType.SRTF.getSchedulerType()) {
                        simulationCPU.getMyProcess().setRemainingCpuTime(0); // process is done
                        simulationCPU.getMyProcess().setCompletionTime(simulationClock.getSimulationTime());
                        simulationCPU.getMyProcess().setTurnaroundTime(simulationCPU.getMyProcess().getCompletionTime()
                                - simulationCPU.getMyProcess().getArrivalTime());
                        double completionMinusStart = simulationCPU.getMyProcess().getCompletionTime() - simulationCPU.getMyProcess().getStartTime();
                        simulationCPU.getMyProcess().setWaitingTime(
                                (simulationCPU.getMyProcess().getStartTime() - simulationCPU.getMyProcess().getArrivalTime()) +
                                (completionMinusStart - simulationCPU.getMyProcess().getBurstTime()));

                        // now that a process is complete, update runningSums that we will use to calculate statistics
                        SchedulingAlgorithm.runningBurstTimeSum += simulationCPU.getMyProcess().getBurstTime();
                        SchedulingAlgorithm.runningTurnaroundSum += simulationCPU.getMyProcess().getTurnaroundTime();
                        SchedulingAlgorithm.runningWaitTimeSum += simulationCPU.getMyProcess().getWaitingTime();

                        simulationCPU.setBusy(false);
                        if (!schedulingAlgorithm.myQueue.isEmpty()) {
                            simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                            simulationCPU.setBusy(true);
                            checkIfReturningAndSetTimes(simulationClock, simulationCPU);

                            //determine completion or preemption
                            Event nextEvent = eventQueue.safelyPeekAtNextEvent();
                            if (nextEvent.getEventType() == EventType.ProcessArrival) {
                                double nextArrival = nextEvent.getEventTime();
                                double elapsedTime = nextArrival - simulationCPU.getMyProcess().getRestartTime();
                                double oldRemTime = simulationCPU.getMyProcess().getRemainingCpuTime();
                                double newRemTime = oldRemTime - elapsedTime;

                                if (newRemTime <= 0) {
                                    Event knownCompletion = new Event(EventType.ProcessCompletion,
                                            simulationCPU.getMyProcess().getRestartTime() + oldRemTime);
                                    eventQueue.insertEvent(knownCompletion);
                                } else {
                                    // we need to preempt when the new process arrives, not right now
                                }

                            }
                        } else {
                            continue;
                        }
                        // set start time for a new, non-returning process
                    } // end srtf completion
                    else if (algorithmType == SchedulerType.RR.getSchedulerType()) {
                        simulationCPU.getMyProcess().setRemainingCpuTime(0); // process is done
                        simulationCPU.getMyProcess().setCompletionTime(simulationClock.getSimulationTime());
                        simulationCPU.getMyProcess().setTurnaroundTime(simulationCPU.getMyProcess().getCompletionTime()
                                - simulationCPU.getMyProcess().getArrivalTime());
                        double completionMinusStart = simulationCPU.getMyProcess().getCompletionTime() - simulationCPU.getMyProcess().getStartTime();
                        //simulationCPU.getMyProcess().setWaitingTime(simulationCPU.getMyProcess().getTurnaroundTime()
                        //        - simulationCPU.getMyProcess().getBurstTime());
                        simulationCPU.getMyProcess().setWaitingTime(
                                (simulationCPU.getMyProcess().getStartTime() - simulationCPU.getMyProcess().getArrivalTime())
                                + (completionMinusStart - simulationCPU.getMyProcess().getBurstTime()));

                        // now that a process is complete, update runningSums that we will use to calculate statistics
                        SchedulingAlgorithm.runningBurstTimeSum += simulationCPU.getMyProcess().getBurstTime();
                        SchedulingAlgorithm.runningTurnaroundSum += simulationCPU.getMyProcess().getTurnaroundTime();
                        SchedulingAlgorithm.runningWaitTimeSum += simulationCPU.getMyProcess().getWaitingTime();

                        simulationCPU.setBusy(false);

                        if (!schedulingAlgorithm.myQueue.isEmpty()) {
                            simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
                            simulationCPU.setBusy(true);
                            // set start time for a new, non-returning process
                            if(!simulationCPU.getMyProcess().isReturning()) {
                                simulationCPU.getMyProcess().setStartTime(simulationClock.getSimulationTime());
                                simulationCPU.getMyProcess().setIsReturning(true);
                            }

                            determineCompletionOrQuantumInterrupt(quantumForRR, simulationClock, eventQueue, simulationCPU);
                        } else {
                            continue;
                        }
                    } // end RR completion
                } // end else-if to handle Process Completions
                else if (eventToProcessType == EventType.TimeSliceOccurrence) {
                    simulationCPU.getMyProcess().setRemainingCpuTime(simulationCPU.getMyProcess().getRemainingCpuTime() - quantumForRR);
                    determineCompletionOrQuantumInterrupt(quantumForRR, simulationClock, eventQueue, simulationCPU);
                } // end time slice occurrence
            } // end while

            System.out.println("Total sim time: " + simulationClock.getSimulationTime());
            calculateStatistics(schedulingAlgorithm, simulationClock.getSimulationTime(), lambda);
        } // end if-else args.length validation
    } // end main

    /**
     *
     * @param simulationClock
     * @param simulationCPU
     */
    private static void checkIfReturningAndSetTimes(Clock simulationClock, CPU simulationCPU) {
        if (!simulationCPU.getMyProcess().isReturning()) {
            simulationCPU.getMyProcess().setStartTime(simulationClock.getSimulationTime());
            simulationCPU.getMyProcess().setRestartTime(simulationCPU.getMyProcess().getStartTime());
            simulationCPU.getMyProcess().setIsReturning(true);
        } else {
            simulationCPU.getMyProcess().setRestartTime(simulationClock.getSimulationTime());
        }
    }

    /**
     *
     * @param lambda
     * @param simulationClock
     * @param eventQueue
     */
    private static void unconditionallyCreateNewArrival(int lambda, Clock simulationClock, EventQueue eventQueue) {
        // routine to unconditionally create new arrival event
        Event newArrival = new Event(EventType.ProcessArrival,
                simulationClock.getSimulationTime() + genexp(lambda));
        eventQueue.insertEvent(newArrival);
    }

    /**
     *
     * @param simulationClock
     * @param eventQueue
     * @param schedulingAlgorithm
     * @param simulationCPU
     */
    private static void fcfsGiveProcessToCpuAndScheduleCompletion(Clock simulationClock,
                                                                  EventQueue eventQueue,
                                                                  SchedulingAlgorithm schedulingAlgorithm,
                                                                  CPU simulationCPU) {
        simulationCPU.setMyProcess(schedulingAlgorithm.getNextProcessForCPU());
        simulationCPU.setBusy(true);
        simulationCPU.getMyProcess().setStartTime(simulationClock.getSimulationTime());
        // we can do turnaround - burst but for FCFS, this works too
        simulationCPU.getMyProcess().setWaitingTime(simulationCPU.getMyProcess().getStartTime()
                - simulationCPU.getMyProcess().getArrivalTime());

        Event knownCompletion = new Event(EventType.ProcessCompletion,
                simulationCPU.getMyProcess().getStartTime() + simulationCPU.getMyProcess().getBurstTime());
        eventQueue.insertEvent(knownCompletion);
    }

    /**
     *
     * @param algorithmType
     * @return
     */
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

    /**
     *
     * @return rand (0,1)
     */
    private static double urand() {
        Random rand = new Random();

        return rand.nextDouble();
    } // end urand

    /**
     *
     * @param lambda
     * @return either arrival time or service time
     */
    private static double genexp(double lambda) {
        double u, x;
        x = 0;

        while (x == 0) {
            u = urand();
            x = (-1/lambda)*log(u);
        }
        return x;
    } // end genexp

    /**
     *
     * @param quantumForRR
     * @param simulationClock
     * @param eventQueue
     * @param simulationCPU
     */
    private static void determineCompletionOrQuantumInterrupt(double quantumForRR, Clock simulationClock, EventQueue eventQueue, CPU simulationCPU) {
        if (simulationCPU.getMyProcess().getRemainingCpuTime() - quantumForRR <= 0) {
            Event knownCompletion = new Event(EventType.ProcessCompletion,
                    simulationClock.getSimulationTime() + simulationCPU.getMyProcess().getRemainingCpuTime());
            eventQueue.insertEvent(knownCompletion);
        }
        else if (simulationCPU.getMyProcess().getRemainingCpuTime() - quantumForRR > 0) {
            Event interrupt = new Event(EventType.TimeSliceOccurrence,
                    simulationClock.getSimulationTime() + quantumForRR);
            eventQueue.insertEvent(interrupt);
        }
    } // end method

    /**
     *
     * @param s - scheduling algorithm
     * @param totalSimTime
     * @param lambda
     * @throws IOException
     */
    private static void calculateStatistics(SchedulingAlgorithm s, double totalSimTime, int lambda) throws IOException {
        System.out.println("Avg turnaround time: " + s.avgTurnaroundTime(totalSimTime));
        System.out.println("Total throughput: " + s.throughput(totalSimTime));
        System.out.println("CPU utilization: " + s.cpuUtilization(totalSimTime));
        System.out.println("Avg # of processes in ready queue: " + s.avgProcessesInReadyQueue(lambda));

        //String header = "Lambda,Avg Turnaround,Throughput,CPU Util,Avg # Processes in RQ";
        FileWriter pw = new FileWriter("test.csv", true);
        BufferedReader br = new BufferedReader(new FileReader("test.csv"));
        StringBuilder sb = new StringBuilder();

        if(br.readLine() == null) {
            sb.append("Lambda");
            sb.append(',');
            sb.append("Avg Turnaround");
            sb.append(',');
            sb.append("Throughput");
            sb.append(',');
            sb.append("CPU Util");
            sb.append(',');
            sb.append("Avg # Processes in RQ");
        }

        sb.append('\n');
        sb.append(lambda);
        sb.append(',');
        sb.append(s.avgTurnaroundTime(totalSimTime));
        sb.append(',');
        sb.append(s.throughput(totalSimTime));
        sb.append(',');
        sb.append(s.cpuUtilization(totalSimTime));
        sb.append(',');
        sb.append(s.avgProcessesInReadyQueue(lambda));

        pw.write(sb.toString());
        pw.close();
    } // end calculateStatistics

} // end class
