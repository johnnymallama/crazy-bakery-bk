package uan.edu.co.crazy_bakery.application.dto.responses.geografia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CiudadDTO {
    private int id;
    private String name;
    private int departmentId;
}
