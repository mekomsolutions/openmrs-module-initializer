package org.openmrs.module.initializer.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class IgnoreBOMInputStream extends InputStream {
	
	private static final byte[] UTF8_BOM = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
	
	private static final int BOM_LENGTH = UTF8_BOM.length;
	
	private final InputStream delegate;
	
	public IgnoreBOMInputStream(InputStream delegate) throws IOException {
		byte[] possibleBOM = new byte[BOM_LENGTH];
		
		PushbackInputStream is = new PushbackInputStream(delegate, BOM_LENGTH);
		int chars = is.read(possibleBOM);
		
		if (chars != 3 || possibleBOM != UTF8_BOM) {
			if (chars > 0) {
				is.unread(possibleBOM, 0, chars);
			}
		}
		
		this.delegate = is;
	}
	
	@Override
	public int read() throws IOException {
		return delegate.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return delegate.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return delegate.read(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException {
		return delegate.skip(n);
	}
	
	@Override
	public int available() throws IOException {
		return delegate.available();
	}
	
	@Override
	public void close() throws IOException {
		delegate.close();
	}
	
	@Override
	public boolean markSupported() {
		return delegate.markSupported();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		delegate.mark(readlimit);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		delegate.reset();
	}
}
