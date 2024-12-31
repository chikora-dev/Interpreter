import java.util.Objects;
import java.util.Stack;
// Class to evaluate expressions with various operators, including mathematical, logical, and comparison operators.
public class ExpressionEvaluator {
    // Main method to demonstrate the evaluation of an expression. Evaluates "1 + 1" and prints the result.
    public static void main(String[] args) {
        String expression = "1 + 1";
        Integer result = evaluateExpression(expression);
        System.out.println("Result: " + result);
    }
    // Method to evaluate a given expression with operators, numbers, and parentheses.
    // Throws ArithmeticException for division by zero.
    public static Integer evaluateExpression(String expression) throws ArithmeticException {
        char[] tokens = expression.replaceAll(" ", "").toCharArray();
        Stack<Integer> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {

            if ((Character.isDigit(tokens[i])) || (tokens[i] == '-' && (i == 0 || isOperator(Character.toString(tokens[i - 1]))))) {
                StringBuilder sb = new StringBuilder();
                sb.append(tokens[i++]);
                while (i < tokens.length && (Character.isDigit(tokens[i]))) {
                    sb.append(tokens[i++]);
                }
                values.push(Integer.parseInt(sb.toString()));
                i--;
            } else if (tokens[i] == '(') {
                operators.push(Character.toString(tokens[i]));
            } else if (tokens[i] == ')') {
                while (!Objects.equals(operators.peek(), "(")) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else {
                if (i + 1 < tokens.length && isOperator(Character.toString(tokens[i]) + Character.toString(tokens[i + 1]))) {
                    String s1 = Character.toString(tokens[i]) + Character.toString(tokens[i + 1]);
                    if (isOperator(s1)) {

                        while (!operators.isEmpty() && hasPrecedence(s1, operators.peek())) {
                            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                        }
                        operators.push(String.valueOf(s1));
                    }
                } else if (isOperator(Character.toString(tokens[i]))) {
                    while (!operators.isEmpty() && hasPrecedence(String.valueOf(tokens[i]), operators.peek())) {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    operators.push(String.valueOf(tokens[i]));
                }
            }

        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }
    // Helper method to check if a string is a valid operator
    private static boolean isOperator(String c) {
        return Objects.equals(c, "+") || Objects.equals(c, "-") || Objects.equals(c, "*") || Objects.equals(c, "/") ||
                Objects.equals(c, "&&") || Objects.equals(c, "||") || Objects.equals(c, "<") ||
                Objects.equals(c, ">") || Objects.equals(c, "<=") || Objects.equals(c, ">=") ||
                Objects.equals(c, "==") || Objects.equals(c, "!=") || Objects.equals(c, "%");
    }
    // Helper method to determine if operator `op1` has precedence over operator `op2`
    private static boolean hasPrecedence(String op1, String op2) {
        if (Objects.equals(op2, "(") || Objects.equals(op2, ")")) return false;
        return precedenceLevel(op1) <= precedenceLevel(op2);
    }
    // Helper method to assign precedence levels to operators
    private static int precedenceLevel(String op) {
        return switch (op) {
            case "&&" -> 1;
            case "||" -> 2;
            case "<", ">", "<=", ">=", "==", "!=" -> 3;
            case "+", "-" -> 4;
            case "*", "/", "%" -> 5;
            default -> 0;
        };
    }
    // Helper method to apply the operator to the operands (a and b) and return the result
    // Throws ArithmeticException if division or modulo by zero occurs
    private static int applyOperator(String op, int b, int a) throws ArithmeticException {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                yield a / b;
            }
            case "%" -> {
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                yield a % b;
            }
            case "&&" -> (a != 0 && b != 0) ? 1 : 0;
            case "||" -> (a != 0 || b != 0) ? 1 : 0;
            case "==" -> (a == b) ? 1 : 0;
            case "!=" -> (a != b) ? 1 : 0;
            case "<" -> (a < b) ? 1 : 0;
            case ">" -> (a > b) ? 1 : 0;
            case "<=" -> (a <= b) ? 1 : 0;
            case ">=" -> (a >= b) ? 1 : 0;
            default -> 0;
        };
    }
}
