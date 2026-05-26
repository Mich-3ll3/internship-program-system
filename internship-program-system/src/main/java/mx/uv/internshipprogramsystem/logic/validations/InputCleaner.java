package mx.uv.internshipprogramsystem.logic.validations;

public final class InputCleaner {
    private InputCleaner() {

    }

    public static String sanitizeText(String input) {
        String sanitizedText = "";

        if (input != null) {
            sanitizedText = input.replaceAll("[\\p{Cntrl}\\p{Cf}]", "");
            sanitizedText = sanitizedText.replaceAll("[\\s\\u00A0]+", " ");
            sanitizedText = sanitizedText.trim();
        }

        return sanitizedText;
    }
}