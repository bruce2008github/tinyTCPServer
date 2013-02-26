package tinyTCPServer.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TcpChannel {

	private SelectionKey selectorKey_ = null;

	private static int defaultOneceBfSize_ = 1024;

	private static int defaultDataBFSize_ = defaultOneceBfSize_ * 10;

	private DataBuffer inputBF_ = null;

	private DataBuffer outputBF_ = null;

	public TcpChannel(SelectionKey k) {

		this(k, defaultDataBFSize_);
	}

	public TcpChannel(TcpChannel ch) {

		this.selectorKey_ = ch.selectorKey_;

		this.inputBF_ = ch.getInputDataBuffer();

		this.outputBF_ = ch.getOutputDataBuffer();

		this.selectorKey_.attach(this);
	}

	public TcpChannel(SelectionKey k, int buffSize) {

		this.selectorKey_ = k;

		this.selectorKey_.attach(this);

		this.inputBF_ = new DataBuffer(buffSize);

		this.outputBF_ = new DataBuffer(buffSize);
	}

	public void enableReadabley() {

		int ops = this.selectorKey_.interestOps();

		this.selectorKey_.interestOps(SelectionKey.OP_READ | ops);

		this.selectorKey_.selector().wakeup();
	}

	public void disabelReadable() {

		int ops = this.selectorKey_.interestOps();

		this.selectorKey_.interestOps(ops & ~SelectionKey.OP_READ);

		this.selectorKey_.selector().wakeup();

	}

	public void enableWritable() {

		int ops = this.selectorKey_.interestOps();

		this.selectorKey_.interestOps(SelectionKey.OP_WRITE | ops);

		this.selectorKey_.selector().wakeup();
	}

	public void disableWriteable() {

		int ops = this.selectorKey_.interestOps();

		this.selectorKey_.interestOps(ops & ~SelectionKey.OP_WRITE);

		this.selectorKey_.selector().wakeup();

	}

	public void appendOutputBuffer(byte[] bytes) {

		this.outputBF_.append(bytes);
	}

	public void appendInputBuffer(byte[] bytes) {

		this.inputBF_.append(bytes);

	}

	public DataBuffer getInputDataBuffer() {

		return this.inputBF_;
	}

	public DataBuffer getOutputDataBuffer() {

		return this.outputBF_;
	}

	public void processEvent(MessageProcessor msgProcessor) {

		try {

			int readyOps = this.selectorKey_.readyOps();

			if ((readyOps & SelectionKey.OP_READ) != 0) {

				if (!handleRead(msgProcessor)) {
					// Connection already closed, no need to handle write.
					return;
				}
			}

			if ((readyOps & SelectionKey.OP_WRITE) != 0) {

				handleWrite();
			}

		} catch (CancelledKeyException e) {

			e.printStackTrace();

		}
	}

	private boolean handleRead(MessageProcessor msgProcessor) {

		final SocketChannel ch = (SocketChannel) this.selectorKey_.channel();
		int ret = 0;
		int readBytes = 0;

		ByteBuffer bb = ByteBuffer.allocate(defaultOneceBfSize_);

		try {
			while ((ret = ch.read(bb)) > 0) {
				readBytes += ret;
				if (!bb.hasRemaining()) {
					break;
				}
			}

		} catch (ClosedChannelException e) {
			// Can happen, and does not need a user attention.
		} catch (Throwable t) {

		}

		if (ret < 0) {

			selectorKey_.cancel();

			try {
				ch.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		if (readBytes > 0) {

			bb.flip();

			byte[] gotData = new byte[readBytes];

			bb.get(gotData);

			this.inputBF_.append(gotData);

			msgProcessor.processMessage(this);

		}

		return true;

	}

	private boolean handleWrite() {

		final SocketChannel ch = (SocketChannel) this.selectorKey_.channel();

		int writeBytes = 0;

		ByteBuffer bf = ByteBuffer.wrap(this.outputBF_.retrieveAllData());

		int dataSize = bf.array().length;

		try {

			if ((writeBytes = ch.write(bf)) >= 0) {
				// if we have write all the data,
				// disable write event
				if (dataSize == writeBytes) {
					this.disableWriteable();
				} else {// there is some data still NOT be sent,need to save
						// them,
						// then send them in the later
					byte[] remain = new byte[bf.remaining()];
					bf.get(remain);
					this.outputBF_.append(remain);
				}
			}

			return true;

		} catch (IOException e1) {

			selectorKey_.cancel();

			try {

				ch.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}

}
