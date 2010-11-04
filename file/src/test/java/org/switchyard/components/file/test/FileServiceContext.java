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

package org.switchyard.components.file.test;

import javax.xml.namespace.QName;

import org.switchyard.ExchangePattern;
import org.switchyard.components.file.FileServiceConfig;
import org.switchyard.internal.BaseContext;

public class FileServiceContext extends BaseContext {

	private ExchangePattern _pattern;
	private QName _serviceRef;
	
	public FileServiceContext() {
	}

	public ExchangePattern getPattern() {
		return _pattern;
	}
	
	public QName getServiceReference() {
		return _serviceRef;
	}
	
	public void setPattern(ExchangePattern pattern) {
		_pattern = pattern;
	}
	
	public void setServiceReference(QName serviceRef) {
		_serviceRef = serviceRef;
	}
	
	public void setTargetPath(String path) {
		setProperty(FileServiceConfig.PATH_KEY, path);
	}
	
	public void setFilter(String filter) {
		setProperty(FileServiceConfig.FILTER_KEY, filter);
	}
}
