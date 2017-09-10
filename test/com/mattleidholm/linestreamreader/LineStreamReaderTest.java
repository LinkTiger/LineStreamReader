package com.mattleidholm.linestreamreader;

import org.junit.Test;

import java.io.BufferedReader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LineStreamReaderTest {

    private static final List<String> INPUT = Arrays.asList("a", "bc", "def", "ghij");

    @Test
    public void readLines() throws Exception {
        final LineStreamReader lineStreamReader = new LineStreamReader(INPUT.stream());
        try (BufferedReader bufferedReader = new BufferedReader(lineStreamReader)) {
            List<String> actual = bufferedReader.lines().collect(Collectors.toList());
            assertEquals(INPUT, actual);
        }
    }

    @Test
    public void testClose() throws Exception {
        boolean[] wasStreamClosed = new boolean[]{false};
        Stream<String> closeableStream = INPUT.stream().onClose(() -> wasStreamClosed[0] = true);
        LineStreamReader lineStreamReader = new LineStreamReader(closeableStream);
        lineStreamReader.close();
        assertTrue(wasStreamClosed[0]);
    }

    @Test
    public void testIndividualCharacterReads() throws Exception {
        StringBuilder builder = new StringBuilder();
        try (LineStreamReader lineStreamReader = new LineStreamReader(INPUT.stream())) {
            int c;
            while ((c = lineStreamReader.read()) > 0) {
                builder.append((char) c);
            }
        }
        String expected = INPUT.stream().map(in -> in + System.lineSeparator()).collect(Collectors.joining());
        assertEquals(expected, builder.toString());
    }

    @Test
    public void testTwoCharacterReads() throws Exception {
        StringBuilder builder = new StringBuilder();
        try (LineStreamReader lineStreamReader = new LineStreamReader(INPUT.stream())) {
            char[] chars = new char[2];
            int i;
            while ((i = lineStreamReader.read(chars, 0, 2)) > 0) {
                builder.append(chars, 0, i);
            }
        }
        String expected = INPUT.stream().map(in -> in + System.lineSeparator()).collect(Collectors.joining());
        assertEquals(expected, builder.toString());
    }

    @Test
    public void testCharBufferReads() throws Exception {
        StringBuilder builder = new StringBuilder();
        try (LineStreamReader lineStreamReader = new LineStreamReader(INPUT.stream())) {
            CharBuffer chars = CharBuffer.allocate(3);
            int i;
            while ((i = lineStreamReader.read(chars)) > 0) {
                chars.rewind();
                builder.append(chars.subSequence(0, i));
                chars = CharBuffer.allocate(3);
            }
        }
        String expected = INPUT.stream().map(in -> in + System.lineSeparator()).collect(Collectors.joining());
        assertEquals(expected, builder.toString());
    }
}