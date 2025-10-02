import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    // Campos obrigatórios
    public final String id;       // gerado de forma determinística
    public final String topic;    // tema da pergunta (ex: "Java")
    public String statement;     // enunciado da pergunta
    public final List<String> options; // alternativas (ordem importa)
    public final int correct;     // índice da alternativa correta (0-based)

    // Construtor principal
    public Question(String topic, String text, List<String> options, int correct) {
        this.id = generateId(topic, text, options, correct);
        this.topic = topic;
        this.statement = text;
        this.options = List.copyOf(options);
        this.correct = correct;
    }

    // ----------------------------
    // Geração de ID determinístico
    // ----------------------------
    private static String generateId(String topic, String text, List<String> options, int correct) {
        String key = topic + "|" + text + "|" + String.join(";", options) + "|" + correct;
        return Integer.toHexString(key.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question question = (Question) obj;
        return statement.equals(question.statement);
    }

    @Override
    public int hashCode() {
        return statement.hashCode();
    }

    public void addSuffix(String suffix) {
        this.statement += suffix;
    }
}

