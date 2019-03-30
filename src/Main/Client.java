package Main;

import java.util.Scanner;


public class Client {
	public static void main(String[] args) throws Exception {

		Scanner input = new Scanner(System.in);
		System.out.print("Please enter the host IP:");
		String url = "182.254.181.205";
		FtpUtils ftpUtils = new FtpUtils(url);
		ftpUtils.ftp_connect();

		System.out.print("Please enter the username:");
		String username = input.nextLine();
		ftpUtils.ftp_user(username);

		System.out.print("Please enter the password:");
		String password = input.nextLine();
		ftpUtils.ftp_pass(password);

		String cmd = null;
		boolean flag=true;
		while (flag) {
			System.out.print("ftp "+ url +" >");
			cmd = input.nextLine();
			String[] order = cmd.split(" ");
			
			switch (order[0]) {
			case "ls":
				ftpUtils.ftp_list();
				break;
			case "pwd":
				ftpUtils.ftp_pwd();
				break;
			case "mkdir":
				ftpUtils.ftp_mkdir(order[1]);
				break;
			case "rmdir":
				ftpUtils.ftp_rmdir(order[1]);
				break;
			case "delete":
				ftpUtils.ftp_delete(order[1]);
				break;
			case "get":
				ftpUtils.ftp_retr(order[1], order[2]);
				break;
			case "send":
				ftpUtils.ftp_stor(order[1]);
				break;
			case "quit":
				ftpUtils.ftp_quit();
				flag = false;
				break;
			case "cd":
				ftpUtils.ftp_cd(order[1]);
				break;
			default:
				break;
			}
		}
		input.close();
	}
}
