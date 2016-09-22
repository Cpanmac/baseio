package com.generallycloud.nio.component;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;

public abstract class TCPSelectorLoop extends AbstractSelectorLoop {

	private Logger				logger	= LoggerFactory.getLogger(TCPSelectorLoop.class);
	protected SelectionAcceptor	_read_acceptor;
	protected SelectionAcceptor	_write_acceptor;
	protected TCPSelectionAlpha	_alpha_acceptor;

	public TCPSelectorLoop(NIOContext context) {
		this._write_acceptor = new TCPSelectionWriter();
		this._read_acceptor = new TCPSelectionReader(context);
	}
	
	public void accept(SelectionKey selectionKey) throws IOException {
		
		if (!selectionKey.isValid()) {
			return;
		}

		try {
			if (selectionKey.isReadable()) {
				_read_acceptor.accept(selectionKey);
			} else if (selectionKey.isWritable()) {
				_write_acceptor.accept(selectionKey);
			} else {
				_alpha_acceptor.accept(selectionKey);
			}
			
//			else if (selectionKey.isAcceptable()) {
//				_alpha_acceptor.accept(selectionKey);
//			} else if (selectionKey.isConnectable()) {
//				_alpha_acceptor.accept(selectionKey);
//			}

		} catch (Throwable e) {
			acceptException(selectionKey, e);
		}
	}

	protected void acceptException(SelectionKey selectionKey, Throwable exception) {

		Object attachment = selectionKey.attachment();

		if (isTCPEndPoint(attachment)) {

			CloseUtil.close(((SocketChannel) attachment));
		}

		selectionKey.cancel();

		logger.error(exception.getMessage(), exception);
	}

	private boolean isTCPEndPoint(Object object) {
		return object != null && (object.getClass() == NioSocketChannel.class || object instanceof SocketChannel);
	}

	public String toString() {
		return "TCP:Selector@" + String.valueOf(selector.toString());
	}

}
