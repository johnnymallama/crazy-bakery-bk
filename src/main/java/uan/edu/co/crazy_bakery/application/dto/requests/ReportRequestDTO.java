package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private String reportId;

}
