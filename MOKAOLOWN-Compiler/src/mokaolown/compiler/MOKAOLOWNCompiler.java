package mokaolown.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Scanner;

public class MOKAOLOWNCompiler {

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        try ( FileReader fr = new FileReader("in.txt");  PrintWriter pw = new PrintWriter("out.txt")) {
            Lexer l = new Lexer(fr);
            Parser p = new Parser(l);
            AST.Exper root = p.S();
            pw.println(root.evaluate());

        }
        System.out.println("build Successful");
        try {
            //get connection
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "Helloitisme42839");
            File file = new File("out.txt");
            Scanner scan = new Scanner(file);
            //create stm
            Statement myStmt = myConn.createStatement();
            while (scan.hasNextLine()) {
                myStmt.executeUpdate(scan.nextLine());
            }

        } catch (FileNotFoundException | SQLException exc) {
        }
        System.out.println("insertation done");
    }
}
