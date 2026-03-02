package br.com.srcsoftware.todoscontratodos.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import br.com.srcsoftware.todoscontratodos.model.dto.AtletaDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.ConfrontoDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaDTO;

@Service
public class ExcelExportService {

	/*public byte[] gerarPlanilhaTorneio(List<AtletaDTO> atletas, List<EtapaDTO> etapas) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {

			// 1. Aba de Ranking
			Sheet sheetRanking = workbook.createSheet("Ranking e Saldo");
			createHeader(sheetRanking, "Atleta", "Saldo Atual (Fórmula)");

			for (int i = 0; i < atletas.size(); i++) {
				Row row = sheetRanking.createRow(i + 1);
				row.createCell(0).setCellValue(atletas.get(i).getNome());

				// Exemplo de fórmula Sênior: SUMIF nas etapas para calcular pontos
				// Aqui simplificaremos com uma célula para input ou totalizador
				row.createCell(1).setCellValue(0);
			}

			// 2. Aba de Confrontos
			Sheet sheetJogos = workbook.createSheet("Confrontos");
			String[] colunas = { "Etapa", "Dupla 1", "Placar 1", "vs", "Placar 2", "Dupla 2" };
			createHeader(sheetJogos, colunas);

			int rowIdx = 1;
			for (EtapaDTO etapa : etapas) {
				for (ConfrontoDTO c : etapa.getConfrontos()) {
					Row row = sheetJogos.createRow(rowIdx++);
					row.createCell(0).setCellValue("Etapa " + etapa.getNomeEtapa());
					row.createCell(1).setCellValue(c.getDupla1().toString());
					row.createCell(2).setCellValue(0); // Placar 1
					row.createCell(3).setCellValue("x");
					row.createCell(4).setCellValue(0); // Placar 2
					row.createCell(5).setCellValue(c.getDupla2().toString());
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
	}*/

	private void createHeader(Sheet sheet, CellStyle headerStyle, String... titles) {		
		Row header = sheet.createRow(0);
		for (int i = 0; i < titles.length; i++) {
			header.createCell(i).setCellValue(titles[i]);
		}
	}

