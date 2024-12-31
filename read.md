# Simple Python Interpreter

# Contributor
- Giorgi Chikvaidze

## Overview
This is a minimal Python interpreter that supports basic input/output, variable assignments, expression evaluations, and control structures like `if`, `else`, and `while` loops. The interpreter works with integer values only.

## Features
- Input: `input()`
- Output: `print()`
- Variable assignment and expression evaluations
- Arithmetic and comparison operations
- Control structures: `if`, `else`, and `while` loops
- Integer-only variables and output
- 1 means True 0 means False

## Language Subset Specification

### 1. Input/Output

- **Input**: Supported through the `input()` function.
  - Example: `x = input()` reads an integer input and stores it in the variable `x`.
  - It is also possible to do expressions like `x = 2 * (input() + 5)`.
  
- **Output**: Supported through the `print()` function.
  - Example: `print(x)` outputs the value of `x`, but only works with integers. Standalone print() prints endline

### 2. Variable Assignment

- **Syntax**: Variable assignment uses the `=` operator.
  - Example: `x = 5` assigns the value `5` to the variable `x`.
  - Variables can hold only integers.

### 3. Expressions

- Supports basic arithmetic and comparison operations:
  - Arithmetic: `+`, `-`, `*`, `/`, `%`
  - Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
  - Example: `y = 2 * (3 + 4)`
  
- When a condition is true, the output is `1`, otherwise the output is `0`.

### 4. Control Structures

- **If/Else Statements**:
  - Syntax:
    ```python
    if condition:
        # code block
    else:
        # code block
    ```

    ```python
    if condition:
        # code block
    ```

  - Example:
    ```python
    if x > 5:
        print(1)
    else:
        print(0)
    ```

    ```python
    if condition:
        x = 2 * x
    ```

- **While Loops**:
  - Syntax:
    ```python
    while condition:
        # code block
    ```
  - Example:
    ```python
    while x < 10:
        x = x + 1
        print(x)
    ```

### 5. Scope and Variables

- All variables are global. There is no local scope for control structures like functions or blocks.

## How to Run

1. Clone this repository.
2. Go to Main.java file.
3. Change filePaht variable to path of the source code.
4. Hit run.

