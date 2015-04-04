package net.roseboy.jeasy.upload;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 文件下载类
 * @author roseboy.net
 *
 */
public class DownloadFile {
	private InputStream inputstream;
	private String filename;

	public InputStream getInputstream() {
		return inputstream;
	}

	public void setInputstream(InputStream inputstream) {
		this.inputstream = inputstream;
	}

	public void setInputstream(String filename) {
		InputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.inputstream = fis;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public DownloadFile(InputStream inputstream, String filename) {
		this.inputstream = inputstream;
		this.filename = filename;
	}

	public DownloadFile() {

	}

}
