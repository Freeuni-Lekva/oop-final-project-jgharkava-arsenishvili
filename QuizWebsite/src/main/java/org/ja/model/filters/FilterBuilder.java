package org.ja.model.filters;

import org.ja.utils.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to build a composite {@link Filter} object based on HTTP request parameters.
 * <p>
 * It reads predefined filter parameter names from the request, creates corresponding filter instances,
 * and combines them into a logical structure of {@link AndFilter} and {@link OrFilter}.
 * </p>
 * <p>
 * The parameters supported are:
 * <ul>
 *   <li>USERNAME - combined with AND logic</li>
 *   <li>QUIZ_NAME - combined with AND logic</li>
 *   <li>CATEGORY - combined with OR logic</li>
 *   <li>TAG - combined with OR logic</li>
 *   <li>ORDER - combined with OR logic, ignoring placeholder values</li>
 * </ul>
 * </p>
 */
public class FilterBuilder {
    private static final String[] PARAMETER_NAMES = {
            Constants.FilterFields.QUIZ_NAME,
            Constants.FilterFields.CATEGORY,
            Constants.FilterFields.TAG,
            Constants.FilterFields.ORDER
    };

    /**
     * Builds a composite {@link Filter} object based on parameters found in the given HTTP request.
     * <p>
     * Username and quiz name filters are combined with AND logic,
     * while category, tag, and order filters are combined with OR logic.
     * </p>
     *
     * @param request the HTTP servlet request containing filter parameters
     * @return a {@link Filter} representing the combined filter conditions from the request parameters
     */
    public static Filter build(HttpServletRequest request) {
        OrFilter orFilter = new OrFilter();
        AndFilter andFilter = new AndFilter();

        for (String parameterName : PARAMETER_NAMES) {
            String[] values = request.getParameterValues(parameterName);

            if (values == null || values.length == 0) continue;

            switch (parameterName) {
                case Constants.FilterFields.QUIZ_NAME -> {
                    for (String value : values) {
                        if (value.trim().isEmpty()) continue;
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
                        if (Constants.FilterFields.ORDER_PLACEHOLDER.equals(value)) continue;
                        orFilter.addFilter(new OrderFilter(value, OrderFilter.DECREASING));
                    }
                }
            }
        }

        andFilter.addFilter(orFilter);

        return andFilter;
    }
}
