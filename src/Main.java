import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.util.Map;

public class Main {



    public static int pc; // Program counter to track the current line number in the code
    public static String[] lines; // Array to store lines of code from the source file
    public static Map<String, Integer> variables; // Map to store variables and their values
    public static String currentLine; // Holds the current line of code being processed
    public static Stack<Trip> stack; // Stack to manage loop states (Trip objects)
    public static Scanner scanner; // Scanner to read user input during execution


    public static class SyntaxErrorException extends Exception {
        public SyntaxErrorException(String message) {
            super(message);
            // Pass the error message to the superclass (Exception)
        }
    }

    // This method adds spaces around operators to normalize the input for easier processing.
// It uses regular expressions to match and add spaces around common operators and symbols.
// Additionally, it replaces specific operator combinations like "= =" with "==",
// "! =" with "!=" and handles cases like "input ()" to "input()".
    public static String addSpacesAroundOperators(String input) {
        String regex = "([!+\\-%*/<>()=\\[\\]])";
        return input.replaceAll(regex, " $1 ")
                .replaceAll("\\s+", " ")
                .replaceAll("= =", "==")
                .replaceAll("! =", "!=")
                .replaceAll("> =", ">=")
                .replaceAll("< =", "<=")
                .replaceAll("& &", "&&")
                .replaceAll("\\| \\|", "||")
                .replaceAll("input \\( \\)", "input()").trim();
    }

// This method processes the input string, replacing variable names with their values.
// It uses the `addSpacesAroundOperators` method to standardize the input format and then
// checks each word to see if it's a defined variable. If a variable is found, it replaces
// it with its corresponding value. If a variable is undefined, a `SyntaxErrorException` is thrown.

    public static String evaluateVariables(String input) throws SyntaxErrorException {

        String[] exp = addSpacesAroundOperators(input).split(" ");

        for(int k = 0; k < exp.length; k++){

            String var = exp[k];

            if(Objects.equals(var, "input()")){
                int nmb = scanner.nextInt();
                exp[k] = Integer.toString(nmb);
                continue;
            }

            if(var.isEmpty() || !Character.isLetter(var.charAt(0)))
                continue;

            if(!variables.containsKey(var))
                throw new SyntaxErrorException("Variable " + var + " is not defined on line: " + pc
                        + "\nLine: " + currentLine);

            exp[k] = variables.get(var).toString();

        }

        return String.join(" ", exp);

    }
// This method evaluates a mathematical or logical expression after substituting
// any variables with their values. It first calls the `evaluateVariables` method
// to process the input, then uses the `ExpressionEvaluator` class to evaluate the
// expression. If there is an arithmetic error (e.g., division by zero), it throws
// a custom error with the details of the issue.

    public static Integer evaluateExpression(String input) throws SyntaxErrorException {


        String expression = evaluateVariables(input);
        Integer res;

        try {

            res = ExpressionEvaluator.evaluateExpression(expression);
        }
        catch (ArithmeticException e) {

            throw new ArithmeticException("Expression Error: " + e.getMessage() + "\nOn line:" + pc + "\nLine: " + currentLine);
        }

        return res;

    }
// This method processes an assignment statement. It extracts the variable name and the
// expression being assigned to it. The expression is evaluated, and if valid, the
// result is stored in the `variables` map. If the expression is empty, a `SyntaxErrorException` is thrown.

    public static void processAssignment(String inputLine) throws SyntaxErrorException {

        String line = addSpacesAroundOperators(inputLine);

        String variableName = line.substring(0, line.indexOf("=")).strip();

        String expression = line.substring(line.indexOf("=") + 1).strip();

        if (expression.isEmpty()) {
            throw new SyntaxErrorException("Nothing is being assigned on line: " + pc + "\nLine: " + currentLine);
        }

        Integer val = evaluateExpression(expression);

        variables.put(variableName, val);

    }
// This method returns the leading tabs (indentation) of a given string. It checks each
// character in the input string to count how many consecutive tab characters ('\t')
// appear at the beginning of the string and returns that portion of the string.

    public static String leadingTabs(String str) {
        String lt = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\t') {
                lt = lt + "\t";
            } else {
                break;
            }
        }

        return lt;
    }

    public static boolean checkTabs(String lt1){

        if (Objects.equals(lt1, "")){
            return true;
        }

        String lt = lt1.substring(0, lt1.length() - 1);

        int pc2 = pc - 1;

        while(pc2 >= 0){

            boolean cond = ((lines[pc2].startsWith(lt + "if")) || (lines[pc2].startsWith(lt + "while"))
                    || (lines[pc2].startsWith(lt + "else")));

            if (lines[pc2].startsWith(lt) && !lines[pc2].startsWith(lt1))
                return cond;

            pc2--;

        }

        return false;

    }
