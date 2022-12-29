package mokaolown.compiler;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class AST {

    public static abstract class Exper {

        public abstract String evaluate();

    }

    public static class TableExper extends Exper {

        Exper rows;
        String tableName;

        public TableExper(String tableName, Exper rows) {
            this.tableName = tableName;
            this.rows = rows;
        }

        @Override
        public String toString() {
            return String.format("<%s> %s </%s>", tableName, rows.toString(), tableName);
        }

        @Override
        public String evaluate() {
            String[] split, dt;
            String inserts = "", dataTypes = "";
            String rowsStr = rows.evaluate();
            String[] rows = rowsStr.split("!!");
            split = rows[0].split("&");
            inserts += split[0];
            dataTypes += split[1];
            for (int i = 1; i < rows.length; i++) {
                split = rows[i].split("&");
                inserts += split[0];
                dataTypes += "," + split[1];
            }
            inserts = inserts.replace("_", tableName);
            dt = dataTypes.split(",");
            LinkedHashSet<String> set = removeDuplicates(dt);
            Iterator<String> it = set.iterator();
            String data = it.next();
            while (it.hasNext()) {
                data += ", " + it.next();
            }
            return String.format("Add New Table With Name %s (%s);\n\n%s", tableName, data, inserts);
        }
    }

    public static class RowsExper extends Exper {

        AST.Exper row, rowPrime;

        public RowsExper(Exper row, Exper rowPrime) {
            this.row = row;
            this.rowPrime = rowPrime;
        }

        @Override
        public String toString() {
            return String.format("%s %s", row.toString(), rowPrime.toString());
        }

        @Override
        public String evaluate() {
            String[] arr2;
            String inserts = "", dataTypes = "";
            String s = row.evaluate();
            String[] arr = s.split("&");
            inserts += arr[0];
            dataTypes += arr[1];
            arr = rowPrime.evaluate().split("!!");
//            System.out.println(rowPrime.evaluate());
            for (var row : arr) {
                arr2 = row.split("&");
                inserts += arr2[0];
                dataTypes += ", " + arr2[1];
            }
            return String.format("%s-%s", inserts, dataTypes);
        }
    }

    public static class RowExper extends Exper {

        String name;
        Exper attributes;

        public RowExper(String name, Exper attributes) {
            this.attributes = attributes;
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("<%s %s />", name, attributes.toString());
        }

        @Override
        public String evaluate() {
            String[] arr2;
            String dataTypes = "", ids = "", values = "";
            String s = attributes.evaluate();
            String[] arr = s.split("#");
            arr2 = arr[0].split(",");
            ids += arr2[0];
            values += arr2[1];
            dataTypes += String.format("%s %s", arr2[0], arr2[2]);
            for (int att = 1; att < arr.length; att++) {
                arr2 = arr[att].split(",");
                ids += ", " + arr2[0];
                values += ", " + arr2[1];
                dataTypes += ", " + String.format("%s %s", arr2[0], arr2[2]);
            }
            return String.format("Add New STUDENT To _ (%s) With VALUES (%s);\n&(%s)", ids, values, dataTypes);
        }
    }

    public static class RowPrimeExper extends Exper {

        Exper row, rowPrime;

        public RowPrimeExper(Exper row, Exper rowPrime) {
            this.row = row;
            this.rowPrime = rowPrime;
        }

        @Override
        public String toString() {
            return String.format("%s %s ", row.toString(), rowPrime.toString());
        }

        @Override
        public String evaluate() {
            return row.evaluate() + "!!" + rowPrime.evaluate();
        }
    }

    public static class AttributesExper extends Exper {

        Exper attributes, attribute;

        public AttributesExper(Exper attribute, Exper attributes) {
            this.attributes = attributes;
            this.attribute = attribute;
        }

        @Override
        public String toString() {
            return attribute.toString() + ", " + attributes.toString();
        }

        @Override
        public String evaluate() {
            return attribute.evaluate() + "#" + attributes.evaluate();
        }
    }

    public static class AttributeExper extends Exper {

        Exper val;
        String id;

        public AttributeExper(String id, Exper val) {
            this.id = id;
            this.val = val;
        }

        @Override
        public String toString() {
            return id + "=" + val.toString();
        }

        @Override
        public String evaluate() {
            return id + "," + val.evaluate();
        }
    }

    public static class ValExper extends Exper {

        String value, dataType;

        public ValExper(String value, String dataType) {
            this.value = value;
            this.dataType = dataType;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public String evaluate() {
            return value + "," + dataType;
        }
    }

    public static LinkedHashSet<String> removeDuplicates(String[] a) {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        // adding elements to LinkedHashSet
        for (String a1 : a) {
            set.add(a1.trim());
        }

        // Print the elements of LinkedHashSet
        return set;
    }
}

