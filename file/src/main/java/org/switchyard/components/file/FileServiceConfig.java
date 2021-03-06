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

package org.switchyard.components.file;

import java.io.File;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.Context;

public class FileServiceConfig {

	public static final String DOMAIN_KEY = "file.domain.name";
	public static final String PATH_KEY = "file.service.path";
	public static final String FILTER_KEY = "file.service.filter";
	public static final String HANDLER_KEY = "file.service.handler";
	
	private Context _context;
	private QName _serviceName;
	private ExchangePattern _pattern;
	
	public FileServiceConfig(QName serviceName, ExchangePattern pattern, 
			Context context) {
		_context = context;
		_serviceName = serviceName;
		_pattern = pattern;
	}
	
	public ExchangeHandler getExchangeHandler() {
		return ( ExchangeHandler) _context.getProperty(HANDLER_KEY);
	}
	
	public void setExchangeHandler(ExchangeHandler handler) {
		_context.setProperty(HANDLER_KEY, handler);
	}
	
	public File getTargetDir() {
		return new File((String)_context.getProperty(PATH_KEY));
	}
	
	public String getFilter() {
		return (String)_context.getProperty(FILTER_KEY);
	}
	
	public String getDomainName() {
		return (String)_context.getProperty(DOMAIN_KEY);
	}
		
	public void setDomainName(String domainName) {
		_context.setProperty(DOMAIN_KEY, domainName);
	}
	
	public ExchangePattern getExchangePattern() {
		return _pattern;
	}
	
	public Context getContext() {
		return _context;
	}
	
	public QName getServiceName() {
		return _serviceName;
	}

	public void setServiceName(QName serviceName) {
		_serviceName = serviceName;
	}
}
