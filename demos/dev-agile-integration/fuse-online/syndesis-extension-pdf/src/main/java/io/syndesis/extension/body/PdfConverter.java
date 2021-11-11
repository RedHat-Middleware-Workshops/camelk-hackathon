package io.syndesis.extension.body;


public class PdfConverter {


	private String content = "unmapped";
	private String filename = "unmapped";

	public Object getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}