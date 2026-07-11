package com.net.convertix.ramossomar.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoUtil {

	private TextoUtil() {
	}

	public static String normalizar(String valor) {
		if (valor == null) {
			return "";
		}
		String semAcento = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "");
		return semAcento.toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
	}

	public static boolean isValorInvalidoTse(String valor) {
		if (valor == null) {
			return true;
		}
		String limpo = valor.trim();
		return limpo.isEmpty() || "-1".equals(limpo) || "#NULO#".equalsIgnoreCase(limpo);
	}

	public static String limparValorTse(String valor) {
		if (isValorInvalidoTse(valor)) {
			return null;
		}
		return valor.trim();
	}
}
