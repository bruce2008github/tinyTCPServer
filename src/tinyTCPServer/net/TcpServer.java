package tinyTCPServer.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TcpServer {

	private Acceptor acceptor_;

	private int port_;

	private int threadNum_;

	private ThreadWorkerPool pool_;

	public TcpServer(int port, int threadNum, Class classObj)
			throws IOException, InstantiationException, IllegalAccessException {

		this.port_ = port;

		this.threadNum_ = threadNum;

		this.pool_ = new ThreadWorkerPool(Executors.newCachedThreadPool(),
				threadNum, EventLoop.class, classObj);

		this.acceptor_ = new Acceptor(new InetSocketAddress(this.port_),
				this.pool_);

	}

	public void start() throws IOException {

		this.pool_.start();

		this.acceptor_.start();

	}

}
