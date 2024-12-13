import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RailwayReservationUI {

    ArrayList<Passenger> passengers = new ArrayList<>();
    int maxAC = 75, maxFirstClass = 125, maxSleeper = 175;
    int pnum = 1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RailwayReservationUI();
            }
        });
    }

    public RailwayReservationUI() {
        JFrame frame = new JFrame("Railway Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JLabel title = new JLabel("Railway Reservation System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton bookBtn = createButton("Book Ticket");
        JButton cancelBtn = createButton("Cancel Ticket");
        JButton searchBtn = createButton("Search Passenger");
        JButton chartBtn = createButton("Reservation Chart");
        JButton unbookedBtn = createButton("Unbooked Tickets");
        JButton exitBtn = createButton("Exit");

        bookBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                bookTicket();
            }
        });
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cancelTicket();
            }
        });
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                searchPassenger();
            }
        });
        chartBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                displayReservationChart();
            }
        });
        unbookedBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                displayUnbookedTickets();
            }
        });
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(chartBtn);
        buttonPanel.add(unbookedBtn);
        buttonPanel.add(exitBtn);

        // Main Frame Layout
        frame.add(title, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 153, 76));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 51), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        return button;
    }

    private void bookTicket() {
        JFrame frame = createPopupFrame("Book Ticket");

        JLabel nameLbl = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel ageLbl = new JLabel("Age:");
        JTextField ageField = new JTextField();
        JLabel phoneLbl = new JLabel("Phone:");
        JTextField phoneField = new JTextField();
        JLabel classLbl = new JLabel("Class (1-AC, 2-First, 3-Sleeper):");
        JTextField classField = new JTextField();
        JLabel ticketsLbl = new JLabel("Number of Tickets:");
        JTextField ticketsField = new JTextField();

        JButton bookBtn = createButton("Book");
        JButton cancelBtn = createButton("Cancel");

        bookBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String name = nameField.getText().trim();
                int age, tickets, ticketClass;
                String phone;

                try {
                    age = Integer.parseInt(ageField.getText());
                    phone = phoneField.getText();
                    ticketClass = Integer.parseInt(classField.getText());
                    tickets = Integer.parseInt(ticketsField.getText());
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid input. Please enter valid details.");
                    return;
                }

                if (!isTicketAvailable(ticketClass, tickets)) {
                    showErrorDialog("Not enough tickets available in this class.");
                    return;
                }

                for (int i = 0; i < tickets; i++) {
                    passengers.add(new Passenger(pnum++, name, age, phone, ticketClass));
                }
                deductTickets(ticketClass, tickets);
                showInfoDialog("Tickets booked successfully!");
                frame.dispose();
            }
        });

        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                frame.dispose();
            }
        });

        frame.setLayout(new GridLayout(7, 2, 10, 10));
        frame.add(nameLbl);
        frame.add(nameField);
        frame.add(ageLbl);
        frame.add(ageField);
        frame.add(phoneLbl);
        frame.add(phoneField);
        frame.add(classLbl);
        frame.add(classField);
        frame.add(ticketsLbl);
        frame.add(ticketsField);
        frame.add(bookBtn);
        frame.add(cancelBtn);

        frame.setVisible(true);
    }

    private void cancelTicket() {
        String input = JOptionPane.showInputDialog("Enter Passenger Number to Cancel:");
        if (input == null || input.trim().isEmpty()) return;

        int pNum;
        try {
            pNum = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid input. Please enter a valid passenger number.");
            return;
        }

        boolean passengerFound = false;
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i).pnum == pNum) {
                Passenger p = passengers.remove(i);
                restoreTickets(p.ticketClass);
                passengerFound = true;
                showInfoDialog("Ticket successfully cancelled for Passenger Number: " + pNum);
                break;
            }
        }

        if (!passengerFound) {
            showErrorDialog("Passenger not found. Please check the Passenger Number.");
        }
    }

    private void searchPassenger() {
        String input = JOptionPane.showInputDialog("Enter Passenger Number to Search:");
        if (input == null || input.trim().isEmpty()) return;

        int pNum;
        try {
            pNum = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid input. Please enter a valid passenger number.");
            return;
        }

        for (Passenger p : passengers) {
            if (p.pnum == pNum) {
                showInfoDialog(p.toString());
                return;
            }
        }

        showErrorDialog("Passenger not found.");
    }

    private void displayReservationChart() {
        StringBuilder chart = new StringBuilder("<html><h2>Reservation Chart:</h2><ul>");
        for (Passenger p : passengers) {
            chart.append("<li>").append(p).append("</li>");
        }
        chart.append("</ul></html>");
        JOptionPane.showMessageDialog(null, chart.toString(), "Reservation Chart", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayUnbookedTickets() {
        String message = String.format("<html><h2>Unbooked Tickets:</h2><ul>" +
                "<li>AC Class: %d</li>" +
                "<li>First Class: %d</li>" +
                "<li>Sleeper Class: %d</li></ul></html>", maxAC, maxFirstClass, maxSleeper);
        JOptionPane.showMessageDialog(null, message, "Unbooked Tickets", JOptionPane.INFORMATION_MESSAGE);
    }

    private JFrame createPopupFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // The isTicketAvailable method
    private boolean isTicketAvailable(int ticketClass, int tickets) {
        if (ticketClass == 1) {
            return tickets <= maxAC;
        } else if (ticketClass == 2) {
            return tickets <= maxFirstClass;
        } else if (ticketClass == 3) {
            return tickets <= maxSleeper;
        } else {
            return false;
        }
    }

    // The deductTickets method
    private void deductTickets(int ticketClass, int tickets) {
        if (ticketClass == 1) {
            maxAC -= tickets;
        } else if (ticketClass == 2) {
            maxFirstClass -= tickets;
        } else if (ticketClass == 3) {
            maxSleeper -= tickets;
        }
    }

    // The restoreTickets method
    private void restoreTickets(int ticketClass) {
        if (ticketClass == 1) {
            maxAC++;
        } else if (ticketClass == 2) {
            maxFirstClass++;
        } else if (ticketClass == 3) {
            maxSleeper++;
        }
    }

    // Passenger class
    class Passenger {
        int pnum;
        String name;
        int age;
        String phone;
        int ticketClass;

        Passenger(int pnum, String name, int age, String phone, int ticketClass) {
            this.pnum = pnum;
            this.name = name;
            this.age = age;
            this.phone = phone;
            this.ticketClass = ticketClass;
        }

        @Override
        public String toString() {
            return String.format("PNum: %d, Name: %s, Age: %d, Phone: %s, Class: %s",
                    pnum, name, age, phone, ticketClassToString());
        }

        private String ticketClassToString() {
            if (ticketClass == 1) {
                return "AC";
            } else if (ticketClass == 2) {
                return "First Class";
            } else if (ticketClass == 3) {
                return "Sleeper";
            } else {
                return "Unknown";
            }
        }
    }
}