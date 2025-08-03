# 🧮 Compilers Projects – LL(1) Parsing & Semantic Analysis

This repository contains two compiler-related projects developed for a university course:

* **Project 1**: Refactoring a grammar and implementing an **LL(1) recursive-descent parser** with arithmetic expression evaluation.
* **Project 2**: Performing **semantic analysis** on a subset of Java code using a symbol table and visitor-based AST traversal.

---

## 📁 Project Structure

```
compilers-project/
├── project1/         # LL(1) parser and grammar transformation
├── project2/         # Semantic analysis and type checking
├── tests/            # Sample Java test files for semantic checking
└── README.md         # You're here!
```

---

## 🔧 Setup & Build Instructions

### 🧩 Project 1: LL(1) Parser – `project1/`

**Build & Run**:

```bash
cd project1/part1
make all
make run
```

### 🧠 Project 2: Semantic Analysis – `project2/`

**Build**:

```bash
cd project2
make all
```

**Run (on a Java test file)**:

```bash
java Main path/to/yourTestFile.java
```

> 📌 Only one test file is supported per execution. Sample tests are provided in the `/tests` folder.

---

## 📘 Project 1: LL(1) Grammar & Parser

To build a predictive parser, the original grammar was transformed to **eliminate left recursion** and **explicitly encode operator precedence**.

### ✏️ Grammar Transformation

We applied the classic transformation:

```
A → A α | β  becomes:
A  → β A'
A' → α A'' | ε
```

The final grammar used is:

```
exp     →  term exp2  
exp2    →  + term exp2 | - term exp2 | ε  
term    →  factor term2  
term2   →  ** factor term2 | ε  
factor  →  num | (exp)  
num     →  digit num2  
num2    →  digit num2 | ε  
digit   →  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9  
```

### 📊 FIRST & FOLLOW Sets

| Non-Terminal | FIRST       | FOLLOW                  |
| ------------ | ----------- | ----------------------- |
| digit        | `{0–9}`     | `{0–9, ), +, -, **, $}` |
| num          | `{0–9}`     | `{), +, -, **, $}`      |
| num2         | `{0–9, ε}`  | `{), +, -, **, $}`      |
| factor       | `{0–9, (}`  | `{**, ), +, -, $}`      |
| term         | `{0–9, (}`  | `{), +, -, $}`          |
| term2        | `{**, ε}`   | `{), +, -, $}`          |
| exp          | `{0–9, (}`  | `{), $}`                |
| exp2         | `{+, -, ε}` | `{), $}`                |

This grammar is implemented in `Calculator.java` and supports operator precedence and parentheses.

---

## 🧠 Project 2: Semantic Analysis & Symbol Table

This component performs **type checking and semantic validation** on a simplified Java-like language using a **visitor pattern** over the abstract syntax tree (AST).

### ✅ Semantic Rules Implemented

1. **Comparison Operators (`<`, `==`, etc.)**: Both operands must be `int`.
2. **Arithmetic Operators (`+`, `-`, `*`)**: Both operands must be `int`.
3. **Control Flow (`if`, `while`)**: Condition must be `boolean`.
4. **Logical AND (`&&`)**: Both operands must be `boolean`.
5. **Print Statements**: Only `int` and `boolean` types allowed.

### ⚙️ Implementation Notes

* **Symbol Table** is constructed using a `SymbolTableVisitor`.
* **Type Checking** is performed via a `TypeCheckerVisitor`.
* On the **first semantic error**, the analysis halts with a message to `stderr` and exit status `1`.

> ⚠️ You may encounter warnings during build due to deprecated methods from JTB — they do not affect correctness.

---

## 🧪 Testing

* All test cases for semantic checking are located in the `tests/` directory.
* Run each test file separately with the `java Main` command as shown above.
* Compilation and testing were done on **WSL** and **Windows** environments.

---
