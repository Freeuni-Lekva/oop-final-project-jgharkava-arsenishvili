package org.ja.model.quiz.response;

import org.ja.model.data.Match;
import java.util.ArrayList;
import java.util.List;

/**
 * A container class for storing a list of answers or matches related to quiz responses.
 * Supports adding and retrieving either String answers or Match objects.
 */
public class Response {
    private final List<Object> list;

    /**
     * Constructs an empty Response object.
     */
    public Response() {
        list = new ArrayList<>();
    }

    /**
     * Adds a String answer to the response list.
     *
     * @param answer the answer to add
     */
    public void addAnswer(String answer) {
        list.add(answer);
    }

    /**
     * Adds a Match object to the response list.
     *
     * @param match the Match to add
     */
    public void addMatch(Match match) {
        list.add(match);
    }

    /**
     * Retrieves the answer stored at the specified index.
     *
     * @param index the index of the answer
     * @return the answer at the given index
     * @throws ClassCastException if the object at the index is not a String
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public String getAnswer(int index) {
        return (String) list.get(index);
    }

    /**
     * Retrieves the Match stored at the specified index.
     *
     * @param index the index of the Match
     * @return the Match at the given index
     * @throws ClassCastException if the object at the index is not a Match
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Match getMatch(int index) {
        return (Match) list.get(index);
    }

    /**
     * Returns the number of elements in the response list.
     *
     * @return the size of the response list
     */
    public int size() {
        return list.size();
    }
}
