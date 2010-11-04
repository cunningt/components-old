package org.switchyard.components.file.test;

/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.File;

import javax.xml.namespace.QName;

import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Direction;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.ServiceDomain;
import org.switchyard.components.file.FileComponent;
import org.switchyard.components.file.FileServiceConfig;
import org.switchyard.event.ExchangeOutEvent;
import org.switchyard.internal.BaseContext;
import org.switchyard.internal.ServiceDomains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FileProviderTest {
	private final QName IN_ONLY_SERVICE = new QName("provider-in-only");
	private final QName IN_OUT_SERVICE = new QName("provider-in-out");
	private final static String DOMAIN_KEY = "FileProviderTestDomain";
	private File _testRoot = new File("target/test/FileProviderTest");
	
	private FileServiceContext _serviceContext;
	private FileComponent _fileComponent;
	private Context _context;
	private ServiceDomain _domain;

	
	@Before
	public void setUp() throws Exception {
		// clean up from last run
		Util.delete(_testRoot);
		
		if (ServiceDomains.getDomain(DOMAIN_KEY) == null) {
			_domain = ServiceDomains.createDomain(DOMAIN_KEY);
		} else {
			_domain = ServiceDomains.getDomain(DOMAIN_KEY);
		}
		
		_context = new BaseContext();
		_serviceContext = new FileServiceContext();

		_fileComponent = new FileComponent(DOMAIN_KEY);
		_fileComponent.init();

		_testRoot.mkdirs();
	}
	
	@After
	public void tearDown() throws Exception {
		_fileComponent.destroy();
	}

	@Test
	public void testInOnly() throws Exception {
		_serviceContext.setPattern(ExchangePattern.IN_ONLY);
		_serviceContext.setTargetPath(_testRoot.getAbsolutePath());
		FileServiceConfig config = new FileServiceConfig(IN_ONLY_SERVICE, ExchangePattern.IN_ONLY, _serviceContext);
		_fileComponent.deploy(config, Direction.SEND);
		_fileComponent.start(IN_ONLY_SERVICE);
		invokeService(IN_ONLY_SERVICE, ExchangePattern.IN_ONLY, "InOnly Test");
		
		// wait for the file spooler to pick up the message and write the file
		Thread.sleep(500);
		
		_fileComponent.stop(IN_ONLY_SERVICE);
		
		File[] requests = _testRoot.listFiles(Util.createFilter(".*request"));
		Assert.assertTrue(requests.length == 1);
	}
	
	@Test
	public void testInOut() throws Exception {
		
		_serviceContext.setPattern(ExchangePattern.IN_OUT);
		_serviceContext.setTargetPath(_testRoot.getAbsolutePath());
		FileServiceConfig config = new FileServiceConfig(IN_OUT_SERVICE, ExchangePattern.IN_OUT, _serviceContext);
		_domain.registerService(IN_OUT_SERVICE, _fileComponent.getFileSpool());
		_fileComponent.deploy(config, Direction.SEND);
		_fileComponent.start(IN_OUT_SERVICE);
		
		invokeService(IN_OUT_SERVICE, ExchangePattern.IN_OUT, "InOut Test");
		
		// wait for the file spooler to pick up the message and write the file
		Thread.sleep(500);
		
		_fileComponent.stop(IN_OUT_SERVICE);
		
		// verify that the request message was created
		File[] requests = _testRoot.listFiles(Util.createFilter(".*request"));
		Assert.assertTrue(requests.length == 1);
		
		// verify that the reply was received by the consuming channel
		//Assert.assertTrue(_replyHandler._receiveCount == 1);
	}
	
	private void invokeService(QName service, ExchangePattern pattern, String content) {
		Exchange exchange = _domain.createExchange(service, pattern);
		MessageBuilder mb = MessageBuilder.newInstance();
		Message message = mb.buildMessage();
		message.setContent(content);
		exchange.sendIn(message);
	}
	

	class ReplyHandler extends BaseHandler {
		int _receiveCount;
		public void exchangeOut(ExchangeOutEvent event) {
			++_receiveCount;
		}
	}
}

