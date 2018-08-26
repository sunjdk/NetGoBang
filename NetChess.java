import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetChess {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		String host="localhost";
		InetAddress ia=InetAddress.getByName(host);
		System.out.println("主机地址："+ia.getHostAddress());
		System.out.println("规范的主机名:"+ia.getCanonicalHostName());
		System.out.println("主机名："+ia.getHostName());
		System.out.println("是否环回地址："+ia.isLoopbackAddress());
	}

}
