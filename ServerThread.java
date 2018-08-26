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
			InputStreamReader isr;//�ֽ���ͨ���ַ���������,�����ֽ���ת��Ϊ�ַ���
			isr=new InputStreamReader(s.getInputStream());
			//getInputStream�����õ�����һ��������������˵�Socket�����ϵ�getInputStream�����õ�����������ʵ���Ǵӿͻ��˷��͸��������˵���������
			br=new BufferedReader(isr);//�ṩͨ�õĻ��巽ʽ�ı���ȡ
			pw=new PrintWriter(s.getOutputStream(),true);//���ı��������ӡ����ĸ�ʽ����ʾ��ʽ
			//ͨ�����е� OutputStream �����µ� PrintWriter
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
						System.out.println("�߳��ж��쳣"+e.toString());
					}
			}while(true);
		} catch (IOException e) {
			System.out.println("IO�쳣"+e.toString());
		}finally{
			System.out.println("�ر����ӡ�����\n");			
			try {
				if(br!=null)
					br.close();
				if(pw!=null)
					pw.close();
				if(s!=null)
					s.close();
			} catch (IOException e) {
				System.out.println("IO�쳣"+e.toString());
			}
		}		
		
	}
}
