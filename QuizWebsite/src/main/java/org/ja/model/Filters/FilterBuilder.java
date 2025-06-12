package org.ja.model.Filters;

import org.ja.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FilterBuilder {

    public static Filter build(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();

        OrFilter orFilter = new OrFilter();
        AndFilter andFilter = new AndFilter();

        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();

            String[] values = request.getParameterValues(parameterName);

            switch (parameterName) {
                case Constants.FilterFields.USERNAME -> {
                    for (String value : values) andFilter.addFilter(new UserNameFilter(value));
                }
                case Constants.FilterFields.QUIZ_NAME -> {
                    for (String value : values) andFilter.addFilter(new QuizNameFilter(value));
                }
                case Constants.FilterFields.CATEGORY -> {
                    for (String value : values) orFilter.addFilter(new CategoryFilter(value));
                }
                case Constants.FilterFields.TAG -> {
                    for (String value : values) orFilter.addFilter(new TagFilter(value));
                }
                case Constants.FilterFields.ORDER -> {
                    for (String value : values) orFilter.addFilter(new OrderFilter(value, OrderFilter.DECREASING));
                }
            }
        }

        andFilter.addFilter(orFilter);

        return andFilter;
    }
}
