package com.net.convertix.ramossomar.config;

import java.awt.Desktop;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SwaggerBrowserOpener {

	private static final Logger log = LoggerFactory.getLogger(SwaggerBrowserOpener.class);

	@Value("${server.port:8080}")
	private int porta;

	@Value("${app.swagger.abrir-navegador:true}")
	private boolean abrirNavegador;

	@EventListener(ApplicationReadyEvent.class)
	public void abrirSwagger() {
		if (!abrirNavegador) {
			return;
		}

		String url = "http://localhost:" + porta + "/swagger-ui/index.html";

		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				abrirComComandoSistema(url);
			}
			log.info("Swagger UI aberto em: {}", url);
		} catch (Exception ex) {
			try {
				abrirComComandoSistema(url);
				log.info("Swagger UI aberto em: {}", url);
			} catch (Exception fallbackEx) {
				log.warn("Não foi possível abrir o Swagger automaticamente. Acesse: {}", url);
			}
		}
	}

	private void abrirComComandoSistema(String url) throws Exception {
		String sistema = System.getProperty("os.name").toLowerCase();
		ProcessBuilder processBuilder;

		if (sistema.contains("win")) {
			processBuilder = new ProcessBuilder("cmd", "/c", "start", url);
		} else if (sistema.contains("mac")) {
			processBuilder = new ProcessBuilder("open", url);
		} else {
			processBuilder = new ProcessBuilder("xdg-open", url);
		}

		processBuilder.start();
	}
}
