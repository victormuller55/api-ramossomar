package com.net.convertix.ramossomar.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.net.convertix.ramossomar.dto.response.ApoiadorResponse;
import com.net.convertix.ramossomar.exception.NegocioException;
import com.net.convertix.ramossomar.model.enums.IntencaoVoto;
import com.net.convertix.ramossomar.security.SegurancaUtil;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {

	private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final ApoiadorService apoiadorService;
	private final SegurancaUtil segurancaUtil;

	public RelatorioService(ApoiadorService apoiadorService, SegurancaUtil segurancaUtil) {
		this.apoiadorService = apoiadorService;
		this.segurancaUtil = segurancaUtil;
	}

	@Transactional(readOnly = true)
	public byte[] exportarApoiadores(
			String formato,
			String nome,
			String cidade,
			UUID idLider,
			IntencaoVoto intencaoVoto,
			String cpf
	) {
		segurancaUtil.exigirAdmin();

		List<ApoiadorResponse> apoiadores = apoiadorService.listar(nome, cidade, idLider, intencaoVoto, cpf);

		if ("pdf".equalsIgnoreCase(formato)) {
			return gerarPdf(apoiadores);
		}
		if ("xlsx".equalsIgnoreCase(formato)) {
			return gerarXlsx(apoiadores);
		}
		throw new NegocioException("Formato inválido. Use xlsx ou pdf.");
	}

	private byte[] gerarXlsx(List<ApoiadorResponse> apoiadores) {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Cadastrados");

			String[] headers = {
					"Nome", "CPF", "Data nascimento", "Telefone", "WhatsApp",
					"CEP", "Endereço", "Número", "Complemento", "Bairro", "Cidade",
					"Local votação", "Intenção voto", "Líder", "Observações", "Data cadastro"
			};

			CellStyle headerStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			int rowIdx = 1;
			for (ApoiadorResponse a : apoiadores) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(texto(a.getNome()));
				row.createCell(1).setCellValue(texto(a.getCpf()));
				row.createCell(2).setCellValue(a.getData_nascimento() != null ? a.getData_nascimento().format(DATA) : "");
				row.createCell(3).setCellValue(texto(a.getTelefone()));
				row.createCell(4).setCellValue(texto(a.getWhatsapp()));
				row.createCell(5).setCellValue(texto(a.getCep()));
				row.createCell(6).setCellValue(texto(a.getEndereco()));
				row.createCell(7).setCellValue(texto(a.getNumero()));
				row.createCell(8).setCellValue(texto(a.getComplemento()));
				row.createCell(9).setCellValue(texto(a.getBairro()));
				row.createCell(10).setCellValue(texto(a.getCidade()));
				row.createCell(11).setCellValue(texto(a.getLocal_votacao()));
				row.createCell(12).setCellValue(a.getIntencao_voto() != null ? a.getIntencao_voto().name() : "");
				row.createCell(13).setCellValue(texto(a.getNome_lider()));
				row.createCell(14).setCellValue(texto(a.getObservacoes()));
				row.createCell(15).setCellValue(
						a.getData_criacao() != null ? a.getData_criacao().format(DATA_HORA) : ""
				);
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (Exception e) {
			throw new NegocioException("Não foi possível gerar o arquivo XLSX");
		}
	}

	private byte[] gerarPdf(List<ApoiadorResponse> apoiadores) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
			PdfWriter.getInstance(document, out);
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(22, 72, 67));
			Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(75, 85, 99));
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
			Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.DARK_GRAY);

			document.add(new Paragraph("Relatório de Cadastrados", titleFont));
			document.add(new Paragraph(
					"Gerado em " + LocalDateTime.now().format(DATA_HORA)
							+ " · " + apoiadores.size() + " registro(s)",
					subtitleFont
			));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(8);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{2.2f, 1.4f, 1.3f, 1.4f, 1.6f, 1.4f, 1.4f, 1.6f});

			String[] headers = {
					"Nome", "CPF", "Telefone", "Cidade", "Intenção", "Líder", "Local votação", "Cadastro"
			};
			Color headerBg = new Color(29, 87, 81);
			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
				cell.setBackgroundColor(headerBg);
				cell.setPadding(6);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			for (ApoiadorResponse a : apoiadores) {
				table.addCell(celula(texto(a.getNome()), cellFont));
				table.addCell(celula(texto(a.getCpf()), cellFont));
				table.addCell(celula(primeiroNaoVazio(a.getWhatsapp(), a.getTelefone()), cellFont));
				table.addCell(celula(texto(a.getCidade()), cellFont));
				table.addCell(celula(a.getIntencao_voto() != null ? a.getIntencao_voto().name() : "", cellFont));
				table.addCell(celula(texto(a.getNome_lider()), cellFont));
				table.addCell(celula(texto(a.getLocal_votacao()), cellFont));
				table.addCell(celula(
						a.getData_criacao() != null ? a.getData_criacao().format(DATA) : "",
						cellFont
				));
			}

			document.add(table);
			document.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new NegocioException("Não foi possível gerar o arquivo PDF");
		}
	}

	private PdfPCell celula(String texto, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(texto, font));
		cell.setPadding(5);
		return cell;
	}

	private String texto(String value) {
		return value == null ? "" : value;
	}

	private String primeiroNaoVazio(String a, String b) {
		if (a != null && !a.isBlank()) return a;
		return texto(b);
	}
}
