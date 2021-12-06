CompassView
===========

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CompassView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1063)

Compass view for Android.

This library provides a fully customizable linear compass view.

![image](screenshot1.jpg)

## How to use

### Configuring your project dependencies

Add the JitPack repository to your root build.gradle at the end of repositories:

```groovy
   	allprojects {
    	repositories {
    		...
    		maven { url 'https://jitpack.io' }
    	}
    }
````


Add the library dependency to your build.gradle file.

```groovy
dependencies {
    ...
    implementation 'com.github.kix2902:CompassView:master-SNAPSHOT'
}
```

## Adding and Customizing the View

You can declare a `CompassView` just like that:

```XML
<com.redinput.compassview.CompassView
    android:id="@+id/compass"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:backgroundColor="#000000"
    app:showMarker="true"
    app:markerColor="#FF0000"
    app:lineColor="#FFFFFF"
    app:textColor="#FFFFFF"
    app:textSize="15sp"
    app:degrees="0"
    app:rangeDegrees="180.0" />
```

Property values shown above are the defaults of the CompassView and it can be omitted for brevity.

Also, you can set all that properties with Java.

```JAVA
CompassView compass = (CompassView) findViewById(R.id.compass);

compass.setDegrees(57); //immediate change
compass.setDegrees(57,true); //with animation
compass.setBackgroundColor(Color.YELLOW);
compass.setLineColor(Color.RED);
compass.setMarkerColor(Color.BLACK);
compass.setTextColor(Color.WHITE);
compass.setShowMarker(false);
compass.setTextSize(37);
compass.setRangeDegrees(270);

compass.getDegrees(); //read current degrees value
```

Another feature is that you can move the 'CompassView' dragging the view horizontally and even you can attach it a 'OnCompassDragListener' to observe the changes on the degrees value.

```JAVA
compass.setOnCompassDragListener(new CompassView.OnCompassDragListener() {
	@Override
	public void onCompassDragListener(float degrees) {
		// Do what you want with the degrees
	}
});
```


License
-------
    Copyright 2014 RedInput

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.