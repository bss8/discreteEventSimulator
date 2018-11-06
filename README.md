# discreteEventSimulator

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
 `java -jar DiscreteEventSimulator.jar <scheduler_type> <lambda> <avg. svc time> <quantum>`   
 #####[scheduler_type] : value can be in the range (1,3) inclusive.   
         1 - First Come First Served (FCFS) Scheduler   
         2 - Shortest Remaining Time First (SRTF) Scheduler   
         3 - Round Robin (RR) Scheduler - requires 4th argument defining a quantum value.   
 [lambda] : average rate lambda that follows a Poisson process, to ensure exponential inter-arrival times.   
 [avg. svc time] : the service time is chosen according to an exponential distribution with an average service time of this third argument   
 [quantum] : optional argument only required for Round Robin (scheduler_type = 3). Defines the length of the quantum time slice.
 
 Please refer to attached documentation for further details.    