import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server extends JFrame implements ActionListener{
	JPanel contentPane;
	JLabel jLabel2=new JLabel();
	JTextField jTextField2=new JTextField("4700");
	JButton jButton1=new JButton();
	JLabel jLabel3=new JLabel();
	JTextField jTextField3=new JTextField();
	JButton jButton2=new JButton();//发送按钮
	JButton jButton3=new JButton();//悔棋按钮
	JScrollPane jScrollPanel=new JScrollPane();
	JTextArea jTextArea1=new JTextArea();
	ServerSocket server=null;
	Socket socket=null;
	BufferedReader instr=null;
	PrintWriter os=null;
	public static String[] ss=new String[10];
	//保存刚刚下的棋子坐标
	int x=0;
	int y=0;
	
	int[][] allChess=new int[19][19];
	boolean isBlack=true;
	boolean canPlay=true;
	String message="";
	JPanel panel1=new JPanel();
	GobangPanel panel2=new GobangPanel();
	
	/**
	 * 服务器端构造方法
	 */
	public Server(){
		jbInit();
	}
	//各种组建的初始化
	private void jbInit() {
		contentPane=(JPanel) this.getContentPane();
		/**
		 * n. [数] 维；尺寸；次元；容积
		 * vt. 标出尺寸
		 * adj. 规格的
		 *
		 * Dimension 类封装单个对象中组件的宽度和高度（精确到整数）。该类与组件的某个属性关联。由 Component 类和LayoutManager 接口定义的一些方法将返回
		 *  Dimension 对象。通常，width 和 height 的值是非负整数。允许创建 dimension 的构造方法不会阻止您为这些属性设置负值。如果width 或 height 的值
		 *  为负，则由其他对象定义的一些方法的行为是不明确的。个人理解：表示一个（单个）组件的大小（该类封装了一个构件的高度和宽度）该类的高度、宽度值
		 *  都是一个整数，表明有多少个像素点。多用于表示 GUI 控件等的大小。
		 *		 
		 */		
		this.setSize(new Dimension(540,640));
		this.setTitle("服务器-黑方");
		jLabel2.setBounds(new Rectangle(22,0,72,28));
		jLabel2.setText("端口号");
		jLabel2.setFont(new Font("宋体",0,14));
		jTextField2.setBounds(new Rectangle(73,0,45,24));
		
		jButton1.setBounds(new Rectangle(120,0,73,25));
		jButton1.setFont(new Font("Dialog",0,14));
		//对参数Border对象的定义主要通过BorderFactory进行，这也是本文讨论的重点，根据API的解释：BorderFactory提供标准 Border 对象的工厂类。在任何可能的地方，此工厂类都将提供对已共享 Border 实例的引用。
		jButton1.setBorder(BorderFactory.createEtchedBorder());
		jButton1.setActionCommand("jButton1");
		jButton1.setText("侦听");
		
		jLabel3.setBounds(new Rectangle(200,0,87,28));
		jLabel3.setText("请输入信息");
		jLabel3.setFont(new Font("宋体",0,14));
		jTextField3.setBounds(new Rectangle(274,0,154,24));
		jTextField3.setText("");
		
		jButton2.setText("发送");
		jButton2.setActionCommand("jButton1");
		jButton2.setBorder(BorderFactory.createEtchedBorder());
		jButton2.setFont(new Font("Dialog",0,14));
		jButton2.setBounds(new Rectangle(430,0,43,25));
		
		jButton3.setText("悔棋");
		jButton3.setActionCommand("jButton1");
		jButton3.setBorder(BorderFactory.createEtchedBorder());
		jButton3.setFont(new Font("Dialog",0,14));
		jButton3.setBounds(new Rectangle(480,0,43,25));
		
		jScrollPanel.setBounds(new Rectangle(23,28,493,89));
		jTextField3.setText("此处输入发送信息");
		jTextArea1.setText("聊天内容");
		
		panel1.setLayout(null);
		panel1.add(jLabel2);
		panel1.add(jTextField2);
		panel1.add(jButton1);
		panel1.add(jLabel3);
		panel1.add(jTextField3);
		panel1.add(jButton2);
		panel1.add(jButton3);
		panel1.add(jScrollPanel);
		
		jScrollPanel.getViewport().add(jTextArea1);
		contentPane.setLayout(null);
		contentPane.add(panel1);
		contentPane.add(panel2);
		
		panel1.setBounds(0, 0, 540, 120);
		panel2.setBounds(10,120,540,460);
		jButton1.addActionListener(this);
		jButton2.addActionListener(this);
		jButton3.addActionListener(this);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				
				try {
					sendData("quit|");
					socket.close();
					instr.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("输入输出异常"+e.toString());
				}					
			}			
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==jButton1){
			int port=Integer.parseInt(jTextField2.getText().trim());
			listenClient(port);
			System.out.print("侦听...");
		}
		if(e.getSource()==jButton2){
			String s=this.jTextField3.getText().trim();
			sendData(s);
			System.out.println("发送文字");
		}
		if(e.getSource()==jButton3){
			if(canPlay!=true){
				allChess[x][y]=0;
				panel2.repaint();
				canPlay=true;
				String s="undo|"+x+"|"+y;
				sendData(s);
				System.out.println("发送悔棋信息！");				
			}else{
				message="对方已经走棋，不能悔棋了";
				JOptionPane.showMessageDialog(this, message);
				System.out.println("对方已经走棋，不能悔棋了");
			}
		}
	}
	private void listenClient(final int port) {
		// TODO Auto-generated method stub
		try{
			if(jButton1.getText().trim().equals("侦听")){
				new Thread(new Runnable(){
					public void run(){
						try {
							server=new ServerSocket(port);
							jButton1.setText("正在侦听......");
							socket=server.accept();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("输入输出异常："+e.toString());
						}
						//this.setTitle("你是黑方");
						sendData("已经成功连接......");
						jButton1.setText("正在聊天......");
						jTextArea1.append("客户端已经连接到服务器\n");
						message="自己是黑方先行";
						panel2.repaint();
						MyThread t=new MyThread();
						t.start();
					}
				}).start();
			}
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
	}
	
	
	//发送信息
	private void sendData(String s) {
		// TODO Auto-generated method stub
		try {
			os=new PrintWriter(socket.getOutputStream());
			os.println(s);
			os.flush();
			if(!s.equals("已经成功连接\n"));
				this.jTextArea1.setText("");
				this.jTextArea1.append("服务器:"+s+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("输入输出异常"+e.toString());
		}
		
	}
	
	
	class MyThread extends Thread{
		public void run(){
			try{
				while(true){
					this.sleep(100);
					instr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					if(instr.ready()){
						String cmd=instr.readLine();
						jTextArea1.append("客户端："+cmd+"\n");
						//在每个|字符处进行分解
						ss=cmd.split("\\|");
						/**
						 * 处理通信协议
						 */
						if(cmd.startsWith("move")){//1)move|下棋子的位置坐标(x,y)
							int x=Integer.parseInt(ss[1]);
							int y=Integer.parseInt(ss[2]);
							allChess[x][y]=2;  //白子
							message="轮到自己下棋";
							panel2.repaint();
							canPlay=true;
						}
						if(cmd.startsWith("undo")){//4)undo|x|y悔棋命令
							JOptionPane.showMessageDialog(null, "对方撤销上步棋");
							int x=Integer.parseInt(ss[1]);
							int y=Integer.parseInt(ss[2]);
							
							allChess[x][y]=0;
							panel2.repaint();
							canPlay=false;
						}
						if(cmd.startsWith("over")){//2)over|哪方赢的信息
							JOptionPane.showMessageDialog(null, message);
							panel2.setEnabled(false);
							canPlay=false;
						}
						if(cmd.startsWith("quit")){//3)quit|表示游戏结束
							JOptionPane.showMessageDialog(null, "游戏结束，对方离开了！！！");
							panel2.setEnabled(false);
							canPlay=false;
						}
						if(cmd.startsWith("chat")){//5)chat|聊天内容
							jTextArea1.append("客户端说："+ss[1]+"\n");
						}
					}
				}
			}catch (InterruptedException e) {
				System.out.println("线程中断异常："+e.toString());
				//e.printStackTrace();
			}catch(IOException f){
				System.out.println("输入输出异常："+f.toString());
				//e.printStackTrace();
			}			
		}
	}
	/**
	 * 采用双缓冲技术防止屏幕闪烁
	 * @author 桃谷六仙
	 * 这是个内部类
	 */
	class GobangPanel extends JPanel{
		BufferedImage bgImage=null;//棋盘背景图片
		GobangPanel(){
			this.addMouseListener(new MouseLis());
			String imagePath="";
			
			try {
				imagePath=System.getProperty("user.dir")+"/background2.jpg";
				System.out.println(imagePath);
				bgImage=ImageIO.read(new File(imagePath.replaceAll("\\\\", "/")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("输入输出异常："+e.toString());
				e.printStackTrace();
			}
		}
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			//双缓冲技术防止屏幕闪烁
			BufferedImage bi=new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB);
			Graphics g2=bi.createGraphics();
			g2.setColor(Color.BLACK);
			//绘制背景
			g2.drawImage(bgImage, 1, 20, this);
			//输出标题信息
			g2.setFont(new Font("黑体",Font.BOLD,15));
			g2.drawString("游戏信息："+message,130,60);
			
			//绘制棋盘
			for(int i=0;i<19;i++){
				g2.drawLine(10, 70+20*i, 370, 70+20*i);
				g2.drawLine(10+20*i, 70, 10+20*i, 430);
			}
			//标注定点
			g2.fillOval(68, 128, 6, 6);
			g2.fillOval(308, 128, 6, 6);
			g2.fillOval(308, 368, 6, 6);
			g2.fillOval(68, 368, 6, 6);
			g2.fillOval(308, 248, 6, 6);
			g2.fillOval(188, 128, 6, 6);
			g2.fillOval(68, 248, 6, 6);
			g2.fillOval(188, 368, 6, 6);
			g2.fillOval(188, 248, 6, 6);
			
			//绘制全部棋子
			for(int i=0;i<19;i++){
				for(int j=0;j<19;j++){
					if(allChess[i][j]==1){
						//黑子
						int tempX=i*20+10;
						int tempY=j*20+70;
						g2.setColor(Color.BLACK);
						g2.fillOval(tempX-7, tempY-7, 14, 14);
					}
					if(allChess[i][j]==2){
						//白子
						int tempX=i*20+10;
						int tempY=j*20+70;
						g2.setColor(Color.WHITE);
						g2.fillOval(tempX-7, tempY-7, 14, 14);
						
						//g2.fillOval(tempX-7, tempY-7, 14, 14);
					}
				}
			}
			g.drawImage(bi, 0, 0, this);
		}
	}
	
	/**
	 * 鼠标事件处理类（内部类）
	 * 在鼠标按下的时候判断位置是否在棋盘中，并且此处无棋子
	 * 然后根据自己是哪方来修改棋子对应位置的数组元素值
	 * 落子后刷新自己的屏幕，并调用checkWin()来判赢
	 * @param args
	 */
	
	class MouseLis extends MouseAdapter{
		//MouseAdapter的介绍
		//http://blog.sina.com.cn/s/blog_62eaeadd0101er24.html
		public void mousePressed(MouseEvent e){
			if(canPlay==true){
				x=e.getX();
				y=e.getY();
				if(x>=10 && x<=370 && y>=70 && y<=430){//判断鼠标是否在棋盘内
					x=x/20;
					y=(y-60)/20;
					if(allChess[x][y]==0){
						//判断当前要下的是什么颜色的棋子
						if(isBlack==true){
							allChess[x][y]=1;
							message="轮到白方";
							sendData("move|"+String.valueOf(x)+"|"+String.valueOf(y));
							canPlay=false;
							repaint();
						}else{
							allChess[x][y]=2;
							message="轮到黑方";
							sendData("move|"+String.valueOf(x)+"|"+String.valueOf(y));
							canPlay=false;
							//白子
							repaint();
						}
						//判断这个棋子是否和其他棋子成5连
						boolean winFlag=this.checkWin();
						if(winFlag==true){
							message="游戏结束，"+(allChess[x][y]==1 ? "黑方" : "白方")+"胜";
							sendData("over|"+message);
							JOptionPane.showMessageDialog(null, message);
							System.out.println(message);
							canPlay=false;
						}
					}else{
						message="当前位置已经有棋子，请重新落子";
						System.out.println(message);
					}
				}else{
					message="请在棋盘内下子";
					System.out.println(message);
				}
				repaint();
			}else{
				message="该对方走棋！";
				JOptionPane.showMessageDialog(null, message);
			}
		}

		private boolean checkWin() {
			// TODO Auto-generated method stub
			boolean flag=false;
			//保存几子相连
			int count=1;
			//判断横向是否有5子相连，特点是纵坐标相同
			int color=allChess[x][y];
			//通过循环来做棋子相连的判断
			//横向判断
			int i=1;
			while(color==allChess[x+i][y+0]){
				count++;
				i++;
			}
			i=1;
			while(color==allChess[x-i][y-0]){
				count++;
				i++;
			}
			if(count>=5){
				flag=true;
			}
			//纵向判断
			int i2=1;
			int count2=1;
			while(color==allChess[x+0][y+i2]){
				count2++;
				i2++;
			}
			i2=1;
			while(color==allChess[x-0][y-i2]){
				count2++;
				i2++;
			}
			if(count2>=5){
				flag=true;
			}
			//斜向判断
			int i3=1;
			int count3=1;
			while(color==allChess[x+i3][y-i3]){
				count3++;
				i3++;
			}
			i3=1;
			while(color==allChess[x-i3][y+i3]){
				count3++;
				i3++;
			}
			if(count3>=5){
				flag=true;
			}
			//反斜向判断
			int i4=1;
			int count4=1;
			while(color==allChess[x+i4][y+i4]){
				count4++;
				i4++;
			}
			i4=1;
			while(color==allChess[x-i4][y-i4]){
				count4++;
				i4++;
			}
			
			if(count4>=5){
				flag=true;
			}
			
			return flag;
		}
	}
	
	
	
	
	
	
	//服务端程序入口方法
	public static void main(String args[]){
		JFrame.setDefaultLookAndFeelDecorated(true);
		Server frm=new Server();
		frm.setVisible(true);
		
		try {
			InetAddress address=InetAddress.getLocalHost().getLocalHost();
			frm.setTitle(frm.getTitle()+"名称及 IP 地址："+address.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("未知主机异常："+e.toString());
		}
		
	}
}
