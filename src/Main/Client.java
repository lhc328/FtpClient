package Main;

import java.io.File;

public class Client {
	public static void main(String[] args) throws Exception{
		FtpUtils ftpUtils = new FtpUtils("182.254.181.205");
		ftpUtils.ftp_connect("koy","lhc6119873");
		ftpUtils.ftp_list();
		ftpUtils.ftp_pwd();
		ftpUtils.ftp_cd("upload");
		ftpUtils.ftp_pwd();
		File file = new File("E:\\eclipse-workspace\\FTPClient\\src\\resource\\1.txt");
		ftpUtils.ftp_stor(file);
		ftpUtils.ftp_quit();
	}
}
