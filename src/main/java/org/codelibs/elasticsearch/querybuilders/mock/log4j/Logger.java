


package org.codelibs.elasticsearch.querybuilders.mock.log4j;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.message.Message;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.message.MessageFactory;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.util.MessageSupplier;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.util.Supplier;

public interface Logger {


    void catching(Level level, Throwable t);


    void catching(Throwable t);


    void debug(Marker marker, Message msg);


    void debug(Marker marker, Message msg, Throwable t);


    void debug(Marker marker, MessageSupplier msgSupplier);


    void debug(Marker marker, MessageSupplier msgSupplier, Throwable t);


    void debug(Marker marker, CharSequence message);


    void debug(Marker marker, CharSequence message, Throwable t);


    void debug(Marker marker, Object message);


    void debug(Marker marker, Object message, Throwable t);


    void debug(Marker marker, String message);


    void debug(Marker marker, String message, Object... params);


    void debug(Marker marker, String message, Supplier<?>... paramSuppliers);


    void debug(Marker marker, String message, Throwable t);


    void debug(Marker marker, Supplier<?> msgSupplier);


    void debug(Marker marker, Supplier<?> msgSupplier, Throwable t);


    void debug(Message msg);


    void debug(Message msg, Throwable t);


    void debug(MessageSupplier msgSupplier);


    void debug(MessageSupplier msgSupplier, Throwable t);


    void debug(CharSequence message);


    void debug(CharSequence message, Throwable t);


    void debug(Object message);


    void debug(Object message, Throwable t);


    void debug(String message);


    void debug(String message, Object... params);


    void debug(String message, Supplier<?>... paramSuppliers);


    void debug(String message, Throwable t);


    void debug(Supplier<?> msgSupplier);


    void debug(Supplier<?> msgSupplier, Throwable t);


    void debug(Marker marker, String message, Object p0);


