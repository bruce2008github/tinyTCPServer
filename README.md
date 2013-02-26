
*****************************************************************************************************
Note:
   
   In order to make myself to familar with JAVA NIO and Network API, I write this tiny tcp servers Java framework. 

   It can handle thousands client tcp connections at same time.

   Base on the framework, I implement a simple echo server as an example.

   And I also write a echo testing tool---echoStressTest.erl by Erlang.

   I use echoStressTest.erl to simulate thousonds clients to connect the tinyTCPServer.
   

*****************************************************************************************************
Build:

   1) Make sure you have installed the JDK 1.7 and ant

   2) run ant

*****************************************************************************************************
Usage:
   
   1) execute "ant run", or "java -cp tinyTCPServer.jar tinyTCPServer.example.EchoServer"
      
      The echo server will listen to the 6678 port to accept the tcp connection.
      
      Please refer to tinyTCPServer.example.EchoServer.java

   2) run your client to access the echo server.

*****************************************************************************************************
Stress Testing:

    1) Make sure you have installed the erlang(http://www.erlang.org/)
    
    2) execute command: "./echoStressTest.erl -h" to see the usage info

    3) If you need to run a lot of concurrent connections testing, 

       Please tune your linux server at first, and check the system limit(ulimit -a)

  


