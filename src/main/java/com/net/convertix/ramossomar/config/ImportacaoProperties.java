package com.net.convertix.ramossomar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.importacao")
public class ImportacaoProperties {

	private String uf = "GO";
	private int batchSize = 500;
	private final Ibge ibge = new Ibge();
	private final Tse tse = new Tse();

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public Ibge getIbge() {
		return ibge;
	}

	public Tse getTse() {
		return tse;
	}

	public static class Ibge {
		private String municipiosUrl = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{uf}/municipios";

		public String getMunicipiosUrl() {
			return municipiosUrl;
		}

		public void setMunicipiosUrl(String municipiosUrl) {
			this.municipiosUrl = municipiosUrl;
		}

		public String resolverUrl(String uf) {
			return municipiosUrl.replace("{uf}", uf);
		}
	}

	public static class Tse {
		private String locaisVotacaoUrl = "https://cdn.tse.jus.br/estatistica/sead/odsele/eleitorado_locais_votacao/eleitorado_local_votacao_2024.zip";

		public String getLocaisVotacaoUrl() {
			return locaisVotacaoUrl;
		}

		public void setLocaisVotacaoUrl(String locaisVotacaoUrl) {
			this.locaisVotacaoUrl = locaisVotacaoUrl;
		}
	}
}
