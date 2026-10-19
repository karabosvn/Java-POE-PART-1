import java.util.regex.Pattern;

public class Login {

    // Stores the details of the one registered user
    private String username;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;

    // Keeps track of whether a user has registered yet
    private boolean isRegistered;

    // Regular expression for the cell phone number: a plus sign, an international
    // country code (1 to 3 digits) followed by the number (no more than 10 digits).
    // Reference (IEEE): Oracle, "Pattern (Java Platform SE 8)," Java Documentation.
    // [Online]. Available: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
    // [Accessed: <add the date you used it>].
    // (Replace this with the exact source you researched if it is different.)
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");

    // Checks that the username contains an underscore and is no more than five characters long
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    // Checks that the password is at least eight characters long and contains
    // a capital letter, a number and a special character
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        // Go through every character in the password
        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                hasCapitalLetter = true;
            } else if (Character.isDigit(currentChar)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(currentChar)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapitalLetter && hasNumber && hasSpecialCharacter;
    }

    // Checks that the cell phone number starts with an international code (plus sign
    // followed by the country code) and that the number after it is no more than ten digits
    public boolean checkCellPhoneNumber(String cellNumber) {
        if (cellNumber == null) {
            return false;
        }
        return CELL_PATTERN.matcher(cellNumber).matches();
    }

    // Registers a new user once the username, password and cell phone number
    // have all passed their validation checks
    public String registerUser(String username, String password, String cellNumber,
                               String firstName, String lastName) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, "
                    + "and a special character.";
        }

        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell number is incorrectly formatted or does not contain an "
                    + "international code; please correct the number and try again.";
        }

        // All checks passed so store the user's details
        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isRegistered = true;

        return "Username successfully captured. Password successfully captured. "
                + "Cell phone number successfully captured. You have been registered successfully!";
    }

    // Checks that the entered username and password match the details
    // captured when the user registered
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (!isRegistered) {
            return false;
        }
        return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
    }

    // Returns the message that should be displayed after a login attempt
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
