# discreteEventSimulator
## 1. How To: Set up & Run
This simulator is implemented using the Java language. 
A Java installation is required to run the JAR artifact. 

A batch (Windows) or shell (Linux) script may be used to obtain simulation results. 

#### On Windows: genData.bat 
`del "test.csv" /q`         
`FOR /L %%A IN (1,1,30) DO (`    
&nbsp;&nbsp;&nbsp;`Java -jar "DiscreteEventSimulation.jar" 2 %%A 0.06 0.02`    
`)`    
`move "test.csv" %temp%`

#### On Linux: genData.bat
`#!/bin/bash`    
`rm sim.data`     
`for ((i = 1; i < 31; i++)); do`     
&nbsp;&nbsp;&nbsp;`./sim 1 $i 0.06 0.01`    
&nbsp;&nbsp;&nbsp;`cp sim.data /data/sim-1-$i-001.data`    
`done`
 
 #####To see SRTF with special non-flat curve
 Add true as a fifth parameter when running the simulator
 `#!/bin/bash`    
 `rm sim.data`     
 `for ((i = 1; i < 31; i++)); do`     
 &nbsp;&nbsp;&nbsp;`./sim 1 $i 0.06 0.01 true`    
 &nbsp;&nbsp;&nbsp;`cp sim.data /data/sim-1-$i-001.data`    
 `done`
 
 As can be observed from the example scripts, to manually run the program, at    
 a command line terminal, invoke the following:
 `java -jar "DiscreteEventSimulation.jar`
 
 Execute without parameters to see instructions and information on what parameters   
 the program accepts. Alternatively, you may also type 'Help' (capitalization does not matter)"
 `java -jar "DiscreteEventSimulation.jar `   
 or `java -jar "DiscreteEventSimulation.jar help`
 
 The following will be displayed: 
         
 Discrete event simulator for 3 scheduling algorithms.    
 Author: Borislav Sabotinov   
 `java -jar DiscreteEventSimulator.jar <scheduler_type> <lambda> <avg. svc time> <quantum> <toggleSrtfCurve>`   
 #####[scheduler_type] : value can be in the range (1,3) inclusive.   
         1 - First Come First Served (FCFS) Scheduler   
         2 - Shortest Remaining Time First (SRTF) Scheduler   
         3 - Round Robin (RR) Scheduler - requires 4th argument defining a quantum value.   
 [lambda] : average rate lambda that follows a Poisson process, to ensure exponential inter-arrival times.   
 [avg. svc time] : the service time is chosen according to an exponential distribution with an average service time of this third argument   
 [quantum] : optional argument only required for Round Robin (scheduler_type = 3). Defines the length of the quantum time slice.
 [toggleSrtfCurve] : accepts 0 or 1. Otional argument to toggle the SRTF curve from flat (0) to non-flat (1).
 Please refer to attached documentation for further details.    
 
## 2. Results 

The most striking difference is in how SRTF is treated. If we leave the model as is, certain curves are flat, indicating starvation may be taking place.    
If we include processes that remain in the ready queue, SRTF emerges as the "better" scheduler that performs well under load.

###Version 1 - Starvation and Flat SRTF for Various Stats

Leaving the model as it is, with the toggleSRTFCurve boolean = false, both average turnaround and average number    
of processes in ready queue are essentially flat (hovering b/w 0-1) when compared to other schedulers. If we consider 
processes left over in the ready queue, then SRTF gets a distribution indicating it outperforms all the others. 
I do not believe this to be true, after discussing with other students it seems SRTF may suffer from starvation. 
Short processes get serviced while others get stuck in the ready queue. I use a strict definition of complete - 
namely a process is only complete if and only if it receives CPU service equal in length to its requested 
burst time. From plotting all schedulers together, we can see that Round Robin outperforms FCFS. Breaking it 
down further, we see that Round Robin with a quantum of 0.01 slightly outperforms RR2 when under a heavy load. 
I presume this is because RR1 can quickly service processes with a short burst time and send them on their way, while
RR2 may spend more time servicing longer processes. Overall, we can confirm the book's note that a Round Robin 
scheduler with too long a quantum may degenerate into FCFS. One caveat is if the quantum is too short, there may be 
high overhead of context switch, though we do not consider it for the purposes of this simulation. 

![alt text](https://i.imgur.com/ccb4e8F.png)

When SRTF (method 1) is examined independently, we do see some slight variance in the flat lines, for a short range.

![alt text](https://i.imgur.com/IUcyI6O.png)

###Round Robin - RR1 vs RR2

Let us zoom in on Round Robin and compare the two sets of data - one with a quantum of 0.01 and the other with a 
quantum of 0.02. 

![alt text](https://i.imgur.com/pGQAgPQ.png)

Both perform similarly in this simulation, with RR of quantum 0.01 slightly outperforming RR with quantum of 0.02. 
As discussed previously, too large a quantum may be detrimental to the performance of this scheduler, while too small a 
quantum can incur a high overhead cost due to context switch. 

## 3. Conclusion
I infer that Round Robin with a quantum of 0.01 emerges as the better performing scheduler when compared to the others. 
Under higher loads (lambda > 16.667), the best schedulers are, in order of efficacy: RR1, RR2, FCFS. SRTF, depending on approach and interpretation, may
either be the better performing scheduler out of the three, or may suffer from starvation. That is to say, 
if we do not make any modifications to how we handle processes, it appears SRTF suffers from starvation and may not 
be a viable option unless we do what the text recommends and implement **aging** - where we **age** processes in the ready 
queue and assign them a higher priority the longer they wait, so that they may receive service as well at some point. 
This is out of scope for this simulation and I do not implement aging or priority schemes for SRTF to address any 
possible starvation.  