package com.gifisan.nio.plugin.jms.server;

import java.util.Map;

import com.gifisan.nio.common.LifeCycleUtil;
import com.gifisan.nio.component.AbstractPluginContext;
import com.gifisan.nio.component.ApplicationContext;
import com.gifisan.nio.component.Configuration;
import com.gifisan.nio.component.Session;
import com.gifisan.nio.component.future.ReadFuture;
import com.gifisan.nio.concurrent.ReentrantMap;
import com.gifisan.nio.concurrent.ReentrantSet;
import com.gifisan.nio.plugin.jms.JMSException;
import com.gifisan.nio.plugin.jms.Message;
import com.gifisan.nio.plugin.jms.decode.DefaultMessageDecoder;
import com.gifisan.nio.plugin.jms.decode.MessageDecoder;
import com.gifisan.nio.server.service.FutureAcceptorService;

public class DefaultMQContext extends AbstractPluginContext implements MQContext {

	private long						dueTime				= 0;
	private ReentrantMap<String, Message>	messageIDs			= new ReentrantMap<String, Message>();
	private P2PProductLine				p2pProductLine			= new P2PProductLine(this);
	private SubscribeProductLine			subProductLine			= new SubscribeProductLine(this);
	private ReentrantSet<String>			receivers				= new ReentrantSet<String>();
	private MessageDecoder				messageDecoder			= new DefaultMessageDecoder();
	private ConsumerPushHandle			consumerPushFailedHandle	= null;

	public Message browser(String messageID) {
		return messageIDs.get(messageID);
	}

	public void initialize(ApplicationContext context, Configuration config) throws Exception {

		long dueTime = config.getLongParameter("due-time");

		setMessageDueTime(dueTime == 0 ? 1000 * 60 * 60 * 24 * 7 : dueTime);

		Thread p2pThread = new Thread(p2pProductLine, "JMS-P2P-ProductLine");

		Thread subThread = new Thread(subProductLine, "JMS-SUB-ProductLine");

		this.consumerPushFailedHandle = new ConsumerPushHandle(this);

		p2pProductLine.start();

		subProductLine.start();

		p2pThread.start();

		subThread.start();

		MQContextFactory.initializeContext(this);
		
		context.addSessionEventListener(new TransactionProtectListener());
	}

	public void destroy(ApplicationContext context, Configuration config) throws Exception {
		LifeCycleUtil.stop(p2pProductLine);
		LifeCycleUtil.stop(subProductLine);
		MQContextFactory.setNullMQContext();
		super.destroy(context, config);
	}

	public long getMessageDueTime() {
		return this.dueTime;
	}

	public int messageSize() {
		return this.p2pProductLine.messageSize();
	}

	public void offerMessage(Message message) {

		messageIDs.put(message.getMsgID(), message);

		p2pProductLine.offerMessage(message);
	}

	public void publishMessage(Message message) {

		subProductLine.offerMessage(message);
	}

	public void consumerMessage(Message message) {

		messageIDs.remove(message.getMsgID());
	}

	public Message parse(ReadFuture future) throws JMSException {
		return messageDecoder.decode(future);
	}

	public void pollMessage(Session session, ReadFuture future, JMSSessionAttachment attachment) {

		p2pProductLine.pollMessage(session, future, attachment);
	}

	public void subscribeMessage(Session session, ReadFuture future, JMSSessionAttachment attachment) {

		subProductLine.pollMessage(session, future, attachment);
	}

	public void setMessageDueTime(long dueTime) {
		this.dueTime = dueTime;
		this.p2pProductLine.setDueTime(dueTime);
		this.subProductLine.setDueTime(dueTime);
	}

	public void addReceiver(String queueName) {
		receivers.add(queueName);
	}

	public boolean isOnLine(String queueName) {
		return receivers.contains(queueName);
	}

	public void removeReceiver(String queueName) {
		receivers.remove(queueName);
	}

	public ConsumerPushHandle getConsumerPushFailedHandle() {
		return consumerPushFailedHandle;
	}

	public void configFutureAcceptor(Map<String, FutureAcceptorService> acceptors) {

		acceptors.put(JMSConsumerServlet.SERVICE_NAME, new JMSConsumerServlet());
		acceptors.put(JMSProducerServlet.SERVICE_NAME, new JMSProducerServlet());
		acceptors.put(JMSSubscribeServlet.SERVICE_NAME, new JMSSubscribeServlet());
		acceptors.put(JMSPublishServlet.SERVICE_NAME, new JMSPublishServlet());
		acceptors.put(JMSTransactionServlet.SERVICE_NAME, new JMSTransactionServlet());
		acceptors.put(JMSBrowserServlet.SERVICE_NAME, new JMSBrowserServlet());

	}

	public void prepare(ApplicationContext context, Configuration config) throws Exception {

		MQContext old = MQContextFactory.getMQContext();

		// FIXME 把老的Context中的数据放到这里

		long dueTime = config.getLongParameter("due-time");

		setMessageDueTime(dueTime == 0 ? 1000 * 60 * 60 * 24 * 7 : dueTime);

		Thread p2pThread = new Thread(p2pProductLine, "JMS-P2P-ProductLine");

		Thread subThread = new Thread(subProductLine, "JMS-SUB-ProductLine");

		this.consumerPushFailedHandle = new ConsumerPushHandle(this);

		p2pProductLine.start();

		subProductLine.start();

		p2pThread.start();

		subThread.start();

		MQContextFactory.setNullMQContext();

		MQContextFactory.initializeContext(this);
	}

	public void unload(ApplicationContext context, Configuration config) throws Exception {
		LifeCycleUtil.stop(p2pProductLine);
		LifeCycleUtil.stop(subProductLine);
		MQContextFactory.setNullMQContext();
		super.destroy(context, config);
	}

}