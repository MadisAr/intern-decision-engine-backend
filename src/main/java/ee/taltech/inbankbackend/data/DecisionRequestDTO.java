package ee.taltech.inbankbackend.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holds the request data of the REST endpoint
 */
@Getter
@AllArgsConstructor
public class DecisionRequestDTO {
    private String personalCode;
    private Long loanAmount;
    private int loanPeriod;
}
