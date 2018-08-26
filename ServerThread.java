import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

public class ServerThread extends Thread{
	private Socket s;
	ServerThread(Socket s){
		this.s=s;
	}
	public void run(){
		BufferedReader br=null;
		PrintWriter pw=null;		
		
		
		
		try {
			InputStreamReader isr;//字节流通向字符流的桥梁,它将字节流转换为字符流
			isr=new InputStreamReader(s.getInputStream());
			//getInputStream方法得到的是一个输入流，服务端的Socket对象上的getInputStream方法得到的输入流其实就是从客户端发送给服务器端的数据流。
			br=new BufferedReader(isr);//提供通用的缓冲方式文本读取
			pw=new PrintWriter(s.getOutputStream(),true);//向文本输出流打印对象的格式化表示形式
			//通过现有的 OutputStream 创建新的 PrintWriter
			Calendar c=Calendar.getInstance();
			do{
				String cmd=br.readLine();
				if(cmd==null){
					break;
				}
				cmd=cmd.toUpperCase();
				if(cmd.startsWith("BYE"))
					break;
				if(cmd.startsWith("DATE") || cmd.startsWith("TIME"))
					pw.println(c.getTime().toString());
				if(cmd.startsWith("DOM"))
					pw.println(""+c.get(Calendar.DAY_OF_MONTH));
				if(cmd.startsWith("DOW"))
					switch(c.get(Calendar.DAY_OF_WEEK)){
						case Calendar.SUNDAY:
							pw.println("SUNDAY");
							break;
						case Calendar.MONDAY:
							pw.println("MONDAY");
							break;
						case Calendar.TUESDAY:
							pw.println("TUESDAY");
							break;
						case Calendar.WEDNESDAY:
							pw.println("WEDNESDAY");
							break;
						case Calendar.THURSDAY:
							pw.println("THURSDAY");
							break;
						case Calendar.FRIDAY:
							pw.println("FRIDAY");
							break;
						case Calendar.SATURDAY:
							pw.println("SATURDAY");
							break;
					}
				if(cmd.startsWith("DOY"))
					pw.println(""+c.get(Calendar.DAY_OF_YEAR));
				if(cmd.startsWith("PAUSE"))
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						System.out.println("线程中断异常"+e.toString());
					}
			}while(true);
		} catch (IOException e) {
			System.out.println("IO异常"+e.toString());
		}finally{
			System.out.println("关闭连接。。。\n");			
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
