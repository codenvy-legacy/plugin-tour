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

package com.codenvy.plugin.tour.client.hopscotch;

import com.eemi.gwt.tour.client.Tour;

/**
 * Allow to interface with real implementation
 * @author Florent Benoit
 */
public interface HopscotchTour {

    Tour getCurrentTour();

    void init();

    int getCurrentStepNum();

    void startTour(Tour tour, int currentStep);

    void startTour(Tour tour);

}
