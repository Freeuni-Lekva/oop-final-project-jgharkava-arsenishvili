package org.ja.servlet;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet("/discard-quiz")
public class DiscardQuizServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        session.setAttribute(Constants.SessionAttributes.QUESTIONS, new HashMap<Question, List<Answer>>());
        session.setAttribute(Constants.SessionAttributes.MATCHES, new HashMap<Question, List<Match>>());
        session.setAttribute(Constants.SessionAttributes.QUIZ, null);
        session.setAttribute(Constants.SessionAttributes.TAGS_TO_ADD, null);
        session.setAttribute(Constants.SessionAttributes.TAG_TO_CREATE, null);
        session.setAttribute(Constants.SessionAttributes.HAS_QUESTIONS, false);

        response.sendRedirect("user-page.jsp");
    }
}
