import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Client {
    private final Socket socket;
    private final QuestionsDB questionsDB;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public Client(Socket socket, QuestionsDB database, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.questionsDB = database;
        this.in = in;
        this.out = out;
    }

    public void play() {
        try {
            out.writeObject("Bem-vindo ao Jogo de Perguntas e Respostas!");
            out.flush();

            // get all the questions from the database
            List<Question> questions = questionsDB.getAllQuestions();
            Question question;
            int max = questions.size();
            int idx = 0;

            // iterate over the questions
            while (true) {    

                // if its the last question, ends            
                if (idx >= max) {
                    out.writeObject("FIM"); 
                    out.flush();
                    break;
                }

                question = questions.get(idx);
                idx += 1;
                // show the question
                out.writeObject(question);
                out.flush();

                // gets the answer give by the user
                int clientAnswer = (int) in.readObject() + 1;

                if (clientAnswer == question.correct) {
                    out.writeObject("Resposta correta!");
                } else {
                    out.writeObject("Resposta incorreta. A resposta certa era a " + (question.correct) + ".");
                }
                out.flush();

                // asks if the user wants to keep going
                if (idx < max) {
                    out.writeObject("Deseja jogar novamente? (s/n)");
                    out.flush();
                }

                // if yes, next question
                String continuar = (idx < max) ? (String) in.readObject() : "s";
                if (!continuar.equalsIgnoreCase("s")) {
                    out.writeObject("FIM"); 
                    out.flush();
                    break;
                }
            }
            out.writeObject("FIM");
            out.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[CLIENTE HANDLER] Cliente desconectado: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}

