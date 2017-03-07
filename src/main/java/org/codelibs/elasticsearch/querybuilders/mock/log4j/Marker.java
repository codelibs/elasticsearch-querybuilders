


package org.codelibs.elasticsearch.querybuilders.mock.log4j;

import java.io.Serializable;


public interface Marker extends Serializable {

    @Override
    boolean equals(Object obj);


    String getName();


    Marker[] getParents();


    @Override
    int hashCode();


    boolean hasParents();


    boolean isInstanceOf(Marker m);


    boolean isInstanceOf(String name);


    boolean remove(Marker marker);


    Marker setParents(Marker... markers);
}
