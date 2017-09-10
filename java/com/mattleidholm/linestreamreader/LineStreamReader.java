package com.mattleidholm.linestreamreader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Spliterator;
import java.util.stream.Stream;

public class LineStreamReader extends Reader {

    private final Stream<String> stream;
    private Spliterator<String> spliterator;
    private StringReader lineReader;

    LineStreamReader(Stream<String> stream) {
        this.stream = stream;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (lineReader == null && !loadNextLine()) {
            return -1;
        }
        int charsRead = lineReader.read(cbuf, off, len);
        if (charsRead < 0) {
            closeCurrentLine();
            charsRead = read(cbuf, off, len);
        }
        return charsRead;
    }

    private void closeCurrentLine() {
        lineReader.close();
        lineReader = null;
    }

    private boolean loadNextLine() {
        return getSpliterator().tryAdvance(s -> lineReader = new StringReader(s + System.lineSeparator()));
    }

    private Spliterator<String> getSpliterator() {
        if (spliterator == null) {
            spliterator = stream.spliterator();
        }
        return spliterator;
    }

    @Override
    public void close() {
        if (lineReader != null) {
            closeCurrentLine();
        }
        stream.close();
    }

    @Override
    public boolean ready() throws IOException {
        return lineReader != null && lineReader.ready();
    }

    public static LineStreamReader of(Stream<String> lineStream) {
        return new LineStreamReader(lineStream);
    }
}
