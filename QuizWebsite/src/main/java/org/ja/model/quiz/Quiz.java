package org.ja.model.quiz;
import java.sql.Timestamp;
import java.time.Instant;

/*
create table quizzes(
    quiz_id bigint primary key auto_increment,
    quiz_name varchar(64) unique not null,
    quiz_description text,
    quiz_score int not null,
    average_rating double not null default 0,
    participant_count bigint not null default 0,
    creation_date timestamp default current_timestamp,
    time_limit_in_minutes int not null default 0,
    category_id bigint not null,
    creator_id bigint not null,
    question_order_status enum('ordered', 'randomized') not null default 'ordered',
    question_placement_status enum('one-page', 'multiple-page') not null default 'one-page',
    question_correction_status enum('immediate-correction', 'final-correction')
        not null default 'final-correction',

    check (
        question_placement_status != 'one-page'
        or question_correction_status = 'final-correction'
    ),

    foreign key (creator_id) references users(user_id) on delete cascade,
    foreign key (category_id) references categories(category_id) on delete cascade
);
 */
public class Quiz {
    private long id;
    private String name;
    private String description;
    private double avgRating;
    private int quizScore;
    private long participantCount;
    private Timestamp creationDate;
    private int timeInMinutes;
    private long categoryId;
    private long creatorId;
    private String questionOrder;
    private  String questionPlacement;
    private String questionCorrection;
    public Quiz(){

    }
    public Quiz(long id, String name, String description, int quizScore, double avgRating,
                long participantCount, Timestamp creationDate, int timeInMinutes,
                long categoryId, long creatorId, String questionOrder,
                String questionPlacement, String questionCorrection) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quizScore = quizScore;
        this.avgRating = avgRating;
        this.participantCount = participantCount;
        this.creationDate = creationDate;
        this.timeInMinutes = timeInMinutes;
        this.categoryId = categoryId;
        this.creatorId = creatorId;
        this.questionOrder = questionOrder;
        this.questionPlacement = questionPlacement;
        this.questionCorrection = questionCorrection;
    }

    public Quiz(String name, String description, int timeInMinutes,
                long categoryId, long creatorId, String questionOrder,
                String questionPlacement, String questionCorrection) {
        this.id = 0L;
        this.name = name;
        this.description = description;
        this.quizScore = 0;
        this.avgRating = 0;
        this.participantCount = 0;
        this.creationDate = Timestamp.from(Instant.now());
        this.timeInMinutes = timeInMinutes;
        this.categoryId = categoryId;
        this.creatorId = creatorId;
        this.questionOrder = questionOrder;
        this.questionPlacement = questionPlacement;
        this.questionCorrection = questionCorrection;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}
    public int getScore() {
        return quizScore;
    }
    public void setScore(int quizScore) {this.quizScore = quizScore;}
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getAvgRating() {
        return avgRating;
    }
    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }
    public long getParticipantCount() {
        return participantCount;
    }
    public void setParticipantCount(long participantCount) {
        this.participantCount = participantCount;
    }
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    public int getTimeInMinutes() {
        return timeInMinutes;
    }
    public void setTimeInMinutes(int timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }
    public long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
    public long getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }
    public String getQuestionOrder() {
        return questionOrder;
    }
    public void setQuestionOrder(String questionOrder) {
        this.questionOrder = questionOrder;
    }
    public String getQuestionPlacement() {
        return questionPlacement;
    }
    public void setQuestionPlacement(String questionPlacement) {
        this.questionPlacement = questionPlacement;
    }
    public String getQuestionCorrection() {
        return questionCorrection;
    }
    public void setQuestionCorrection(String questionCorrection) {
        this.questionCorrection = questionCorrection;
    }

    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Quiz)) return false;

        return id == ((Quiz) o).getId() &&
                name.equals(((Quiz) o).getName()) &&
                description.equals(((Quiz) o).getDescription()) &&
                avgRating == ((Quiz) o).getAvgRating() &&
                quizScore == ((Quiz) o).getScore() &&
                participantCount == ((Quiz) o).getParticipantCount() &&
                creationDate.equals(((Quiz) o).getCreationDate()) &&
                timeInMinutes == ((Quiz) o).getTimeInMinutes() &&
                categoryId == ((Quiz) o).getCategoryId() &&
                creatorId == ((Quiz) o).getCreatorId() &&
                questionOrder.equals(((Quiz) o).getQuestionOrder()) &&
                questionPlacement.equals(((Quiz) o).getQuestionPlacement()) &&
                questionCorrection.equals(((Quiz) o).getQuestionCorrection());
    }

}
