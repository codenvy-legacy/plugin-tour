/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.plugin.tour.client.log.console;

import com.codenvy.plugin.tour.client.TourExtension;
import com.codenvy.plugin.tour.client.log.Log;

/**
 * @author Florent Benoit
 */
public class ConsoleLogImpl implements Log {

    private boolean debugMode;

    /**
     * Toggle to debug mode
     *
     * @param debugMode
     */
    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Debug the given message with args
     */
    public void debug(String message, Object... args) {
        if (debugMode) {
            com.codenvy.ide.util.loging.Log.info(TourExtension.class, format(message, args));
        }
    }

    /**
     * Display into Javascript console some message
     */
    public void info(String message, Object... args) {
        com.codenvy.ide.util.loging.Log.info(TourExtension.class, message);
    }


    /**
     * Format the given pattern and args like MessageFormat in JDK (but in GWT there)
     */
    public static String format(String pattern, Object... arguments) {
        // A very simple implementation of format
        int i = 0;
        while (i < arguments.length) {
            String delimiter = "{" + i + "}";
            while (pattern.contains(delimiter)) {
                pattern = pattern.replace(delimiter, String.valueOf(arguments[i]));
            }
            i++;
        }
        return pattern;
    }

}
