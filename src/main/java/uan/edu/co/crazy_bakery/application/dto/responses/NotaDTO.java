package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class NotaDTO {
    private Long id;
    private Date fechaCreacion;
    private String nota;
    private String usuarioNombre;
}
