# Introduction
This is the movie app for the Udacity Android nanodegree course. I've implemented phase 1 and 2 at
the same time.


# IMPORTANT: GETTING STARTED
Follow these instructions or the app won't run!

1. Use Android Studio3.0. You can install it from here: 
https://developer.android.com/studio/preview/index.html
2. Specify the API key by creating a class called Secrets.java.
3. Use the RELEASE build variant. The DEBUG build variant will reset the database on app restart.


# How to specify the api key

Create a class like this:
```java
package com.playposse.udacitymovie;

/**
 * Storage for app secrets that cannot be checked into GitHub.
 */
public class Secrets {

    private static final String movieApiKeyV3 = "[your api secret]";

    public static String getMovieApiKeyV3() {
        return movieApiKeyV3;
    }
}
```


# Coding Standard
- I am a conscientious dissenter of prefixing field names with the letter 'm'. This app follows
the Google coding standard, not that of the Android team. There are plenty of intelligent arguments
on the Internet by leaders of the community why the prefixes are bad. If you are looking for a
pointer to get started on this topic, here you go: 
http://jakewharton.com/just-say-no-to-hungarian-notation/

- The Udacity coding standard asks for all public methods to be commented. I did NOT comment
obvious methods. For example, a 'newInstance' method on a fragment is probably going to create a
new instance of the fragment.


# Network states
The handling of the network states is quite sophisticated.

- On WiFi, the app will greedily try to cache all movie data. Without wifi, it'll cache the 
discovery lists, but defer querying for extended movie info until the user visits the movie detail
activity.

- Without Internet, the app will try to use the cache to offer the user offline usage. If the app
doesn't have a cache to serve from, the user will be parked on a "no network" activity until the
network is restored. Toasts inform the user when the the app switches from cache-only to network
access and back.

- Glide has its own cache. Glide caches the image files. Depending on its own cache, posters and
backdrops may or may not show up in the offline mode.

Example:
The user has only a regular data connection (no WiFi). The user will see the lists instantly from
the cache. When tapping a movie, the user will see the basic movie info. A fraction of a second
later, the full movie info (e.g. tagline, backdrop) will appear as the background query completes.


# Notes
- You will find some unused methods in the utility classes. I've created a few utility classes that
I use on my projects. I copy them from one project to the next. So, I didn't create them from
scratch for this one.

- The icon to favorite a movie is in the top right of the backdrop on the movie detail page.
Depending on the movie backdrop, it can be hard to see. I've added a translucent white background.
The icon is a heart.
