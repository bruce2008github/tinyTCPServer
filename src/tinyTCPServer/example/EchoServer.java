package tinyTCPServer.example;

import java.io.IOException;

import tinyTCPServer.net.EchoMessageProcessor;
import tinyTCPServer.net.TcpServer;

public class EchoServer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		
		int port = 6678;
		
		int threadNum = Runtime.getRuntime().availableProcessors()+1;
		
		TcpServer tcpServer = new TcpServer(port,threadNum, EchoMessageProcessor.class);
		
		tcpServer.start();
	}

}
