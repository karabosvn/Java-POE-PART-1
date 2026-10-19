import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LoginTest {

    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String VALID_CELL = "+27838968976";

    // Username tests

    @Test
    public void testUsernameCorrectlyFormatted() {
        Login login = new Login();
        assertTrue(login.checkUserName("ka_b"));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        Login login = new Login();
        assertFalse(login.checkUserName("karabo!!!!!!!"));
    }

    // Password tests

    @Test
    public void testPasswordMeetsComplexity() {
        Login login = new Login();
        assertTrue(login.checkPasswordComplexity(VALID_PASSWORD));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        Login login = new Login();
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // Cell phone number tests

    @Test
    public void testCellPhoneNumberCorrectlyFormatted() {
        Login login = new Login();
        assertTrue(login.checkCellPhoneNumber(VALID_CELL));
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormatted() {
        Login login = new Login();
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    // registerUser message tests

    @Test
    public void testRegisterUserUsernameIncorrectMessage() {
        Login login = new Login();
        String result = login.registerUser("karabo!!!!!!!", VALID_PASSWORD, VALID_CELL,
                "Karabo", "Mthombeni");
        assertEquals("Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.", result);
    }

    @Test
    public void testRegisterUserPasswordIncorrectMessage() {
        Login login = new Login();
        String result = login.registerUser("ka_b", "password", VALID_CELL,
                "Karabo", "Mthombeni");
        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, "
                + "and a special character.", result);
    }

    @Test
    public void testRegisterUserCellNumberIncorrectMessage() {
        Login login = new Login();
        String result = login.registerUser("ka_b", VALID_PASSWORD, "08966553",
                "Karabo", "Mthombeni");
        assertEquals("Cell number is incorrectly formatted or does not contain an "
                + "international code; please correct the number and try again.", result);
    }

    @Test
    public void testRegisterUserSuccessMessage() {
        Login login = new Login();
        String result = login.registerUser("ka_b", VALID_PASSWORD, VALID_CELL,
                "Karabo", "Mthombeni");
        assertEquals("Username successfully captured. Password successfully captured. "
                + "Cell phone number successfully captured. You have been registered successfully!",
                result);
    }

    // Login tests

    @Test
    public void testLoginSuccessful() {
        Login login = new Login();
        login.registerUser("ka_b", VALID_PASSWORD, VALID_CELL, "Karabo", "Mthombeni");
        assertTrue(login.loginUser("ka_b", VALID_PASSWORD));
    }

    @Test
    public void testLoginFailed() {
        Login login = new Login();
        login.registerUser("ka_b", VALID_PASSWORD, VALID_CELL, "Karabo", "Mthombeni");
        assertFalse(login.loginUser("ka_b", "wrongPassword1!"));
    }

    @Test
    public void testLoginFailedWhenNotRegistered() {
        Login login = new Login();
        assertFalse(login.loginUser("ka_b", VALID_PASSWORD));
    }

    // returnLoginStatus tests

    @Test
    public void testReturnLoginStatusSuccessful() {
        Login login = new Login();
        login.registerUser("ka_b", VALID_PASSWORD, VALID_CELL, "Karabo", "Mthombeni");
        assertEquals("Welcome Karabo, Mthombeni it is great to see you again.",
                login.returnLoginStatus(true));
    }

    @Test
    public void testReturnLoginStatusFailed() {
        Login login = new Login();
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(false));
    }
}
