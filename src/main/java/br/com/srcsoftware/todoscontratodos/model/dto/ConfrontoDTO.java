package br.com.srcsoftware.todoscontratodos.model.dto;
import java.util.Objects;

import lombok.Data;

@Data
public class ConfrontoDTO {

	private DuplaDTO dupla1;
	private DuplaDTO dupla2;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfrontoDTO that)) return false;
        
        // Lógica Combinatória: (A vs B) é igual a (B vs A)
        return (Objects.equals(dupla1, that.dupla1) && Objects.equals(dupla2, that.dupla2)) ||
               (Objects.equals(dupla1, that.dupla2) && Objects.equals(dupla2, that.dupla1));
    }

    @Override
    public int hashCode() {
        // Soma os hashCodes para que a ordem das duplas não altere o resultado do hash
        return Objects.hashCode(dupla1) + Objects.hashCode(dupla2);
    }
}
