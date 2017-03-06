package org.codelibs.elasticsearch.querybuilders.log4j.util;

import org.codelibs.elasticsearch.querybuilders.log4j.message.Message;

/**
 * Classes implementing this interface know how to supply {@link Message}s.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">functional
 * interface</a> intended to support lambda expressions in log4j 2.
 *
 * <p>Implementors are free to cache values or return a new or distinct value each time the supplier is invoked.
 *
 * <p><strong>DEPRECATED:</strong> this class should not normally be used outside a Java 8+ lambda syntax. Instead,
 * {@link Supplier Supplier<Message>} should be used as an anonymous class. Both this and {@link Supplier} will be
 * removed in 3.0.
 * </p>
 *
 * @since 2.4
 */
public interface MessageSupplier {

    /**
     * Gets a Message.
     *
     * @return a Message
     */
    Message get();
}
