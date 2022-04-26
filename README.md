# Project to run YCSB Benchmark for Clen

## Compile
From the project directory run the following commands:

    mvn package
    cp target/ClenYCSBTestBench-1.0-SNAPSHOT-jar-with-dependencies.jar .
    
To run the YCSB workload A for Clen (Update heavy workload), run the following command:

    java -cp ./ClenYCSBTestBench-1.0-SNAPSHOT-jar-with-dependencies.jar:. site.ycsb.Client -db edgelab.lc.workbench.ClenYCSBClient -P ./workloads/workload_a -threads 1 > out.csv
    
To run the YCSB workload A for Clen (Update heavy workload), run the following command:

    java -cp ./ClenYCSBTestBench-1.0-SNAPSHOT-jar-with-dependencies.jar:. site.ycsb.Client -db edgelab.paxos.workbench.PaxosYCSBClient -P ./workloads/workload_a -threads 1 > out.csv