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
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import br.com.srcsoftware.todoscontratodos.model.dto.AtletaDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.ConfrontoDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaDTO;

@Service
public class ExcelExportService {

	
	private void createHeaderSaldo(Sheet sheet, CellStyle headerStyle, String... titles) {		
		Row header = sheet.createRow(0);
		for (int i = 0; i < titles.length; i++) {
			Cell cellHeader = header.createCell(i);
			cellHeader.setCellValue(titles[i]);
			cellHeader.setCellStyle(headerStyle);
		}
	}
	private void createHeaderConfrontos(Sheet sheet, CellStyle headerStyle) {
	    Row headerRow = sheet.createRow(0);
	    headerRow.setHeightInPoints(20); // Aumenta a altura para destacar com a fonte maior

	    // Definição dos textos (pulando as colunas que serão "engolidas" pela mesclagem)
	    headerRow.createCell(0).setCellValue("Etapa");
	    headerRow.createCell(1).setCellValue("Dupla 1"); // Será mesclada com a 2
	    headerRow.createCell(3).setCellValue("P1");
	    headerRow.createCell(4).setCellValue("x");
	    headerRow.createCell(5).setCellValue("P2");
	    headerRow.createCell(6).setCellValue("Dupla 2"); // Será mesclada com a 7
	    
	    // Aplicar estilo em todas as células do range (importante para bordas)
	    for (int i = 0; i <= 7; i++) {
	        Cell cell = headerRow.getCell(i);
	        if (cell == null) cell = headerRow.createCell(i);
	        cell.setCellStyle(headerStyle);
	    }

	    // Mesclagem da Dupla 1 (Colunas B e C -> índices 1 e 2)
	    CellRangeAddress dupla1 = new CellRangeAddress(0, 0, 1, 2);
	    sheet.addMergedRegion(dupla1);

	    // Mesclagem da Dupla 2 (Colunas G e H -> índices 6 e 7)
	    CellRangeAddress dupla2 = new CellRangeAddress(0, 0, 6, 7);
	    sheet.addMergedRegion(dupla2);

	    // Dica Sênior: Garantir bordas na região mesclada (opcional se o headerStyle já tiver)
	    RegionUtil.setBorderBottom(BorderStyle.THIN, dupla1, sheet);
	    RegionUtil.setBorderBottom(BorderStyle.THIN, dupla2, sheet);
	}
	
	public byte[] gerarPlanilhaTorneioComFormulasNova(List<AtletaDTO> atletas, List<EtapaDTO> etapas) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			CellStyle inputStyle = createInputStyle(workbook);
			Sheet sheetRanking = workbook.createSheet("Ranking");
			Sheet sheetJogos = workbook.createSheet("Confrontos");
			
			// No seu ExcelExportService, após criar a sheet:
			sheetRanking.setDisplayGridlines(false);
			// Se quiser remover da aba de jogos também:
			sheetJogos.setDisplayGridlines(false);

			
			CellStyle baseStyle = createBaseStyle(workbook);
			
			CellStyle headerStyle = createHeaderStyle(workbook);
			CellStyle centerCellValue = createValueCenterStyle(workbook);
			CellStyle borderCellValue = createInputBorderStyle(workbook);
			
			// 1. PRIMEIRA PASSADA: Preencher os Jogos para descobrir o rowIdx final
			createHeaderConfrontos(sheetJogos, headerStyle);

			int rowIdx = 1;
			for (EtapaDTO etapa : etapas) {
				// Linha de Título da Etapa (Separador visual)
				Row rowTitulo = sheetJogos.createRow(rowIdx++);
				Cell cellTitulo = rowTitulo.createCell(0);
				cellTitulo.setCellValue(">>> ETAPA " + etapa.getNomeEtapa());

				for (ConfrontoDTO c : etapa.getConfrontos()) {
					Row row = sheetJogos.createRow(rowIdx++);
					
					Cell cellNomeAtleta = row.createCell(0);
					cellNomeAtleta.setCellValue("");
					cellNomeAtleta.setCellStyle(baseStyle);
					
					Cell cellAtleta1 = row.createCell(1);
					cellAtleta1.setCellStyle(borderCellValue);
					cellAtleta1.setCellValue(c.getDupla1().getAtleta1().getNome());
					
					Cell cellAtleta2 = row.createCell(2);
					cellAtleta2.setCellStyle(borderCellValue);
					cellAtleta2.setCellValue(c.getDupla1().getAtleta2().getNome());

					// Coluna D (P1)
					Cell cellP1 = row.createCell(3);
					cellP1.setCellStyle(inputStyle);

					Cell cellX = row.createCell(4);
					cellX.setCellValue("x");
					cellX.setCellStyle(centerCellValue);

					// Coluna F (P2)
					Cell cellP2 = row.createCell(5);
					cellP2.setCellStyle(inputStyle);

					Cell cellAtleta1Dupla2 = row.createCell(6);
					cellAtleta1Dupla2.setCellStyle(borderCellValue);
					cellAtleta1Dupla2.setCellValue(c.getDupla2().getAtleta1().getNome());
					
					Cell cellAtleta2Dupla2 = row.createCell(7);
					cellAtleta2Dupla2.setCellStyle(borderCellValue);
					cellAtleta2Dupla2.setCellValue(c.getDupla2().getAtleta2().getNome());
				}
				// Linha em branco para separar as etapas
				rowIdx++;
			}

