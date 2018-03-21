# Image processing & contour extraction

This is my [AVGL](https://www.avantgarde-labs.de) Hackathon 2018 Project - an image processing pipeline using J8 functional patterns.
It contains some basic processing units for image transformation. Just enough to enhance the image to a
point where contours can be extracted flawlessly. Though it is not limited to image data in any way. 

## Usage

A processing unit is nothing more then a `java.util.function.Function<I, O>` which itself can be composed
to a full processing pipeline using its built-in methods `compose(..)` and `andThen(..)`.
Additionally a `Source<T>` interface exists which is just a `Supplier<T>` with some helper methods to chain 
it with these functions. 

```
    import static de.mknblch.vpipe.Functions.*;

    final Source<BufferedImage> pipe = SarxosWebcamSource.choose()
            .connectTo(grayscale())
            .connectTo(gamma(20))
            .connectTo(contrast(2))
            .connectTo(contours(128))
            .connectTo(renderAll(640, 480))
            .connectTo(toBufferedImage());

    Viewer.start(pipe);
```

**(incomplete) list of built-in functions**:

- Convolution
- signal split into parallel processing chains (by [jkraml](https://github.com/jkraml)) 
- Delation / Erosion
- pixel based enhancement like grayscale, gamma & contrast
- threshold based contour extraction
- some visualizers

## Usage

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
## Examples

contours with different properties

![contours](https://mknblch.github.io/vpipe/fiducial2.png)

colorization by depth in the contour tree

![contours](https://mknblch.github.io/vpipe/fiducial.png)

drawing bounding boxes instead of contours gives you this

![contours](https://mknblch.github.io/vpipe/fiducial4.png)

using a more complex pipe which splits the image into
its 3 colors and computes the contours on each of them separately
leads to this view

![contours](https://mknblch.github.io/vpipe/acid.png)