
package uan.edu.co.crazy_bakery.infrastructure.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "cost")
@Getter
@Setter
public class CostoProperties {

    private Labor labor = new Labor();
    private Operating operating = new Operating();
    private Benefit benefit = new Benefit();

    @Getter
    @Setter
    public static class Labor {
        private double value;
    }

    @Getter
    @Setter
    public static class Operating {
        private double value;
    }

    @Getter
    @Setter
    public static class Benefit {
        private double percentage;
    }
}
