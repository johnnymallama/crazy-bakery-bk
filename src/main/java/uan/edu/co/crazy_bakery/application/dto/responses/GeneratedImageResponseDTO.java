package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedImageResponseDTO {

    private String prompt;
    private String imageUrl;

}
