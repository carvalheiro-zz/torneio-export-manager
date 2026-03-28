package br.com.srcsoftware.todoscontratodos.model.dto;
import lombok.Data;

@Data
public class TimeDTO {

	private String nome;
	private Integer pontos;
	private Integer saldo;	
	
	@Override
	public String toString() {
		return String.format("%s", nome );
	}		
}