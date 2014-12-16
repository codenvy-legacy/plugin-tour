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

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.plugin.tour.client.action.ExternalAction;
import com.codenvy.plugin.tour.client.hopscotch.HopscotchTour;
import com.codenvy.plugin.tour.client.html.CustomImage;
import com.codenvy.plugin.tour.client.html.DomElementHelper;
import com.codenvy.plugin.tour.client.lifecycle.GuidedTourLifeCycle;
import com.codenvy.plugin.tour.client.log.Log;
import com.codenvy.plugin.tour.dto.GuidedTourAction;
import com.codenvy.plugin.tour.dto.GuidedTourConfiguration;
import com.codenvy.plugin.tour.dto.GuidedTourImageOverlay;
import com.codenvy.plugin.tour.dto.GuidedTourStep;
import com.eemi.gwt.tour.client.GwtTour;
import com.eemi.gwt.tour.client.Placement;
import com.eemi.gwt.tour.client.Tour;
import com.eemi.gwt.tour.client.TourStep;
import com.eemi.gwt.tour.client.jso.Function;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * Analytics
     */
    @Inject
    private AnalyticsEventLogger analyticsEventLogger;

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
     * Name of this guided tour
     */
    private String tourName;

    /**
     * DomElement helper
     */
    private DomElementHelper domElementHelper;

    /**
     * Specify that first step is more a welcome step than a step (no arrow, no number)
     */
    private boolean hasWelcomeStep;

    /**
     * List of images that may be displayed at a given step.
     */
    private List<Image> currentImages;

    /**
     * Default constructor.
     */
    public GuidedTour() {
        // Empty list
        this.guidedTourSteps = new ArrayList<>();

        this.callbacks = new HashSet<>();

        this.currentImages = new ArrayList<>();
    }

    /**
     * Starts the tour. If will start to listen on the end action.
     */
    protected void startTour() {

        // Load
        hopscotchTour.init();

        if (domElementHelper == null) {
            domElementHelper = new DomElementHelper();
        }

        // Define the tour!
        this.tour = new Tour(tourName);

        // listen on end callback
        GwtTour.listen("end", new EndFunction());

        GwtTour.startTour(tour);
        inProgress = true;

        // send start event
        Map<String, String> data = new HashMap<>();
        data.put("name", tourName);
        analyticsEventLogger.log(GuidedTour.class, "start", data);

    }


    /**
     * Loads the given JSON data and start the tour
     * @param json the JSON data
     */
    public void start(String json) {

        // reset the tour steps
        guidedTourSteps.clear();

        // load configuration
        GuidedTourConfiguration configuration = dtoFactory.createDtoFromJson(json, GuidedTourConfiguration.class);

        log.setDebugMode(configuration.getDebugMode());
        this.guidedTourSteps = configuration.getSteps();

        // name of the tour
        this.tourName = configuration.getName();
        if (this.tourName == null || tourName.isEmpty()) {
            tourName = "unamed";
        }

        // welcome step
        this.hasWelcomeStep = configuration.getHasWelcomeStep();

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
        int currentStepNum = Integer.MAX_VALUE;
        if (currentTour != null) {
            log.debug("Checking tour GwtTour.getCurrStepNum() {0}", hopscotchTour.getCurrentStepNum());
            currentStepNum = hopscotchTour.getCurrentStepNum();
        }

        if (inProgress && currentStepNum <= currentStep) {
            log.debug("InProgress and tour != null and currentStepNum <= currentStep");
            return;
        }

        final GuidedTourStep guidedTourStep = guidedTourSteps.get(nextStepToCheck);

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
            if (guidedTourStep.getYOffset() != null) {
                tourStep.setYOffset(Integer.parseInt(guidedTourStep.getYOffset()));
            }
            if (guidedTourStep.getArrowOffset() != null) {
                tourStep.setArrowOffset(Integer.parseInt(guidedTourStep.getArrowOffset()));
            }
            tourStep.setZIndex(Integer.MAX_VALUE);

            // hide widgets at welcome step
            if (hasWelcomeStep && currentStep == 0) {
                guidedTourStep.setHideArrow(Boolean.TRUE);
                guidedTourStep.setHideBubbleNumber(Boolean.TRUE);
            }

            // do we display the skip button ?
            Boolean skip = guidedTourStep.getSkipButton();
            if (skip == null || skip.booleanValue()) {
                tourStep.setShowCTAButton(true);
                String skipLabel = guidedTourStep.getSkipButtonLabel();
                if (skipLabel == null || skipLabel.isEmpty()) {
                    skipLabel = "Skip";
                }
                tourStep.setCTALabel(skipLabel);
                // Register skip action
                tourStep.onCTA(new Function() {
                    @Override
                    public void execute() {
                        tour = null;
                        log.debug("Tour ended");
                        hopscotchTour.endTour();
                        // send skip event
                        Map<String, String> data = new HashMap<>();
                        data.put("name", tourName);
                        data.put("step", String.valueOf(currentStep));
                        analyticsEventLogger.log(GuidedTour.class, "skip", data);

                        // clear any images
                        clearImages();

                    }
                });
            }


            // width ?
            String width = guidedTourStep.getWidth();
            if (width != null && !width.isEmpty()) {
                try {
                    int bubbleWidth = Integer.parseInt(width);
                    tourStep.setWidth(bubbleWidth);
                } catch(NumberFormatException e) {
                    // no width
                }
            }

            // display the text of the next button
            tourStep.onShow(new NextFunction(guidedTourStep));

            // hide arrow
            if (guidedTourStep.getHideArrow() != null && guidedTourStep.getHideArrow().booleanValue()) {
                domElementHelper.getArrowElement().getStyle().setDisplay(Style.Display.NONE);
            } else {
                domElementHelper.getArrowElement().getStyle().clearDisplay();
            }

            // hide bubble number
            if (guidedTourStep.getHideBubbleNumber() != null && guidedTourStep.getHideBubbleNumber().booleanValue()) {
                domElementHelper.getBubbleNumberElement().getStyle().setDisplay(Style.Display.NONE);
                domElementHelper.getContentElement().getStyle().setMarginLeft(15, Style.Unit.PX);
            } else {
                domElementHelper.getBubbleNumberElement().getStyle().clearDisplay();
                domElementHelper.getContentElement().getStyle().clearMarginLeft();
            }


            // Recreate tour in order to start at correct number
            if (hasWelcomeStep && currentStep == 1) {
                this.tour = new Tour(tourName);
            }

            tour.addStep(tourStep);

            // do we have overlays
            List<GuidedTourImageOverlay> overlays = guidedTourStep.getOverlays();
            if (overlays != null) {
                for (GuidedTourImageOverlay imageOverlay : overlays) {
                    addOverlay(imageOverlay);
                }
            }

            log.info("currentStep ==" + currentStep);
            nextStepToCheck++;
            if (hasWelcomeStep && currentStep > 0) {
                log.info("hasWelcomeStep");
                log.info("hasWelcomeStep starting at " + (currentStep - 1));
                hopscotchTour.startTour(tour, currentStep - 1);
            } else if (currentTour == null) {
                log.info("CurrentTOur == null");
                log.debug("no current tour, so start from the current step");
                hopscotchTour.startTour(tour, currentStep);
            } else {
                log.info("else");

                log.debug("existing tour so start again the tour");
                hopscotchTour.startTour(tour);
            }

            // send skip event
            Map<String, String> data = new HashMap<>();
            data.put("name", tourName);
            data.put("step", String.valueOf(currentStep));
            analyticsEventLogger.log(GuidedTour.class, "show", data);

            inProgress = true;

        } else {
            log.debug("not changing the element");

        }
    }

    /**
     * Adds the given overlay.
     * @param overlay the details of the overlay
     */
    protected void addOverlay(GuidedTourImageOverlay overlay) {

        // First, check if element is there
        String elementToCheckName = overlay.getElement();
        Element elementToCheck = Document.get().getElementById(elementToCheckName);
        if (elementToCheck == null) {
            log.error("The element " + elementToCheckName + " does not exist, skipping overlay");
            return;
        }

        if (overlay.getUrl() == null) {
            log.error("No URL given for image URL, skipping overlay");
            return;
        }

        int top = elementToCheck.getAbsoluteTop();
        int left = elementToCheck.getAbsoluteLeft();

        // do we have shift offset ?
        String xOffsetStr = overlay.getXOffset();
        if (xOffsetStr != null && !xOffsetStr.isEmpty()) {
            try {
                left = left + Integer.parseInt(xOffsetStr);
            } catch (NumberFormatException e) {
                log.error("Invalid xOffset" + xOffsetStr);
            }
        }

        String yOffsetStr = overlay.getYOffset();
        if (yOffsetStr != null && !yOffsetStr.isEmpty()) {
            try {
                top = top + Integer.parseInt(yOffsetStr);
            } catch (NumberFormatException e) {
                log.error("Invalid yOffset" + yOffsetStr);
            }
        }


        // Create a Image widget
        final Image image = new Image();

        // Set image URL
        image.setUrl(UriUtils.sanitizeUri(overlay.getUrl()));

        // absolute position
        image.getElement().getStyle().setTop(top, Style.Unit.PX);
        image.getElement().getStyle().setLeft(left, Style.Unit.PX);
        image.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        image.getElement().getStyle().setZIndex(5);

        // add image
        RootPanel.get().add(image);
        currentImages.add(image);


        // image should disappear on click
        image.addClickHandler(new RemoveImageClickHandler(image));

    }

    /**
     * Clear all images that may be displayed now
     */
    protected void clearImages() {
        for (Image image : currentImages) {
            RootPanel.get().remove(image);
        }
        currentImages.clear();
    }


    /**
     * Callback when tour is being cancelled
     */
    protected void cancel() {
        clearImages();
        if (callbacks != null) {
            for (GuidedTourLifeCycle guidedTourLifeCycle : callbacks) {
                guidedTourLifeCycle.end();
            }
        }
    }


    /**
     * Action to perform when next is being displayed
     */
    private static class NextFunction implements Function {
        private final GuidedTourStep guidedTourStep;

        public NextFunction(GuidedTourStep guidedTourStep) {
            this.guidedTourStep = guidedTourStep;
        }

        @Override
        public void execute() {
            Element doneButton = Document.get().getElementById("hopscotch-done");
            if (doneButton != null) {
                String nextLabel = guidedTourStep.getNextButtonLabel();
                if (nextLabel == null || nextLabel.isEmpty()) {
                    nextLabel = "Next";
                }
                doneButton.setInnerText(nextLabel);
            }

        }
    }

    /**
     * Remove the given image from root panel
     */
    private static class RemoveImageClickHandler implements ClickHandler {
        private final Image image;

        public RemoveImageClickHandler(Image image) {
            this.image = image;
        }

        @Override
        public void onClick(ClickEvent event) {
            RootPanel.get().remove(image);
        }
    }


    /**
     * Callback used when step has been displayed and user clicked on "Next"
     */
    private class EndFunction implements Function {

        @Override
        public void execute() {
            // clear any images
            clearImages();


            // tour ended
            if (tour == null) {
                return;
            }

            inProgress = false;
            if (!guidedTourSteps.isEmpty() && currentStep < guidedTourSteps.size()) {

                // send next event
                Map<String, String> data = new HashMap<>();
                data.put("name", tourName);
                data.put("step", String.valueOf(currentStep));
                analyticsEventLogger.log(GuidedTour.class, "next", data);

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
