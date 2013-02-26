package tinyTCPServer.net;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadWorkerPool {

	private final ThreadWorker[] workers_;

	private final Executor workerExecutor_;

	private final AtomicInteger workerIndex_ = new AtomicInteger();

	public ThreadWorkerPool(Executor workerExecutor, int workerCount,
			Class taskType) throws InstantiationException,
			IllegalAccessException {

		this.workers_ = new ThreadWorker[workerCount];

		for (int i = 0; i < workers_.length; i++) {

			workers_[i] = createWorker(workerExecutor, "worker_" + i,
					(Task) taskType.newInstance());
		}

		this.workerExecutor_ = workerExecutor;
	}

	public ThreadWorkerPool(Executor workerExecutor, int workerCount,
			Class taskType, Class messageProcessorType)
			throws InstantiationException, IllegalAccessException {
		this.workers_ = new ThreadWorker[workerCount];

		for (int i = 0; i < workers_.length; i++) {

			MessageProcessor msgProcessor = (MessageProcessor) messageProcessorType
					.newInstance();

			EventLoop eventLoop = (EventLoop) taskType.newInstance();

			eventLoop.setMessageProcessor(msgProcessor);

			workers_[i] = createWorker(workerExecutor, "worker_" + i, eventLoop);
		}

		this.workerExecutor_ = workerExecutor;
	}

	private ThreadWorker createWorker(Executor executor, String name, Task task) {

		if (task instanceof EventLoop) {
			return new EventLoopThreadWorker(executor, name, (EventLoop) task);
		}

		return new ThreadWorker(executor, name, task);
	}

	public ThreadWorker nextWorker() {

		return workers_[Math.abs(workerIndex_.getAndIncrement()
				% workers_.length)];
	}

	public void start() {

		for (int i = 0; i < workers_.length; i++) {
			workers_[i].startThread();
		}
	}

}
