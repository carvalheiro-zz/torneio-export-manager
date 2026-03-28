package br.com.srcsoftware.todoscontratodos.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class EtapaTimesDTO {

	private String nomeEtapa;
	private List<ConfrontoTimesDTO> confrontos;
}
