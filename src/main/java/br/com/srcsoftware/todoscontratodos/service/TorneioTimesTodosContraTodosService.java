package br.com.srcsoftware.todoscontratodos.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.srcsoftware.todoscontratodos.model.dto.ConfrontoTimesDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaTimesDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.TimeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TorneioTimesTodosContraTodosService {

    /**
     * Processa a lista de times e gera o chaveamento completo em etapas.
     */
    public List<EtapaTimesDTO> processar(String listaInput) {
        log.info("Iniciando processamento de torneio de TIMES (Todos contra Todos)");
        
        // 1. Parsing da String para lista mutável de TimeDTO
        List<TimeDTO> times = parseParaTimes(listaInput);
        
        // 2. Tratamento de número ímpar: Adiciona o time "FOLGA" para o algoritmo de Round Robin
        if (times.size() % 2 != 0) {
            log.info("Número ímpar de times detectado ({}). Adicionando FOLGA ao sorteio.", times.size());
            TimeDTO folga = new TimeDTO();
            folga.setNome("FOLGA");
            times.add(folga);
        }

        log.debug("Gerando etapas aleatórias para {} participantes", times.size());
        
        // 3. Gerar as etapas usando a lógica de Round Robin (Círculo) com 3 níveis de aleatoriedade
        List<List<ConfrontoTimesDTO>> etapasBrutas = gerarEtapasAleatorias(times);

        // 4. Formatar para EtapaTimesDTO conforme solicitado
        List<EtapaTimesDTO> etapasFinalizadas = formatarEtapas(etapasBrutas);

        log.info("Torneio de times criado com SUCESSO. {} etapas geradas.", etapasFinalizadas.size());
        return etapasFinalizadas;
    }

    /**
     * Algoritmo de Round Robin com Rotação de Círculo e aleatoriedade múltipla.
     */
    public List<List<ConfrontoTimesDTO>> gerarEtapasAleatorias(List<TimeDTO> times) {
        int numParticipantes = times.size();
        int numEtapas = numParticipantes - 1;
        int jogosPorEtapa = numParticipantes / 2;
        
        times = sorteiarAleatoriamente(times);
	    times = sorteiarAleatoriamente(times);
	    times = sorteiarAleatoriamente(times);

        // Nível 1: Aleatoriedade inicial (quem será o pivô fixo no índice 0)
        List<TimeDTO> rotativo = new ArrayList<>(times);
        //Collections.shuffle(rotativo); sorteio antigo

        List<List<ConfrontoTimesDTO>> etapas = new ArrayList<>();

        for (int i = 0; i < numEtapas; i++) {
            List<ConfrontoTimesDTO> rodadaAtual = new ArrayList<>();

            for (int j = 0; j < jogosPorEtapa; j++) {
                TimeDTO t1 = rotativo.get(j);
                TimeDTO t2 = rotativo.get(numParticipantes - 1 - j);

                // Regra de Ouro: Ignora confrontos que envolvam o time de folga
                if (!t1.getNome().equalsIgnoreCase("FOLGA") && !t2.getNome().equalsIgnoreCase("FOLGA")) {
                    ConfrontoTimesDTO confronto = new ConfrontoTimesDTO();
                    confronto.setTime1(t1);
                    confronto.setTime2(t2);
                    rodadaAtual.add(confronto);
                }
            }

            // Nível 2: Embaralha a ordem dos confrontos dentro de cada etapa
            Collections.shuffle(rodadaAtual);
            etapas.add(rodadaAtual);

            // Rotação do Círculo (Java 21 sequenced collections: removeLast)
            TimeDTO ultimo = rotativo.removeLast();
            rotativo.add(1, ultimo);
        }

        // Nível 3: Embaralha a ordem das etapas cronológicas
        Collections.shuffle(etapas);

        return etapas;
    }

    /**
     * Encapsula as listas de confrontos na estrutura EtapaTimesDTO.
     */
    private List<EtapaTimesDTO> formatarEtapas(List<List<ConfrontoTimesDTO>> listasDeConfrontos) {
        List<EtapaTimesDTO> etapas = new ArrayList<>();

        for (int i = 0; i < listasDeConfrontos.size(); i++) {
            EtapaTimesDTO etapaDTO = new EtapaTimesDTO();
            etapaDTO.setNomeEtapa("Etapa " + (i + 1));
            etapaDTO.setConfrontos(listasDeConfrontos.get(i));
            etapas.add(etapaDTO);
        }

        return etapas;
    }

    /**
     * Converte o input de texto para uma lista mutável de TimeDTO.
     */
    private List<TimeDTO> parseParaTimes(String input) {
        if (input == null || input.isBlank()) return new ArrayList<>();
        
        return Arrays.stream(input.split("\\r?\\n"))
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .distinct()
                .map(n -> {
                    TimeDTO t = new TimeDTO();
                    t.setNome(n);
                    t.setPontos(0);
                    t.setSaldo(0);
                    return t;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public List<TimeDTO> sorteiarAleatoriamente(List<TimeDTO> times) {
	    List<TimeDTO> timesSorteados = new ArrayList<>(times);
	    // Shuffle robusto
	    Collections.shuffle(timesSorteados, new SecureRandom());
	    
	    return timesSorteados;
	}
}