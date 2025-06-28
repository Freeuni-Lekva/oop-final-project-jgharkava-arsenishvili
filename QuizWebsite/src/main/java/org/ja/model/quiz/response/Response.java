package org.ja.model.quiz.response;

import org.ja.model.OtherObjects.Match;
import java.util.ArrayList;
import java.util.List;

public class Response {
    private final List<Object> list;

    public Response(){
        list = new ArrayList<Object>();
    }

    public void addAnswer(String answer){
        list.add(answer);
    }

    public void addMatch(Match match){
        list.add(match);
    }

    public String getAnswer(int index){
        return (String) list.get(index);
    }

    public Match getMatch(int index){
        return (Match) list.get(index);
    }

    public int size(){
        return list.size();
    }

}
