# NetGoBang
网络五子棋源码(java版)

网络五子棋游戏介绍

socket 编程方法来制作 “网络五子棋” 程序
网络五子棋采用 C/S 架构，分为服务端和客户端。
服务器首先启动，单击 “侦听” 按钮启动服务器侦听是否有客户端连接，
如果有客户端连接则进入聊天和下棋功能，同时侦听按钮变成 “正在聊天”

界面设计
棋盘是通过JPanel面板类GobangPanel通过一张图片做背景
而棋盘线条、准星点位及双方的落子是绘制出来的。游戏界面中要求用户输入服务器
IP、端口等

通信协议

这里的通信是基于socket的连接
socket 通信需要 建立连接、收发数据、断开连接

本游戏是采用面向连接的socket编程实现

这里虽然两台计算机不分主次，但我们设计时假设一台做服务器端（黑方）
等待其他人加入，其他想加入的人输入服务器的主机IP,为区分通信中传销的是“输赢信息”
“下的妻子位置信息”、“重新开始”等，在发送信息的首部加上代号。
因此定义了如下的协议：

1)move|下棋子的位置坐标(x,y)
2)over|哪方赢的信息
3)quit|表示游戏结束
4)undo|x|y悔棋命令
5)chat|聊天内容

接受信息的线程中做如下处理：
public void run(){
	try{
		while(true){
			this.sleep(100);
			instr=new BufferedReader(new InputStreamReader(socket getInputStream()));
			if(instr.ready()){
				String cmd=instr.readLine();
				ss=cmd.split("\\|");
				if(cmd.startWidth("move")){
					message="轮到自己下棋子";
					int x=Integer.parseInt(ss[1]);
					int y=Integer.parseInt(ss[2]);
					allChess[x][y]=2;
					panel2.repaint();
					canPlay=true;
				}
				if(cmd.startWith(undo)){
					JOptionPanel.showMessageDialog(null,"对方撤销上步棋");
					int x=Integer.parseInt(ss[1]);
					int y=Integer.parseInt(ss[2]);
					allChess[x][y]=0;
					panel2.repaint();
					canPlay=false;
				}
				if(cmd.startWith("over")){
					JOptionPane.showMessageDialog(null,message);
					panel2.setEnable(false);
					canPlay=false;
				}
				if(cmd.startWidth("quit")){
					JOptionPane.showMessageDialog(null,"游戏结束，对方离开");
					panel2.setEnableed(false);
					canPlay=false;
				}
				if(cmd.startWith("chat")){
					jTextAreal.append("客户端说："+ss[1]+"\n");
				}
			}
		}
	}catch(Exception ex){
		System.out.print("error:"+ex);
	}
}


基于TCP网络的程序其他程序通信中依靠socket进行通信。socket可以看成在两个程序进行通信连接中的一个端点
一个程序将一段信息写入socket中，该socket将这段信息发送给另外一个socket处，使这段信息能够传送到其他
程序中，无论何时。在两个网络应用之间发送和接收信息时，都需要建立一个可靠的链接。流套接字
依靠TCP协议来保证信息正确到达目的地，实际上，IP包有可能在网络中丢失或者在传输过程中发生错
误，任何一种情况发生作为接收方的TCP将联系发送方重新发送这个IP包。这就是所谓的在
两个流套接字接之间建立可靠的连接。

流套接字指在C/S程序中扮演一个重要角色，客户机程序。创建个半夜服务器程序。客户机程序。创建一个
扮演服务器程序的主机ip地址。和服务器程序。的端口号的刘涛今日对象。后来刘涛的初始化代码将ip
地址和端口号。传奇客户端主机的网络管理软件。网络管理软件将ip地址和端口通ic卡传给服务器的主
机服务器的主机。读到经过h传递过来的数据，然后查看服务器端程序。是否处于监听状态，这种贱精英
演示通过套接字和端口来劲。如果服务器程序处于监听状态那么服务器端网络管理软件，就像客户机。
网络管理软件发出一个积极的详细好。



InetAddress 			32或者128为的ipv4或ipv6网络地址
Socket 					客户端流套接字
ServerSocket			服务程序流套接字

双方的落子信息保存在二维数组allChess[][]中
0	表示这个位置没有棋子
1	表示这个位置是黑子
2	表示这个位置是白子

int[][] allChess=new int[19][19];
//自己是黑棋
boolean isBlack=true;
标识当前游戏是否可继续
boolean canPlay=true;
String message="";
