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

package com.codenvy.plugin.tour.client.hopscotch.core;

import com.codenvy.plugin.tour.client.hopscotch.HopscotchTour;
import com.eemi.gwt.tour.client.GwtTour;
import com.eemi.gwt.tour.client.Tour;

/**
 * @author Florent Benoit
 */
public class HopscotchTourImpl implements HopscotchTour {
    @Override
    public Tour getCurrentTour() {
        return GwtTour.getCurrTour();
    }

    @Override
    public void init() {
        GwtTour.load();

        // remove all callouts
        GwtTour.removeAllCallOuts();
        GwtTour.endTour(true);

    }

    @Override
    public int getCurrentStepNum() {
        return GwtTour.getCurrStepNum();
    }

    @Override
    public void startTour(Tour tour, int currentStep) {
        GwtTour.startTour(tour, currentStep);
    }

    @Override
    public void startTour(Tour tour) {
        GwtTour.startTour(tour);
    }
}
