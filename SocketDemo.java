import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String host="localhost";
		if(args.length==1)
			host=args[0];
		BufferedReader br=null;
		PrintWriter pw=null;
		Socket s=null;
		
		try {
			s=new Socket(host,10000);
			InputStreamReader isr;
			isr=new InputStreamReader(s.getInputStream());
			br=new BufferedReader(isr);			
			pw=new PrintWriter(s.getOutputStream(),true);
			
			pw.println("DATE");
			System.out.println(br.readLine());
			pw.println("PAUSE");
			pw.println("DOW");
			System.out.println(br.readLine());
			pw.println("DOM");
			System.out.println(br.readLine());
			do{
				pw.println("DOY");
				System.out.println(br.readLine());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("线程中断异常："+e.toString());
				}
			}while(true);
		} catch (UnknownHostException e) {
			System.out.println("未知的主机异常"+e.toString());
		} catch (IOException e) {
			System.out.println("IO异常"+e.toString());
		}finally{			
				try {
					if(br!=null)
						br.close();
					if(pw!=null)
						pw.close();
					if(s!=null)
						s.close();
				} catch (IOException e) {
					System.out.println("IO异常"+e.toString());
				}
			
		}
		
	}

}
