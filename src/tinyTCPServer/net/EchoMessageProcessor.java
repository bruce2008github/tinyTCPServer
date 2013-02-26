package tinyTCPServer.net;

public class EchoMessageProcessor implements MessageProcessor {

	public void processMessage(TcpChannel ch) {
		// If you want to process the formated message
		// we need to split the stream data into the frames
		// every frame will be processed(according to the business logical)
		// then the processed result(frame) will be encoded
		// and put them into the outputDatBF,then send out
		// currently, just process as the echo server
		DataBuffer inputDataBF = ch.getInputDataBuffer();

		DataBuffer outputDataBF = ch.getOutputDataBuffer();

		byte[] data = inputDataBF.retrieveAllData();

		outputDataBF.append(data);

		ch.enableWritable();

	}

}
