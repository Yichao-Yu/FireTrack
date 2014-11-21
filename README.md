FireTrack
========
An Android and Java Enterprise App that tracks and manages personal activties.

At Android side, the app captures the current location (coordinates) of the device by calling Google Play APIs. The website, which is going to be based on Spring MVC, will allow users to manage their tracks, etc.

The two sides talk to each other via Firebase Android API and REST API.

The authentication mechanism is based on Firebase's Security & Rules: https://www.firebase.com/docs/security/ Essentially, auth rules are set up on the Firebase side, including Authentication and Authorization, data validation.
