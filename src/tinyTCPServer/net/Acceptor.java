package tinyTCPServer.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor {

	private InetSocketAddress listenAddr_;

	private Selector selector_;

	private ServerSocketChannel listnChannel_;

	private ThreadWorkerPool eventLoopThreadPool_;

	private int number_ = 0;

	public Acceptor(InetSocketAddress listenAddr, ThreadWorkerPool pool)
			throws IOException {

		this.listenAddr_ = listenAddr;
		this.eventLoopThreadPool_ = pool;
		this.selector_ = Selector.open();

		this.listnChannel_ = ServerSocketChannel.open();
		this.listnChannel_.socket().bind(this.listenAddr_);
		this.listnChannel_.configureBlocking(false);
		this.listnChannel_.socket().setReuseAddress(true);
		this.listnChannel_.register(selector_, SelectionKey.OP_ACCEPT);
	}

	public void start() throws IOException {

		for (;;) {

			// this is only 1 fd(listener socket) for acceptor listener
			// the accepted fd will dispatch to the eventloop thread
			// so, the selector only used to wait work
			this.selector_.select();

			this.selector_.selectedKeys().clear();

			// accept connections in loop until no new connection is ready
			// the listnChannel_ has been configured to non-blocking
			for (;;) {
				SocketChannel acceptedSocket = this.listnChannel_.accept();
				if (acceptedSocket == null) {
					break;
				}
				dispatch(acceptedSocket);

			}

		}
	}

	private void dispatch(final SocketChannel acceptedSocket) {

		number_++;

		try {

			acceptedSocket.configureBlocking(false);

			EventLoopThreadWorker worker = (EventLoopThreadWorker) this.eventLoopThreadPool_
					.nextWorker();

			worker.getEventLoop().assignMonitorChannel(acceptedSocket);

		} catch (Exception e) {

			try {

				acceptedSocket.close();

			} catch (IOException e2) {

				e2.printStackTrace();

			}
		}
	}

}
