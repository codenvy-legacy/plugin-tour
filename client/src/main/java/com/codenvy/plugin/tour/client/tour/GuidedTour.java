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

package com.codenvy.plugin.tour.client.tour;

import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.plugin.tour.client.action.ExternalAction;
import com.codenvy.plugin.tour.client.hopscotch.HopscotchTour;
import com.codenvy.plugin.tour.client.html.CustomImage;
import com.codenvy.plugin.tour.client.lifecycle.GuidedTourLifeCycle;
import com.codenvy.plugin.tour.client.log.Log;
import com.codenvy.plugin.tour.dto.GuidedTourAction;
import com.codenvy.plugin.tour.dto.GuidedTourConfiguration;
import com.codenvy.plugin.tour.dto.GuidedTourStep;
import com.eemi.gwt.tour.client.GwtTour;
import com.eemi.gwt.tour.client.Placement;
import com.eemi.gwt.tour.client.Tour;
import com.eemi.gwt.tour.client.TourStep;
import com.eemi.gwt.tour.client.jso.Function;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines the tour logic that will be used to display the steps
 * @author Florent Benoit
 */
public class GuidedTour {

    /**
     * Logger.
     */
    @Inject
    private Log log;

    /**
     * List of callbacks.
     */
    private Set<GuidedTourLifeCycle> callbacks;

    /**
     * List of external actions.
     */
    @Inject
    private Set<ExternalAction> externalActions;

    /**
     * Dto factory for handling Json data
     */
    @Inject
    private DtoFactory dtoFactory;

    /**
     * Wrapper to a hopscotch tour.
     */
    @Inject
    private HopscotchTour hopscotchTour;

    /**
     * Allow to inject images
     */
    @Inject
    private CustomImage customImage;

    /**
     * Instance of the tour that may be displayed when the project is opened.
     */
    private Tour tour;

    /**
     * Current step of the tour (default is -1 = no current step)
     */
    private int currentStep = -1;

    /**
     * Next step to check (it is incremented when a new step is done)
     */
    private int nextStepToCheck = 0;

    /**
     * Is that a tour is being shown ?
     */
    private boolean inProgress = false;

    /**
     * List of steps that need to be displayed. This is the result of the JSON parsing.
     */
    private List<GuidedTourStep> guidedTourSteps;


    /**
     * Default constructor.
     */
    public GuidedTour() {
        // Empty list
        this.guidedTourSteps = new ArrayList<>();

        this.callbacks = new HashSet<>();
    }

    /**
     * Starts the tour. If will start to listen on the end action.
     */
    protected void startTour() {

        // Load
        hopscotchTour.init();

        // Define the tour!
        this.tour = new Tour("myTour");

        // listen on end callback
        GwtTour.listen("end", new EndFunction());

        GwtTour.startTour(tour);
        inProgress = true;

    }


    /**
     * Loads the given JSON data and start the tour
     *
     * @param json
     *         the JSON data
     */
    public void start(String json) {

        // reset the tour steps
        guidedTourSteps.clear();

        // load configuration
        GuidedTourConfiguration configuration = dtoFactory.createDtoFromJson(json, GuidedTourConfiguration.class);

        log.setDebugMode(configuration.getDebugMode());
        this.guidedTourSteps = configuration.getSteps();

        // All has been parsed, let's start
        startTour();
    }

    /**
     * Loop that check if steps have to be displayed
     */
    public void checkTour() {
        log.debug("Checking tour.... currentStep = {0}", currentStep);
        log.debug("Checking tour nextStepToCheck...  {0}", nextStepToCheck);

        if (tour == null) {
            log.debug("tour is null, return");
            return;
        }

        if (guidedTourSteps.isEmpty()) {
            log.debug("no steps in the tour, canceling");
            cancel();
            return;
        }

        // too many items
        if (nextStepToCheck >= guidedTourSteps.size()) {
            log.debug("nextStepToCheck >= guidedTourSteps.size(), canceling");
            cancel();
            return;
        }

        Tour currentTour = hopscotchTour.getCurrentTour();
        if (currentTour != null) {
            log.debug("Checking tour GwtTour.getCurrStepNum() {0}", hopscotchTour.getCurrentStepNum());
        }

        if (inProgress && currentTour != null && hopscotchTour.getCurrentStepNum() == currentStep) {
            log.debug("InProgress and tour != null and current step == step of the tour");
            return;
        }

        GuidedTourStep guidedTourStep = guidedTourSteps.get(nextStepToCheck);

        String elementToCheckName = guidedTourStep.getElement();
        Element elementToCheck = Document.get().getElementById(elementToCheckName);
        log.debug("elementToCheckName = {0}", elementToCheckName);
        log.debug("elementToCheck = {0} ", elementToCheck);

        // Element is present (one of the value is greater than 0 and both are also >= 0)
        if (elementToCheck.getAbsoluteLeft() >= 0 && elementToCheck.getAbsoluteTop() >= 0 &&
            (elementToCheck.getAbsoluteLeft() > 0 || elementToCheck.getAbsoluteTop() > 0)) {

            log.debug("element is visible, updating");
            currentStep = nextStepToCheck;

            Placement placement = Placement.valueOf(guidedTourStep.getPlacement());

            // build new step
            TourStep tourStep = new TourStep(placement, elementToCheck);
            tourStep.setTitle(customImage.addImages(SimpleHtmlSanitizer.sanitizeHtml(guidedTourStep.getTitle()).asString()));
            tourStep.setContent(customImage.addImages(SimpleHtmlSanitizer.sanitizeHtml(guidedTourStep.getContent()).asString()));
            if (guidedTourStep.getXOffset() != null) {
                tourStep.setXOffset(Integer.parseInt(guidedTourStep.getXOffset()));
            }
            tourStep.setZIndex(Integer.MAX_VALUE);

            tour.addStep(tourStep);

            nextStepToCheck++;

            if (currentTour == null) {
                log.debug("no current tour, so start from the current step");
                hopscotchTour.startTour(tour, currentStep);
            } else {
                log.debug("existing tour so start again the tour");
                hopscotchTour.startTour(tour);
            }
            inProgress = true;

        } else {
            log.debug("not changing the element");

        }
    }

    protected void cancel() {
        if (callbacks != null) {
            for (GuidedTourLifeCycle guidedTourLifeCycle : callbacks) {
                guidedTourLifeCycle.end();
            }
        }
    }


    /**
     * Callback used when step has been displayed and user clicked on "Done"
     */
    private class EndFunction implements Function {
        @Override
        public void execute() {
            inProgress = false;

            if (!guidedTourSteps.isEmpty() && currentStep < guidedTourSteps.size()) {
                GuidedTourStep guidedTourStep = guidedTourSteps.get(currentStep);
                List<GuidedTourAction> actions = guidedTourStep.getActions();

                // Execute post actions if required
                if (actions != null) {
                    for (GuidedTourAction guidedTourAction : actions) {
                        String userAction = guidedTourAction.getAction();
                        int firstSpace = userAction.indexOf(' ');
                        if (firstSpace < 0) {
                            continue;
                        }
                        String category = userAction.substring(0, firstSpace);
                        String action = userAction.substring(firstSpace + 1);

                        log.debug("Value of category = ''{0}'' and action ''{1}''", category, action);

                        if (externalActions != null) {
                            for (ExternalAction externalAction : externalActions) {
                                if (externalAction.accept(category)) {
                                    externalAction.execute(action);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public void addCallback(GuidedTourLifeCycle guidedTourLifeCycle) {
        this.callbacks.add(guidedTourLifeCycle);
    }

}
