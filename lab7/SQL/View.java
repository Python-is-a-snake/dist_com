package sql;
import java.sql.*;
import java.util.*;
public class View {
    private final Scanner scanner = new Scanner(System.in);

    public String[] connect(){
        String params[] = new String[3];
        System.out.println("ENTER URL : ");
        params[0] = scanner.nextLine();
        System.out.println("ENTER USER : ");
        params[1] = scanner.nextLine();
        System.out.println("ENTER PASSWORD : ");
        params[2] = scanner.nextLine();
        return params;
    }

    public void start() {
        Controller controller;
        try {
            Connection connection = DriverManager.getConnection(connect()[0], connect()[1], connect()[2]);
            controller = new Controller(connection, scanner);
            System.out.println("CONNECTION SUCCEED");
        } catch (SQLException e) {
            System.out.println("CONNECTION ERROR");
            System.out.println(e.getMessage());
            throw new RuntimeException();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        try {
            showCommands();
            while (true) {
                System.out.print("Enter a command: ");
                String command = scanner.nextLine();
                switch (command) {
                    case "exit":
                        return;
                    case "create file":
                        controller.createFile();
                        break;
                    case "create folder":
                        controller.createFolder();
                        break;
                    case "delete file":
                        while (true) {
                            try {
                                System.out.print("Enter file code to delete: ");
                                String str = scanner.nextLine();
                                int code = Integer.parseInt(str);
                                if (code < 0) throw new NumberFormatException();
                                controller.deleteFile(code);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("You must enter a non negative integer");
                            }
                        }
                        break;
                    case "delete folder":
                        while (true) {
                            try {
                                System.out.print("Enter folder code to delete: ");
                                String str = scanner.nextLine();
                                int code = Integer.parseInt(str);
                                if (code < 0) throw new NumberFormatException();
                                controller.deleteFolder(code);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("You must enter a non negative integer");
                            }
                        }
                        break;
                    case "files":
                        controller.readAllFiles();
                        break;
                    case "folders":
                        controller.readAllFolders();
                        break;
                    case "update file":
                        while (true) {
                            try {
                                System.out.print("Enter file code to update: ");
                                String str = scanner.nextLine();
                                int code = Integer.parseInt(str);
                                if (code < 0) throw new NumberFormatException();
                                controller.updateFile(code);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("You must enter a non negative integer");
                            }
                        }
                        break;
                    case "update folder":
                        try {
                            System.out.print("Enter folder code to update: ");
                            String str = scanner.nextLine();
                            int code = Integer.parseInt(str);
                            if (code < 0) throw new NoSuchElementException();
                            controller.updateFolder(code);
                        } catch (NumberFormatException e) {
                            System.out.println("You must enter a non negative integer value");
                        }
                        break;
                    default:
                        System.out.printf("Command \"%s\" is not recognized\nTry one of the following commands:\n", command);
                        showCommands();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCommands() {
        System.out.println("* exit\n" +
                "* create file\n" +
                "* create folder\n" +
                "* delete file\n" +
                "* delete folder\n" +
                "* files\n" +
                "* folders\n" +
                "* update file\n" +
                "* update folder\n");
    }
}
