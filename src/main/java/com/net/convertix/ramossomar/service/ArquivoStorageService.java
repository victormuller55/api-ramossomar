package com.net.convertix.ramossomar.service;

import com.net.convertix.ramossomar.config.UploadProperties;
import com.net.convertix.ramossomar.exception.NegocioException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArquivoStorageService {

	private static final Logger log = LoggerFactory.getLogger(ArquivoStorageService.class);
	private static final Set<String> EXTENSOES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "webp", "gif");
	private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
			"image/jpeg",
			"image/png",
			"image/webp",
			"image/gif"
	);

	private final UploadProperties uploadProperties;
	private final Path diretorioRaiz;

	public ArquivoStorageService(UploadProperties uploadProperties) {
		this.uploadProperties = uploadProperties;
		this.diretorioRaiz = Paths.get(uploadProperties.getDiretorio()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(diretorioRaiz);
		} catch (IOException ex) {
			throw new IllegalStateException("Não foi possível criar o diretório de uploads: " + diretorioRaiz, ex);
		}
	}

	public String salvarImagem(MultipartFile arquivo, String subpasta) {
		validarImagem(arquivo);

		String nomeArquivo = UUID.randomUUID() + ".jpg";
		Path pastaDestino = diretorioRaiz.resolve(subpasta).normalize();

		try {
			Files.createDirectories(pastaDestino);
			Path destino = pastaDestino.resolve(nomeArquivo).normalize();
			if (!destino.startsWith(diretorioRaiz)) {
				throw new NegocioException("Caminho de arquivo inválido");
			}

			BufferedImage original;
			try (InputStream inputStream = arquivo.getInputStream()) {
				original = ImageIO.read(inputStream);
			}

			if (original == null) {
				throw new NegocioException("Arquivo de imagem inválido ou corrompido");
			}

			BufferedImage preparada = prepararParaJpeg(original);
			BufferedImage redimensionada = redimensionarSeNecessario(preparada);
			comprimirESalvarJpeg(redimensionada, destino);

			String caminhoRelativo = uploadProperties.getUrlBase() + "/" + subpasta + "/" + nomeArquivo;
			log.info(
					"Imagem comprimida e salva em {} (original={} bytes)",
					caminhoRelativo,
					arquivo.getSize()
			);
			return caminhoRelativo.replace("\\", "/");
		} catch (NegocioException ex) {
			throw ex;
		} catch (IOException ex) {
			log.error("Falha ao comprimir/salvar imagem", ex);
			throw new NegocioException("Não foi possível processar a imagem no servidor");
		}
	}

	public void excluirSeExistir(String caminhoRelativo) {
		if (caminhoRelativo == null || caminhoRelativo.isBlank()) {
			return;
		}

		String prefixo = uploadProperties.getUrlBase() + "/";
		if (!caminhoRelativo.startsWith(prefixo)) {
			return;
		}

		String relativo = caminhoRelativo.substring(prefixo.length());
		Path arquivo = diretorioRaiz.resolve(relativo).normalize();
		if (!arquivo.startsWith(diretorioRaiz)) {
			return;
		}

		try {
			Files.deleteIfExists(arquivo);
		} catch (IOException ex) {
			log.warn("Não foi possível excluir o arquivo {}: {}", arquivo, ex.getMessage());
		}
	}

	private BufferedImage prepararParaJpeg(BufferedImage original) {
		if (original.getType() == BufferedImage.TYPE_INT_RGB) {
			return original;
		}

		BufferedImage rgb = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = rgb.createGraphics();
		try {
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
			graphics.drawImage(original, 0, 0, null);
		} finally {
			graphics.dispose();
		}
		return rgb;
	}

	private BufferedImage redimensionarSeNecessario(BufferedImage original) {
		int maxLado = uploadProperties.getLarguraMaximaPx();
		int largura = original.getWidth();
		int altura = original.getHeight();

		if (largura <= maxLado && altura <= maxLado) {
			return original;
		}

		double escala = Math.min((double) maxLado / largura, (double) maxLado / altura);
		int novaLargura = Math.max(1, (int) Math.round(largura * escala));
		int novaAltura = Math.max(1, (int) Math.round(altura * escala));

		Image scaled = original.getScaledInstance(novaLargura, novaAltura, Image.SCALE_SMOOTH);
		BufferedImage redimensionada = new BufferedImage(novaLargura, novaAltura, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = redimensionada.createGraphics();
		try {
			graphics.drawImage(scaled, 0, 0, null);
		} finally {
			graphics.dispose();
		}
		return redimensionada;
	}

	private void comprimirESalvarJpeg(BufferedImage imagem, Path destino) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
		if (!writers.hasNext()) {
			throw new NegocioException("Compressão JPEG não disponível neste servidor");
		}

		ImageWriter writer = writers.next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		if (param.canWriteCompressed()) {
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(uploadProperties.getQualidadeCompressao());
		}

		try (OutputStream outputStream = Files.newOutputStream(destino);
				ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
			writer.setOutput(imageOutputStream);
			writer.write(null, new IIOImage(imagem, null, null), param);
		} finally {
			writer.dispose();
		}
	}

	private void validarImagem(MultipartFile arquivo) {
		if (arquivo == null || arquivo.isEmpty()) {
			throw new NegocioException("Envie um arquivo de imagem");
		}

		if (arquivo.getSize() > uploadProperties.getTamanhoMaximoBytes()) {
			throw new NegocioException(
					"A imagem deve ter no máximo " + uploadProperties.getTamanhoMaximoMb() + " MB"
			);
		}

		String contentType = arquivo.getContentType();
		if (contentType == null || !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
			throw new NegocioException("Apenas imagens são permitidas. Use JPG, PNG, WEBP ou GIF");
		}

		String extensao = extrairExtensao(arquivo.getOriginalFilename());
		if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
			throw new NegocioException("Apenas imagens são permitidas. Use JPG, PNG, WEBP ou GIF");
		}
	}

	private String extrairExtensao(String nomeOriginal) {
		if (nomeOriginal == null || !nomeOriginal.contains(".")) {
			throw new NegocioException("Nome do arquivo de imagem inválido");
		}
		String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT).trim();
		if (extensao.isBlank()) {
			throw new NegocioException("Extensão do arquivo de imagem inválida");
		}
		return extensao;
	}
}
