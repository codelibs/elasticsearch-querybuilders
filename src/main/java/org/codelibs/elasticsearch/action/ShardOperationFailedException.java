/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codelibs.elasticsearch.action;

import org.codelibs.elasticsearch.common.io.stream.Streamable;
import org.codelibs.elasticsearch.common.xcontent.ToXContent;
import org.codelibs.elasticsearch.rest.RestStatus;

/**
 * An exception indicating that a failure occurred performing an operation on the shard.
 *
 *
 */
public interface ShardOperationFailedException extends Streamable, ToXContent {

    /**
     * The index the operation failed on. Might return <tt>null</tt> if it can't be derived.
     */
    String index();

    /**
     * The index the operation failed on. Might return <tt>-1</tt> if it can't be derived.
     */
    int shardId();

    /**
     * The reason of the failure.
     */
    String reason();

    /**
     * The status of the failure.
     */
    RestStatus status();

    /**
     * The cause of this failure
     */
    Throwable getCause();
}
