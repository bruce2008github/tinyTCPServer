package tinyTCPServer.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import tinyTCPServer.net.Poller;

;

public class EventLoop implements Task {

	private Poller poller_;

	private MessageProcessor msgProcessor_ = new EchoMessageProcessor();

	private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();

	public EventLoop() throws IOException {

		this.poller_ = new Poller();
	}

	public void setMessageProcessor(MessageProcessor msgProcessor) {

		this.msgProcessor_ = msgProcessor;
	}

	public void execute() {

		for (;;) {

			try {

				this.poller_.poll();

				this.poller_.processActiveChannels(this.msgProcessor_);

				processPendingEventLoopTaskQueue();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				return;
			}

		}

	}

	public void processPendingEventLoopTaskQueue() {

		for (;;) {

			final Task task = taskQueue.poll();

			if (task == null) {
				break;
			}

			task.execute();
		}

	}

	public void injectNewTask(Task task) {

		// thread safe
		taskQueue.offer(task);

		this.poller_.wakeUp();

	}

	public void assignMonitorChannel(final SocketChannel acceptedSocketChannel) {

		Task task = new Task() {

			public void execute() {
				poller_.registerChannel(acceptedSocketChannel);
			}
		};

		this.injectNewTask(task);

	}

}
