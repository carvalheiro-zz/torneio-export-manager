package br.com.srcsoftware.todoscontratodos.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.srcsoftware.todoscontratodos.export.ExcelExportService; // Certifique-se de ajustar este serviço para receber EtapaTimesDTO
import br.com.srcsoftware.todoscontratodos.export.ExcelExportTimesSetUnicoService;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaTimesDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.GrupoDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.TimeDTO;
import br.com.srcsoftware.todoscontratodos.service.TorneioTimesTodosContraTodosService;
import br.com.srcsoftware.todoscontratodos.service.TorneioTimesTodosContraTodosSetUnicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/torneio-times")
public class TorneioTimesController {

    private final TorneioTimesTodosContraTodosService torneioService;
    private final ExcelExportService excelService;
    
    private final TorneioTimesTodosContraTodosSetUnicoService torneioServiceContraTodosSetUnicoService;
    private final ExcelExportTimesSetUnicoService excelServiceExcelExportTimesSetUnicoService;

    @PostMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarParaExcel(@RequestParam("lista1") String lista1) throws IOException {
        log.info("Iniciando exportação de torneio de times para Excel.");

        // 1. Parsing da String para TimeDTO (Garantindo lista mutável para o processamento)
        List<TimeDTO> times = Arrays.stream(lista1.split("\\r?\\n"))
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .distinct()
                .map(n -> {
                    TimeDTO t = new TimeDTO();
                    t.setNome(n);
                    t.setPontos(0);
                    t.setSaldo(0);
                    return t;
                }).collect(Collectors.toCollection(ArrayList::new));

        // 2. Validação defensiva (mínimo de times para um torneio todos contra todos)
        if (times.size() < 2) {
            log.warn("Tentativa de exportação com apenas {} time(s). Mínimo exigido: 2.", times.size());
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Processamento do chaveamento utilizando o serviço de Times
        List<EtapaTimesDTO> etapas = torneioService.processar(lista1);

        // 4. Geração do binário Excel
        // Nota: O método do excelService deve ser sobrecarregado ou adaptado para aceitar List<EtapaTimesDTO>
        byte[] excelBytes = excelService.gerarPlanilhaTorneioTimes(times, etapas);

        // 5. Definição do nome do arquivo com timestamp (Padrão TORNEIO_EXPORT_MANAGER)
        String fileName = "TORNEIO_TIMES_EXPORT_" + times.size() + "-" + System.currentTimeMillis() + ".xlsx";
        
        log.info("Arquivo {} gerado com sucesso. Tamanho: {} bytes.", fileName, excelBytes.length);

        // 6. Retorno do binário para download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }
                
    @PostMapping("/gerar-excel-set-unico")
    public ResponseEntity<byte[]> gerarExcelSetUnico(@RequestParam("lista1") String lista1) throws IOException {
        log.info("Iniciando exportação de torneio de times para Excel.");

        // 1. Parsing da String para TimeDTO (Garantindo lista mutável para o processamento)
        List<TimeDTO> times = Arrays.stream(lista1.split("\\r?\\n"))
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .distinct()
                .map(n -> {
                    TimeDTO t = new TimeDTO();
                    t.setNome(n);
                    t.setPontos(0);
                    t.setSaldo(0);
                    return t;
                }).collect(Collectors.toCollection(ArrayList::new));

        // 2. Validação defensiva (mínimo de times para um torneio todos contra todos)
        if (times.size() < 2) {
            log.warn("Tentativa de exportação com apenas {} time(s). Mínimo exigido: 2.", times.size());
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Processamento do chaveamento utilizando o serviço de Times
        List<EtapaTimesDTO> etapas = torneioServiceContraTodosSetUnicoService.gerarEtapasTodosContraTodos(times);

        // 4. Geração do binário Excel
        // Nota: O método do excelService deve ser sobrecarregado ou adaptado para aceitar List<EtapaTimesDTO>
        byte[] excelBytes = excelServiceExcelExportTimesSetUnicoService.exportarTorneio(etapas);

        // 5. Definição do nome do arquivo com timestamp (Padrão TORNEIO_EXPORT_MANAGER)
        String fileName = "TORNEIO_TIMES_SET_UNICO_EXPORT_" + times.size() + "-" + System.currentTimeMillis() + ".xlsx";
        
        log.info("Arquivo {} gerado com sucesso. Tamanho: {} bytes.", fileName, excelBytes.length);

        // 6. Retorno do binário para download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }
    
    @PostMapping("/exportar-grupos")
    public ResponseEntity<byte[]> exportarGrupos(
            @RequestParam("lista1") String lista1,
            @RequestParam("numGrupos") int numGrupos) throws IOException {
        
        // Parsing padrão que já utilizamos
        List<TimeDTO> times = Arrays.stream(lista1.split("\\r?\\n"))
                .map(String::trim)
                .filter(nome -> !nome.isEmpty())
                .map(nome -> {
                    TimeDTO dto = new TimeDTO();
                    dto.setNome(nome);
                    return dto;
                }).toList();

        // Geração da estrutura de Grupos
        List<GrupoDTO> grupos = torneioServiceContraTodosSetUnicoService.gerarGruposComConfrontos(times, numGrupos);

        // Exportação (Reaproveitando seu ExcelExportService adaptado para Grupos)
        byte[] bytes = excelServiceExcelExportTimesSetUnicoService.exportarGrupos(grupos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Torneio_Grupos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}