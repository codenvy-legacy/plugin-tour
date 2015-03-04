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
package com.codenvy.plugin.tour.shared;

/**
 * Shared constants for the contribution project type.
 *
 * @author Kevin Pollet
 */
public final class PluginTourProjectTypeConstants {
    public static final String PLUGIN_TOUR_PROJECT_TYPE_ID = "guidedtour";

    public static final String PLUGIN_TOUR_PROJECT_TYPE_DISPLAY_NAME = "CodenvyGuidedTour";

    /** Flag used to trigger the 'automatic' contribution flow. */
    public static final String PLUGIN_TOUR_ATTRIBUTE_NAME = "codenvyGuidedTour";

    /**
     * Disable instantiation.
     */
    private PluginTourProjectTypeConstants() {
    }
}