			// 2. SEGUNDA PASSADA: Criar Ranking com o limite real do Excel
			createHeaderSaldo(sheetRanking, headerStyle, "Atleta", "Vitórias", "Saldo Total");

			// O limite agora é o rowIdx exato que o loop acima atingiu
			int limiteExcel = rowIdx;

			for (int i = 0; i < atletas.size(); i++) {
				Row row = sheetRanking.createRow(i + 1);
				String nomeAtleta = atletas.get(i).getNome();
				
				Cell cellNomeAtleta = row.createCell(0);
				cellNomeAtleta.setCellStyle(baseStyle);
				cellNomeAtleta.setCellValue(nomeAtleta);

				// Ranges dinâmicos baseados no limiteExcel
				String rD1A1 = "Confrontos!$B$2:$B$" + limiteExcel;
				String rD1A2 = "Confrontos!$C$2:$C$" + limiteExcel;
				String rD2A1 = "Confrontos!$G$2:$G$" + limiteExcel;
				String rD2A2 = "Confrontos!$H$2:$H$" + limiteExcel;
				String rP1 = "Confrontos!$D$2:$D$" + limiteExcel;
				String rP2 = "Confrontos!$F$2:$F$" + limiteExcel;
				
				// 1. Defina o template com os placeholders corretos (8 placeholders por template)
				// Argumentos: DuplaA, NomeAtleta, DuplaB, NomeAtleta, ColunaP1, ColunaP2, ColunaVencedora, ColunaPerdedora
				String templateVitoria = "SUMPRODUCT(((%s=\"%s\")+(%s=\"%s\"))*ISNUMBER(%s)*ISNUMBER(%s)*(%s>%s))";

				// 2. No loop de atletas, passe os argumentos exatamente nesta ordem:
				String fVitD1 = String.format(templateVitoria, 
				    rD1A1, nomeAtleta, rD1A2, nomeAtleta, // Quem é a dupla?
				    rP1, rP2,                             // As células têm números?
				    rP1, rP2                              // P1 > P2?
				);

				String fVitD2 = String.format(templateVitoria, 
				    rD2A1, nomeAtleta, rD2A2, nomeAtleta, // Quem é a dupla?
				    rP1, rP2,                             // As células têm números?
				    rP2, rP1                              // P2 > P1?
				);

				// 3. Atribua a fórmula à célula de Vitórias (Coluna B)
				Cell cellVitorias = row.createCell(1);
				cellVitorias.setCellStyle(centerCellValue);
				cellVitorias.setCellFormula(fVitD1 + "+" + fVitD2);
				
				String templateSaldo = """
						SUMPRODUCT(((%s="%s")+(%s="%s"))*(%s<>"")*(%s<>"")*(%s-%s))
						""";
				
				String f1 = String.format(templateSaldo, rD1A1, nomeAtleta, rD1A2, nomeAtleta, rP1, rP2, rP1, rP2, rP1, rP2);
				String f2 = String.format(templateSaldo, rD2A1, nomeAtleta, rD2A2, nomeAtleta, rP1, rP2, rP2, rP1, rP2, rP1);

				Cell cellSaldo = row.createCell(2);
				cellSaldo.setCellStyle(createInputStyleRed(workbook));
				cellSaldo.setCellFormula(f1 + "+" + f2);
			}

			// 1. Regras de Negócio e Estética (Pódio e Filtros)
			aplicarCoresPodio(sheetRanking, atletas.size());

			sheetRanking.setAutoFilter(new CellRangeAddress(0, atletas.size(), 0, 2));
			
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
	
	private CellStyle createInputBorderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();

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
		
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);

		return style;
	}
	
	private CellStyle createValueCenterStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);
		
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		return style;
	}

	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());	
		font.setFontHeightInPoints((short) 18);		
		style.setFont(font);		
		
		style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	private CellStyle createInputStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);

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
		
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		redStyle.setFont(font);

		return redStyle;
	}
	
	private CellStyle createBaseStyle(Workbook workbook) {				
		CellStyle style = workbook.createCellStyle();		
		
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);

		return style;
	}

	private void aplicarCoresPodio(Sheet sheet, int totalAtletas) {
		SheetConditionalFormatting cf = sheet.getSheetConditionalFormatting();
		int ultimaLinha = totalAtletas + 1;
		CellRangeAddress[] regions = { CellRangeAddress.valueOf("B2:B" + ultimaLinha) };
		String rangeRef = "$B$2:$B$" + ultimaLinha;		

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
}