package Main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


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
	 * 登录
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
	 * list
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
	   * 查看当前目录
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
	 * 退出
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
	 * 更改目录
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
	 * 新建目录
	 * MKD DIR
	 */
	public void ftp_mkdir(String dir) {
		String mkd = "MKD " + dir +"\n";
		try {
			outputStream.write(mkd.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 删除目录
	 * RMD DIR
	 */
	public void ftp_rmdir(String dir) {
		String rmd = "RMD " + dir +"\n";
		try {
			outputStream.write(rmd.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 删除文件
	 * DELE
	 */
	
	/*
	 * 上传文件
	 * STOR FILENAME
	 * STOU FILENAME
	 */
	public void ftp_stor(String sendfile) {
		File file = new File(sendfile);
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
			//ResponseResult.getResponse(inputStream);
			
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
	public void ftp_retr(String file, String fileName) {
		String pasv = "PASV\n";
		String downFile = "RETR " +file +"\n"; 
		try {
			outputStream.write(pasv.getBytes());
			int port = ResponseResult.getPort(inputStream);
			System.out.println(port);
			outputStream.write(downFile.getBytes());
			
			Socket socket = new Socket(this.url, port);
			//先连接被动端口，才能接受是否能下载的命令码
			String response = ResponseResult.getResponse(inputStream);
			if(response.startsWith("150")) {
				File newFile = new File(fileName);
				OutputStream fileOutput = new FileOutputStream(newFile);
				InputStream fileInput = socket.getInputStream();
				byte[] bytes = new byte[10000];
				int fileLen = 0;
				while((fileLen = fileInput.read(bytes))!=-1) {
					System.out.println("file is downloading ...");
					fileOutput.write(bytes, 0, fileLen);
				}
				fileOutput.close();
				fileInput.close();
				ResponseResult.getResponse(inputStream);
			}
			socket.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}
