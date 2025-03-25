package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.data.DecisionResponseDTO;
import ee.taltech.inbankbackend.exceptions.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    // Used to check for the validity of the presented ID code.
    private final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();
    private int creditModifier = 0;

    /**
     * Calculates the maximum loan amount and period for the customer based on their ID code,
     * the requested loan amount and the loan period.
     * The loan period must be between 12 and 60 months (inclusive).
     * The loan amount must be between 2000 and 10000€ months (inclusive).
     *
     * @param personalCode ID code of the customer that made the request.
     * @param loanAmount   Requested loan amount
     * @param loanPeriod   Requested loan period
     * @return A Decision object containing the approved loan amount and period, and an error message (if any)
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException   If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException   If the requested loan period is invalid
     * @throws NoValidLoanException         If there is no valid loan found for the given ID code, loan amount and loan period
     */
    public DecisionResponseDTO calculateApprovedLoan(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException,
            NoValidLoanException {
        verifyInputs(personalCode, loanAmount, loanPeriod);
        verifyAge(personalCode);

        int outputLoanAmount;
        creditModifier = getCreditModifier(personalCode);
        if (creditModifier == 0) {
            throw new NoValidLoanException("No valid loan found!");
        }

        //if we cannot approve such a loan decrease the months and money until we do (or don't)
        if (creditScore(creditModifier, loanAmount, loanPeriod) < DecisionEngineConstants.MINIMUM_CREDIT_SCORE) {
            Long originalAmount = loanAmount;
            boolean found = false;

            while (loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD) {
                loanAmount = originalAmount;
                while (loanAmount >= DecisionEngineConstants.MINIMUM_LOAN_AMOUNT) {
                    loanAmount -= 5;
                    if (creditScore(creditModifier, loanAmount, loanPeriod) >= 0.1) {
                        found = true;
                        break;
                    }
                }
                if (found) break;

                loanPeriod++;
            }

            // if we didn't find a suitable loan throw error
            if (!found) throw new NoValidLoanException("No valid loan found!");


        } else {
            // we add to the loan amount until the credit score gets too low
            while (creditScore(creditModifier, loanAmount, loanPeriod) > DecisionEngineConstants.MINIMUM_CREDIT_SCORE) {
                System.out.println();
                loanAmount += 5;
            }
        }


        outputLoanAmount = (int) Math.min(DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT, loanAmount);
        return new DecisionResponseDTO(outputLoanAmount, loanPeriod, null);
    }

    /**
     * Calculates the creditscore with given input
     *
     * @param creditModifier user's creditmodifier
     * @param loanAmount     user's loan size in euros
     * @param loanPeriod     loanperiod in months
     * @return creditscore with inputs
     */
    private double creditScore(int creditModifier, Long loanAmount, int loanPeriod) {
        return ((double) creditModifier / loanAmount) * loanPeriod / 10;
    }

    /**
     * Calculates the credit modifier of the customer to according to the last four digits of their ID code.
     * Debt - 0000...2499
     * Segment 1 - 2500...4999
     * Segment 2 - 5000...7499
     * Segment 3 - 7500...9999
     *
     * @param personalCode ID code of the customer that made the request.
     * @return Segment to which the customer belongs.
     */
    private int getCreditModifier(String personalCode) {
        int segment = Integer.parseInt(personalCode.substring(personalCode.length() - 4));
        if (DecisionEngineConstants.CREDIT_MODIFIERS.containsKey(segment)) {
            return DecisionEngineConstants.CREDIT_MODIFIERS.get(segment);
        }

        throw new NoValidLoanException("No valid loan found!");
    }

    /**
     * Verify that all inputs are valid according to business rules.
     * If inputs are invalid, then throws corresponding exceptions.
     *
     * @param personalCode Provided personal ID code
     * @param loanAmount   Requested loan amount
     * @param loanPeriod   Requested loan period
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException   If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException   If the requested loan period is invalid
     */
    private void verifyInputs(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_AMOUNT <= loanAmount)
                || !(loanAmount <= DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT)) {
            throw new InvalidLoanAmountException("Invalid loan amount!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_PERIOD <= loanPeriod)
                || !(loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD)) {
            throw new InvalidLoanPeriodException("Invalid loan period!");
        }

    }

    /**
     * returns if the given age is suitable
     * @param personalCode personalCode to check
     */
    private void verifyAge(String personalCode) {
        int[] centuries = {1800, 1900, 2000};

        int firstDigit = Integer.parseInt(personalCode.substring(0, 1));
        int year = centuries[(firstDigit - 1) / 2] + Integer.parseInt(personalCode.substring(1, 3));
        int month = Integer.parseInt(personalCode.substring(3, 5));
        int day = Integer.parseInt(personalCode.substring(5, 7));

        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentTime = LocalDate.now();

        int age = Period.between(birthDate, currentTime).getYears();
        if (age < DecisionEngineConstants.MINIMUM_AGE || age >= DecisionEngineConstants.EXPECTED_LIFESPAN) {
            throw new InvalidAgeException("Invalid age for loan.");
        }
    }
}
