import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    // Shared by every message: how many have been sent and the sent messages themselves
    private static int totalMessagesSent = 0;
    private static final List<Message> sentMessages = new ArrayList<>();

    // The file that stored messages are written to (can be changed for testing)
    private static String storageFile = "storedMessages.json";

    // The details of this one message
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    // Normal constructor: the message ID is randomly generated
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    // Constructor that lets a message ID be supplied (used by the unit tests so the
    // expected hash is known in advance)
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Generates a random ten digit number (leading zeros are allowed)
    private static String generateMessageID() {
        long number = ThreadLocalRandom.current().nextLong(0L, 10_000_000_000L);
        return String.format("%010d", number);
    }

    // Checks that the message ID is not more than ten characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Returns the text shown when a message ID is created
    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    // Checks the recipient cell number by reusing the check from the Login class
    public String checkRecipientCell() {
        Login login = new Login();
        if (login.checkCellPhoneNumber(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // Checks that the message is not longer than 250 characters
    public String checkMessageLength() {
        int length = messageText.length();
        if (length <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - 250)
                + "; please reduce the size.";
    }

    // Creates the hash: first two digits of the ID, the message number,
    // then the first and last words of the message, all in capitals (e.g. 00:0:HITHANKS)
    public String createMessageHash() {
        String idPart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;

        String[] words = messageText.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);

        return (idPart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // Removes punctuation from a word so "tonight?" becomes "tonight"
    private String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // Handles what the user chose to do with the message:
    // 1 = send, 2 = disregard, 3 = store to send later
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return storeMessage();
            default:
                return "Invalid option.";
        }
    }

    // Returns the details of this message in the required order
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    // Returns all the messages that were sent while the program has been running
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }

        StringBuilder builder = new StringBuilder();
        for (Message message : sentMessages) {
            builder.append(message.getDetails()).append("\n\n");
        }
        return builder.toString().trim();
    }

    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Stores this message in a JSON file so it can be sent later.
    // The JSON is written by hand so that no extra library is needed.
    public String storeMessage() {
        Path path = Paths.get(storageFile);
        try {
            String existing = "";
            if (Files.exists(path)) {
                existing = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            }

            String newContent;
            int closingBracket = existing.lastIndexOf(']');

            if (existing.isEmpty() || existing.equals("[]") || closingBracket < 0) {
                // New file (or nothing usable in it): start a fresh JSON array
                newContent = "[\n" + toJson() + "\n]\n";
            } else {
                // Add this message to the end of the existing JSON array
                String withoutBracket = existing.substring(0, closingBracket).replaceAll("\\s+$", "");
                newContent = withoutBracket + ",\n" + toJson() + "\n]\n";
            }

            Files.write(path, newContent.getBytes(StandardCharsets.UTF_8));
            return "Message successfully stored.";
        } catch (IOException e) {
            return "Message could not be stored: " + e.getMessage();
        }
    }

    // Builds the JSON object for this message
    private String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(messageText) + "\"\n"
                + "  }";
    }

    // Escapes characters that are not allowed to appear raw inside a JSON string
    private static String escapeJson(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\\': builder.append("\\\\"); break;
                case '"':  builder.append("\\\""); break;
                case '\n': builder.append("\\n"); break;
                case '\r': builder.append("\\r"); break;
                case '\t': builder.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        builder.append(String.format("\\u%04x", (int) c));
                    } else {
                        builder.append(c);
                    }
            }
        }
        return builder.toString();
    }

    // Getters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }

    // Used by the unit tests to start each test with a clean slate
    public static void resetMessages() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }

    // Used by the unit tests so they do not write into the real JSON file
    public static void setStorageFile(String fileName) {
        storageFile = fileName;
    }
}
