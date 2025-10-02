import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuestionsDB {
    private final List<Question> questions = new CopyOnWriteArrayList<>();

    public QuestionsDB() {
        loadInitialQuestions();
    }

    // load the questions
    private void loadInitialQuestions() {
        System.out.println("[BANCO] Carregando perguntas locais...");
        questions.add(new Question(
            "Cultura",
            "Para onde a Carreta Furacão segue?",
            List.of("1. Em frente", "2. Para a direita", "3. Para a esquerda", "4. Para trás"),
            1
        ));
        questions.add(new Question(
            "Cultura",
            "Utilizando a resposta da primeira, para onde a Carreta Furacão recomenda olhar",
            List.of("1. Para frente", "2. Para cima", "3. Para o lado", "4. Para trás"),
            3 
        ));
        questions.add(new Question(
            "Cinema",
            "O acordo proposto pelo Aldo Raine para Utivich é um bom acordo?",
            List.of("1. Não", "2. Sim", "3. Um pouco"),
            2
        ));
        questions.add(new Question(
            "Filmes",
            "Em 'Matrix', qual pílula Neo escolhe tomar?",
            List.of("1. Azul", "2. Vermelha", "3. Verde", "4. Amarela"),
            2
        ));
        System.out.println("[BANCO] " + questions.size() + " perguntas carregadas.");
    }

    // add questions, adding a suffix to prevent duplicates
    public synchronized void addQuestions(List<Question> newQuestions) {
        int added = 0;
        Question suffixQuestion;
        for (Question newQuestion : newQuestions) {
            if (this.questions.contains(newQuestion)) {
                int num = 1;
                do {
                    suffixQuestion = newQuestion;
                    suffixQuestion.addSuffix(" (" + num + ")");
                    num += 1;
                } while (this.questions.contains(suffixQuestion));
            }
            this.questions.add(newQuestion);
            added++;
        }
        if (added > 0) {
            System.out.println("[BANCO] " + added + " novas perguntas foram adicionadas ao banco.");
        }
    }

    // get all questions
    public List<Question> getAllQuestions() {
        return new ArrayList<>(this.questions); 
    }

    public Question getRandomQuestion() {
        if (questions.isEmpty()) {
            return null;
        }
        List<Question> copy = getAllQuestions();
        Collections.shuffle(copy);
        return copy.get(0);
    }
}

