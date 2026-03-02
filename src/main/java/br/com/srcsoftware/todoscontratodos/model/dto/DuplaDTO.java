package br.com.srcsoftware.todoscontratodos.model.dto;
import java.util.Objects;

import lombok.Data;

@Data
public class DuplaDTO {

	private AtletaDTO atleta1;
	private AtletaDTO atleta2;
	private Integer pontos;
	
	@Override
	public String toString() {
		return String.format("%s e %s", atleta1.getNome(), atleta2.getNome() );
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DuplaDTO that)) return false;
        // Considera igual se tiver os mesmos atletas, independente da ordem (1,2 ou 2,1)
        return (Objects.equals(atleta1, that.atleta1) && Objects.equals(atleta2, that.atleta2)) ||
               (Objects.equals(atleta1, that.atleta2) && Objects.equals(atleta2, that.atleta1));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(atleta1) + Objects.hashCode(atleta2);
    }
}
