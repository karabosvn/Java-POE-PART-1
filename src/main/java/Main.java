import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        // Registration (repeats until the details are valid)
        System.out.println("=== Registration ===");
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        boolean registered = false;
        while (!registered) {
            System.out.print("Enter a username (must contain an underscore and be "
                    + "no more than five characters long): ");
            String username = scanner.nextLine();
            System.out.print("Enter a password (at least eight characters, one capital "
                    + "letter, one number and one special character): ");
            String password = scanner.nextLine();
            System.out.print("Enter your cell phone number (must include the "
                    + "international country code, e.g. +27...): ");
            String cellNumber = scanner.nextLine();

            String result = login.registerUser(username, password, cellNumber, firstName, lastName);
            System.out.println(result);
            registered = result.contains("registered successfully");
        }

        // Login (repeats until the user logs in successfully)
        System.out.println();
        System.out.println("=== Login ===");
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter your username: ");
            String loginUsername = scanner.nextLine();
            System.out.print("Enter your password: ");
            String loginPassword = scanner.nextLine();

            loggedIn = login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(loggedIn));
        }

        // Only reached once the user has logged in successfully
        System.out.println();
        System.out.println("Welcome to QuickChat.");

        int messagesLeft = askForNumberOfMessages(scanner);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Please choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    messagesLeft = sendMessages(scanner, messagesLeft);
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, please enter 1, 2 or 3.");
            }
        }

        scanner.close();
    }

    // Asks how many messages the user wants to enter (must be a whole number above zero)
    private static int askForNumberOfMessages(Scanner scanner) {
        while (true) {
            System.out.print("How many messages do you wish to enter? ");
            try {
                int count = Integer.parseInt(scanner.nextLine().trim());
                if (count > 0) {
                    return count;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message below
            }
            System.out.println("Please enter a whole number greater than zero.");
        }
    }

    // Lets the user enter the messages they have left; returns how many are still left
    private static int sendMessages(Scanner scanner, int messagesLeft) {
        if (messagesLeft == 0) {
            System.out.println("You have already entered all of your messages.");
            return 0;
        }

        while (messagesLeft > 0) {
            // Recipient (repeats until it is valid)
            String recipient;
            while (true) {
                System.out.print("Enter the recipient's cell number (with international code): ");
                recipient = scanner.nextLine().trim();
                Message check = new Message(Message.returnTotalMessages(), recipient, "check");
                String recipientResult = check.checkRecipientCell();
                System.out.println(recipientResult);
                if (recipientResult.startsWith("Cell phone number successfully")) {
                    break;
                }
            }

            // Message text (repeats until it is no longer than 250 characters)
            String text;
            Message message;
            while (true) {
                System.out.print("Enter your message: ");
                text = scanner.nextLine();
                if (text.trim().isEmpty()) {
                    System.out.println("The message cannot be empty.");
                    continue;
                }
                message = new Message(Message.returnTotalMessages(), recipient, text);
                String lengthResult = message.checkMessageLength();
                System.out.println(lengthResult);
                if (lengthResult.equals("Message ready to send.")) {
                    break;
                }
            }

            System.out.println(message.messageIDGenerated());

            // What should happen to the message?
            int choice = askSendOption(scanner);
            System.out.println(message.sentMessage(choice));

            if (choice == 1) {
                System.out.println();
                System.out.println(message.getDetails());
            } else if (choice == 2) {
                // The message is only deleted once the user presses 0
                while (!scanner.nextLine().trim().equals("0")) {
                    System.out.println("Press 0 to delete the message.");
                }
                System.out.println("Message deleted.");
            }

            messagesLeft--;
        }

        System.out.println();
        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
        return messagesLeft;
    }

    // Asks whether to send, disregard or store the message
    private static int askSendOption(Scanner scanner) {
        while (true) {
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Option: ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2") || input.equals("3")) {
                return Integer.parseInt(input);
            }
            System.out.println("Invalid option, please enter 1, 2 or 3.");
        }
    }
}
