package org.ja.model.quiz.response;

import org.ja.model.OtherObjects.Match;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResponseBuilder {

    //response_1_1 other question types
    //response_1_banana matching

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

            while (questionIndex > responses.size()) {
                responses.add(new Response());
            }

            if (isNumeric(suffix))
                responses.get(responses.size() - 1).addAnswer(request.getParameter(currParameter));
            else
                responses.get(responses.size() - 1).addMatch(new Match(suffix, request.getParameter(currParameter)));
        }


        return responses;
    }

    private static int extractMiddleNumber(String s){
        try {
            int start = s.indexOf('_') + 1;
            int end = s.indexOf('_', start);

            return Integer.parseInt(s.substring(start, end));
        } catch (Exception e){
            return Integer.MAX_VALUE;
        }
    }

    private static String extractSuffix(String s){
        int underScore = s.lastIndexOf('_') + 1;
        return underScore != 0 ? s.substring(underScore) : "";
    }

    private static boolean isNumeric(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
