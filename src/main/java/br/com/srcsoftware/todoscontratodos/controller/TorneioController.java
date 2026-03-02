package br.com.srcsoftware.todoscontratodos.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.srcsoftware.todoscontratodos.export.ExcelExportService;
import br.com.srcsoftware.todoscontratodos.model.dto.AtletaDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaDTO;
import br.com.srcsoftware.todoscontratodos.service.TorneioService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/torneio")
public class TorneioController {

	private final TorneioService torneioService;
    private final ExcelExportService excelService;

    @PostMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarParaExcel(@RequestParam("lista1") String lista1) throws IOException {
        
        // 1. Parsing da String para Atletas (Java 21 toList() é imutável e eficiente)
        List<AtletaDTO> atletas = Arrays.stream(lista1.split("\\r?\\n"))
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .distinct() // Garante integridade se o usuário repetir nomes
                .map(n -> {
                    AtletaDTO a = new AtletaDTO();
                    a.setNome(n);
                    a.setSaldo(0);
                    return a;
                }).toList();

        // Validação defensiva rápida
        if (atletas.size() < 4 || atletas.size() % 2 != 0) {
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Conversão para EtapaDTO (agrupando em confrontos 2vs2 para o Excel)
        List<EtapaDTO> etapas = torneioService.processar(lista1);

        // 4. Geração do binário Excel (Apache POI com fórmulas SUMPRODUCT)
        byte[] excelBytes = excelService.gerarPlanilhaTorneioComFormulasNova(atletas, etapas);

        // 5. Definição do nome do arquivo com timestamp para evitar cache no browser
        String fileName = "TORNEIO_EXPORT_MANAGER_Super_" + atletas.size() + "-" + System.currentTimeMillis() + ".xlsx";
        
        // 6. Retorno como binário (Download forçado pelo browser)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }
}