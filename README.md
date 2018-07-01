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
1. Used MVVM architecture
2. Used popular third party libraries, such as: Gson, Palette, Retrofit, Fresco and Butterknife
3. Used mockito test methods to test applicarion
4. Used Repository Design Pattern

# Software Architectural Pattern used
[MVVM (Model–View–ViewModel)](https://en.wikipedia.org/wiki/Model–view–viewmodel)

-----------------------------------------------------------------------------------------------------

# User Documentation :

1. **Login Screen :**

User can press on Login in button to Log in the application via your Twitter Account. If Twitter Application exists in your mobile phone, so it will be redirected to the Twitter Application to get Authontecation Permission otherwise A Webpage will be opened.

2. **Tweets Screen :**

Tweets will appear realted to the logged user. Pull to Refresh and Infinite Scrolling are applied.

3. **Create Tweets :**

Press add button and create new tweet

-----------------------------------------------------------------------------------------------------

# Technical Documentation
-----------------------------------------------------------------------------------------------------
**Fresco :**

Images add much-needed context and visual flair to Android applications. Fresco allows for hassle-free image loading in your application—often in one line of code!

Many common pitfalls of image loading on Android are handled automatically by Fresco:
- Handling ImageView recycling and download cancelation in an adapter.
- Complex image transformations with minimal memory use.
- Automatic memory and disk caching.

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

# MVVM Example implemented on This Application

**- HomeActivity VIEW :**
Defines the structure, layout and appearance of a view on HomeActivity screen. Data binding configuration takes place here since you make a data binding between ViewModel and its XML Layout.

**- HomeViewModel VIEWMODEL :**
Acts a link between the HomeActivity "View" and Tweet "Model", send notification to HomeActivity "View" and FollowersActivity "View" make a data minding commands to HomeViewModel.

**- Tweet MODEL :**
Data model containing business and validation logic.

-----------------------------------------------------------------------------------------------------

Android versions
==========
Min Sdk Version 17 <br/>
Target Sdk Version 25 <br/>
Compile Sdk Version 25

Developed By
==========
David Galstyan
