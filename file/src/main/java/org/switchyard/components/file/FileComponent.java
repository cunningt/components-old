package org.switchyard.components.file;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.switchyard.Context;
import org.switchyard.Direction;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.internal.ServiceRegistration;

public class FileComponent {
	
	private static final String SERVICE_TYPE = "file";
	
	private String _domainName;
	private ServiceDomain _domain;
	private Service _service;
	private Context _context;
	private ServiceRegistration _sr;
	private ScheduledExecutorService _scheduler;
	private FileSpool _spooler;
	private Map<QName, Future<?>> _pollers = 
		new HashMap<QName, Future<?>>();
	private Map<QName, FileServiceConfig> _consumedServices = 
		new HashMap<QName, FileServiceConfig>();
	private Map<QName, FileServiceConfig> _providedServices = 
		new HashMap<QName, FileServiceConfig>();

	public FileComponent(String domainName) {
		_scheduler = Executors.newScheduledThreadPool(2);
		_domain = ServiceDomains.getDomain(domainName);
		_domainName = domainName;
	}

	public void init() {
		_spooler = new FileSpool();
	}

	public String getServiceType() {
		return SERVICE_TYPE;
	}

	public void destroy() {
	}

	public void deploy(FileServiceConfig config, Direction direction) {		
		config.setDomainName(_domainName);
		_spooler.addService(config);
		if (direction == Direction.RECEIVE) {
			_consumedServices.put(config.getServiceName(), config);
		} else if (direction == Direction.SEND) {
			_providedServices.put(config.getServiceName(), config);
		}
	}

	
	public void start(QName service) {
		if (_consumedServices.containsKey(service)) {
			createPoller(_consumedServices.get(service));
		}
		else {
			_service = _domain.registerService(service, _spooler);
			FileServiceConfig config = _providedServices.get(service);
		}
	}	
	
	public ExchangeHandler getFileSpool() {
		return _spooler;
	}

	public void stop(QName service) {
		if (_providedServices.containsKey(service)) {
			if (_service != null) { 
				_service.unregister();	
			}
		}
		
		if (_pollers.containsKey(service)) {
			_pollers.get(service).cancel(true);
		}
	}

	public void undeploy(QName service) {
		if (_consumedServices.containsKey(service)) {
			_consumedServices.remove(service);
		}
		else {
			_spooler.removeService(_providedServices.remove(service));
		}
	}
	
	private void createPoller(FileServiceConfig config) {
		FilePoll consumer = new FilePoll(config);
		Future<?> scheduledConsumer = _scheduler.scheduleAtFixedRate(
				consumer, 0, 3, TimeUnit.SECONDS);
		_pollers.put(config.getServiceName(), scheduledConsumer);
	}	
}
