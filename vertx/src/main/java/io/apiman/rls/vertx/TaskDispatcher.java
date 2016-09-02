/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.rls.vertx;

import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Vertx;

/**
 * @author eric.wittmann@gmail.com
 */
public class TaskDispatcher {

    private static final TaskDispatcher sInstance = new TaskDispatcher();
    public static final TaskDispatcher getInstance() {
        return sInstance;
    }
    
    private TaskExecutor [] executors = null;

    /**
     * Constructor.
     */
    public TaskDispatcher() {
    }
    
    /**
     * Starts the dispatcher by creating all of the task executors.
     * @param vertx
     */
    public synchronized void start(Vertx vertx) {
        // Only init the executor array once
        if (executors != null) {
            return;
        }
        
        // TODO configure the # of task executors
        executors = new TaskExecutor[4];
        executors[0] = new TaskExecutor(0, vertx);
        executors[1] = new TaskExecutor(1, vertx);
        executors[2] = new TaskExecutor(2, vertx);
        executors[3] = new TaskExecutor(3, vertx);
    }

    /**
     * Dispatches the task to a task executor.
     * @param limitId
     * @param task
     * @param handler
     */
    public <R> void dispatch(String limitId, ITask<R> task, AsyncResultHandler<R> handler) {
        int executorId = Math.abs(limitId.hashCode()) % executors.length;
        executors[executorId].execute(task, handler);
    }

}
