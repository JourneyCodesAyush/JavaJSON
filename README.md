# JavaJSON

![Java](https://img.shields.io/badge/Java-21%2B-orange)
![Maven](https://img.shields.io/badge/Maven-3.19.15-blue)
![Version](https://img.shields.io/github/v/tag/JourneyCodesAyush/JavaJSON?label=latest&color=purple)
![License](https://img.shields.io/badge/license-MIT-green)

A JSON parser and CLI tool written in Java, built from scratch following recursive descent parsing principles.

## Features

- Parse and validate JSON
- Pretty print JSON
- Minify JSON
- Query JSON with jq-like path expressions

---

## Build

```bash
mvn package
```

---

## Usage

```bash
java -jar target/JavaJSON-0.5.1.jar format input.json
java -jar target/JavaJSON-0.5.1.jar minify input.json
java -jar target/JavaJSON-0.5.1.jar validate input.json
java -jar target/JavaJSON-0.5.1.jar get input.json ".users[0].email"
```

---

## Path Query Syntax

```
.key          # object key access
.[0]          # array index access
.users[0].email  # chained access
```

---

## Project Structure

```
JavaJSON/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/journeycodesayush/javajson/
│   │           ├── JavaJSON.java   # Driver code
│   │           ├── lexer/          # Lexer, Token and TokenType
│   │           ├── parser/         # Parser, Expression and Statement
│   │           ├── query/          # jq-lite path query engine
│   │           └── tool/           # Generate AST
│   └── test/
│       └── java/
│           └── io/github/journeycodesayush/javajson/
│               ├── lexer/
│               ├── parser/
│               └── query/
│
│
├── LICENSE                  # MIT License
└── README.md                # You're reading it!
```

---

## Running Tests

```bash
mvn test
```

---

## Contributing

Contributions are welcome. Please follow these guidelines:

- Fork the repository and create a branch: `feat/feature-name` or `fix/bug-name`
- Follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) for commit messages
- Run `mvn test` before submitting a pull request
- Open a pull request with a clear description of your changes

### Commit Scopes

| Scope  | Description                    |
| ------ | ------------------------------ |
| lexer  | Changes to the lexer           |
| parser | Changes to the parser          |
| query  | Changes to the query engine    |
| cli    | Changes to the CLI entry point |
| tool   | Changes to the AST generator   |
| test   | Changes to tests               |
| docs   | Documentation changes          |
| build  | Build configuration changes    |
| ci     | CI/CD changes                  |

---

## License

MIT - See [LICENSE](LICENSE)
