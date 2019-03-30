package Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResponseResult {
	
	public static String getResponse(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[256];
		int len = inputStream.read(bytes);
		String result = new String(bytes, 0, len);
		System.out.println(result);
		return result;
	}
	
	public static void sendLine(OutputStream outputStream, String cmd) throws IOException {
		if(cmd==null) {
			System.out.println("cmd null");
			return;
		}
		String sends = cmd+"\n";
		outputStream.write(sends.getBytes());
	}
	
	public static int getPort(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[256];
		int len = inputStream.read(bytes);
		String result = new String(bytes, 0, len);
		System.out.println(result);
		String[] sl = result.split(",");
		String port2 = sl[sl.length-1];
		String port1 = sl[sl.length-2];
		port2 = port2.substring(0, port2.indexOf(")"));
		int port = Integer.parseInt(port1)*256+Integer.parseInt(port2);
		return port;
	}
}
