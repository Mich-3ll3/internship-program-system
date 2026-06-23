package mx.uv.internshipprogramsystem.gui.util;

public final class TextNormalizationUtility {

    private TextNormalizationUtility() {
    }

    public static String toTitleCase(String input) {
        if (input == null) {
            return "";
        }
        String cleanInput = input.replaceAll("\\s+", " ").trim();
        if (cleanInput.isEmpty()) {
            return "";
        }
        String[] words = cleanInput.split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1).toLowerCase());
                if (i < words.length - 1) {
                    titleCase.append(" ");
                }
            }
        }
        return titleCase.toString();
    }
}
