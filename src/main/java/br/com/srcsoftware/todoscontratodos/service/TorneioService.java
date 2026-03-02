package br.com.srcsoftware.todoscontratodos.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.srcsoftware.todoscontratodos.model.dto.AtletaDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.ConfrontoDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.DuplaDTO;
import br.com.srcsoftware.todoscontratodos.model.dto.EtapaDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TorneioService {

	public List<EtapaDTO> processar(String lista1) {

		log.info("Criando um List com os Atletas informados: {}", lista1);
		// Gera um List contendo os Atletas
		List<AtletaDTO> atletasA = parseParaAtletas(lista1);
		atletasA.forEach(atleta -> {
			log.info("Atleta: {}", atleta);
		});

		log.info("Montando as duplas com os {} atletas", atletasA.size());
		// Forma as duplas utilizando os atletas da lista. Usando a regra Todos jogam
		// com Todos.
		List<DuplaDTO> duplas = gerarTodasAsDuplasUnicas(atletasA);
		duplas.forEach(dupla -> {
			log.info("Duplas: {}", dupla);
		});

		log.info("Gerando os confrontos para {} duplas:", duplas.size());
		List<List<DuplaDTO>> confrontos = gerarConfrontos(atletasA);
		confrontos.forEach(confronto -> {
			log.info("Confronto {} X {} | {} X {}", confronto.get(0), confronto.get(1), confronto.get(2), confronto.get(3));
		});

		log.info("Gerando os confrontosAleatorios para {} duplas:", duplas.size());
		List<List<DuplaDTO>> confrontosAleatorios = gerarEtapasAleatorias(atletasA);
		confrontosAleatorios.forEach(confronto -> {
			log.info("Confronto {} X {} | {} X {}", confronto.get(0), confronto.get(1), confronto.get(2), confronto.get(3));
		});

		List<EtapaDTO> etapas = gerarEtapas(confrontosAleatorios);
		etapas.forEach(etapa -> {
			log.info("Etapa {} - {}", etapa.getNomeEtapa(), etapa.getConfrontos());
		});

		return etapas;
	}

	private List<EtapaDTO> gerarEtapas(List<List<DuplaDTO>> listasDeDuplas) {

		List<EtapaDTO> etapas = new ArrayList<>();

		for (int i = 0; i < listasDeDuplas.size(); i++) {
			List<DuplaDTO> duplasDaEtapa = listasDeDuplas.get(i);
			EtapaDTO etapaDTO = new EtapaDTO();
			etapaDTO.setNomeEtapa(String.valueOf(i + 1)); // Nome da etapa de 1 a 7

			List<ConfrontoDTO> confrontos = new ArrayList<>();

			// Agrupa as duplas de 2 em 2 para formar os confrontos
			for (int j = 0; j < duplasDaEtapa.size(); j += 2) {
				if (j + 1 < duplasDaEtapa.size()) {
					ConfrontoDTO confronto = new ConfrontoDTO();
					confronto.setDupla1(duplasDaEtapa.get(j));
					confronto.setDupla2(duplasDaEtapa.get(j + 1));
					confrontos.add(confronto);
				}
			}

			etapaDTO.setConfrontos(confrontos);
			etapas.add(etapaDTO);
		}

		return etapas;
	}

	private List<AtletaDTO> parseParaAtletas(String input) {
		if (input == null || input.isBlank()) {
			return List.of();
		}		
		
		// 1. Parsing da String para Atletas (Java 21 toList() é imutável e eficiente)
        List<AtletaDTO> atletas = Arrays.stream(input.split("\\r?\\n"))
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .distinct() // Garante integridade se o usuário repetir nomes
                .map(n -> {
                    AtletaDTO a = new AtletaDTO();
                    a.setNome(n);
                    a.setSaldo(0);
                    return a;
                }).toList();
        
        return atletas;
	}

	public List<DuplaDTO> gerarTodasAsDuplasUnicas(List<AtletaDTO> atletas) {
		int n = atletas.size();
		List<DuplaDTO> duplas = new ArrayList<>();

		// Loop i: do primeiro ao penúltimo
		for (int i = 0; i < n - 1; i++) {
			// Loop j: sempre à frente de i para evitar (1,2 e 2,1) e (1,1)
			for (int j = i + 1; j < n; j++) {
				DuplaDTO dupla = new DuplaDTO();
				dupla.setAtleta1(atletas.get(i));
				dupla.setAtleta2(atletas.get(j));
				dupla.setPontos(0);
				duplas.add(dupla);
			}
		}
		return duplas;
	}

	public List<List<DuplaDTO>> gerarConfrontos(List<AtletaDTO> atletas) {
		int numAtletas = atletas.size();
		int numEtapas = numAtletas - 1;
		int jogosPorEtapa = numAtletas / 2;

		List<List<DuplaDTO>> etapas = new ArrayList<>();

		// Lista auxiliar para rotacionar (copiamos a original)
		List<AtletaDTO> rotativo = new ArrayList<>(atletas);

		for (int etapaIdx = 0; etapaIdx < numEtapas; etapaIdx++) {
			List<DuplaDTO> rodadaAtual = new ArrayList<>();

			for (int jogoIdx = 0; jogoIdx < jogosPorEtapa; jogoIdx++) {
				AtletaDTO a1 = rotativo.get(jogoIdx);
				AtletaDTO a2 = rotativo.get(numAtletas - 1 - jogoIdx);

				DuplaDTO dupla = new DuplaDTO();
				dupla.setAtleta1(a1);
				dupla.setAtleta2(a2);
				dupla.setPontos(0);
				rodadaAtual.add(dupla);
			}

			etapas.add(rodadaAtual);

			// Rotaciona a lista mantendo o índice 0 fixo
			AtletaDTO ultimo = rotativo.remove(numAtletas - 1);
			rotativo.add(1, ultimo);
		}

		return etapas;
	}

	public List<List<DuplaDTO>> gerarEtapasAleatorias(List<AtletaDTO> atletas) {
		int numAtletas = atletas.size();
		int numEtapas = numAtletas - 1;
		int jogosPorEtapa = numAtletas / 2;

		// Nível 1: Embaralha os atletas antes de começar
		// Isso garante que o "Atleta 1" (fixo) não seja sempre o mesmo
		List<AtletaDTO> rotativo = new ArrayList<>(atletas);
		Collections.shuffle(rotativo);

		List<List<DuplaDTO>> etapas = new ArrayList<>();

		for (int etapaIdx = 0; etapaIdx < numEtapas; etapaIdx++) {
			List<DuplaDTO> rodadaAtual = new ArrayList<>();

			for (int jogoIdx = 0; jogoIdx < jogosPorEtapa; jogoIdx++) {
				AtletaDTO a1 = rotativo.get(jogoIdx);
				AtletaDTO a2 = rotativo.get(numAtletas - 1 - jogoIdx);

				DuplaDTO dupla = new DuplaDTO();
				dupla.setAtleta1(a1);
				dupla.setAtleta2(a2);
				dupla.setPontos(0);
				rodadaAtual.add(dupla);
			}

			// Nível 2: Embaralha a ordem das duplas dentro desta etapa
			// Evita que o Atleta Fixo seja sempre o primeiro jogo da lista
			Collections.shuffle(rodadaAtual);

			etapas.add(rodadaAtual);

			// Rotação padrão do algoritmo do círculo
			AtletaDTO ultimo = rotativo.remove(numAtletas - 1);
			rotativo.add(1, ultimo);
		}

		// Nível 3: Embaralha a ordem das etapas (Rounds)
		// Assim, a "Etapa 1" cronológica pode ser a última a ser exibida
		Collections.shuffle(etapas);

		return etapas;
	}	
}
