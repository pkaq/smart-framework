package org.smart4j.framework.orm;

import java.util.ArrayList;
import java.util.List;
import org.smart4j.framework.util.StringUtil;

public class Sorts {

    private List<Sort> sortList = new ArrayList<Sort>();

    public Sorts sort(String name, String style) {
        Sort sort = new Sort(name, style);
        sortList.add(sort);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Sort sort : sortList) {
            String name = sort.getName().trim();
            String style = sort.getStyle().trim();
            if (StringUtil.isNotEmpty(name)) {
                builder.append(StringUtil.camelhumpToUnderline(name));
            }
            if (StringUtil.isNotEmpty(style)) {
                builder.append(" ").append(style);
            } else {
                builder.append(" asc");
            }
            if (count++ < sortList.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString().trim();
    }

    private class Sort {

        private String name;
        private String style;

        private Sort(String name, String style) {
            this.name = name;
            this.style = style;
        }

        public String getName() {
            return name;
        }

        public String getStyle() {
            return style;
        }
    }

    public static void main(String[] args) {
        Sorts sorts = new Sorts()
            .sort("fizzBuzz", "asc")
            .sort("buzzWhizz", "desc")
            .sort("fizzWhizz", "");
        System.out.println(sorts.toString());
    }
}
