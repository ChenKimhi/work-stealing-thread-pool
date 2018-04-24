# work-stealing-thread-pool
Smartphone Factory simulation based on a pool of threads stealing tasks from each other to balance work load.

This project contains two main parts:
#### part 1: implementing a Work Stealing Threads Pool 
A multi-threaded pool of "processors" or "workers", where each processor is a thread that have its own queue of tasks. Tasks may depend on other sub-tasks. When a processor is working on a such a task, the main task has a deferred result (implemented using lambda callbacks) and is removed from the queue until all sub-tasks are completed. The processor handling such a task Spawns the sub-tasks to its own queue and continue to handle tasks on its queue. When a processor's task is empty, he tries to steal tasks from other processors' queues. 
If a processor has no tasks to handle and it couldn't find any other queue to steal from (a queue with more than 1 task), the processor (thread) goes to sleep and wait for tasks to handle. Any time a new task or a deferred task is added to the queue, all threads are notified and compete each other on stealing tasks to handle.

**Testing:**
The first part was tested with a basic usage of JUnit and with a MergeSort implementation using the Work-Stealing-Thread-Pool where, obviously, the initial unsorted array is splitted to two sub tasks (sub-arrays) submitted to the pool and so on.

#### part 2: Smartphone Factory Simulation
Using the infrastructure built in part 1, where each "processor" is now a diligent worker (ran by a thread) practicing work stealing in order to quickly assemble products ordered by their clients.
Products and their parts are identified by ID’s. Parts are assembled according to plans using special tools (the products, parts, assembling plans and tools are given and parsed from a JSON file). Each product is a Manufacturing Task (implements the abstract class "Task" from part 1) and its parts (products by themselves) are the correspondent sub-tasks.
Tools are stored in a "Warehouse" implemented as a class containing the queues of available tools from each type (there are finite amount of tools) and the Plans - a mapping between a product and its manufactoring plan (the parts it depends on and the type of tools needed for the assembly of the parts).
Acquiring a tool from the warehouse is implemented using the Deferred result class from part 1: Workers try to acquire a tool using a callback: if there's an available tool from the required type in the warehouse, the tool is acquired (e.g. 'the Deferred is resolved') immediately. otherwise, it is added to a waiting list until other Worker releases such a tool.

**The project was built** using Maven and the simulation is run by giving it a configuration (JSON) file containig the number of threads to use and the products, plans and tools - arranged in Waves. We simulate the assembly process in waves of products, implemented using CountDownLatch, so that at the beginning we add manufacturing tasks only for the products in the first wave, and the pool of workers handle this tasks concurrently, and only when we finish assembling all the current wave's products, the second wave of products is added to the pool of tasks and so on.

_This project was an assignment in the academic course "System Programming Languages" (SPL) i took as part of my Computer Science B.Sc studies at Ben Gurion University of The Negev, Israel. I worked on this project with another coleague to this course._


