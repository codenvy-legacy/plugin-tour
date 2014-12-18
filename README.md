Guided Tour Plugin
==================

![Example](https://raw.githubusercontent.com/benoitf/tour-resources/master/example.png "Example")

The Guided Tour plugin allows to make some on-boarding flows for projects.

## Setup

### Factory setup
Guided Tour is inserted in a factory by using an attribute named 'codenvyGuidedTour'.

```json
{
    "v": "2.0",
    "project": {
        ...
        "attributes": {
            "language": [
                "java"
            ],
            "codenvyGuidedTour": [
                "https://gist.githubusercontent.com/benoitf/.....guidedtour.json"
            ]
        }
    },
    ...
}
```
### For developing a Guided Tour without factory
Add CodenvyGuidedTour.json file in the root folder

## Cheat Sheet

### File content
JSON Codenvy Guided Tour file is composed of root attributes which are
```json
{
"name": "Name of the tour",
"hasWelcomeStep" : boolean,
"steps" : [
    ...All the steps that are in a file
    ]
}
```
#### Root attributes

* 'name' (String): Defines the name of the Guided Tour

* 'hasWelcomeStep' (boolean): if this optional attribute is there, the first step of the Guided Tour will be unnumbered and without any arrows. Following steps will start numbering at 1

* 'steps' (composite): Defines all the steps and their settings that will be displayed

### Step attributes

* title (String) (mandatory): Title of the step (HTML with limited content : only "b", "em", "i", "h1", "h2", "h3", "h4", "h5", "h6", "hr","ul","ol", "li")

* content (String) (mandatory): HTML code that will be displayed as content of the step (HTML with limited content : only "b", "em", "i", "h1","h2", "h3", "h4", "h5", "h6", "hr", "ul", "ol", "li".
  * For inserting image the following syntax is allowed ```![alt name](URL of image) or ![alt name](URL of image = WIDTHxHEIGHT) (HEIGHT being optional)```
  * For inserting links, the wiki hyperlink syntax ``` [http://www.codenvy.com] or [http://www.codenvy.com A title for this link] can be used```

* element (String) (mandatory): HTML dom element that should be checked (example: <em>gwt-debug-MainToolbar/runApp-true</em>)

Global parameter:

* name : name of the Guided Tour
 
Parameter for a step:

* title : title of the bubble

* content : Content of the bubble

* nextButtonLabel: Label for the next button

* skipButton: Boolean for specifying if skip button is displayed (default is true)

* skipButtonLabel: Label for the skip button

* element: dom element that should be checked

* placement: It could be LEFT, RIGHT, TOP, BOTTOM

* xOffset: shift the arrow from the given offset

* yOffset: shift the arrow from the given offset

* arrowOffset: shift the arrow from the given offset

* width : width of the bubble

* actions: actions executed after the step: action|openfile

  * "action runApp" (runApp or any other IDE action)

  * "openfile /path" 

  * "opentab tab-identifier.titleOfTheTab"
   A tab identifier could be NAVIGATION (left of IDE), INFORMATION (bottom of IDE), EDITING (central), TOOLING (right)



Here is an example of CodenvyGuidedTour.json JSON file
```json
{
  "steps": [
        {
          "title": "Run the app",
          "content": "A Docker microservice is launched, dependencies are auto-installed and the project is executed. Create custom environments through Docker recipes.![](http://spring.io/img/spring-by-pivotal.png = 50x)",
          "element": "gwt-debug-MainToolbar/runApp-true",
          "placement": "BOTTOM",
          "xOffset": "-20",
          "actions": [
           {
             "action": "action runApp"
            }
           ]
        },
        {
          "title": "Use the app",
          "content": "The application URL will appear in the Runner panel, click to open your app in another tab.",
          "element": "gwt-debug-Application",
          "placement": "TOP",
          "xOffset": "100"
          },
          {
          "title": "Stop the app",
          "content": "Stop the application when you're done",
          "element": "gwt-debug-MainToolbar/shutdownApp-true",
          "placement": "TOP",
          "xOffset": "-20"
          },
          {
          "title": "Edit the app",
          "content": "In the Project Explorer, navigate to src > main > resources > db > hsqldb > populateDB.sql.Replace one of           the names with your own name. Save the file.",
          "element": "gwt-debug-projectExplorerTree-panel",
          "placement": "RIGHT",
          "actions": [
          {
          "action": "openfile pom.xml"
          }
          ]
          },
          {
          "title": "Commit the changes",
          "content": "Select Git > Commit. Select the checkbox, add a comment and commit. You canâ€™t push your change because           this factory is not set up with a fork.",
          "element": "gwt-debug-MainMenu/git-true",
          "placement": "BOTTOM",
          "xOffset": "-20"
          }
    ]
}
```


