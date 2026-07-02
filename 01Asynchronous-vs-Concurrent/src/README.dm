asynchronous:
 - not multi-thread (it can be combined with multi-thread)
 - it is about executing tasks (not forcing the thread to wait for a task to finish)
 - asynchronous tasks may not be concurrent

concurrency:
 - it's about pieces of code executed by more than one thread -> doing more at the same time
 - it is a form of asynchronous