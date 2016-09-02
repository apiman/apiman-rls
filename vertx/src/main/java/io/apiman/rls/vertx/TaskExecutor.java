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

import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author eric.wittmann@gmail.com
 */
public class TaskExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    private final LinkedBlockingQueue<TaskAndStuff> queue = new LinkedBlockingQueue<>();
    private final int executorId;
    private final Vertx vertx;
    private Thread thread;

    /**
     * Constructor.
     * @param executorId
     * @param vertx
     */
    public TaskExecutor(int executorId, Vertx vertx) {
        this.vertx = vertx;
        log.debug("Creating task executor: {0} :: {1}", executorId, Thread.currentThread()); //$NON-NLS-1$
        
        this.executorId = executorId;
        thread = new Thread(() -> {
            while (Boolean.TRUE) {
                TaskAndStuff taskAndHandler = null;
                try {
                    taskAndHandler = queue.take();
                    log.debug("Task received by executor, processing:: {0}", Thread.currentThread()); //$NON-NLS-1$
                    final Object rval = taskAndHandler.task.execute();
                    AsyncResult result = new AsyncResult() {
                        @Override
                        public Object result() {
                            return rval;
                        }

                        @Override
                        public Throwable cause() {
                            return null;
                        }

                        @Override
                        public boolean succeeded() {
                            return true;
                        }

                        @Override
                        public boolean failed() {
                            return false;
                        }
                    };

                    final AsyncResultHandler handler = taskAndHandler.handler;
                    taskAndHandler.context.runOnContext((v1) -> {
                        handler.handle(result);
                    });
                } catch (InterruptedException e) {
                    // skip - keep trying!
                } catch (Exception e) {
                    AsyncResult result = new AsyncResult() {
                        @Override
                        public Object result() {
                            return null;
                        }

                        @Override
                        public Throwable cause() {
                            return e;
                        }

                        @Override
                        public boolean succeeded() {
                            return false;
                        }

                        @Override
                        public boolean failed() {
                            return true;
                        }
                    };
                    final AsyncResultHandler handler = taskAndHandler.handler;
                    taskAndHandler.context.runOnContext((v1) -> {
                        handler.handle(result);
                    });
                }
            }
        });
        thread.setName("TaskExecutor-" + this.executorId); //$NON-NLS-1$
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Puts the task into the task executor's queue of tasks.  The executor
     * will eventually get around to executing the task.
     * @param task
     */
    public <T> void execute(ITask<T> task, AsyncResultHandler<T> handler) {
        try {
            queue.put(new TaskAndStuff<T>(task, handler, vertx.getOrCreateContext()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class TaskAndStuff<T> {
        final ITask<T> task;
        final AsyncResultHandler<T> handler;
        final Context context;
        
        /**
         * Constructor.
         * @param context 
         */
        public TaskAndStuff(ITask<T> task, AsyncResultHandler<T> handler, Context context) {
            this.task = task;
            this.handler = handler;
            this.context = context;
        }
    }

}
