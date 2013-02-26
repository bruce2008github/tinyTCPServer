package tinyTCPServer.net;

public interface MessageProcessor {

	public void processMessage(TcpChannel ch);

}
