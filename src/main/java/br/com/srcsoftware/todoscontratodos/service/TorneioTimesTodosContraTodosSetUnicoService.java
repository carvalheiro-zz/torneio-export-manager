package br.com.srcsoftware.todoscontratodos.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.srcsoftware.todoscontratodos.model.dto.ConfrontoTimesDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaTimesDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.GrupoDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.TimeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TorneioTimesTodosContraTodosSetUnicoService {

	public List<EtapaTimesDTO> gerarEtapasTodosContraTodos(List<TimeDTO> times) {
	    if (times == null || times.size() < 2) return List.of();

	    // Se a quantidade de times for ímpar, adicionamos um "Time Bye" (Descanso)
	    List<TimeDTO> participantes = new ArrayList<>(times);
	    if (participantes.size() % 2 != 0) {
	        TimeDTO bye = new TimeDTO();
	        bye.setNome("DESCANSO");
	        participantes.add(bye);
	    }

	    int totalTimes = participantes.size();
	    int totalEtapas = totalTimes - 1;
	    int jogosPorEtapa = totalTimes / 2;
	    
	    List<EtapaTimesDTO> etapas = new ArrayList<>();

	    for (int i = 0; i < totalEtapas; i++) {
	        EtapaTimesDTO etapa = new EtapaTimesDTO();
	        etapa.setNomeEtapa("Rodada " + (i + 1));
	        List<ConfrontoTimesDTO> confrontos = new ArrayList<>();

	        for (int j = 0; j < jogosPorEtapa; j++) {
	            int time1Index = j;
	            int time2Index = (totalTimes - 1) - j;

	            TimeDTO t1 = participantes.get(time1Index);
	            TimeDTO t2 = participantes.get(time2Index);

	            // Ignora o confronto se um dos times for o "DESCANSO"
	            if (!t1.getNome().equals("DESCANSO") && !t2.getNome().equals("DESCANSO")) {
	                ConfrontoTimesDTO confronto = new ConfrontoTimesDTO();
	                confronto.setTime1(t1);
	                confronto.setTime2(t2);
	                confrontos.add(confronto);
	            }
	        }
	        
	        etapa.setConfrontos(confrontos);
	        etapas.add(etapa);

	        // Rotaciona a lista (mantém o índice 0 fixo e move o resto)
	        // Performance: Java 21 Collections.rotate seria possível, mas o manual é mais preciso aqui
	        TimeDTO ultimo = participantes.remove(totalTimes - 1);
	        participantes.add(1, ultimo);
	    }

	    return etapas;
	}
	
	
	public List<GrupoDTO> gerarGruposComConfrontos(List<TimeDTO> times, int quantidadeGrupos) {
	    if (times == null || times.isEmpty() || quantidadeGrupos <= 0) return List.of();
	    
	    times = sorteiarAleatoriamente(times);
	    times = sorteiarAleatoriamente(times);
	    times = sorteiarAleatoriamente(times);

	    List<GrupoDTO> grupos = new ArrayList<>();
	    for (int i = 0; i < quantidadeGrupos; i++) {
	        grupos.add(new GrupoDTO(new ArrayList<>(), new ArrayList<>()));
	    }

	    // 1. Distribuição dos times nos grupos (Round Robin de inserção)
	    for (int i = 0; i < times.size(); i++) {
	        grupos.get(i % quantidadeGrupos).getTimes().add(times.get(i));
	    }

	    // 2. Geração de confrontos internos para cada grupo
	    for (GrupoDTO grupo : grupos) {
	        if (grupo.getTimes().size() >= 2) {
	            // Reaproveita sua lógica de Todos contra Todos para os times do grupo
	            List<EtapaTimesDTO> etapas = gerarEtapasTodosContraTodos(grupo.getTimes());
	            
	            // Achata as etapas em uma lista única de confrontos do grupo
	            List<ConfrontoTimesDTO> todosConfrontos = etapas.stream()
	                .flatMap(etapa -> etapa.getConfrontos().stream())
	                .collect(Collectors.toList());
	            
	            grupo.setConfrontos(todosConfrontos);
	        }
	    }

	    return grupos;
	}
	
	public List<TimeDTO> sorteiarAleatoriamente(List<TimeDTO> times) {
	    List<TimeDTO> timesSorteados = new ArrayList<>(times);
	    // Shuffle robusto
	    Collections.shuffle(timesSorteados, new SecureRandom());
	    
	    return timesSorteados;
	}
}