# Image processing & contour extraction

This is my [AVGL](https://www.avantgarde-labs.de) Hackathon 2018 Project - an image processing pipeline using J8 functional patterns.

## Overview

The framework utilizes the power and simplicity of j8's functional patterns
to get its work done. They provide everything needed to build an image
processing pipeline. In fact, the part of the code I would call the _framework_
consists of only a few classes like `Source`, `Split` and `Merge`.
Im using Sarxos's great [Webcam Capture Library](https://github.com/sarxos/webcam-capture)
to access data stream from my webcam. Everything else is implemented as 
separate processing unit. 

Processing units are nothing more then a `java.util.function.Function<I, O>` which
itself can be composed to a full processing pipeline using its built-in methods 
`compose(..)` and `andThen(..)`. Additionally the `Source<T>` interface exists 
which is just an extended `Supplier<T>` with some helper methods to chain it 
with processing units.

These units are defined in `de.mknblch.vpipe.Functions.*` and implement some
basic image processing functions like Color/Intensity-to-Color/Intensity image 
transformation, spatial convolution, contour extraction and some visualization 
units to transform the image back into a `BufferedImage`.

A common pipe is shown below:

```
    import static de.mknblch.vpipe.Functions.*;

    final Source<BufferedImage> pipe = SarxosWebcamSource.choose()
            .connectTo(grayscale())
            .connectTo(contrast(2))
            .connectTo(contours(128))
            .connectTo(renderAll(640, 480));

    Viewer.start(pipe);
```

####  Maven

Yep - Im using github as [poor-mans-repo](https://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github) ;)

``` 
<repositories>
    <repository>
        <id>vpipe-mvn-repo</id>
        <url>https://raw.github.com/mknblch/vpipe/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>de.mknblch.vpipe</groupId>
        <artifactId>vpipe</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
## ToDo's

- add multithreading in `de.mknblch.vpipe.functions.Split` using a predefined
thread pool
- pattern recognition (fourier descriptors)

## Examples

contours 

![contours](https://mknblch.github.io/vpipe/fiducial2.png)

colorization by depth in the contour tree

![contours](https://mknblch.github.io/vpipe/fiducial.png)

drawing bounding boxes instead of contours gives you this

![contours](https://mknblch.github.io/vpipe/fiducial4.png)

using a more complex pipe which splits the image into
its 3 colors and computes the contours on each of them separately
leads to this view

![contours](https://mknblch.github.io/vpipe/acid.png)

besides all the sexy looking images the software can also do some
useful stuff like adding gifs to your webcam image in real time ;)

![contours](https://mknblch.github.io/vpipe/overlay.png)


