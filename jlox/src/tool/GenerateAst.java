package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


/*
  GenerateAst: Generate Abstract Syntax Tree

  This is a tool that will generate code for our Lox Abstract Syntax Tree. In Chapter 04 we created
  our Scanner for the Lox interpreter that is able to take Lox code and distinguish Tokens or the 
  "words" of Lox.

  Now with this tool we are setting up the code to be able to distinguish/recognize the "sentences"
  of Lox. Chapter 05 "this chapter" generates the code needed to define the rules for sentences.
  Next Chapter 06 makes the interpreter able to recognize said sentences.
*/

/*
  Lox's current grammar (as of Ch05):

  expression -> literal | unary | binary | grouping ;
  literal    -> NUMBER | STRING | "true" | "false" | "nil" ;
  grouping   -> "(" expression ")" ;
  unary      -> expression operator expression ;
  binary     -> "==" | "!=" | "<" | "<=" | ">" | ">=" ;
  operator   -> "+" | "-" | "*" | "/" ;
*/
public class GenerateAst {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
      System.exit(64);
    }
    String outputDir = args[0];

    // To generate each class, need to have some description of each type and it's fields.
    defineAst(outputDir, "Expr", Arrays.asList(
      "Binary   : Expr left, Token operator, Expr right",
      "Grouping : Expr expression",
      "Literal  : Object value",
      "Unary    : Token operator, Expr right"
    ));
  }


  private static void defineAst(String outputDir, String baseName, List<String> types) 
  throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    writer.println("package lox;");
    writer.println();
    writer.println("import java.util.List;");

    // Spacing between imports and `abstract class Expr {}`.
    writer.println(); 
    writer.println();

    writer.println("abstract class " + baseName + " {");

    defineVisitor(writer, baseName, types);

    // The AST classes.
    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);

      // Class spacing.
      writer.println();
      writer.println();
    }

    // The base accept() method.
    writer.println("  abstract <R> R accept(Visitor<R> visitor);");

    writer.println("}");
    writer.close();
  }


  private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
    writer.println(); // Space out from parent `abstract class Expr`.

    writer.println("  interface Visitor<R> {");

    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + 
        baseName.toLowerCase() + ");");
    }

    writer.println("  }");

    // Class spacing.
    writer.println();
    writer.println();
  }


  private static void defineType(
    PrintWriter writer, 
    String baseName, 
    String className, 
    String fieldList
  ) {
    writer.println("  static class " + className + " extends " + baseName + " {");

    // Constructor
    writer.println("    " + className + "(" + fieldList + ") {");

    // Store parameters in fields.
    String[] fields = fieldList.split(", ");
    for (String field : fields) {
      String name = field.split(" ")[1];
      writer.println("      this." + name + " = " + name + ";");
    }

    writer.println("    }");

    // Visitor pattern.
    writer.println();
    writer.println("    @Override");
    writer.println("    <R> R accept(Visitor<R> visitor) {");
    writer.println("      return visitor.visit" + className + baseName + "(this);");
    writer.println("    }");

    //Fields
    writer.println();
    for (String field : fields) {
      writer.println("    final " + field + ";");
    }

    writer.println("  }");
  }
}
