import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

    // Test data from the brief
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mpho, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Lethabo, did you receive the payment?";

    private Path storagePath;

    // Runs before every test so each test starts with no messages and a temporary JSON file
    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        Message.resetMessages();
        storagePath = tempDir.resolve("storedMessages.json");
        Message.setStorageFile(storagePath.toString());
    }

    // Message length tests (assertEquals)

    @Test
    public void testMessageLengthSuccess() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String tooLong = "a".repeat(260);
        Message message = new Message("0012345678", 0, RECIPIENT_1, tooLong);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    // Recipient number tests (assertEquals)

    @Test
    public void testRecipientCorrectlyFormatted() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {
        Message message = new Message("0098765432", 1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    // Message hash tests

    @Test
    public void testMessageHashTestCase1() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        String[] ids = {"0012345678", "0098765432", "0055555555"};
        String[] texts = {MESSAGE_1, MESSAGE_2, "Hi, thanks"};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT", "00:2:HITHANKS"};

        for (int i = 0; i < ids.length; i++) {
            Message message = new Message(ids[i], i, RECIPIENT_1, texts[i]);
            assertEquals(expectedHashes[i], message.createMessageHash());
        }
    }

    // Message ID tests

    @Test
    public void testMessageIDIsCreated() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        String id = message.getMessageID();

        assertTrue(message.checkMessageID());
        assertTrue(id.matches("\\d{10}"));
        assertEquals("Message ID generated: " + id, message.messageIDGenerated());
    }

    // MessageSent tests (send, disregard, store)

    @Test
    public void testSentMessageSend() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", message.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        Message message = new Message("0098765432", 1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully stored.", message.sentMessage(3));
    }

    // Total messages and printing

    @Test
    public void testReturnTotalMessages() {
        Message first = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        Message second = new Message("0098765432", 1, RECIPIENT_2, MESSAGE_2);

        first.sentMessage(1);   // sent
        second.sentMessage(2);  // disregarded, so it is not counted

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessages() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        message.sentMessage(1);

        String printed = Message.printMessages();
        assertTrue(printed.contains("Message ID: 0012345678"));
        assertTrue(printed.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(printed.contains("Recipient: " + RECIPIENT_1));
        assertTrue(printed.contains("Message: " + MESSAGE_1));
    }

    // JSON storage

    @Test
    public void testStoreMessageWritesJsonFile() throws IOException {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        message.storeMessage();

        String json = new String(Files.readAllBytes(storagePath));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"" + RECIPIENT_1 + "\""));
    }
}
