# Joypad View for Android

[![](https://img.shields.io/github/release/eigenein/joypad-android.svg?label=JitPack)](https://jitpack.io/#eigenein/joypad-android/)

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

## License

The MIT License (MIT)

Copyright (c) 2015 Pavel Perestoronin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