// This method checks if the indentation (tabs) of a given line follows the correct
// structure based on previous lines. It ensures that lines inside control structures
// (like `if`, `while`, or `else`) are indented correctly according to their context.
// If the indentation is inconsistent, it returns `false`, otherwise `true`.

    public static void checkSyntax() throws SyntaxErrorException {

        for(int k = 0; k < lines.length; k++) {

            pc = k;

            String lt = leadingTabs(lines[pc]);

            String line = lines[pc].trim();

            if(!checkTabs(lt))
                throw new SyntaxErrorException("Line " + line + " tabs don't align");

            if (line.startsWith("if")) {

                if (!line.endsWith(":"))
                    throw new SyntaxErrorException("Statement must end with ':' on line: " + pc + "\nLine: " + line);

                if (pc == lines.length - 1 || !lines[pc + 1].startsWith(lt + "\t"))
                    throw new SyntaxErrorException("Nothing follows if on line: " + pc + "\nLine: " + line);

            } else if (line.startsWith("else")) {

                if (!line.endsWith(":"))
                    throw new SyntaxErrorException("Statement must end with ':' on line: " + pc + "\nLine: " + line);

                int pc2 = pc - 2;

                while (pc2 >= 0 && lines[pc2].startsWith(lt + "\t"))
                    pc2--;

                if (pc2 < 0 || !lines[pc2].startsWith("if")) {
                    throw new SyntaxErrorException("Else does not have an if on line: " + pc + "\nLine: " + line);
                }

                if (pc == lines.length - 1 || !lines[pc + 1].startsWith(lt + "\t"))
                    throw new SyntaxErrorException("Nothing follows else on line: " + pc + "\nLine: " + line);

            } else if (line.startsWith("while")) {
                if (!line.endsWith(":"))
                    throw new SyntaxErrorException("Statement must end with ':' on line: " + pc + "\nLine: " + line);

                if (pc == lines.length - 1 || !lines[pc + 1].startsWith(lt + "\t"))
                    throw new SyntaxErrorException("Nothing follows while on line: " + pc + "\nLine: " + line);

            } else if (line.startsWith("print")) {
                if(line.length() < 7 || line.charAt(5) != '(' || line.charAt(line.length() - 1) != ')')
                    throw new SyntaxErrorException("Invalid statement on line: " + pc + "\nLine: " + line);
            }
            else {
                if (!line.contains("=") || !Character.isAlphabetic(line.charAt(0))) {
                    throw new SyntaxErrorException("Invalid statement on line: " + pc + "\nLine: " + line);
                }
            }

        }

    }
// This method processes each line of code, determining its type (such as `if`, `else`, `while`, `print`, or an assignment),
// evaluating conditions, and updating the program counter (`pc`). It handles control flow, variable assignments, and
// expression evaluation.

    public static void processLine() throws SyntaxErrorException {

        String lt = leadingTabs(lines[pc]);

        String line = lines[pc].trim();
        currentLine = line;

        if(line.startsWith("if")) {

            String condition = line.substring(2, line.length() - 1).trim();
            Integer cond = evaluateExpression(condition);

            if(cond.equals(0)) {
                pc++;

                while (pc < lines.length && lines[pc].startsWith(lt + "\t"))
                    pc++;

                if (lines[pc].startsWith(lt + "else"))
                    pc++;

                return;

            }

        }
        else if(line.startsWith("else")) {
            pc++;
            while (pc < lines.length && lines[pc].startsWith(lt + "\t"))
                pc++;
            return;
        }
        else if (line.startsWith("while")) {

            String condition = line.substring(5, line.length() - 1).trim();
            Integer cond = evaluateExpression(condition);

            if (cond.equals(0)) {
                pc++;

                while (pc < lines.length && lines[pc].startsWith(lt + "\t"))
                    pc++;

                return;
            }

            int endPc = pc + 1;

            while (endPc < lines.length && lines[endPc].startsWith(lt + "\t"))
                endPc++;

            endPc--;

            stack.push(new Trip(pc + 1, endPc, condition));

        }
        else if (line.startsWith("print")) {
            String condition = line.substring(6, line.length() - 1).trim();

            if(condition.isEmpty())
                System.out.println();
            else {
                Integer cond = evaluateExpression(condition);
                System.out.println(cond);
            }
        }
        else{
            processAssignment(line);
        }

        if(!stack.isEmpty()) {

            Trip t = stack.peek();

            if (t.pc2 == pc) {
                if(evaluateExpression(t.condition) == 1.0) {
                    pc = t.pc1;
                    return;
                }
                else stack.pop();
            }

        }

        pc++;


    }
//it is reading  lines and putting them into lines array except of empty lines
    public static void readLines(String filePath) {
        ArrayList<String> linesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                linesList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> filteredStrings = new ArrayList<>();
        for (String s : linesList) {
            if (!s.trim().isEmpty()) {
                filteredStrings.add(s);
            }
        }

        lines = filteredStrings.toArray(new String[0]);

    }
    // This method prepares the environment for executing the program by initializing necessary variables,
// checking syntax, and formatting the input code. It sets up the scanner for user input, initializes
// data structures for managing variables and control flow, and processes the lines of code (e.g.,
// replacing spaces with tabs for indentation). It also performs a syntax check to ensure the code is valid.
    public static void preprocess() throws SyntaxErrorException {

        scanner = new Scanner(System.in);

        variables = new HashMap<>();
        stack = new Stack<>();

        pc = 0;

        for(int k = 0; k < lines.length; k++){
            lines[k] = lines[k].replaceAll(" {4}", "\t");
        }

        checkSyntax();

        pc = 0;

    }
    // The main method serves as the entry point for executing the program. It reads the source code from
// a file, preprocesses it (including syntax checks and environment setup), and then processes each
// line of the code by executing the appropriate actions (such as variable assignments, control structures, etc.).
    public static void main(String[] args) throws SyntaxErrorException {

        String codePath = "npyCodes/NthFibonacciNumber.npy";
        readLines(codePath);

        preprocess();

        while (pc < lines.length)
            processLine();

    }
}