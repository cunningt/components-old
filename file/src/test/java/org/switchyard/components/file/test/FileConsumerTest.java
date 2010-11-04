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
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.BaseHandler;
import org.switchyard.Direction;
import org.switchyard.ExchangeEvent;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ServiceDomain;
import org.switchyard.components.file.FileComponent;
import org.switchyard.components.file.FileServiceConfig;
import org.switchyard.event.ExchangeInEvent;
import org.switchyard.event.ExchangeOutEvent;
import org.switchyard.internal.ServiceDomains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileConsumerTest {
	private final QName IN_ONLY_SERVICE = new QName("consumer-in-only");
	private final QName IN_OUT_SERVICE = new QName("consumer-in-out");
	private final static String DOMAIN_KEY = "FileConsumerTestDomain";
	private List<ExchangeEvent> outEvents = new LinkedList<ExchangeEvent>();
	
	private File _testRoot = new File("target/test/FileConsumerTest");
	
	private ServiceDomain _domain;
	private FileServiceContext _serviceContext;
	private FileComponent _fileComponent;
	
	@Before
	public void setUp() throws Exception {
		// clean up from last run		
		Util.delete(_testRoot);
		
		if (ServiceDomains.getDomain(DOMAIN_KEY) == null) {
			_domain = ServiceDomains.createDomain(DOMAIN_KEY);
		} else {
			_domain = ServiceDomains.getDomain(DOMAIN_KEY);
		}
		
		_serviceContext = new FileServiceContext();		
		_fileComponent = new FileComponent(DOMAIN_KEY);
		_fileComponent.init();
		_testRoot.mkdirs();
		
//		File test = new File("components-file/src/test/resources/message.txt");
		File test = new File("src/test/resources/message.txt");

		Util.copyFile(test, new File(_testRoot.getAbsoluteFile() + File.separator + "file.request"));
		outEvents.clear();
	}
	
	@After
	public void tearDown() throws Exception {
		_fileComponent.destroy();		
	}

	@Test
	public void testInOnly() throws Exception {
        ExchangeHandler consumer = new BaseHandler() {
        	public void exchangeIn(ExchangeInEvent event) {
            	System.out.println("Handler received event " + event.toString());        		
            	outEvents.add(event);
        	}

        	public void exchangeOut(ExchangeInEvent event) {
            	System.out.println("Handler received event " + event.toString());
            	Assert.fail("Should not hit the exchangeOut method in this Handler");
        	}
        };
        
		_serviceContext.setPattern(ExchangePattern.IN_ONLY);
		_serviceContext.setFilter(".*.request");
		_serviceContext.setTargetPath(_testRoot.getAbsolutePath());
		FileServiceConfig config = new FileServiceConfig(IN_ONLY_SERVICE, ExchangePattern.IN_ONLY, _serviceContext);
		config.setExchangeHandler(consumer);
		_fileComponent.deploy(config, Direction.RECEIVE);
	
		File[] requests = _testRoot.listFiles(Util.createFilter(".*.request"));
		Assert.assertTrue(requests.length == 1);

        _fileComponent.start(IN_ONLY_SERVICE);
		Thread.sleep(1000);		
		_fileComponent.stop(IN_ONLY_SERVICE);
		
		requests = _testRoot.listFiles(Util.createFilter(".*.request"));
		Assert.assertTrue(requests.length == 0);

		Assert.assertTrue(outEvents.size() == 1);		

	}
	
	@Test
	public void testInOut() throws Exception {
        ExchangeHandler consumer = new BaseHandler() {
        	public void exchangeIn(ExchangeInEvent event) {
            	System.out.println("Handler received event " + event.toString());        		
            	outEvents.add(event);
        	}
        	
            public void exchangeOut(ExchangeOutEvent event) {
            	System.out.println("Handler received event " + event.toString());
            	Assert.fail("Should not hit the exchangeOut method in this Handler");
            }
        };
		_serviceContext.setPattern(ExchangePattern.IN_OUT);
		_serviceContext.setFilter(".*.request");
		_serviceContext.setTargetPath(_testRoot.getAbsolutePath());
		
		FileServiceConfig config = new FileServiceConfig(IN_OUT_SERVICE, ExchangePattern.IN_OUT, _serviceContext);
		config.setExchangeHandler(consumer);
		_fileComponent.deploy(config, Direction.RECEIVE);
		
		File[] requests = _testRoot.listFiles(Util.createFilter(".*request"));
		Assert.assertTrue(requests.length == 1);

		_fileComponent.start(IN_OUT_SERVICE);
		Thread.sleep(1000);
		_fileComponent.stop(IN_OUT_SERVICE);
		
		// verify that the request message was removed
		requests = _testRoot.listFiles(Util.createFilter(".*request"));
		Assert.assertTrue(requests.length == 0);
		Assert.assertTrue(outEvents.size() == 1);		
	}	
}