


package org.codelibs.elasticsearch.querybuilders.mock.log4j.message;

import java.util.Arrays;


public class ParameterizedMessage implements Message {


    private static final int DEFAULT_STRING_BUILDER_SIZE = 255;

    private static final long serialVersionUID = -665975803997290697L;

    private static final int HASHVAL = 31;


    private static ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<>();

    private String messagePattern;
    private transient Object[] argArray;

    private String formattedMessage;
    private transient Throwable throwable;


    @Deprecated
    public ParameterizedMessage(final String messagePattern, final String[] arguments, final Throwable throwable) {
        this.argArray = arguments;
        this.throwable = throwable;
        init(messagePattern);
    }


    public ParameterizedMessage(final String messagePattern, final Object[] arguments, final Throwable throwable) {
        this.argArray = arguments;
        this.throwable = throwable;
        init(messagePattern);
    }


    public ParameterizedMessage(final String messagePattern, final Object... arguments) {
        this.argArray = arguments;
        init(messagePattern);
    }


    public ParameterizedMessage(final String messagePattern, final Object arg) {
        this(messagePattern, new Object[]{arg});
    }


    public ParameterizedMessage(final String messagePattern, final Object arg0, final Object arg1) {
        this(messagePattern, new Object[]{arg0, arg1});
    }

    private void init(final String messagePattern) {
    }

    @Override
    public String getFormat() {
        return messagePattern;
    }


    @Override
    public Object[] getParameters() {
        return argArray;
    }


    @Override
    public Throwable getThrowable() {
        return throwable;
    }


    @Override
    public String getFormattedMessage() {
        if (formattedMessage == null) {
            final StringBuilder buffer = getThreadLocalStringBuilder();
            formatTo(buffer);
            formattedMessage = buffer.toString();
        }
        return formattedMessage;
    }

    private static StringBuilder getThreadLocalStringBuilder() {
        StringBuilder buffer = threadLocalStringBuilder.get();
        if (buffer == null) {
            buffer = new StringBuilder(DEFAULT_STRING_BUILDER_SIZE);
            threadLocalStringBuilder.set(buffer);
        }
        buffer.setLength(0);
        return buffer;
    }

    public void formatTo(final StringBuilder buffer) {
    }


    public static String format(final String messagePattern, final Object[] arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ParameterizedMessage that = (ParameterizedMessage) o;

        if (messagePattern != null ? !messagePattern.equals(that.messagePattern) : that.messagePattern != null) {
            return false;
        }
        if (!Arrays.equals(this.argArray, that.argArray)) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        int result = messagePattern != null ? messagePattern.hashCode() : 0;
        result = HASHVAL * result + (argArray != null ? Arrays.hashCode(argArray) : 0);
        return result;
    }


    public static String deepToString(final Object o) {
        throw new UnsupportedOperationException();
    }


    public static String identityToString(final Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "ParameterizedMessage[messagePattern=" + messagePattern + ", stringArgs=" +
            Arrays.toString(argArray) + ", throwable=" + throwable + ']';
    }
}
