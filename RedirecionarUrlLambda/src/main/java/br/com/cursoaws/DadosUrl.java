package br.com.cursoaws;

public class DadosUrl {
	private String originalUrl;
	private long expiracaoTempo;

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public long getExpiracaoTempo() {
		return expiracaoTempo;
	}

	public void setExpiracaoTempo(long expiracaoTempo) {
		this.expiracaoTempo = expiracaoTempo;
	}

	public DadosUrl(String originalUrl, long expiracaoTempo) {
		super();
		this.originalUrl = originalUrl;
		this.expiracaoTempo = expiracaoTempo;
	}
	
	public DadosUrl() {
		super();
	}
}