	/*public byte[] gerarPlanilhaTorneioComFormulas(List<AtletaDTO> atletas, List<EtapaDTO> etapas) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			CellStyle inputStyle = workbook.createCellStyle();
			inputStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
			inputStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			inputStyle.setBorderBottom(BorderStyle.THIN);
			// Aplique este estilo nas células de P1 (coluna 3) e P2 (coluna 5)

			Sheet sheetRanking = workbook.createSheet("Ranking");
			Sheet sheetJogos = workbook.createSheet("Confrontos");

			// 1. Cabeçalhos
			createHeader(sheetRanking, "Atleta", "Saldo Total");
			createHeader(sheetJogos, "Etapa", "Atleta 1 (D1)", "Atleta 2 (D1)", "P1", "x", "P2", "Atleta 1 (D2)", "Atleta 2 (D2)");

			// 2. Preencher Confrontos (Aba "Confrontos")
			int rowIdx = 1;
			for (EtapaDTO etapa : etapas) {
				for (ConfrontoDTO c : etapa.getConfrontos()) {
					Row row = sheetJogos.createRow(rowIdx++);
					row.createCell(0).setCellValue("Etapa " + etapa.getNomeEtapa());
					row.createCell(1).setCellValue(c.getDupla1().getAtleta1().getNome());
					row.createCell(2).setCellValue(c.getDupla1().getAtleta2().getNome());

					// row.createCell(3).setCellValue(0); // Coluna D (P1)
					// Na hora de criar as células de placar (P1 e P2):
					Cell cellP1 = row.createCell(3);
					cellP1.setCellStyle(inputStyle); // Aplica o estilo visual de campo editável
					// cellP1.setCellValue(0); // Coluna D (P1)

					row.createCell(4).setCellValue("x"); // Coluna E

					// row.createCell(5).setCellValue(0); // Coluna F (P2)
					// Na hora de criar as células de placar (P1 e P2):
					Cell cellP2 = row.createCell(5);
					cellP2.setCellStyle(inputStyle);
					// cellP2.setCellValue(0); // Coluna F (P2)

					row.createCell(6).setCellValue(c.getDupla2().getAtleta1().getNome());
					row.createCell(7).setCellValue(c.getDupla2().getAtleta2().getNome());
				}

				// 2. A LINHA EM BRANCO: Simplesmente incrementa o índice
				// Isso deixa uma linha física vazia no Excel antes da próxima etapa
				rowIdx++;
			}

			// 3. Preencher Ranking com Fórmulas (Aba "Ranking")
			// int totalJogos = etapas.stream().mapToInt(e ->
			// e.getConfrontos().size()).sum();

			int limiteExcel = rowIdx; // O valor exato da última linha

			for (int i = 0; i < atletas.size(); i++) {
				Row row = sheetRanking.createRow(i + 1);
				String nomeAtleta = atletas.get(i).getNome();
				row.createCell(0).setCellValue(nomeAtleta);

				// Definição dos Ranges (Ex: Confrontos!$B$2:$B$120)
				String rD1A1 = "Confrontos!$B$2:$B$" + limiteExcel;
				String rD1A2 = "Confrontos!$C$2:$C$" + limiteExcel;
				String rD2A1 = "Confrontos!$G$2:$G$" + limiteExcel;
				String rD2A2 = "Confrontos!$H$2:$H$" + limiteExcel;
				String rP1 = "Confrontos!$D$2:$D$" + limiteExcel;
				String rP2 = "Confrontos!$F$2:$F$" + limiteExcel;

				// Usamos String.format para evitar erros de concatenação "Left-hand side"
				// A lógica: (Atleta na D1) * (Placar preenchido) * (Cálculo de pontos)

				String templateFormula = """
						SUMPRODUCT(((%s="%s")+(%s="%s"))*(%s<>"")*(%s<>"")*((%s>%s)*3+(%s=%s)*1))
						""";

				String f1 = String.format(templateFormula, rD1A1, nomeAtleta, rD1A2, nomeAtleta, rP1, rP2, rP1, rP2, rP1, rP2);
				String f2 = String.format(templateFormula, rD2A1, nomeAtleta, rD2A2, nomeAtleta, rP1, rP2, rP2, rP1, rP2, rP1);

				Cell cellSaldo = row.createCell(1);
				cellSaldo.setCellFormula(f1 + "+" + f2);
			}

			// Forçar o Excel a recalcular as fórmulas ao abrir
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			sheetRanking.setForceFormulaRecalculation(true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
	}*/

	public byte[] gerarPlanilhaTorneioComFormulasNova(List<AtletaDTO> atletas, List<EtapaDTO> etapas) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			CellStyle inputStyle = createInputStyle(workbook);
			Sheet sheetRanking = workbook.createSheet("Ranking");
			Sheet sheetJogos = workbook.createSheet("Confrontos");

			CellStyle headerStyle = createHeaderStyle(workbook);
			
			// 1. PRIMEIRA PASSADA: Preencher os Jogos para descobrir o rowIdx final
			createHeader(sheetJogos, headerStyle, "Etapa", "Atleta 1 (D1)", "Atleta 2 (D1)", "P1", "x", "P2", "Atleta 1 (D2)", "Atleta 2 (D2)");

			int rowIdx = 1;
			for (EtapaDTO etapa : etapas) {
				// Linha de Título da Etapa (Separador visual)
				Row rowTitulo = sheetJogos.createRow(rowIdx++);
				Cell cellTitulo = rowTitulo.createCell(0);
				cellTitulo.setCellValue(">>> ETAPA " + etapa.getNomeEtapa());

				for (ConfrontoDTO c : etapa.getConfrontos()) {
					Row row = sheetJogos.createRow(rowIdx++);
					row.createCell(0).setCellValue(etapa.getNomeEtapa());
					row.createCell(1).setCellValue(c.getDupla1().getAtleta1().getNome());
					row.createCell(2).setCellValue(c.getDupla1().getAtleta2().getNome());

					// Coluna D (P1)
					Cell cellP1 = row.createCell(3);
					cellP1.setCellStyle(inputStyle);

					row.createCell(4).setCellValue("x");

					// Coluna F (P2)
					Cell cellP2 = row.createCell(5);
					cellP2.setCellStyle(inputStyle);

					row.createCell(6).setCellValue(c.getDupla2().getAtleta1().getNome());
					row.createCell(7).setCellValue(c.getDupla2().getAtleta2().getNome());
				}
				// Linha em branco para separar as etapas
				rowIdx++;
			}

