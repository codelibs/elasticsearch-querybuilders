


package org.codelibs.elasticsearch.querybuilders.mock.log4j;

import java.io.Serializable;


public final class Level implements Comparable<Level>, Serializable {

    public static Level OFF;

    public static Level FATAL;

    public static Level ERROR;

    public static Level WARN;

    public static Level INFO = new Level("info", 0);

    public static Level DEBUG;

    public static Level TRACE;

    public static Level ALL;

    private static final long serialVersionUID = 1581082L;

    private Level(final String name, final int intLevel) {
    }

    public String name() {
        return null;
    }

    public static Level valueOf(final String name) {
        return INFO;
    }

    @Override
    public int compareTo(Level o) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")

    public Level clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
