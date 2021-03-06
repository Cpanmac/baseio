/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.balance.reverse;

import com.generallycloud.baseio.common.Logger;
import com.generallycloud.baseio.component.SocketSession;
import com.generallycloud.baseio.protocol.ReadFuture;

/**
 * @author wangkai
 *
 */
public class BalanceReverseLogger {

	public void logBroadcast(SocketSession session, ReadFuture future, Logger logger) {
		logger.info("broadcast msg: F:{}, msg:{}", session.getRemoteSocketAddress(), future);
	}

	public void logPushLost(SocketSession session, ReadFuture future, Logger logger) {
		logger.info("connection lost: F:{}, msg:{}", session.getRemoteSocketAddress(), future);
	}

	public void logPush(SocketSession session, SocketSession response, ReadFuture future, Logger logger) {
		logger.info("reply msg: F:[{}], T:[{}], msg: {}",
				new Object[] { session.getRemoteSocketAddress(), response.getRemoteSocketAddress(), future });
	}
	
	public void logPush(SocketSession response, ReadFuture future, Logger logger) {
		logger.info("reply msg: T:[{}], msg: {}", response.getRemoteSocketAddress(), future );
	}

}
