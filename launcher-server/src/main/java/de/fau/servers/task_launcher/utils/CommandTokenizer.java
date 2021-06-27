package de.fau.servers.task_launcher.utils;

import java.util.ArrayList;
import java.util.List;

public final class CommandTokenizer {

    /**
     * Tokenizes the given command-line argument string to be ready to be feed into the
     * `ProcessBuilder`. Thereby, sections in quotes getting escaped and striped from the quote
     * signs.
     *
     * @param cmdWithArgs The string with the command and his arguments.
     * @return A List containing the separated command and the arguments.
     */
    public static List<String> splitArgsAndIgnoreQuotes(final String cmdWithArgs) {
        final ArrayList<String> retList = new ArrayList<>();
        boolean isQuoteOpen = false;
        boolean isDoubleQuoteOpen = false;
        int start = -1;

        final String tr = cmdWithArgs.trim();
        for (int i = 0; i < tr.length(); i++) {
            int cp = tr.codePointAt(i);
            switch (cp) {
                case ' ':
                    if (isQuoteOpen || isDoubleQuoteOpen) {
                        continue;
                    }
                    retList.add(stripQuotes(tr.substring(start + 1, i)));
                    start = i;
                    break;
                case '\'':
                    isQuoteOpen = !isQuoteOpen;
                    break;
                case '\"':
                    isDoubleQuoteOpen = !isDoubleQuoteOpen;
                    break;
                default:
                    break;
            }
        }

        retList.add(stripQuotes(tr.substring(start + 1, tr.length())));
        return retList;
    }

    /**
     * Helper function which strips potential leading and trailing `"` and `'` quote sings from the
     * given string.
     *
     * @param enclosedInQuotes The string which may be enclosed in quote sings.
     * @return The string without enclosing quote signs.
     */
    protected static String stripQuotes(final String enclosedInQuotes) {
        if (enclosedInQuotes.startsWith("\"") || enclosedInQuotes.startsWith("\'")) {
            return enclosedInQuotes.substring(1, enclosedInQuotes.length() - 1);
        }
        return enclosedInQuotes;
    }
}