			// 2. SEGUNDA PASSADA: Criar Ranking com o limite real do Excel
			createHeader(sheetRanking, headerStyle, "Atleta", "Saldo Total");

			// O limite agora é o rowIdx exato que o loop acima atingiu
			int limiteExcel = rowIdx;

			for (int i = 0; i < atletas.size(); i++) {
				Row row = sheetRanking.createRow(i + 1);
				String nomeAtleta = atletas.get(i).getNome();
				row.createCell(0).setCellValue(nomeAtleta);

				// Ranges dinâmicos baseados no limiteExcel
				String rD1A1 = "Confrontos!$B$2:$B$" + limiteExcel;
				String rD1A2 = "Confrontos!$C$2:$C$" + limiteExcel;
				String rD2A1 = "Confrontos!$G$2:$G$" + limiteExcel;
				String rD2A2 = "Confrontos!$H$2:$H$" + limiteExcel;
				String rP1 = "Confrontos!$D$2:$D$" + limiteExcel;
				String rP2 = "Confrontos!$F$2:$F$" + limiteExcel;

				// Fórmula corrigida: Ignora 0x0 se as células estiverem vazas (<>"")
				// String templateSaldo = """
				// SUMPRODUCT(((%s="%s")+(%s="%s"))*(%s<>"")*(%s<>"")*((%s>%s)*3+(%s=%s)*1))
				// """;
				String templateSaldo = """
						SUMPRODUCT(((%s="%s")+(%s="%s"))*(%s<>"")*(%s<>"")*(%s-%s))
						""";

				String f1 = String.format(templateSaldo, rD1A1, nomeAtleta, rD1A2, nomeAtleta, rP1, rP2, rP1, rP2, rP1, rP2);
				String f2 = String.format(templateSaldo, rD2A1, nomeAtleta, rD2A2, nomeAtleta, rP1, rP2, rP2, rP1, rP2, rP1);

				Cell cellSaldo = row.createCell(1);
				cellSaldo.setCellStyle(createInputStyleRed(workbook));
				cellSaldo.setCellFormula(f1 + "+" + f2);
			}

			// 1. Regras de Negócio e Estética (Pódio e Filtros)
			aplicarCoresPodio(sheetRanking, atletas.size());
			sheetRanking.setAutoFilter(new CellRangeAddress(0, atletas.size(), 0, 1));
			sheetJogos.createFreezePane(0, 1); // Congela o cabeçalho dos jogos

			// 2. Preparação do Motor de Fórmulas
			// O evaluateAll() processa as fórmulas no lado do servidor (Java).
			// O setForceFormulaRecalculation(true) garante que o Excel recalcule ao abrir (Client).
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			sheetRanking.setForceFormulaRecalculation(true);
			sheetJogos.setForceFormulaRecalculation(true);

