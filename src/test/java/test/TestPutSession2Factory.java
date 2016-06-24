package test;

import java.io.IOException;

import com.gifisan.nio.client.TCPConnector;
import com.gifisan.nio.client.ConnectorSession;
import com.gifisan.nio.client.OnReadFuture;
import com.gifisan.nio.common.CloseUtil;
import com.gifisan.nio.common.ThreadUtil;
import com.gifisan.nio.component.future.ReadFuture;

public class TestPutSession2Factory {
	
	
	public static void main(String[] args) throws IOException {


		String serviceKey = "PutSession2FactoryServlet";
		String param = ClientUtil.getParamString();
		
		final TCPConnector connector = ClientUtil.getClientConnector();
		
		connector.connect();
		
		ConnectorSession session = connector.getClientSession();
		
		ReadFuture future = session.request(serviceKey, param);
		System.out.println(future.getText());
		
		session.listen(serviceKey, new OnReadFuture() {
			public void onResponse(ConnectorSession session, ReadFuture future) {
				System.out.println(future.getText());
			}
		});
		
		session.write(serviceKey, param);
//		response = session.request(serviceKey, param);
//		System.out.println(response.getContent());
		
		ThreadUtil.sleep(500);
		CloseUtil.close(connector);
		
	}
}