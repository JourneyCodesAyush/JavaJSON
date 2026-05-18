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

## Build

```bash
mvn package
```

## Usage

```bash
java -jar target/JavaJSON-0.5.0.jar format input.json
java -jar target/JavaJSON-0.5.0.jar minify input.json
java -jar target/JavaJSON-0.5.0.jar validate input.json
java -jar target/JavaJSON-0.5.0.jar get input.json ".users[0].email"
```

## Path Query Syntax

```
.key          # object key access
.[0]          # array index access
.users[0].email  # chained access
```

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

## License

MIT - See [LICENSE](LICENSE)
