package Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sound.sampled.Port;

import com.sun.media.jfxmedia.events.NewFrameEvent;

public class FtpUtils {
	
	private Socket socket;
	private String url;
	private int port;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	public FtpUtils(String url) {
		this(url, 21);
	}
	
	public FtpUtils(String url, int port) {
		this.url = url;
		this.port = port;
	}
	
	
	/*
	 * 建立连接
	 */
	public void ftp_connect(String username, String password) {
		try {
			System.out.println(username+password+url+port);
			socket = new Socket(url, port);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			ResponseResult.getResponse(inputStream);
			
			String name = "USER "+username+"\n";
			outputStream.write(name.getBytes());
			ResponseResult.getResponse(inputStream);
			
			String pass = "PASS " + password + "\n";
			outputStream.write(pass.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 查看当前目录
	 */
	public void ftp_list() {
		String modMsg = "PASV\n";
		String ls = "LIST \n";
		try {
			outputStream.write(modMsg.getBytes());			
			int port = ResponseResult.getPort(inputStream);
			
			FtpThread ftpThread = new FtpThread();
			ftpThread.port=port;
			ftpThread.url=url;
			Thread thread = new Thread(ftpThread);
			thread.start();
			
			outputStream.write(ls.getBytes());
			ResponseResult.getResponse(inputStream);
			thread.join(3000);
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 查看当前路径
	 */
	public void ftp_pwd() {
		String cwd = "PWD\n";
		try {
			System.out.println("[+]send pwd");
			outputStream.write(cwd.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 断开连接
	 */
	public void ftp_quit() {
		String cmd = "QUIT\n";
		try {
			outputStream.write(cmd.getBytes());
			ResponseResult.getResponse(inputStream);
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("bye!");
	}
	
	/*
	 * 进入目录
	 */
	public void ftp_cd(String dir) {
		String cwd = "CWD " + dir + "\n";
		try {
			System.out.println("[+]change dir");
			outputStream.write(cwd.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 新建文件夹
	 * MKD DIR
	 */
	
	
	/*
	 * 删除文件夹
	 * RMD DIR
	 */
	
	/*
	 * 上传文件
	 * STOR FILENAME
	 * STOU FILENAME
	 */
	public void ftp_stor(File file) {
		if(file.isDirectory()) {
			System.out.println("cannot upload a directory");
			return;
		}
		String fileName = file.getName();
		String pasv = "PASV\n";
		String sendFile = "STOR " +fileName +"\n";
		try {
			outputStream.write(pasv.getBytes());
			int port = ResponseResult.getPort(inputStream);
			
			outputStream.write(sendFile.getBytes());
			
			System.out.println(port);
			
			Socket socket = new Socket(this.url, port);
			OutputStream fileOutput = socket.getOutputStream();
			InputStream fileInput = new FileInputStream(file);
			byte[] bytes = new byte[4096];
			int fileLen = 0;
			while((fileLen = fileInput.read(bytes)) != -1) {
				System.out.println("1");
				fileOutput.write(bytes, 0, fileLen);
			}
			fileOutput.flush();
			fileOutput.close();
			fileInput.close();
			socket.close();
			
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 下载文件
	 */
}
