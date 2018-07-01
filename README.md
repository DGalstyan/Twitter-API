Twitter Client - Android Application
==========
A Simple Twitter Client Application that is integrated with Twitter SDK, implementing MVVM Model and using a lot of Great Libraries

Getting Started
==========
```
Post a status update with an image to Twitter using public API 
The image can be taken by the device camera OR chosen from the gallery. The app should allow the user to do both. 
When the internet connection is turned off, the app should show a popup, saying that there is no internet. The popup should be dismissed automatically when the internet is back. 
Display the history of posted tweets
```

Features
==========
1. Used MVP architecture
2. Used popular third party libraries, such as: Gson, Palette, Retrofit, Fresco and Butterknife
3. Used mockito test methods to test applicarion
4. Used Repository Design Pattern

# Plugins used
[Android Drawable Importar](http://androidgifts.com/material-design-icons-android-studio-drawable-importer/)

# Software Architectural Pattern used
[MVVM (Model–View–ViewModel)](https://en.wikipedia.org/wiki/Model–view–viewmodel)

# Technical Documentation
==========
-----------------------------------------------------------------------------------------------------

# MVVM Software Architecture Pattern :

The Data Binding library for android is something that I’ve been keen to check out for a short while. I decided to experiment with it using the Model-View-ViewModel architectural approach.

**What is MVVM?**

Model-View-ViewModel is an architecural approach used to abstract the state and behaviour of a view, which allows us to separate the development of the UI from the business logic. This is accomplished by the introduction of a ViewModel, whose responsibility is to expose the data objects of a model and handle any of the applications logic involved in the display of a view.

This approach (MVVM) is made up of three core components, each with it’s own distinct and separate role:
- **Model** - Data model containing business and validation logic
- **View** - Defines the structure, layout and appearance of a view on screen
- **ViewModel** - Acts a link between the View and Model, dealing with any view logic

![alt tag](https://cdn-images-1.medium.com/max/1400/1*WfT-BCzN0ZAGzdE30oea1g.png)

**The architecture for MVC is as follows:**
- **The View** sits at the top of the architure with the Controller below it, followed by the Model
- **The Controller** is aware of both the View and Model
- **The View** is aware of just the Model and is notified whenever there are changes to it

**Overall view of MVVM Architecutre :**
- **Model Layer:** Like in MVP, DataManager holds a reference to the RestApi (like Retrofit), database (SQLite), etc. Typical scenario is that model layer gets data from the backend and saves data. The difference between MVP and MVVM from the perspective of the Model Layer is that in MVVM architecture DataManager returns response to Activity/Fragment instead to Presenter. That means that Activity/Fragment is aware of business logic (POJO).
- **View Layer**is a combination of Activity/Fragment with XML and binding. Typical scenario is that Activity requests data from the backend, gets data (POJO) and forwards it to ViewModel Layer. ViewModel Layer updates the UI with the new data.
- **ViewModel** is the middle man between the View Layer and the model (POJO). It receives data from Model Layer and updates the View Layer. Also, it manipulates the model state (fields in POJO objects) as a result from user interaction from the View Layer.

-----------------------------------------------------------------------------------------------------


Android versions
==========
Min Sdk Version 17 <br/>
Target Sdk Version 25 <br/>
Compile Sdk Version 25

Developed By
==========
David Galstyan
