<img src='http:wiki/aparapi_90x660.jpg' />

# Notice #
Whilst there are useful resources here, please note that the source for Aparapi is now managed over on GitHub (.  The repositories will be kept here for posterity, but please follow this link if you are looking for the latest code.

  * https://github.com/aparapi/aparapi

We will need to migrate the text/wiki pages from here in light of Google's latest announcement

  * http://google-opensource.blogspot.com/2015/03/farewell-to-google-code.html

# What is Aparapi? #
Aparapi allows Java developers to take advantage of the compute power of GPU and APU devices by executing data parallel code fragments on the GPU rather than being confined to the local CPU.  It does this by converting Java bytecode to OpenCL at runtime and executing on the GPU, if for any reason Aparapi can't execute on the GPU it will execute in a Java thread pool.

We like to think that for the appropriate workload this extends Java's 'Write Once Run Anywhere' to include GPU devices.

With Aparapi we can take a sequential loop such as this (which adds each element from inA[.md](.md) and inB[.md](.md) arrays and puts the result in result[.md](.md)).
```
final float inA[] = .... // get a float array of data from somewhere
final float inB[] = .... // get a float array of data from somewhere (inA.length==inB.length)
final float result = new float[inA.length];

for (int i=0; i<array.length; i++){
    result[i]=intA[i]+inB[i];
}
```

And refactor the sequential loop to the following form:

```
Kernel kernel = new Kernel(){
   @Override public void run(){
      int i= getGlobalId();
      result[i]=intA[i]+inB[i];
   }
};
Range range = Range.create(result.length); 
kernel.execute(range);
```

In the above code we extend `com.amd.aparapi.Kernel` base class and override the `Kernel.run()` method to express our data parallel algorithm.  We initiate the execution of the Kernel(over a specific `range 0..results.length`) using `Kernel.execute(range)`.

For folks following the new Java 8 lambda extensions.  Here is how we expect the above Aparapi code to look when Java 8 arrives.
```
Device.getBest().forEach(result.length, id -> result[id] = intA[id]+inB[id]);
```

Because we are also targeting the new "HSA Intermediate Language" https://hsafoundation.app.box.com/s/m6mrsjv8b7r50kqeyyal in the 'lambda' branch, Aparapi users will now be able to access `String`s (and other objects) from the Java heap.  So given an array of `String`s and `int`s we can extract the lengths in parallel.
```
Device.hsa().forEach(strings.length, id -> lengths[id] = strings[id].length());
```

Note that because the example ''only'' works with HSA we need to select a HSA device.

If you would like to download and try Aparapi follow the 'Downloads' link above and read the [UsersGuide](UsersGuide.md), alternatively if you would like to contribute or access the code you can check out the code from the SVN repository above and jump right in by reading the [DevelopersGuide](DevelopersGuide.md) pages.

## Aparapi in the news + upcoming presentations ##
  * ["GPU Acceleration of Interactive Large Scale Data Analytics Utilizing The Aparapi Framework" - Ryan LaMothe - AFDS](https://amdfusion.activeevents.com/scheduler/catalog/catalog.jsp)
  * ["Aparapi: OpenCL GPU and Multi-Core CPU Heterogeneous Computing for Java" - Ryan LaMothe and Gary Frost - AFDS](https://amdfusion.activeevents.com/scheduler/catalog/catalog.jsp)
  * ["Performance Evaluation of AMD-APARAPI Using Real World Applications" - Prakash Raghavendra - AFDS](https://amdfusion.activeevents.com/scheduler/catalog/catalog.jsp)
  * ["Aparapi: An Open Source tool for extending the Java promise of ‘Write Once Run Anywhere’ to include the GPU" - Gary Frost - OSCON 7/18/2012](http://www.oscon.com/oscon2012/public/schedule/detail/23434)
  * [Aparapi talk at DOSUG (Denver Open Source User Group) Sept 4th 2012](http://meetup.denveropensource.org/events/72220712)
  * [Meetup meeting in Paris Sept 25th 2012](http://www.meetup.com/HPC-GPU-Supercomputing-Group-of-Paris-Meetup/)


## Useful links ##
  * [Quick Reference Guide](http://aparapi.googlecode.com/svn/trunk/QuickReference.pdf)
  * [2010 InfoQ Interview](http://www.infoq.com/news/2010/09/aparapi-java-and-gpus)
  * [JavaOne 2010 Aparapi presentation](http://www.parleys.com/#st=5&id=2275)
  * [AMD Fusion Developer Summit presentation](http://developer.amd.com/afds/assets/presentations/2912_final.pdf)
  * [AMD Fusion Developer Summit video](http://developer.amd.com/afds/pages/video.aspx#/Dev_AFDS_Reb_2912)
  * [Open Source Announcement at developer.amd.com](http://blogs.amd.com/developer/2011/09/14/i-dont-always-write-gpu-code-in-java-but-when-i-do-i-like-to-use-aparapi/)
  * [Aparapi Mandlebrot demo on YouTube](http://www.youtube.com/watch?v=LlDT1FcCG5A)
  * [Witold Bolt's "The smell of fresh coffee: Aparapi - Java on the GPU!"](http://translate.google.com/translate?hl=en&sl=pl&u=http://www.trzeciakawa.pl/%3Fp%3D248)
  * [Some interesting feedback on Aparapi from this blogger 'a hackers craic'](http://a-hackers-craic.blogspot.com/2012/03/aparapi.html)
  * [A nice blog showing how to implement an option volatility surface using Aparapi](http://www.snowfallsystems.com/vol-surface-on-gpu)
  * [Here is Eduardo Dudu's Game of Life extensions including links to the code](https://forum.processing.org/topic/aparapi-opencl-directly-from-processing-java-grayscott-conways-examples-shared)  Really nice graphics!
  * [Here also is a Spanish Blog](http://edumo.net/wp/gray-scott-conways-game-of-life-aparapi-processing-org)
  * [Some really good performance analysis of algorithms implemented using Aparapi](http://aparapi-vortex.blogspot.com)
  * [Nice YouTube demo (Aparapi + JMonkeyEngine](http://www.youtube.com/watch?v=vX-tsp1f3Qs)
  * [Another YouTube demo using Aparapi](http://forum.processing.org/topic/videomapping-processing-2-aparapi-opencl-shader)
  * [A nice blog describing one developers take on Aparapi](http://www.beyondjava.net/blog/aparapi-run-java-applications-on-your-graphics-accelerator-card/#more-543)
  * [Aparapi running on Nexus 4 Phone](http://mahadevangorti.blogspot.in/2013/03/nexus-4-is-running-with-opencl-programs.html)
  * [An attempt to use Aparapi for Neural Network applications](http://aparacog.wikia.com/wiki/Aparacog_Wiki)
  * [A nice writeup of Aparapi from Tomas Zrybak](http://tomaszrybak.wordpress.com/2013/12/11/aparapi)

## Similar Work ##
  * [Peter Calvert's java-GPU has similar goals and offers a mechanism for converting Java code for use on the GPU](http://code.google.com/p/java-gpu/)
    * Check out Peter's dissertation ["Parallelisation of Java for Graphics Processors" which can be found here](http://www.cl.cam.ac.uk/~prc33/)
  * [Marco Hutter's Java bindings for CUDA](http://www.jcuda.org/)
  * [Marco Hutter's Java bindings for OpenCL](http://www.jocl.org/)
  * [Ian Wetherbee's Java acceleration project - creates accelerated code from Java (currently C code and native Android - but CUDA creation planned)](https://bitbucket.org/wetherbeei/acceljava)
  * ["Rootbeer: Seamlessly using GPUs from Java" by Philip C. Pratt-Szeliga](https://github.com/pcpratts/rootbeer1#readme)


## Wiki Pages ##
  * User’s Guide [UsersGuide](UsersGuide.md)
  * Developer’s Guide [DevelopersGuide](DevelopersGuide.md), for developers who want to build Aparapi or contribute to the project
  * Frequently Asked Questions [FrequentlyAskedQuestions](FrequentlyAskedQuestions.md)

## About the name ##

Aparapi is just a contraction of "A PARallel API"

However... "Apa rapi" in Indonesian (the language spoken on the island of Java) translates to "What a neat...".  So "Apa rapi Java Project" translates to "What a neat Java Project" [How cool is that?](http://translate.google.com/?tl=id&q=undefined#id/en/Apa%20rapi%20java%20project)
