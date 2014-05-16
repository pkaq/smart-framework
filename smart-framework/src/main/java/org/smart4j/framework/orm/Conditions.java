package org.smart4j.framework.orm;

import java.util.ArrayList;
import java.util.List;
import org.smart4j.framework.util.StringUtil;

public class Conditions {

    private List<Condition> conditionList = new ArrayList<Condition>();

    public Conditions condition(String name, String operator, String value) {
        return append(name, operator, value);
    }

    public Conditions and() {
        return append("and");
    }

    public Conditions or() {
        return append("or");
    }

    public Conditions left() {
        return append("(");
    }

    public Conditions right() {
        return append(")");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Condition condition : conditionList) {
            String name = condition.getName().trim();
            String operator = condition.getOperator().trim();
            String value = condition.getValue().trim();
            if (StringUtil.isNotEmpty(name)) {
                builder.append(StringUtil.camelhumpToUnderline(name));
            }
            if (StringUtil.isNotEmpty(operator)) {
                builder.append(" ").append(operator.trim()).append(" ");
            }
            if (StringUtil.isNotEmpty(value)) {
                if (value.contains("?")) {
                    builder.append(value);
                } else {
                    builder.append("'").append(value).append("'");
                }
            }
        }
        return builder.toString().trim().replaceAll("\\s{2}+", " ");
    }

    private Conditions append(String name, String operator, String value) {
        Condition condition = new Condition(name, operator, value);
        conditionList.add(condition);
        return this;
    }

    private Conditions append(String operator) {
        return append("", operator, "");
    }

    private class Condition {

        private String name;
        private String operator;
        private String value;

        private Condition(String name, String operator, String value) {
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getOperator() {
            return operator;
        }

        public String getValue() {
            return value;
        }
    }

    public static void main(String[] args) {
        Conditions conditions = new Conditions()
            .condition("fizzBuzz", "=", "1")
            .and()
            .left()
            .condition("buzzWhizz", "=", "2")
            .or()
            .condition("fizzWhizz", "=", "3")
            .right();

        System.out.println(conditions.toString());
    }
}