			// 3. Persistência e Flush
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);

			// Nota: O try-with-resources que você provavelmente está usando 
			// cuidará do workbook.close() após o return.
			return out.toByteArray();

		}

	}

	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	private CellStyle createInputStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();

		// Cor de fundo para indicar campo de entrada (Amarelo bem suave)
		style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Configuração de Bordas (Transforma a célula em uma "caixa")
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());

		// Alinhamento centralizado para os números do placar
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		return style;
	}

	private CellStyle createInputStyleRed(Workbook workbook) {
		// No loop do Ranking, após setar a fórmula:
		// Você pode criar um Style para números negativos em Vermelho
		DataFormat df = workbook.createDataFormat();
		CellStyle redStyle = workbook.createCellStyle();
		redStyle.setDataFormat(df.getFormat("#,##0;[Red]-#,##0"));

		return redStyle;
	}

	private void aplicarCoresPodio(Sheet sheet, int totalAtletas) {
		SheetConditionalFormatting cf = sheet.getSheetConditionalFormatting();
		int ultimaLinha = totalAtletas + 1;
		CellRangeAddress[] regions = { CellRangeAddress.valueOf("B2:B" + ultimaLinha) };
		String rangeRef = "$B$2:$B$" + ultimaLinha;

		// FÓRMULA SÊNIOR (DENSE RANK):
		// Conta quantos valores ÚNICOS e MAIORES existem acima do valor atual.
		// Se o resultado for 0, ele é o 1º (Ouro). Se for 1, ele é o 2º (Prata)...

		// Ouro: 0 valores únicos maiores que ele
		String fOuro = String.format("AND(B2<>0, SUMPRODUCT((%s>B2)/COUNTIF(%s, %s&\"\"))=0)", rangeRef, rangeRef, rangeRef);

		// Prata: 1 valor único maior que ele
		String fPrata = String.format("AND(B2<>0, SUMPRODUCT((%s>B2)/COUNTIF(%s, %s&\"\"))=1)", rangeRef, rangeRef, rangeRef);

		// Bronze: 2 valores únicos maiores que ele
		String fBronze = String.format("AND(B2<>0, SUMPRODUCT((%s>B2)/COUNTIF(%s, %s&\"\"))=2)", rangeRef, rangeRef, rangeRef);

		// Criar e aplicar regras
		pincelarRegra(cf, regions, fOuro, IndexedColors.GOLD.getIndex());
		pincelarRegra(cf, regions, fPrata, IndexedColors.GREY_25_PERCENT.getIndex());
		pincelarRegra(cf, regions, fBronze, IndexedColors.TAN.getIndex());
	}

	private void pincelarRegra(SheetConditionalFormatting cf, CellRangeAddress[] regions, String formula, short cor) {
		ConditionalFormattingRule rule = cf.createConditionalFormattingRule(formula);
		PatternFormatting fill = rule.createPatternFormatting();
		fill.setFillBackgroundColor(cor);
		fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
		cf.addConditionalFormatting(regions, rule);
	}

	/*
	 * private void aplicarCoresPodio(Sheet sheet, int totalAtletas) {
	 * SheetConditionalFormatting cf = sheet.getSheetConditionalFormatting();
	 * 
	 * // Range: B2 até B(N) int ultimaLinha = totalAtletas + 1; CellRangeAddress[]
	 * regions = { CellRangeAddress.valueOf("B2:B" + ultimaLinha) };
	 * 
	 * // O range interno da função LARGE deve ser absoluto ($B$2:$B$N) String
	 * rangeRef = "$B$2:$B$" + ultimaLinha;
	 * 
	 * // IMPORTANTE: Use VÍRGULA (,) para os argumentos da função, // o Excel
	 * traduzirá para ponto e vírgula sozinho no PT-BR. // B2 deve ser relativo (sem
	 * $) para que a regra avalie cada linha individualmente. String f1 =
	 * String.format("AND(B2<>0, B2=LARGE(%s, 1))", rangeRef); String f2 =
	 * String.format("AND(B2<>0, B2=LARGE(%s, 2))", rangeRef); String f3 =
	 * String.format("AND(B2<>0, B2=LARGE(%s, 3))", rangeRef);
	 * 
	 * ConditionalFormattingRule r1 = cf.createConditionalFormattingRule(f1);
	 * pincelar(r1, IndexedColors.GOLD.getIndex());
	 * 
	 * ConditionalFormattingRule r2 = cf.createConditionalFormattingRule(f2);
	 * pincelar(r2, IndexedColors.GREY_25_PERCENT.getIndex());
	 * 
	 * ConditionalFormattingRule r3 = cf.createConditionalFormattingRule(f3);
	 * pincelar(r3, IndexedColors.TAN.getIndex());
	 * 
	 * cf.addConditionalFormatting(regions, r1);
	 * cf.addConditionalFormatting(regions, r2);
	 * cf.addConditionalFormatting(regions, r3); }
	 * 
	 * private void pincelar(ConditionalFormattingRule regra, short cor) {
	 * PatternFormatting fill = regra.createPatternFormatting();
	 * fill.setFillBackgroundColor(cor);
	 * fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND); }
	 */
}