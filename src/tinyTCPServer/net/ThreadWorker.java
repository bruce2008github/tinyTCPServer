package tinyTCPServer.net;

import java.util.concurrent.Executor;

public class ThreadWorker implements Runnable {

	private Thread thread_;

	private Executor executor_;

	private String threadName_;

	private Task task_;

	public ThreadWorker(Executor executor, String name, Task task) {

		this.executor_ = executor;

		this.threadName_ = name;

		this.task_ = task;

	}

	public void startThread() {

		this.executor_.execute(this);
	}

	public void run() {

		this.thread_ = Thread.currentThread();

		this.thread_.setName(this.threadName_);

		this.task_.execute();

	}

}
