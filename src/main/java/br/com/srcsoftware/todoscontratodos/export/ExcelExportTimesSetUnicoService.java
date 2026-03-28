package br.com.srcsoftware.todoscontratodos.export;

import br.com.srcsoftware.todoscontratodos.model.dto.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportTimesSetUnicoService {

    public byte[] exportarTorneio(List<EtapaTimesDTO> etapas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (EtapaTimesDTO etapa : etapas) {
                Sheet sheet = workbook.createSheet(etapa.getNomeEtapa());
                
                // Cabeçalho
                Row headerRow = sheet.createRow(0);
                createCell(headerRow, 0, "Time Mandante", headerStyle);
                createCell(headerRow, 1, "vs", headerStyle);
                createCell(headerRow, 2, "Time Visitante", headerStyle);

                int rowIdx = 1;
                for (ConfrontoTimesDTO confronto : etapa.getConfrontos()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(confronto.getTime1().getNome());
                    row.createCell(1).setCellValue("x");
                    row.createCell(2).setCellValue(confronto.getTime2().getNome());
                }

                // Auto-ajuste das colunas
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(2);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
    
    
    public byte[] exportarGrupos(List<GrupoDTO> grupos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 1. Aba de Resumo (Visão Geral dos Grupos)
            Sheet resumoSheet = workbook.createSheet("Definição de Grupos");
            int colIdx = 0;
            for (int i = 0; i < grupos.size(); i++) {
                GrupoDTO grupo = grupos.get(i);
                int rowIdx = 0;
                
                // Cabeçalho do Grupo (ex: Grupo 1)
                Row headerRow = resumoSheet.getRow(rowIdx);
                if (headerRow == null) headerRow = resumoSheet.createRow(rowIdx);
                createCell(headerRow, colIdx, "GRUPO " + (i + 1), headerStyle);
                
                rowIdx++;
                // Listagem dos times do grupo
                for (TimeDTO time : grupo.getTimes()) {
                    Row row = resumoSheet.getRow(rowIdx);
                    if (row == null) row = resumoSheet.createRow(rowIdx);
                    row.createCell(colIdx).setCellValue(time.getNome());
                    rowIdx++;
                }
                resumoSheet.autoSizeColumn(colIdx);
                colIdx += 2; // Espaço entre as colunas dos grupos
            }

            // 2. Abas Individuais por Grupo (Confrontos)
            for (int i = 0; i < grupos.size(); i++) {
                GrupoDTO grupo = grupos.get(i);
                String nomeAba = "Jogos Grupo " + (i + 1);
                Sheet sheet = workbook.createSheet(nomeAba);
                
                // Cabeçalho dos Jogos
                Row headerRow = sheet.createRow(0);
                createCell(headerRow, 0, "Mandante", headerStyle);
                createCell(headerRow, 1, "vs", headerStyle);
                createCell(headerRow, 2, "Visitante", headerStyle);

                int rowIdx = 1;
                for (ConfrontoTimesDTO confronto : grupo.getConfrontos()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(confronto.getTime1().getNome());
                    row.createCell(1).setCellValue("x");
                    row.createCell(2).setCellValue(confronto.getTime2().getNome());
                }

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(2);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
