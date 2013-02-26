package tinyTCPServer.net;

import java.util.concurrent.Executor;

public class EventLoopThreadWorker extends ThreadWorker {

	private EventLoop eventLoop_ = null;

	public EventLoopThreadWorker(Executor executor, String name,
			EventLoop eventLoop) {

		super(executor, name, eventLoop);

		this.eventLoop_ = eventLoop;
	}

	public EventLoop getEventLoop() {

		return this.eventLoop_;
	}

}
