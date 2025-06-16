package org.ja.model.OtherObjects;
/*
create table matches(
    match_id bigint primary key auto_increment,
    question_id bigint not null,
    left_match text not null,
    right_match text not null,

    foreign key (question_id) references questions(question_id) on delete cascade
);

 */
public class Match {
    private long matchId;
    private long questionId;
    private String leftMatch;
    private String rightMatch;

    // Empty constructor
    public Match() {}

    public Match(String leftMatch, String rightMatch){
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
    }

    // Constructor with all parameters
    public Match(long matchId, long questionId, String leftMatch, String rightMatch) {
        this.matchId = matchId;
        this.questionId = questionId;
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
    }

    // Getters and Setters

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getLeftMatch() {
        return leftMatch;
    }

    public void setLeftMatch(String leftMatch) {
        this.leftMatch = leftMatch;
    }

    public String getRightMatch() {
        return rightMatch;
    }

    public void setRightMatch(String rightMatch) {
        this.rightMatch = rightMatch;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return questionId==((Match) o).getQuestionId()
                &&leftMatch.equals(((Match) o).getLeftMatch())&&rightMatch.equals(((Match) o).getRightMatch());
    }
}
