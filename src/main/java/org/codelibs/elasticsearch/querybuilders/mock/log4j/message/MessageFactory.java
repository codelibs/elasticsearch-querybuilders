


package org.codelibs.elasticsearch.querybuilders.mock.log4j.message;


public interface MessageFactory {


    Message newMessage(Object message);


    Message newMessage(String message);


    Message newMessage(String message, Object... params);
}
