package com.example.springpractice.spel;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

@Service
public class ExpressionService {
    private SpelExpressionParser expressionParser;
    private MethodProvider methodProvider;

    public ExpressionService() {
        expressionParser = new SpelExpressionParser();
        methodProvider = new MethodProvider();
    }

    public void eval(MyLog log, String expr) {
        SpelExpression expression = (SpelExpression) expressionParser.parseExpression(expr);

        StandardEvaluationContext spelContext = new StandardEvaluationContext();
        spelContext.addPropertyAccessor(new MapAccessor());
        spelContext.addPropertyAccessor(new ReflectivePropertyAccessor());

        for (Field field : MyLog.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(log);
                if (field.getType() == Map.class) {
                    spelContext.setVariables((Map<String, Object>) value);
                } else {
                    spelContext.setVariable(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                System.out.println("get " + field.getName() + " failed.");
            }
        }

        expression.setEvaluationContext(spelContext);
        expression.getValue(methodProvider, String.class);
    }
}