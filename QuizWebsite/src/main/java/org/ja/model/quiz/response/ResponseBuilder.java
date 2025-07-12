package org.ja.model.quiz.response;

import org.ja.model.data.Match;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for constructing a list of Response objects from HTTP request parameters.
 * The parameters are expected to follow a naming pattern like "response_{questionIndex}_{suffix}".
 */
public class ResponseBuilder {

    /**
     * Builds a list of Response objects based on request parameters.
     * The method filters parameters starting with "response_", sorts them
     * by question index and suffix, and then parses them into Response objects.
     *
     * The suffix determines whether the parameter is treated as an answer (numeric suffix)
     * or a match (non-numeric suffix).
     *
     * @param request the HTTP request containing response parameters
     * @return a List of Response objects constructed from the request parameters
     */
    public static List<Response> buildResponse(HttpServletRequest request){
        List<String> parametersList = Collections.list(request.getParameterNames());

        List<String> parametersSorted = parametersList.stream()
                .filter(name -> name.startsWith("response_"))
                .sorted((a, b) -> {
                    int idA = extractMiddleNumber(a);
                    int idB = extractMiddleNumber(b);

                    if (idA != idB) return Integer.compare(idA, idB);

                    String suffixA = extractSuffix(a);
                    String suffixB = extractSuffix(b);

                    boolean isNumA = isNumeric(suffixA);
                    boolean isNumB = isNumeric(suffixB);

                    if (isNumA && isNumB) return Integer.compare(Integer.parseInt(suffixA), Integer.parseInt(suffixB));
                    else if (isNumA) return -1;
                    else if (isNumB) return 1;
                    return suffixA.compareTo(suffixB);

                })
                .toList();

        List<Response> responses = new ArrayList<>();

        for (String currParameter : parametersSorted) {
            int questionIndex = extractMiddleNumber(currParameter);
            String suffix = extractSuffix(currParameter);

            // Add new Response objects if needed to reach the current question index
            while (questionIndex > responses.size()) {
                responses.add(new Response());
            }

            // Add answer or match based on suffix type
            if (isNumeric(suffix))
                responses.get(responses.size() - 1).addAnswer(request.getParameter(currParameter));
            else
                responses.get(responses.size() - 1).addMatch(new Match(suffix, request.getParameter(currParameter)));
        }

        return responses;
    }

    /**
     * Extracts the middle number from a parameter name string.
     * Expected format: "response_<number>_<suffix>"
     * Returns Integer.MAX_VALUE on failure to ensure such parameters sort last.
     *
     * @param s the parameter name string
     * @return the extracted number, or Integer.MAX_VALUE if parsing fails
     */
    private static int extractMiddleNumber(String s){
        try {
            int start = s.indexOf('_') + 1;
            int end = s.indexOf('_', start);

            return Integer.parseInt(s.substring(start, end));
        } catch (Exception e){
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Extracts the suffix from a parameter name string.
     * Expected format: "response_<number>_<suffix>"
     *
     * @param s the parameter name string
     * @return the suffix substring, or an empty string if none found
     */
    private static String extractSuffix(String s){
        int underScore = s.lastIndexOf('_') + 1;
        return underScore != 0 ? s.substring(underScore) : "";
    }

    /**
     * Checks if a string represents a valid integer number.
     *
     * @param s the string to check
     * @return true if the string can be parsed as an integer, false otherwise
     */
    private static boolean isNumeric(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
