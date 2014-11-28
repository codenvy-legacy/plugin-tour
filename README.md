plugin-tour
===========

guided tour plugin for the on boarding flow

Two options for enabling the GuidedTour
1. CodenvyGuidedTour.json at the root of the project
2. Insert link of the Codenvy tour JSON file inside the factory


Here is an example of CodenvyGuidedTour.json JSON file
```json
{
"debugMode": true,
"steps": [
{
"title": "Run the app",
"content": "A Docker microservice is launched, dependencies are auto-installed and the project is executed. Create custom environments through Docker recipes.<img width='50'src='http://spring.io/img/spring-by-pivotal.png'>",
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
"content": "The appâ€™s URL will appear in the Runner panel, click to open your app in another tab.",
"element": "gwt-debug-Application",
"placement": "TOP",
"xOffset": "100"
},
{
"title": "Stop the app",
"content": "Stop the application when you're done",
"element": "gwt-debug-MainToolbar/shutdownApp-false",
"placement": "TOP",
"xOffset": "-20"
},
{
"title": "Edit the app",
"content": "In the Project Explorer, navigate to src > main > resources > db > hsqldb > populateDB.sql.Replace one of the names with your own name. Save the file.",
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
"content": "Select Git > Commit. Select the checkbox, add a comment and commit. You canâ€™t push your change because this factory is not set up with a fork.",
"element": "gwt-debug-MainMenu/Git-true",
"placement": "BOTTOM",
"xOffset": "-20"
}
]
}
```


Example in a factory JSON:
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
                "https://gist.githubusercontent.com/benoitf/fd6877ecca8651ef0e64/raw/3ef3abcbd9199511fb458ba5937e89570d0d5768/gistfile1.json"
            ]
        }
    },
    ...
}
```
