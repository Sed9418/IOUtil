import java.io.File;

public class Test2 {
    public static void main(String[] args) {
        String path = "/home/seda";
        if (args.length == 1 && args[0].equals("ls")) {
            printRecursive(path);
        } else {
            path = args[args.length - 1];
            if (args.length == 2) {
                printRecursive(path);
            } else {
                printRecursiveWithData(path);
            }
        }
    }

    public static void printRecursive(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println(file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    printRecursive(file1.getAbsolutePath());
                }
            }
        }
    }

    public static void printRecursiveWithData(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println(file.getAbsolutePath() + " " + file.length() + "" + file.setLastModified(1000));
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    printRecursive(file1.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
