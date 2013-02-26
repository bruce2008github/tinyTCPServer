package tinyTCPServer.net;

public class DataBuffer {

	private byte[] bf_ = null;

	private int readBeginIdx_ = 0;

	private int writeBeginIdx_ = 0;

	public DataBuffer(int size) {

		this.bf_ = new byte[size];
	}

	public int dataSize() {

		return this.writeBeginIdx_ - this.readBeginIdx_;

	}

	public byte[] retrieveAllData() {

		byte[] data = new byte[this.dataSize()];

		System.arraycopy(this.bf_, this.readBeginIdx_, data, 0, this.dataSize());

		this.reset();

		return data;
	}

	public void setWriteIdx(int idx) {

		this.writeBeginIdx_ = idx;
	}

	public void setReadIdx(int idx) {
		this.readBeginIdx_ = idx;
	}

	public void reset() {
		this.writeBeginIdx_ = 0;

		this.readBeginIdx_ = 0;
	}

	public void append(byte[] bytes) {

		int capcity = this.bf_.length - this.writeBeginIdx_;

		if (capcity >= bytes.length) {

			System.arraycopy(bytes, 0, this.bf_, this.writeBeginIdx_,
					bytes.length);

			this.writeBeginIdx_ = this.writeBeginIdx_ + bytes.length;

			return;
		} else {
			int oldContentLen = this.writeBeginIdx_ - this.readBeginIdx_;

			int totalEmptySize = this.bf_.length - oldContentLen;

			int deltaSize = totalEmptySize - bytes.length;

			if (deltaSize >= 0) {

				// have enough place to handle the new content ,
				// but need to adjust/move the buffer data then append the new
				// data to the end

				byte[] newBuff = new byte[this.bf_.length];

				System.arraycopy(this.bf_, this.readBeginIdx_, newBuff, 0,
						oldContentLen);

				System.arraycopy(bytes, 0, newBuff, oldContentLen, bytes.length);

				this.readBeginIdx_ = 0;

				this.writeBeginIdx_ = this.readBeginIdx_ + oldContentLen
						+ bytes.length;

				this.bf_ = newBuff;

			} else {

				// Policy: enlarge the buffer size to double
				int requireSize = (this.bf_.length + Math.abs(deltaSize)) * 2;

				byte[] newBuff = new byte[requireSize];

				System.arraycopy(this.bf_, 0, newBuff, 0, oldContentLen);

				System.arraycopy(bytes, 0, newBuff, 0, bytes.length);

				this.readBeginIdx_ = 0;

				this.writeBeginIdx_ = this.readBeginIdx_ + oldContentLen
						+ bytes.length;

				this.bf_ = newBuff;

			}

		}

	}

}
