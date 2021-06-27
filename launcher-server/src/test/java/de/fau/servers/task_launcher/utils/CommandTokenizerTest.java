package de.fau.servers.task_launcher.utils;

import static de.fau.servers.task_launcher.utils.CommandTokenizer.splitArgsAndIgnoreQuotes;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CommandTokenizerTest {

    @Test
    public void splitArgsAndIgnoreQuotes_split() {
        List<String> res = splitArgsAndIgnoreQuotes("a b c");
        assertEquals(3, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b", res.get(1));
        assertEquals("c", res.get(2));

        res = splitArgsAndIgnoreQuotes(" add -b --clean ");
        assertEquals(3, res.size());
        assertEquals("add", res.get(0));
        assertEquals("-b", res.get(1));
        assertEquals("--clean", res.get(2));
    }

    @Test
    public void splitArgsAndIgnoreQuotes_splitIgnoreQuotes() {
        List<String> res = splitArgsAndIgnoreQuotes("a \"b c\"");
        assertEquals(2, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b c", res.get(1));

        res = splitArgsAndIgnoreQuotes("a \'b c\'");
        assertEquals(2, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b c", res.get(1));

        res = splitArgsAndIgnoreQuotes("a \"b c\" d e \"f g\" h");
        assertEquals(6, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b c", res.get(1));
        assertEquals("d", res.get(2));
        assertEquals("e", res.get(3));
        assertEquals("f g", res.get(4));
        assertEquals("h", res.get(5));

        res = splitArgsAndIgnoreQuotes("a \'b c\' d e \'f g\' h");
        assertEquals(6, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b c", res.get(1));
        assertEquals("d", res.get(2));
        assertEquals("e", res.get(3));
        assertEquals("f g", res.get(4));
        assertEquals("h", res.get(5));
    }

    @Test
    public void splitArgsAndIgnoreQuotes_splitIgnoreNestedQuotes() {
        List<String> res = splitArgsAndIgnoreQuotes("a \"b c \'d e\' f g\"");
        assertEquals(2, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b c \'d e\' f g", res.get(1));

        res = splitArgsAndIgnoreQuotes("a \'b c \"d e\" f g\'");
        assertEquals(2, res.size());
        assertEquals("a", res.get(0));
        assertEquals("b c \"d e\" f g", res.get(1));
    }
}
