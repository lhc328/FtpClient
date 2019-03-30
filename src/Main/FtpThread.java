package Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class FtpThread implements Runnable {
	
	public String url;
	public int port=0;
	
	public void run() {
		try {
			Socket socket = new Socket(this.url, this.port);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			byte[] bytes = new byte[10000];
			System.out.println("[+]PASV thread is start at port " + this.port);
			int len = inputStream.read(bytes);
			String ls = new String(bytes, 0, len, "utf-8");
			System.out.println(ls);
			inputStream.close();
			outputStream.close();
			socket.close();
			System.out.println("[+] PASV thread is close");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
