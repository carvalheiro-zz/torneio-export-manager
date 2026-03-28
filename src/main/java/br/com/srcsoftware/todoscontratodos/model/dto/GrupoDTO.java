package br.com.srcsoftware.todoscontratodos.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrupoDTO {	
	private List<TimeDTO> times;
	private List<ConfrontoTimesDTO> confrontos;
}
