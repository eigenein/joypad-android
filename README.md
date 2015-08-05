# Joypad View Library for Android

## Installation

TODO

## Using `JoypadView`

Basic usage is pretty simple:

```xml
<ninja.eigenein.joypad.JoypadView
    android:id="@+id/joypad"
    android:layout_width="200dp"
    android:layout_height="200dp"/>
```

You can configure inner circle color, outer circle color, moveable circle color and triangles color:

```xml
<ninja.eigenein.joypad.JoypadView
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:outer_color="@color/grey_800"
    app:moveable_color="@color/blue_500"
    app:directions_color="@color/blue_900"
    app:inner_color="@color/blue_800"/>
```

You can also configure inner circle radius, outer circle width and moveable circle radius by setting `inner_radius`, `outer_width` and `moveable_radius` attributes.
