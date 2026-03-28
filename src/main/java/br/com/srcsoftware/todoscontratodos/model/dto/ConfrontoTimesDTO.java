package br.com.srcsoftware.todoscontratodos.model.dto;
import java.util.Objects;

import lombok.Data;

@Data
public class ConfrontoTimesDTO {

	private TimeDTO time1;
	private TimeDTO time2;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfrontoTimesDTO that)) return false;
        
        // Lógica Combinatória: (A vs B) é igual a (B vs A)
        return (Objects.equals(time1, that.time1) && Objects.equals(time2, that.time2)) ||
               (Objects.equals(time1, that.time2) && Objects.equals(time2, that.time1));
    }

    @Override
    public int hashCode() {
        // Soma os hashCodes para que a ordem das duplas não altere o resultado do hash
        return Objects.hashCode(time1) + Objects.hashCode(time2);
    }
}