    void debug(Marker marker, String message, Object p0, Object p1);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
               Object p6);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8);


    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8, Object p9);


    void debug(String message, Object p0);


    void debug(String message, Object p0, Object p1);


    void debug(String message, Object p0, Object p1, Object p2);


    void debug(String message, Object p0, Object p1, Object p2, Object p3);


    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8);


    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8, Object p9);


    @Deprecated
    void entry();


    void entry(Object... params);


    void error(Marker marker, Message msg);


    void error(Marker marker, Message msg, Throwable t);


    void error(Marker marker, MessageSupplier msgSupplier);


    void error(Marker marker, MessageSupplier msgSupplier, Throwable t);


    void error(Marker marker, CharSequence message);


    void error(Marker marker, CharSequence message, Throwable t);


    void error(Marker marker, Object message);


    void error(Marker marker, Object message, Throwable t);


    void error(Marker marker, String message);


    void error(Marker marker, String message, Object... params);


    void error(Marker marker, String message, Supplier<?>... paramSuppliers);


    void error(Marker marker, String message, Throwable t);


    void error(Marker marker, Supplier<?> msgSupplier);


    void error(Marker marker, Supplier<?> msgSupplier, Throwable t);


    void error(Message msg);


    void error(Message msg, Throwable t);


    void error(MessageSupplier msgSupplier);


    void error(MessageSupplier msgSupplier, Throwable t);


    void error(CharSequence message);


    void error(CharSequence message, Throwable t);


    void error(Object message);


    void error(Object message, Throwable t);


    void error(String message);


    void error(String message, Object... params);


    void error(String message, Supplier<?>... paramSuppliers);


    void error(String message, Throwable t);


    void error(Supplier<?> msgSupplier);


    void error(Supplier<?> msgSupplier, Throwable t);


    void error(Marker marker, String message, Object p0);


    void error(Marker marker, String message, Object p0, Object p1);


    void error(Marker marker, String message, Object p0, Object p1, Object p2);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
               Object p6);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8);


    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8, Object p9);


    void error(String message, Object p0);


    void error(String message, Object p0, Object p1);


    void error(String message, Object p0, Object p1, Object p2);


    void error(String message, Object p0, Object p1, Object p2, Object p3);


    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8);


    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8, Object p9);


    @Deprecated
    void exit();


    @Deprecated
    <R> R exit(R result);


    void fatal(Marker marker, Message msg);


    void fatal(Marker marker, Message msg, Throwable t);


    void fatal(Marker marker, MessageSupplier msgSupplier);


    void fatal(Marker marker, MessageSupplier msgSupplier, Throwable t);


    void fatal(Marker marker, CharSequence message);


    void fatal(Marker marker, CharSequence message, Throwable t);


    void fatal(Marker marker, Object message);


    void fatal(Marker marker, Object message, Throwable t);


    void fatal(Marker marker, String message);


    void fatal(Marker marker, String message, Object... params);


    void fatal(Marker marker, String message, Supplier<?>... paramSuppliers);


    void fatal(Marker marker, String message, Throwable t);


    void fatal(Marker marker, Supplier<?> msgSupplier);


    void fatal(Marker marker, Supplier<?> msgSupplier, Throwable t);


    void fatal(Message msg);


    void fatal(Message msg, Throwable t);


    void fatal(MessageSupplier msgSupplier);


    void fatal(MessageSupplier msgSupplier, Throwable t);


    void fatal(CharSequence message);


    void fatal(CharSequence message, Throwable t);


    void fatal(Object message);


    void fatal(Object message, Throwable t);


    void fatal(String message);


    void fatal(String message, Object... params);


    void fatal(String message, Supplier<?>... paramSuppliers);


    void fatal(String message, Throwable t);


    void fatal(Supplier<?> msgSupplier);


    void fatal(Supplier<?> msgSupplier, Throwable t);


    void fatal(Marker marker, String message, Object p0);


    void fatal(Marker marker, String message, Object p0, Object p1);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
               Object p6);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8);


    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8, Object p9);


    void fatal(String message, Object p0);


    void fatal(String message, Object p0, Object p1);


    void fatal(String message, Object p0, Object p1, Object p2);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8);


    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8, Object p9);


    Level getLevel();


    <MF extends MessageFactory> MF getMessageFactory();


    String getName();


    void info(Marker marker, Message msg);


    void info(Marker marker, Message msg, Throwable t);


    void info(Marker marker, MessageSupplier msgSupplier);


    void info(Marker marker, MessageSupplier msgSupplier, Throwable t);


    void info(Marker marker, CharSequence message);


    void info(Marker marker, CharSequence message, Throwable t);


    void info(Marker marker, Object message);


    void info(Marker marker, Object message, Throwable t);


    void info(Marker marker, String message);


    void info(Marker marker, String message, Object... params);


    void info(Marker marker, String message, Supplier<?>... paramSuppliers);


    void info(Marker marker, String message, Throwable t);


    void info(Marker marker, Supplier<?> msgSupplier);


    void info(Marker marker, Supplier<?> msgSupplier, Throwable t);


    void info(Message msg);


    void info(Message msg, Throwable t);


    void info(MessageSupplier msgSupplier);


    void info(MessageSupplier msgSupplier, Throwable t);


    void info(CharSequence message);


    void info(CharSequence message, Throwable t);


    void info(Object message);


    void info(Object message, Throwable t);


    void info(String message);


    void info(String message, Object... params);


    void info(String message, Supplier<?>... paramSuppliers);


    void info(String message, Throwable t);


    void info(Supplier<?> msgSupplier);


    void info(Supplier<?> msgSupplier, Throwable t);


    void info(Marker marker, String message, Object p0);


    void info(Marker marker, String message, Object p0, Object p1);


    void info(Marker marker, String message, Object p0, Object p1, Object p2);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
              Object p6);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
              Object p7);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
              Object p7, Object p8);


    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
              Object p7, Object p8, Object p9);


    void info(String message, Object p0);


    void info(String message, Object p0, Object p1);


    void info(String message, Object p0, Object p1, Object p2);


    void info(String message, Object p0, Object p1, Object p2, Object p3);


    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
              Object p8);


    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
              Object p8, Object p9);


    boolean isDebugEnabled();


    boolean isDebugEnabled(Marker marker);


    boolean isEnabled(Level level);


    boolean isEnabled(Level level, Marker marker);


    boolean isErrorEnabled();


    boolean isErrorEnabled(Marker marker);


    boolean isFatalEnabled();


    boolean isFatalEnabled(Marker marker);


    boolean isInfoEnabled();


    boolean isInfoEnabled(Marker marker);


    boolean isTraceEnabled();


    boolean isTraceEnabled(Marker marker);


    boolean isWarnEnabled();


    boolean isWarnEnabled(Marker marker);


    void log(Level level, Marker marker, Message msg);


    void log(Level level, Marker marker, Message msg, Throwable t);


    void log(Level level, Marker marker, MessageSupplier msgSupplier);


    void log(Level level, Marker marker, MessageSupplier msgSupplier, Throwable t);


    void log(Level level, Marker marker, CharSequence message);


    void log(Level level, Marker marker, CharSequence message, Throwable t);


    void log(Level level, Marker marker, Object message);


    void log(Level level, Marker marker, Object message, Throwable t);


    void log(Level level, Marker marker, String message);


    void log(Level level, Marker marker, String message, Object... params);


    void log(Level level, Marker marker, String message, Supplier<?>... paramSuppliers);


    void log(Level level, Marker marker, String message, Throwable t);


    void log(Level level, Marker marker, Supplier<?> msgSupplier);


    void log(Level level, Marker marker, Supplier<?> msgSupplier, Throwable t);


    void log(Level level, Message msg);


    void log(Level level, Message msg, Throwable t);


    void log(Level level, MessageSupplier msgSupplier);


    void log(Level level, MessageSupplier msgSupplier, Throwable t);


    void log(Level level, CharSequence message);


    void log(Level level, CharSequence message, Throwable t);


    void log(Level level, Object message);


    void log(Level level, Object message, Throwable t);


    void log(Level level, String message);


    void log(Level level, String message, Object... params);


    void log(Level level, String message, Supplier<?>... paramSuppliers);


    void log(Level level, String message, Throwable t);


    void log(Level level, Supplier<?> msgSupplier);


    void log(Level level, Supplier<?> msgSupplier, Throwable t);


    void log(Level level, Marker marker, String message, Object p0);


    void log(Level level, Marker marker, String message, Object p0, Object p1);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
             Object p6);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
             Object p7);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
             Object p7, Object p8);


    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
             Object p7, Object p8, Object p9);


    void log(Level level, String message, Object p0);


    void log(Level level, String message, Object p0, Object p1);


    void log(Level level, String message, Object p0, Object p1, Object p2);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
             Object p8);


    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
             Object p8, Object p9);


    void printf(Level level, Marker marker, String format, Object... params);


    void printf(Level level, String format, Object... params);


    <T extends Throwable> T throwing(Level level, T t);


    <T extends Throwable> T throwing(T t);


    void trace(Marker marker, Message msg);


    void trace(Marker marker, Message msg, Throwable t);


    void trace(Marker marker, MessageSupplier msgSupplier);


    void trace(Marker marker, MessageSupplier msgSupplier, Throwable t);


    void trace(Marker marker, CharSequence message);


    void trace(Marker marker, CharSequence message, Throwable t);


    void trace(Marker marker, Object message);


    void trace(Marker marker, Object message, Throwable t);


    void trace(Marker marker, String message);


    void trace(Marker marker, String message, Object... params);


    void trace(Marker marker, String message, Supplier<?>... paramSuppliers);


    void trace(Marker marker, String message, Throwable t);


    void trace(Marker marker, Supplier<?> msgSupplier);


    void trace(Marker marker, Supplier<?> msgSupplier, Throwable t);


    void trace(Message msg);


    void trace(Message msg, Throwable t);


    void trace(MessageSupplier msgSupplier);


    void trace(MessageSupplier msgSupplier, Throwable t);


    void trace(CharSequence message);


    void trace(CharSequence message, Throwable t);


    void trace(Object message);


    void trace(Object message, Throwable t);


    void trace(String message);


    void trace(String message, Object... params);


    void trace(String message, Supplier<?>... paramSuppliers);


    void trace(String message, Throwable t);


    void trace(Supplier<?> msgSupplier);


    void trace(Supplier<?> msgSupplier, Throwable t);


    void trace(Marker marker, String message, Object p0);


    void trace(Marker marker, String message, Object p0, Object p1);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
               Object p6);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8);


    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
               Object p7, Object p8, Object p9);


    void trace(String message, Object p0);


    void trace(String message, Object p0, Object p1);


    void trace(String message, Object p0, Object p1, Object p2);


    void trace(String message, Object p0, Object p1, Object p2, Object p3);


    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8);


    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
               Object p8, Object p9);


    void warn(Marker marker, Message msg);


    void warn(Marker marker, Message msg, Throwable t);


    void warn(Marker marker, MessageSupplier msgSupplier);


    void warn(Marker marker, MessageSupplier msgSupplier, Throwable t);


    void warn(Marker marker, CharSequence message);


    void warn(Marker marker, CharSequence message, Throwable t);


    void warn(Marker marker, Object message);


    void warn(Marker marker, Object message, Throwable t);


    void warn(Marker marker, String message);


    void warn(Marker marker, String message, Object... params);


    void warn(Marker marker, String message, Supplier<?>... paramSuppliers);


    void warn(Marker marker, String message, Throwable t);


    void warn(Marker marker, Supplier<?> msgSupplier);


    void warn(Marker marker, Supplier<?> msgSupplier, Throwable t);


    void warn(Message msg);


    void warn(Message msg, Throwable t);


    void warn(MessageSupplier msgSupplier);


    void warn(MessageSupplier msgSupplier, Throwable t);


    void warn(CharSequence message);


    void warn(CharSequence message, Throwable t);


    void warn(Object message);


    void warn(Object message, Throwable t);


    void warn(String message);


    void warn(String message, Object... params);


    void warn(String message, Supplier<?>... paramSuppliers);


    void warn(String message, Throwable t);


    void warn(Supplier<?> msgSupplier);


    void warn(Supplier<?> msgSupplier, Throwable t);


    void warn(Marker marker, String message, Object p0);


    void warn(Marker marker, String message, Object p0, Object p1);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
              Object p6);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
              Object p7);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
              Object p7, Object p8);


    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
              Object p7, Object p8, Object p9);


    void warn(String message, Object p0);


    void warn(String message, Object p0, Object p1);


    void warn(String message, Object p0, Object p1, Object p2);


    void warn(String message, Object p0, Object p1, Object p2, Object p3);


    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4);


    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);


    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);


    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);


    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
              Object p8);


    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
              Object p8, Object p9);

}
