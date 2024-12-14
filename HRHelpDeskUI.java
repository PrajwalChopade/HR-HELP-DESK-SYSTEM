import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Employee Class
class Employee {
    private String name;
    private String department;
    private String email;
    private String phoneNumber;
    private double salary;

    public Employee(String name, String department, String email, String phoneNumber, double salary) {
        this.name = name;
        this.department = department;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.salary = salary;
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public double getSalary() { return salary; }
}

// Database Manager Class
class DatabaseManager {
    private Connection connection;

    public DatabaseManager() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "system");
    }

    public void addEmployee(Employee employee) throws SQLException {
        String query = "INSERT INTO employees (name, department, email, phone_number, salary) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, employee.getName());
        ps.setString(2, employee.getDepartment());
        ps.setString(3, employee.getEmail());
        ps.setString(4, employee.getPhoneNumber());
        ps.setDouble(5, employee.getSalary());
        ps.executeUpdate();
    }

    public List<Employee> fetchEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            employees.add(new Employee(
                rs.getString("name"),
                rs.getString("department"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getDouble("salary")
            ));
        }
        return employees;
    }

    public Employee searchEmployee(String name) throws SQLException {
        String query = "SELECT * FROM employees WHERE name = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Employee(
                rs.getString("name"),
                rs.getString("department"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getDouble("salary")
            );
        }
        return null;
    }

    public void removeEmployee(String name) throws SQLException {
        String query = "DELETE FROM employees WHERE name = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ps.executeUpdate();
    }

    public void updateSalary(String name, double salary) throws SQLException {
        String query = "UPDATE employees SET salary = ? WHERE name = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setDouble(1, salary);
        ps.setString(2, name);
        ps.executeUpdate();
    }

    public void updateEmployeeDetails(String name, String department, String email, String phone) throws SQLException {
        String query = "UPDATE employees SET department = ?, email = ?, phone_number = ? WHERE name = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, department);
        ps.setString(2, email);
        ps.setString(3, phone);
        ps.setString(4, name);
        ps.executeUpdate();
    }
}

// Main HR Help Desk UI Class
public class HRHelpDeskUI {
    private JFrame frame;
    private DatabaseManager dbManager;

    public HRHelpDeskUI() {
        try {
            dbManager = new DatabaseManager();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error connecting to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("HR Help Desk");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.LIGHT_GRAY);

        // Top Panel with Logo and Heading
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel(new ImageIcon(new ImageIcon("logo.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        JLabel title = new JLabel("HR DESK MANAGEMENT SYSTEM", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.BLUE);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);

        // Middle Panel with Image and Buttons
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.LIGHT_GRAY);

        JLabel middleImage = new JLabel(new ImageIcon(new ImageIcon("middle.png").getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
        middleImage.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        String[] buttonLabels = {
            "Add Employee",
            "Print Employees",
            "Search Employee",
            "Remove Employee",
            "Update Salary",
            "Update Details"
        };

        for (String label : buttonLabels) {
            JButton button = createMenuButton(label);
            buttonPanel.add(button);

            switch (label) {
                case "Add Employee":
                    button.addActionListener(e -> addEmployee());
                    break;
                case "Print Employees":
                    button.addActionListener(e -> printEmployees());
                    break;
                case "Search Employee":
                    button.addActionListener(e -> searchEmployee());
                    break;
                case "Remove Employee":
                    button.addActionListener(e -> removeEmployee());
                    break;
                case "Update Salary":
                    button.addActionListener(e -> updateSalary());
                    break;
                case "Update Details":
                    button.addActionListener(e -> updateDetails());
                    break;
            }
        }
        // Add Exit Button
JButton exitButton = createMenuButton("Exit");
exitButton.addActionListener(e -> System.exit(0));
buttonPanel.add(exitButton);

        middlePanel.add(middleImage, BorderLayout.CENTER);
        middlePanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    private void addEmployee() {
        String name = JOptionPane.showInputDialog(frame, "Enter Employee Name:");
        String department = JOptionPane.showInputDialog(frame, "Enter Department:");
        String email = JOptionPane.showInputDialog(frame, "Enter Email:");
        String phone = JOptionPane.showInputDialog(frame, "Enter Phone Number:");
        String salaryStr = JOptionPane.showInputDialog(frame, "Enter Salary:");

        try {
            double salary = Double.parseDouble(salaryStr);
            dbManager.addEmployee(new Employee(name, department, email, phone, salary));
            JOptionPane.showMessageDialog(frame, "Employee Added Successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void printEmployees() {
        try {
            List<Employee> employees = dbManager.fetchEmployees();
            StringBuilder sb = new StringBuilder("Employees:\n");
            for (Employee emp : employees) {
                sb.append(emp.getName()).append(", ")
                  .append(emp.getDepartment()).append(", ")
                  .append(emp.getEmail()).append(", ")
                  .append(emp.getPhoneNumber()).append(", ")
                  .append(emp.getSalary()).append("\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(frame, scrollPane);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void searchEmployee() {
        String name = JOptionPane.showInputDialog(frame, "Enter Employee Name to Search:");
        try {
            Employee emp = dbManager.searchEmployee(name);
            if (emp != null) {
                JOptionPane.showMessageDialog(frame, "Found: \n" +
                        "Name: " + emp.getName() + "\n" +
                        "Department: " + emp.getDepartment() + "\n" +
                        "Email: " + emp.getEmail() + "\n" +
                        "Phone: " + emp.getPhoneNumber() + "\n" +
                        "Salary: " + emp.getSalary());
            } else {
                JOptionPane.showMessageDialog(frame, "Employee Not Found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void removeEmployee() {
        String name = JOptionPane.showInputDialog(frame, "Enter Employee Name to Remove:");
        try {
            dbManager.removeEmployee(name);
            JOptionPane.showMessageDialog(frame, "Employee Removed Successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void updateSalary() {
        String name = JOptionPane.showInputDialog(frame, "Enter Employee Name to Update Salary:");
        String salaryStr = JOptionPane.showInputDialog(frame, "Enter New Salary:");
        try {
            double salary = Double.parseDouble(salaryStr);
            dbManager.updateSalary(name, salary);
            JOptionPane.showMessageDialog(frame, "Salary Updated Successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void updateDetails() {
        String name = JOptionPane.showInputDialog(frame, "Enter Employee Name to Update Details:");
        String department = JOptionPane.showInputDialog(frame, "Enter New Department:");
        String email = JOptionPane.showInputDialog(frame, "Enter New Email:");
        String phone = JOptionPane.showInputDialog(frame, "Enter New Phone Number:");

        try {
            dbManager.updateEmployeeDetails(name, department, email, phone);
            JOptionPane.showMessageDialog(frame, "Details Updated Successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HRHelpDeskUI::new);
    }
}
