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



public class Client extends JFrame implements ActionListener{
	
	JPanel contentPane;
	JLabel jLabel1=new JLabel();
	JTextField jTextField1=new JTextField("127.0.0.1"); //IP地址框
	JLabel jLabel2=new JLabel();  //端口号标签
	JTextField jTextField2=new JTextField("4700"); //端口号内容
	
	JButton jButton1=new JButton(); //连接按钮
	JLabel jLabel3=new JLabel();
	JTextField jTextField3=new JTextField();
	JButton jButton2=new JButton();//发送按钮
	JButton jButton3=new JButton();//悔棋按钮
	JScrollPane jScrollPanel=new JScrollPane();
	JTextArea jTextArea1=new JTextArea();
	BufferedReader instr=null;
	Socket socket=null;
	PrintWriter os=null;
	public static String[] ss=new String[10];
	//保存棋子的坐标
	int x=0;
	int y=0;
	
	//allChess[][] 0 无子  1 黑子  2 白子
	int[][] allChess=new int[19][19]; 
	boolean isBlack=false;
	//标识当前游戏是否可以继续
	boolean canPlay=false;
	//保存显示的提示信息
	String message="";
	JPanel panel1=new JPanel();
	GobangPanel panel2=new GobangPanel();
	
	
	public Client(){
		jbInit();
	}
	private void jbInit(){
		contentPane=(JPanel) this.getContentPane();
		
		jLabel1.setFont(new Font("宋体",0,14));
		jLabel1.setText("服务器名称");
		jLabel1.setBounds(new Rectangle(25,22,87,28));
		
		this.setSize(new Dimension(540,640));
		this.setTitle("客户端");
		
		jTextField1.setBounds(new Rectangle(114,26,68,24));
		
		jLabel2.setFont(new Font("宋体",0,14));
		jLabel2.setText("端口号");
		jLabel2.setBounds(new Rectangle(197,22,60,28));
		jTextField2.setBounds(new Rectangle(260,25,54,24));
		
		jButton1.setBounds(new Rectangle(400,28,73,25));
		jButton1.setFont(new Font("Dialog",0,14));
		jButton1.setBorder(BorderFactory.createEtchedBorder());
		jButton1.setActionCommand("jButton1");
		jButton1.setText("连接");
		
		jLabel3.setBounds(new Rectangle(23,57,87,28));
		jLabel3.setText("请输入信息");
		jLabel3.setFont(new Font("宋体",0,14));
		jTextField3.setBounds(new Rectangle(114,60,314,24));
		
		jButton2.setText("发送");
		jButton2.setActionCommand("jButton1");
		jButton2.setBorder(BorderFactory.createEtchedBorder());
		jButton2.setFont(new Font("Dialog",0,14));
		jButton2.setBounds(new Rectangle(440,58,73,25));
		
		jButton3.setText("悔棋");
		jButton3.setActionCommand("jButton1");
		jButton3.setBorder(BorderFactory.createEtchedBorder());
		jButton3.setFont(new Font("Dialog",0,14));
		jButton3.setBounds(new Rectangle(480,28,43,25));
		
		jScrollPanel.setBounds(new Rectangle(23,85,493,69));
		
		jTextField3.setText("此处输入发送信息");
		jTextArea1.setText("");
		
		panel1.setLayout(null);
		
		panel1.add(jLabel1);
		panel1.add(jTextField1);
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
		panel1.setBounds(0, 0, 540, 160);
		panel2.setBounds(10,160,540,460);
		jButton1.addActionListener(this);
		jButton2.addActionListener(this);
		jButton3.addActionListener(this);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){				
				try {
					sendData("quit|");
					socket.close();
					instr.close();
					os.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("输入输出异常"+e1.toString());
					//e1.printStackTrace();
				}
			}
		});
	}
	
	
	@Override 
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==jButton1){//连接按钮
			String ip=jTextField1.getText().trim();
			int port=Integer.parseInt(jTextField2.getText().trim());
			if(connectServer(ip,port)==true){
				jTextArea1.setText("成功连接到服务器");
			}else{
				jTextArea1.setText("连接失败");
			}
		}
		if(e.getSource()==jButton2){//发送按钮
			String s=this.jTextField3.getText().trim();
			sendData(s);
			
		}
		if(e.getSource()==jButton3){//悔棋按钮
			if(canPlay!=true){//该对方走棋
				allChess[x][y]=0;
				panel2.repaint();
				canPlay=true;
				String s="undo|"+x+"|"+y;
				sendData(s);
				System.out.print("发送悔棋信息");
			}else{//对方已经走棋
				message="对方已经走棋，不能悔棋了";
				JOptionPane.showMessageDialog(this, message);
				System.out.print("对方已经走棋，不能悔棋了");
			}
		}	
	}
	//连接按钮事件处理方法
	private boolean connectServer(String ip, int port) {
		// TODO Auto-generated method stub
		boolean isLink=true;
		if(jButton1.getText().trim().equals("连接")){
			jButton1.setText("连接服务器");
			try {
				socket=new Socket(ip,port);
				this.setTitle("你是白方");
				jButton1.setText("正在聊天");
				message="自己是白棋，黑方先行";
				panel2.repaint();
				MyThread t=new MyThread();
				t.start();
				isLink=true;
			} catch (UnknownHostException e1) {
				System.out.println("未知主机异常");
				isLink=false;
				//e.printStackTrace();				
			} catch (IOException e2) {
				System.out.println("输入输出异常");
				isLink=false;
				//e.printStackTrace();
			}			
		}	
		return isLink;		
	}
	private void sendData(String s) {
		// TODO Auto-generated method stub
		try {
			os=new PrintWriter(socket.getOutputStream());
			os.println(s);
			os.flush();
			this.jTextArea1.setText("");
			this.jTextArea1.append("客户端:"+s+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("输入输出异常");
			//e.printStackTrace();
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
	class MyThread extends Thread{
		public void run(){
			
			try {
				os=new PrintWriter(socket.getOutputStream());
				instr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				while(true){
					this.sleep(100);
					if(instr.ready()){
						
						String cmd=instr.readLine();
						//在每个空格字符处进行分割
						ss=cmd.split("\\|");
						if(cmd.startsWith("move")){
							int x=Integer.parseInt(ss[1]);
							int y=Integer.parseInt(ss[2]);
							allChess[x][y]=1;
							message="轮到自己下棋子";
							panel2.repaint();
							canPlay=true;
						}
						if(cmd.startsWith("undo")){
							JOptionPane.showMessageDialog(null, "对方撤销上步棋");
							int x=Integer.parseInt(ss[1]);
							int y=Integer.parseInt(ss[2]);
							allChess[x][y]=0;
							panel2.repaint();
							canPlay=false;
						}
						if(cmd.startsWith("over")){
							JOptionPane.showMessageDialog(null, "游戏结束，对方胜！");
							panel2.setEnabled(false);
							canPlay=false;
						}
						if(cmd.startsWith("quit")){
							JOptionPane.showMessageDialog(null, "游戏结束，对方离开了！");
							panel2.setEnabled(false);
							canPlay=false;
						}
						if(cmd.startsWith("chat")){
							jTextArea1.append("服务器说："+ss[1]+"\n");
						}						
					}					
				}				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame.setDefaultLookAndFeelDecorated(true);
		Client frm=new Client();
		frm.setVisible(true);
	}

}
