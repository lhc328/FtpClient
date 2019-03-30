package Main;

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
	 * ��������
	 */
	public void ftp_connect() {
		try {
			socket = new Socket(url, port);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			ResponseResult.getResponse(inputStream);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ftp_user(String username) {
		String name = "USER "+username+"\n";
		try {
			outputStream.write(name.getBytes());
			String response = ResponseResult.getResponse(inputStream);
			if(response.startsWith("220")) {
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void ftp_pass(String password) {
		try {
			String pass = "PASS " + password + "\n";
			outputStream.write(pass.getBytes());
			String response = ResponseResult.getResponse(inputStream);
			if(response.startsWith("220")) {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/*
	 * �鿴��ǰĿ¼
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
	 * �鿴��ǰ·��
	 */
	public void ftp_pwd() {
		String cwd = "PWD\n";
		try {
			outputStream.write(cwd.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * �Ͽ�����
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
	 * ����Ŀ¼
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
	 * �½��ļ���
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
	 * ɾ���ļ���
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
	 * ɾ���ļ�
	 */
	public void ftp_delete(String name) {
		String dete = "DELE " + name + "\n";
		try {
			outputStream.write(dete.getBytes());
			ResponseResult.getResponse(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * �ϴ��ļ�
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
			
			Socket socket = new Socket(this.url, port);
			ResponseResult.getResponse(inputStream);
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
	 * �����ļ�
	 */
	public void ftp_retr(String file, String fileName) {
		String pasv = "PASV\n";
		String downFile = "RETR " +file +"\n"; 
		try {
			outputStream.write(pasv.getBytes());
			int port = ResponseResult.getPort(inputStream);
			outputStream.write(downFile.getBytes());
			
			Socket socket = new Socket(this.url, port);
			//�����ӱ����˿ڣ����ܽ����Ƿ������ص�������
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
