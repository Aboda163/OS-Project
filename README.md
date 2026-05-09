Team member names : 
1.عبدالله محمد عصام عبدالرحمن مصطفى 20240581
2.احمد ايمن سمحان عبدالمحسن 20240024
3.احمد عامر عامر محمد 20240051
4.محمد ضريف صلاح الدين حسن 20230476 
5.عادل ايهاب عادل  20240503 
6.عبدالله يحيى رحيم على 20240590
Project description:
Priority vs SRTF Comparison Project
Project at a glance: Algorithms compared: Priority Scheduling and Shortest Remaining Time First (SRTF). Main focus: policy-driven service versus shortest-remaining-time efficiency. Special input: Priority value for each process.
Project Objective
In this project, your team will implement and compare Priority Scheduling and Shortest Remaining Time First (SRTF).
The purpose is to study the difference between a scheduler that selects processes according to priority values and a scheduler that selects according to the shortest remaining burst time.
Your comparison should reveal how scheduling policy affects fairness, preemption, starvation risk, and the final performance metrics.
Required Functionality
•
Accept a dynamic number of processes and all required process data at runtime.
•
Validate all input before simulation begins.
•
Simulate Priority Scheduling correctly, with a clearly documented priority rule and tie-breaking rule.
•
Simulate SRTF correctly, including immediate preemption when a shorter remaining job arrives.
•
Display separate Gantt charts and separate per-process metrics tables for both algorithms.
•
Calculate WT, TAT, RT, average WT, average TAT, and average RT.
Required Comparison Focus
•
How Priority Scheduling behaves when a short job has low priority.
•
How SRTF behaves when a longer but high-priority process competes with shorter jobs.
•
Fairness versus efficiency.
•
Starvation risk and policy-driven service.
Required Interface Sections
•
Input Panel
•
Process Table
•
Gantt Chart for Priority
•
Gantt Chart for SRTF
•
Results Table for Priority
•
Results Table for SRTF
•
Comparison Summary Section
•
Final Conclusion Area
Required Test Scenarios
Scenario A: Basic mixed workload
•
A normal workload with multiple processes, different arrival times, and different burst times.
Scenario B: Conflict between priority and burst time
•
Include a high-priority long process and a low-priority short process so the two algorithms behave differently.
Scenario C: Starvation-sensitive case
•
Prepare a workload where one process may wait much longer depending on the selected policy.
Operating Systems Course | Scheduling Comparison Projects
Scenario D: Validation case
•
Include at least one invalid input example and show how the simulator handles it safely.
Required Analysis Questions
•
Which algorithm produced the lower average waiting time?
•
Which algorithm produced the lower average response time?
•
Did priority values improve treatment of urgent processes?
•
Did SRTF favor short jobs more aggressively?
•
Which algorithm would you recommend for the tested workload, and why?
Required Conclusion
•
State which algorithm performed better on the selected datasets.
•
State which metrics were better under each algorithm.
•
Identify the main trade-off observed.
•
State which algorithm appeared fairer in practice.



Java 25 using swing in the project
