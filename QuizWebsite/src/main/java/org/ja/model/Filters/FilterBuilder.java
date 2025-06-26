package org.ja.model.Filters;

import org.ja.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FilterBuilder {
    private static final String[] PARAMETER_NAMES = {Constants.FilterFields.USERNAME,
                                                     Constants.FilterFields.QUIZ_NAME,
                                                     Constants.FilterFields.CATEGORY,
                                                     Constants.FilterFields.TAG,
                                                     Constants.FilterFields.ORDER};

    public static Filter build(HttpServletRequest request) {
        OrFilter orFilter = new OrFilter();
        AndFilter andFilter = new AndFilter();

        for (String parameterName : PARAMETER_NAMES) {
            String[] values = request.getParameterValues(parameterName);

            if(values == null || values.length == 0) continue;

            switch (parameterName) {
                case Constants.FilterFields.USERNAME -> {
                    for (String value : values) {
                        if(value.trim().isEmpty()) continue;
                        andFilter.addFilter(new UserNameFilter(value.trim()));
                    }
                }
                case Constants.FilterFields.QUIZ_NAME -> {
                    for (String value : values) {
                        if(value.trim().isEmpty()) continue;
                        andFilter.addFilter(new QuizNameFilter(value.trim()));
                    }
                }
                case Constants.FilterFields.CATEGORY -> {
                    for (String value : values) orFilter.addFilter(new CategoryFilter(value));
                }
                case Constants.FilterFields.TAG -> {
                    for (String value : values) orFilter.addFilter(new TagFilter(value));
                }
                case Constants.FilterFields.ORDER -> {
                    for (String value : values) {
                        if(Constants.FilterFields.ORDER_PLACEHOLDER.equals(value)) continue;
                        orFilter.addFilter(new OrderFilter(value, OrderFilter.DECREASING));
                    }
                }
            }
        }

        andFilter.addFilter(orFilter);

        return andFilter;
    }
}
