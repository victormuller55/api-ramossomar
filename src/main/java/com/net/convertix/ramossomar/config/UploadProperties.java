package com.net.convertix.ramossomar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

	private String diretorio = "uploads";
	private String urlBase = "/uploads";
	private long tamanhoMaximoMb = 5;
	private float qualidadeCompressao = 0.75f;
	private int larguraMaximaPx = 1920;

	public String getDiretorio() {
		return diretorio;
	}

	public void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}

	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public long getTamanhoMaximoMb() {
		return tamanhoMaximoMb;
	}

	public void setTamanhoMaximoMb(long tamanhoMaximoMb) {
		this.tamanhoMaximoMb = tamanhoMaximoMb;
	}

	public float getQualidadeCompressao() {
		return qualidadeCompressao;
	}

	public void setQualidadeCompressao(float qualidadeCompressao) {
		this.qualidadeCompressao = qualidadeCompressao;
	}

	public int getLarguraMaximaPx() {
		return larguraMaximaPx;
	}

	public void setLarguraMaximaPx(int larguraMaximaPx) {
		this.larguraMaximaPx = larguraMaximaPx;
	}

	public long getTamanhoMaximoBytes() {
		return tamanhoMaximoMb * 1024 * 1024;
	}
}
