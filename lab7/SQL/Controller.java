package sql;
import model.File;
import model.Folder;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Controller {
    private final Connection connection;
    private Scanner scanner;

    public Controller(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void createFile() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM folder;");
        if (!resultSet.next()) {
            System.out.println("ERROR: before file creation at least one folder must exist");
            return;
        }
        int folderCode;
        while (true) {
            System.out.print("Folder code where to place the file: ");
            String line = scanner.nextLine();
            try {
                folderCode = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Folder code must be an integer");
                continue;
            }
            String sql = "SELECT * FROM folder " +
                    "WHERE folder_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, folderCode);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                break;
            preparedStatement.close();
            System.out.printf("There is no folder with code %d\n", folderCode);
            if (!askUser("Try another code?"))
                return;
        }
        String name;
        String folderName = getFolderName(folderCode);
        while (true) {
            System.out.print("File name: ");
            name = scanner.nextLine();
            try {
                Path.of(name);
            } catch (InvalidPathException e) {
                System.out.println(e.getMessage());
                if (askUser("Create file with another name?"))
                    continue;
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM file WHERE folder_id = ?;");
            preparedStatement.setInt(1, folderCode);
            resultSet = preparedStatement.executeQuery();
            boolean br = true;
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(name)) {
                    System.out.printf("File with name \"%s\" is already EXISTS in folder \"%s\"\n", name, folderName);
                    if (askUser("Create file with another name?")) {
                        br = false;
                        break;
                    } else {
                        return;
                    }
                }
            }
            if (br) break;
        }
        int size;
        while (true) {
            System.out.print("File size in bytes: ");
            try {
                size = Integer.parseInt(scanner.nextLine());
                if (size < 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: size of file must be a positive integer");
            }
        }
        System.out.println(" File properties. Everything except \"true\" is considered as false");
        System.out.print("File visible? ");
        boolean visible = scanner.nextLine().equals("true");
        System.out.print("File readable? ");
        boolean readable = scanner.nextLine().equals("true");
        System.out.print("File writeable? ");
        boolean writeable = scanner.nextLine().equals("true");
        String datetime = LocalDateTime.now().toString().replace('T', ' ').substring(0, 19);
        PreparedStatement preparedStatement = connection.prepareStatement("""
                INSERT INTO file
                (folder_id,
                name,
                visible,
                readable,
                writeable,
                size,
                last_updated)
                VALUES (?,?,?,?,?,?,?)
                """);
        preparedStatement.setInt(1, folderCode);
        preparedStatement.setString(2, name);
        preparedStatement.setBoolean(3, visible);
        preparedStatement.setBoolean(4, readable);
        preparedStatement.setBoolean(5, writeable);
        preparedStatement.setInt(6, size);
        preparedStatement.setTimestamp(7, Timestamp.valueOf(datetime));
        preparedStatement.execute();
        int code = getFileCode(folderCode, name);
        System.out.printf("File \"%s/%s\" with code %d is SUCCESSFULLY created\n", folderName, name, code);
    }

    public void createFolder() throws SQLException {
        String name;
        while (true) {
            System.out.print("Folder name: ");
            name = scanner.nextLine();
            try {
                Path.of(name + "/file.txt");
            } catch (InvalidPathException e) {
                System.out.println("Incorrect folder name");
                if (askUser("Do you want to choose another name?"))
                    continue;
                else
                    break;
            }
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM folder;");
            boolean br = true;
            while (rs.next()) {
                if (rs.getString(1).equals(name)) {
                    System.out.printf("Name \"%s\" is already present on disk\n", name);
                    if (!askUser("Do you want to choose another name?"))
                        return;
                    else {
                        br = false;
                        break;
                    }
                }
            }
            if (br) break;
        }
        PreparedStatement ps = connection.prepareStatement("INSERT INTO folder (name) VALUES (?)");
        ps.setString(1, name);
        ps.execute();
        ps = connection.prepareStatement("SELECT folder_id FROM folder WHERE name = ?;");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        System.out.printf("Folder \"%s\" with code %d is successfully created\n", name, rs.getInt(1));
    }

    public void readAllFiles() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM file");
        boolean empty = true;
        while (rs.next()) {
            empty = false;
            System.out.println(new File(rs.getInt("file_id"), rs.getInt("folder_id"), rs.getString("name"),
                    rs.getBoolean("visible"), rs.getBoolean("readable"), rs.getBoolean("writeable"),
                    rs.getInt("size"), rs.getTimestamp("last_updated").toLocalDateTime()));
        }
        if (empty) System.out.println("<empty>");
    }

    public void readAllFolders() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM folder");
        boolean empty = true;
        while (rs.next()) {
            empty = false;
            System.out.println(new Folder(rs.getInt("folder_id"), rs.getString("name")));
        }
        if (empty) System.out.println("<empty>");
    }

    public void updateFile(int code) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT name, folder_id FROM file WHERE file_id = ?");
        ps.setInt(1, code);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            System.out.printf("There is no file with code %d on the disk\n", code);
            return;
        }
        String name = rs.getString("name");
        ps = connection.prepareStatement("SELECT name FROM folder WHERE folder_id = ?");
        ps.setInt(1, rs.getInt("folder_id"));
        rs = ps.executeQuery();
        rs.next();
        String folderName = rs.getString("name");
        while (true) {
            System.out.print("choose the attribute to update: ");
            String attr = scanner.nextLine();
            switch (attr) {
                case "name":
                    while (true) {
                        scanner = new Scanner(System.in);
                        System.out.print("Enter new name: ");
                        name = scanner.nextLine();
                        try {
                            Path.of(name);
                            ps = connection.prepareStatement("UPDATE file SET name = ? WHERE file_id =  ?");
                            ps.setString(1, name);
                            ps.setInt(2, code);
                            ps.execute();
                            break;
                        } catch (InvalidPathException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case "size":
                    while (true) {
                        System.out.print("New value for size: ");
                        try {
                            scanner = new Scanner(System.in);
                            int size = scanner.nextInt();
                            if (size < 0) throw new InputMismatchException();
                            ps = connection.prepareStatement("UPDATE file SET size = ? WHERE file_id = ?");
                            ps.setInt(1, size);
                            ps.setInt(2, code);
                            ps.execute();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Enter a non-negative integer");
                        }
                    }
                    break;
                case "folder id":
                    while (true) {
                        System.out.print("Enter new folder id: ");
                        try {
                            scanner = new Scanner(System.in);
                            int folderCode = scanner.nextInt();
                            if (folderCode < 0) throw new InputMismatchException();
                            ps = connection.prepareStatement("SELECT name FROM folder WHERE folder_id = ?");
                            ps.setInt(1, folderCode);
                            rs = ps.executeQuery();
                            if (!rs.next()) {
                                System.out.printf("There is no folder with code %d on disk\n", folderCode);
                                if (askUser("Do you want to try another folder id?"))
                                    continue;
                                else return;
                            }
                            folderName = rs.getString("name");
                            ps = connection.prepareStatement("SELECT * FROM file WHERE folder_id = ? AND name = ?");
                            ps.setInt(1, folderCode);
                            ps.setString(2, name);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                System.out.printf("There is already file with name \"%s\" in folder \"%s\"\n", name, folderName);
                                if (askUser("Do you want to choose another folder id?"))
                                    continue;
                                else break;
                            }
                            ps = connection.prepareStatement("UPDATE file SET folder_id = ? WHERE file_id = ?");
                            ps.setInt(1, folderCode);
                            ps.setInt(2, code);
                            ps.execute();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Enter a non-negative integer");
                        }
                    }
                    break;
                case "visible":
                    while (true) {
                        System.out.print("New value for isVisible: ");
                        try {
                            scanner = new Scanner(System.in);
                            ps = connection.prepareStatement("UPDATE file SET visible = ? WHERE file_id = ?");
                            ps.setBoolean(1, scanner.nextBoolean());
                            ps.setInt(2, code);
                            ps.execute();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Enter \"true\" or \"false\"");
                        }
                    }
                    break;
                case "readable":
                    while (true) {
                        System.out.print("New value for isReadable: ");
                        try {
                            scanner = new Scanner(System.in);
                            ps = connection.prepareStatement("UPDATE file SET readable = ? WHERE file_id = ?");
                            ps.setBoolean(1, scanner.nextBoolean());
                            ps.setInt(2, code);
                            ps.execute();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Enter \"true\" or \"false\"");
                        }
                    }
                    break;
                case "writeable":
                    while (true) {
                        System.out.print("New value for isWriteable: ");
                        try {
                            scanner = new Scanner(System.in);
                            ps = connection.prepareStatement("UPDATE file SET writeable = ? WHERE file_id = ?");
                            ps.setBoolean(1, scanner.nextBoolean());
                            ps.setInt(2, code);
                            ps.execute();
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Enter \"true\" or \"false\"");
                        }
                    }
                    break;
                default:
                    System.out.printf("""
                                Attribute "%s" is not recognized
                                Attributes of file are:
                                 - name (String)
                                 - size (integer)
                                 - visible (boolean)
                                 - readable (boolean)
                                 - writeable (boolean)
                                 - folder id (integer)
                                """, attr);
                    continue;
            }
            String datetime = LocalDateTime.now().toString().replace('T', ' ').substring(0, 19);
            ps = connection.prepareStatement("UPDATE file SET last_updated = ? WHERE file_id = ?");
            ps.setTimestamp(1, Timestamp.valueOf(datetime));
            ps.setInt(2, code);
            ps.execute();
            if (!askUser("Do you want to update another attribute of this file?")) {
                System.out.printf("File \"%s\"/\"%s\" is successfully updated\n", folderName, name);
                break;
            }
        }
    }

    public void updateFolder(int code) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM folder");
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            System.out.printf("There is no file with code %d\n", code);
            return;
        }
        while (true) {
            System.out.print("Enter a new name for this folder: ");
            try {
                scanner = new Scanner(System.in);
                String name = scanner.nextLine();
                Path.of(name + "/file.txt");
                ps = connection.prepareStatement("UPDATE folder SET name = ? WHERE folder_id = ?");
                ps.setString(1, name);
                ps.setInt(2, code);
                ps.execute();
                break;
            } catch (InvalidPathException e) {
                System.out.println(e.getMessage());
                if (!askUser("Do you want to try another name?"))
                    break;
            }
        }
        System.out.println("Folder name is successfully updated");
    }

    public void deleteFile(int code) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT name, folder_id FROM file WHERE file_id = ?");
        ps.setInt(1, code);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            System.out.printf("There is no file with code %d on disk\n", code);
            return;
        }
        String name = rs.getString(1);
        ps = connection.prepareStatement("SELECT name FROM folder WHERE folder_id = ?");
        ps.setInt(1, rs.getInt(2));
        rs = ps.executeQuery();
        rs.next();
        ps = connection.prepareStatement("DELETE FROM file WHERE file_id = ?");
        ps.setInt(1, code);
        ps.execute();
        ps.close();
        System.out.printf("File \"%s/%s\" is successfully deleted\n", rs.getString(1), name);
    }

    public void deleteFolder(int code) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT name FROM folder WHERE folder_id = ?");
        ps.setInt(1, code);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            System.out.println("There is no folder with code " + code);
            return;
        }
        String name = rs.getString(1);
        ps = connection.prepareStatement("SELECT name FROM file WHERE folder_id = ?");
        ps.setInt(1, code);
        rs = ps.executeQuery();
        StringBuilder question = new StringBuilder("Are you sure you want to delete folder \"").append(name).append("\" with the following files:\n");
        boolean empty = true;
        while (rs.next()) {
            empty = false;
            question.append(" - ").append(rs.getString(1)).append('\n');
        }
        if (empty) question.append("<empty>\n");
        question.append("?");
        if (askUser(question.toString())) {
            ps = connection.prepareStatement("DELETE FROM folder WHERE folder_id = ?");
            ps.setInt(1, code);
            ps.execute();
            ps = connection.prepareStatement("DELETE FROM file WHERE folder_id = ?");
            ps.setInt(1, code);
            ps.execute();
            System.out.println("Folder is successfully deleted");
        }
    }

    private int getFileCode(int folderCode, String fileName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT file_id FROM file WHERE name = ? AND folder_id = ?");
        statement.setString(1, fileName);
        statement.setInt(2, folderCode);
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs.getInt("file_id");
    }

    private String getFolderName(int folderCode) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT name FROM folder WHERE folder_id = ?;");
        statement.setInt(1, folderCode);
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs.getString(1);
    }

    private boolean askUser(String question) {
        while (true) {
            scanner = new Scanner(System.in);
            System.out.printf("%s (y/n): ", question);
            String answer = scanner.nextLine();
            if (answer.equals("y"))
                return true;
            else if (answer.equals("n"))
                return false;
            System.out.printf("Don't understand \"%s\"\n", answer);
        }
    }
}
