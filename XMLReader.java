import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLReader {
    public static List<Question> readQuestionsFromXML(String filePath) {
        List<Question> questions = new ArrayList<>();
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("pregunta");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Node textNode = eElement.getElementsByTagName("text").item(0);
                    String questionText = (textNode != null) ? textNode.getTextContent() : "";

                    Node correctNode = eElement.getElementsByTagName("correcta").item(0);
                    String correctAnswer = (correctNode != null) ? correctNode.getTextContent() : "";

                    List<String> options = new ArrayList<>();
                    NodeList optionsList = eElement.getElementsByTagName("respostes").item(0).getChildNodes();
                    if (optionsList != null && optionsList.getLength() > 0) {
                        for (int i = 0; i < optionsList.getLength(); i++) {
                            Node optionNode = optionsList.item(i);
                            if (optionNode.getNodeType() == Node.ELEMENT_NODE) {
                                options.add(optionNode.getTextContent());
                            }
                        }
                    }

                    questions.add(new Question(questionText, options, correctAnswer));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
