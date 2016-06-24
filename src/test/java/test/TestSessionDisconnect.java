package test;

import java.io.IOException;

import com.gifisan.nio.client.ConnectorSession;
import com.gifisan.nio.client.TCPConnector;
import com.gifisan.nio.client.OnReadFuture;
import com.gifisan.nio.component.future.ReadFuture;

public class TestSessionDisconnect {
	
	
	public static void main(String[] args) throws IOException {


		String serviceName = "TestSessionDisconnectServlet";
		String param = ClientUtil.getParamString();
		
		TCPConnector connector = null;
		try {
			connector = ClientUtil.getClientConnector();
			
			connector.connect();
			
			ConnectorSession session = connector.getClientSession();
			
			ReadFuture future = session.request(serviceName, param);
			System.out.println(future.getText());
			
			session.listen(serviceName, new OnReadFuture() {
				public void onResponse(ConnectorSession session, ReadFuture future) {
					System.out.println(future.getText());
				}
			});
			
			
			session.write(serviceName, param);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			ThreadUtil.sleep(1000);
//			CloseUtil.close(connector);
		}
		
	
	}
}