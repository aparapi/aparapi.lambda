# Profiling Kernels with AMD profiler in Eclipse (Indigo) #

Wayne Johnson

12 May 2012

## Disclaimer: This has been tested with Eclipse (Indigo SR1) only on W7SR1. ##

Assume your Eclipse project follows a typical Maven layout:
<pre>
Project<br>
src/main/java/...<br>
AlgorithmImplementation.java<br>
src/test/java/...<br>
BenchmarkRunner.java<br>
BenchmarkTest.java<br>
lib/aparapi-2012-02-15/<br>
aparapi jar file<br>
native libraries for W7, Linux, and OSX<br>
…<br>
profiles/<br>
[this is where the profiles and logs will be generated]<br>
</pre>

<ol>
<li> Download and install the current  <a href='http://developer.amd.com/sdks/AMDAPPSDK/downloads/Pages/default.aspx'>AMD APP SDK</a></li>
<li>Download and install Aparapi (see Wiki), making sure that the native libraries are on your build path.</li>
<li>Create your algorithm implementation(s).<br>
<blockquote>example: AlgorithmImplementations.java</li>
<li>Create your performance benchmark test as a JUnit test case to exercise your implementations.<br>
example: BenchmarkTest.java</li>
<li>Test your JUnit test case inside Eclipse using BenchmarkRunner to make sure it works. The runner will be the main application for the runnable jar file you create in the next step.<br>
This step will also automatically create the launch configuration that the export command will ask you for.<br>
Select BenchmarkRunner.java<br>
<pre>Right-click > Run as > Java application</pre>
</li>
<li>Export your project as a runnable jar file.<br>
<pre>
Right-click > Export...<br>
[wizard] Java > Runnable Jar File. Next.<br>
Launch configuration: BenchmarkRunner [1] - Project<br>
Export destination: Project\runner.jar<br>
Library handling: [use default]<br>
Finish.<br>
Ok on “...repacks referenced libraries”<br>
Yes on “Confirm replace” [You won’t see this dialog on the first export but will on subsequent exports]<br>
Ok [ignore warning dialog]<br>
</pre>
After refreshing Project, you should see a runner.jar file at the top level.</li>
<li>Create an external tool configuration to generate the performance counter profile<br>
<pre>
Run > External Tools > External Tool Configurations...<br>
Name: AMD counters - Project<br>
Location: C:\Program Files (x86)\AMD APP\tools\AMD APP Profiler 2.4\x64\sprofile.exe<br>
Arguments:<br>
-o "${project_loc}\profiles\counters.csv"<br>
-w "${project_loc}"<br>
"C:\Program Files\Java\jdk1.6.0_30\bin\java.exe"<br>
-Djava.library.path="lib\aparapi-2012-02-15"<br>
-jar "${project_loc}\runner.jar"<br>
</pre>
Note: The ''java.library.path'' indicates the relative location of the folder containing the native libraries used by Aparapi. If this is not set correctly, steps 9 and 10 below will run in JTP execution<br>
mode and the only error message you will see on the Eclipse console is that the profile was not generated. This is because nothing executed on the GPU.</li></blockquote>

<li>Create an external tool configuration to generate the cltrace and summary profiles.<br>
<pre>
Run > External Tools > External Tool Configurations...<br>
Name: AMD cltrace - Project<br>
Location: C:\Program Files (x86)\AMD APP\tools\AMD APP Profiler 2.4\x64\sprofile.exe<br>
Arguments:<br>
-o "${project_loc}\profiles\cltrace.txt" -k all -r -O -t -T<br>
-w "${project_loc}"<br>
"C:\Program Files\Java\jdk1.6.0_30\bin\java.exe"<br>
-Djava.library.path="lib\aparapi-2012-02-15"<br>
-jar "${project_loc}\runner.jar"<br>
</pre>
</li>
<li>Run the AMD profiler counter configuration to generate the counter profile.<br>
<pre>
Run > External Tools > AMD counters - Project<br>
</pre>
</li>
<li>Run the AMD profiler cltrace configuration to generate the cltrace and summary profiles.<br>
<pre>
Run > External Tools > AMD cltrace - Project<br>
</pre>
</li>
<ol>

A project file for testing the above instructions can be found <a href='http://code.google.com/p/aparapi/source/browse/trunk/wiki-collateral/ProfilingKernelsFormEclipseProject.zip'>http://code.google.com/p/aparapi/source/browse/trunk/wiki-collateral/ProfilingKernelsFormEclipseProject.zip</a>