import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
        Options options=new Options();
        options.addOption("ls", true, "list the contents of the current directory in short form");
        CommandLineParser parser=new DefaultParser();
        CommandLine commandLine=null;
        try {
            commandLine= parser.parse(options,args);
        } catch (ParseException e) {
           e.fillInStackTrace();
        }

    }
}
