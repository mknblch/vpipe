# Image processing & contour extraction

This is my [AVGL](https://www.avantgarde-labs.de) Hackathon 2018 Project - an image processing pipeline using J8 functional patterns.
It contains some basic processing units for image transformation. Just enough to enhance the image to a
point where contours can be extracted flawlessly. Though it is not limited to image data in any way. 

## Usage

A processing unit is nothing more then a `java.util.function.Function<I, O>` which itself can be composed
to a full processing pipeline using nothing more then its built-in methods `compose(..)` and `andThen(..)`.
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

- Linear Convolution with all sorts of kernels
- signal split into parallel processing chains (by [jkraml](https://github.com/jkraml)) 
- Delation / Erosion
- pixel based enhancement like grayscale, gamma & contrast
- threshold based contour extraction

## Example
 
- **Green** : outer contour
- **Yellow** : inner contours
- **Red** : Leafs

> ![contours](https://mknblch.github.io/videopipe/fiducial.png)

---

