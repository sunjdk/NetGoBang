import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetChess {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		String host="localhost";
		InetAddress ia=InetAddress.getByName(host);
		System.out.println("������ַ��"+ia.getHostAddress());
		System.out.println("�淶��������:"+ia.getCanonicalHostName());
		System.out.println("��������"+ia.getHostName());
		System.out.println("�Ƿ񻷻ص�ַ��"+ia.isLoopbackAddress());
	}

}
