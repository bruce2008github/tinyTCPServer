package tinyTCPServer.net;

import tinyTCPServer.net.*;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Poller {

	private Selector selector_;

	private List<TcpChannel> activeChannels_ = null;

	public Poller() throws IOException {

		this.selector_ = Selector.open();
	}

	public void poll() throws IOException {

		int num = this.selector_.select();

		this.activeChannels_ = new ArrayList<TcpChannel>();

		if (num > 0) {

			Set<SelectionKey> selectedKeys = this.selector_.selectedKeys();

			for (Iterator<SelectionKey> i = selectedKeys.iterator(); i
					.hasNext();) {

				SelectionKey k = i.next();
				i.remove();

				TcpChannel attachement = (TcpChannel) k.attachment();

				TcpChannel channel;

				if (null != attachement) {// for old channel
					channel = new TcpChannel(attachement);
				} else {// for new channel
					channel = new TcpChannel(k);
				}

				this.activeChannels_.add(channel);

			}
		}
	}

	public void processActiveChannels(MessageProcessor msgProcessor) {

		for (TcpChannel chan : this.activeChannels_) {
			chan.processEvent(msgProcessor);
		}
	}

	public void wakeUp() {

		this.selector_.wakeup();
	}

	public void registerChannel(SocketChannel acceptedSocketChannel) {

		try {

			acceptedSocketChannel
					.register(this.selector_, SelectionKey.OP_READ);

		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
