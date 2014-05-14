Compile:
javac LazyComputer.java WorkerComputer.java ComputeUnit.java

How to run:

1. Run worker computer

1.1 Run multiple workers at a time. Note the num_workers must be <= 10
java WorkerComputer num_workers
This will start num_workers workers at port 12345, 12346, ... , 12345 + num_workers -1

1.2 Run one worker at a time. Note that the port_num must be >= 10000
java WorkerComputer port_num
This will start a worker at the given port

2 Run Lazy computer

2.1 If you use 1.1 command to run multiple workers, it is easier to use the command below to automatically connect to all workers
java LazyComputer num_doubles num_workers worker_host

2.2 If you use 1.2 command to run one worker at a time, you have to let lazy computer know where workers are one by one
java LazyComputer num_doubles num_workers


For example, run 10 workers at localhost to compute 1000 doubles
Worker terminal; java WorkerComputer 10
Lazy terminal: java LazyComputer 1000 10 localhost
