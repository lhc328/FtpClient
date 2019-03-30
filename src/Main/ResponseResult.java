package Main;

import java.io.IOException;
import java.io.InputStream;

public class ResponseResult {
	
	public static String getResponse(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[256];
		int len = inputStream.read(bytes);
		String result = new String(bytes, 0, len);
		System.out.print(result);
		return result;
	}
	
	
	public static int getPort(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[256];
		int len = inputStream.read(bytes);
		String result = new String(bytes, 0, len);
		System.out.print(result);
		String[] sl = result.split(",");
		String port2 = sl[sl.length-1];
		String port1 = sl[sl.length-2];
		port2 = port2.substring(0, port2.indexOf(")"));
		int port = Integer.parseInt(port1)*256+Integer.parseInt(port2);
		return port;
	}
}
