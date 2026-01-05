package uan.edu.co.crazy_bakery.domain.model.Ingredientes;

import org.springframework.data.annotation.Id;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity(name = "ingrediente")
public class Ingrediente {

    @Id
    private String codigo;

    private String nombre;
    private String composicion;
    private TipoIngrediente TipoIngrediente;
    private float valor;
}
