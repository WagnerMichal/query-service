package cz.muni.fi.pa053.query_service.service;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

@Service
public class ExpressionEvaluatorService {

    private final ExpressionParser parser = new SpelExpressionParser();

    public double evaluate(String expression) {
        try {
            Double value = parser.parseExpression(expression)
                    .getValue(Double.class);
            if (value == null) {
                throw new IllegalArgumentException("Expression did not return a number.");
            }
            return value;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid arithmetic expression.", ex);
        }
    }
}