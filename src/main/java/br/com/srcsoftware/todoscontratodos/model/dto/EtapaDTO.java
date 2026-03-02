package br.com.srcsoftware.todoscontratodos.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class EtapaDTO {

	private String nomeEtapa;
	private List<ConfrontoDTO> confrontos;
}
