import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerDemo {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("服务器开启....\n");
		ServerSocket server=new ServerSocket(10000);
		while(true){
			Socket s=server.accept();
			System.out.println("接受连接....\n");
			new ServerThread(s).start();
		}
	}

}
