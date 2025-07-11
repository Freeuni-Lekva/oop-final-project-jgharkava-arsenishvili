package org.ja.servlet;

import org.ja.dao.*;
import org.ja.model.data.Tag;
import org.ja.model.data.Answer;
import org.ja.model.data.Match;
import org.ja.model.data.QuizTag;
import org.ja.model.quiz.Quiz;
import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;

import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;


/**
 * Servlet responsible for finalizing quiz creation.
 *
 * <p>
 * This servlet processes the quiz, questions, answers, matches, and tags stored
 * in the user's session, persists them to the database, clears the session data,
 * and then forwards the user to the quiz overview page.
 * </p>
 *
 * <p>
 * Expected session attributes:
 * <ul>
 *   <li>QUESTIONS: Map<Question, List<Answer>> — all created questions and their answers</li>
 *   <li>MATCHES: Map<Question, List<Match>> — all matching questions and their pairs</li>
 *   <li>QUIZ: Quiz — the quiz object being created</li>
 *   <li>TAGS_TO_ADD: List<Long> — IDs of tags associated with the quiz</li>
 *   <li>TAG_TO_CREATE: Tag — a new tag to create and associate with the quiz</li>
 * </ul>
 * </p>
 */
@WebServlet("/finish-quiz")
public class FinishQuizServlet extends HttpServlet {


    /**
     * Handles the POST request to finish and save a quiz.
     *
     * <p>The method:
     * <ul>
     *   <li>Retrieves quiz data, questions, answers, matches, and tags from the session</li>
     *   <li>Calculates and sets the total score (sum of all answers/matches counts)</li>
     *   <li>Inserts the quiz, questions, answers, matches, and tags into the database</li>
     *   <li>Resets the related session attributes</li>
     *   <li>Forwards to the quiz overview page showing the newly created quiz</li>
     * </ul>
     * </p>
     *
     * @param request  the HttpServletRequest containing session and request parameters
     * @param response the HttpServletResponse to forward or redirect the client
     * @throws IOException      if an input or output error occurs while handling the request
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        QuizzesDao quizzesDao = (QuizzesDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZZES_DAO);
        QuizTagsDao quizTagsDao = (QuizTagsDao) getServletContext().getAttribute(Constants.ContextAttributes.QUIZ_TAG_DAO);
        TagsDao tagsDao = (TagsDao) getServletContext().getAttribute(Constants.ContextAttributes.TAGS_DAO);
        QuestionDao questionDao = (QuestionDao) getServletContext().getAttribute(Constants.ContextAttributes.QUESTIONS_DAO);
        MatchesDao matchesDao = (MatchesDao) getServletContext().getAttribute(Constants.ContextAttributes.MATCHES_DAO);
        AnswersDao answersDao = (AnswersDao) getServletContext().getAttribute(Constants.ContextAttributes.ANSWERS_DAO);

        Map<Question, List<Answer>> questionAnswerMap =
                (Map<Question, List<Answer>>) session.getAttribute(Constants.SessionAttributes.QUESTIONS);
        Map<Question, List<Match>> questionMatchMap =
                (Map<Question, List<Match>>) session.getAttribute(Constants.SessionAttributes.MATCHES);
        Quiz quiz = (Quiz) session.getAttribute(Constants.SessionAttributes.QUIZ);
        List<Long> tagIds = (List<Long>) session.getAttribute(Constants.SessionAttributes.TAGS_TO_ADD);
        Tag tagToAdd = (Tag) session.getAttribute(Constants.SessionAttributes.TAG_TO_CREATE);

        // resetting data stored
        session.setAttribute(Constants.SessionAttributes.QUESTIONS, new HashMap<Question, List<Answer>>());
        session.setAttribute(Constants.SessionAttributes.MATCHES, new HashMap<Question, List<Match>>());
        session.setAttribute(Constants.SessionAttributes.QUIZ, null);
        session.setAttribute(Constants.SessionAttributes.TAGS_TO_ADD, null);
        session.setAttribute(Constants.SessionAttributes.TAG_TO_CREATE, null);
        session.setAttribute(Constants.SessionAttributes.HAS_QUESTIONS, false);

        if (quiz == null) return;

        int score = questionAnswerMap.keySet().stream()
                    .mapToInt(Question::getNumAnswers)
                    .sum();

        score += questionMatchMap.keySet().stream()
                .mapToInt(Question::getNumAnswers)
                .sum();

        quiz.setScore(score);
        quizzesDao.insertQuiz(quiz);

        if (tagToAdd != null) {
            tagsDao.insertTag(tagToAdd);
            tagIds.add(tagToAdd.getTagId());
        }

        Optional.ofNullable(tagIds)
                .stream()
                .flatMap(Collection::stream)
                .map(tagId -> new QuizTag(quiz.getId(), tagId))
                .forEach(quizTagsDao::insertQuizTag);


        Optional.of(questionAnswerMap)
                .filter(map -> !map.isEmpty())
                .ifPresent(map -> map.forEach((question, answers) -> {
                    question.setQuizId(quiz.getId());
                    questionDao.insertQuestion(question);

                    answers.forEach(answer -> {
                        answer.setQuestionId(question.getQuestionId());
                        answersDao.insertAnswer(answer);
                    });
                }));

        Optional.of(questionMatchMap)
                .filter(map -> !map.isEmpty())
                .ifPresent(map -> map.forEach((question, matches) -> {
                    question.setQuizId(quiz.getId());
                    questionDao.insertQuestion(question);

                    matches.forEach(match -> {
                        match.setQuestionId(question.getQuestionId());
                        matchesDao.insertMatch(match);
                    });
                }));


        request.getRequestDispatcher("quiz-overview.jsp?"+Constants.RequestParameters.QUIZ_ID+"="+quiz.getId()).forward(request, response);
    }
}
