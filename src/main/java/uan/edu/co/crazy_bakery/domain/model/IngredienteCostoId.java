
package uan.edu.co.crazy_bakery.domain.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteCostoId implements Serializable {
    private Long tamanoId;
    private Long ingredienteId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredienteCostoId that = (IngredienteCostoId) o;
        return Objects.equals(tamanoId, that.tamanoId) && Objects.equals(ingredienteId, that.ingredienteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tamanoId, ingredienteId);
    }
}
