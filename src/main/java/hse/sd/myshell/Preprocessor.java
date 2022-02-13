package hse.sd.myshell;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class which does variable substitutions
 */
public class Preprocessor {
    
    private final Logger logger = Logger.getLogger(Preprocessor.class.getName());

    /**
     * @param str string in which the variables will be substituted
     * @return result string with completed substitutions
     */
    public String process(String str) {
        if (str == null) { // but it can be empty
            logger.log(Level.WARNING, "Input string is null");
        }
        str = str + " "; // not to handle the end separately, will be deleted in the end of the method
        char leftQuote = '\0'; // means that we haven't met an opening quote yet
        boolean isCollectingVar = false; //true if we are collecting a variable
        StringBuilder variableName = new StringBuilder(), result = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char symbol = str.charAt(i);

            if (isCollectingVar) {
                if (Character.isDigit(symbol) && variableName.length() == 0) { // case with ${number}
                    result.setLength(result.length() - 1);
                    result.append(" ");
                    isCollectingVar = false;
                    continue;
                } else if (Character.isLetter(symbol) || Character.isDigit(symbol)) { // case with normal variable
                    variableName.append(symbol);
                    continue;
                }
                isCollectingVar = false;
                String variable = Environment.getVariableValue(variableName.toString());
                if (variableName.length() > 0) {
                    result.setLength(result.length() - 1);
                    if (variable == null) {
                        result.append(" ");
                    } else {
                        result.append(variable);
                    }
                }
                variableName.setLength(0);
            }

            if (symbol == '\"' || symbol == '\'') {
                if (leftQuote == '\0') {
                    leftQuote = symbol;
                } else if (leftQuote == symbol) {
                    leftQuote = '\0';
                }
            }

            if (symbol == '$' && (leftQuote == '\"' || leftQuote == '\0')) {
                isCollectingVar = true;
            }
            result.append(symbol);
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }
}
