Go to http://www.plantuml.com/plantuml and type in the text for you diagram.

Hit submit and check out the diagram.

Once you are happy, so with something like
```
start
:kernel.execute(range);
if (?) then (first call for this instance)
    : Convert Kernel.run() to OpenCL;
    note
       We also convert all 
       methods reachable from
       kernel.run()
    end note
    if (?) then (Conversion was successful)
       : Compile OpenCL;
       : Map compiled OpenCL to this Kernel;
    else (Conversion unsuccessful)
    endif
else (not first call)
endif
if (?) then (OpenCL mapped for this instance)
   : Bind args (send to GPU);
   : Execute kernel;
else (false)
   : Execute using a Java Thread Pool;
endif
stop
```

Paste the resulting URL into the wiki page but append %20as.png at the end of the URL

`http://www.plantuml.com:80/plantuml/img/BLAHBLAH%20as.png`

To get this!

![http://www.plantuml.com:80/plantuml/img/TP5DJiGm38NtbNe7BziD1sWNI8mG4ZzY5Y3M9dNQQDAaYjE1u-CscJ8L8giep_RxyimHGooBvaJ1aRsXRr9pf2gWwwbkoy9eg6vhY0CvgBG9746XjQ1za4V3O1n7T8hgiW0v3HoyErE8y9GcXjbLqk_XTI9tU6vJcVEHqatE1m5Qzg1ovp9_4qUAW-yO0g4QyDCIwE37JJvTkQH7SjtL-1r_GcFZ7NmX0yzA4REURRtDM_Z7oOZDZdTLNd0InbNjihnyR8qX_JPNasNQkStkZvTW6bqMgLHuuJTSgSZgZZuxZZXDul_F0XguCn80XfwXuYKB8NmuljTjbk_JXdDmxOL0omQs4PUQcOth0U4HpmXlWwoWWwFtQrS2vEYd7m00%20&foo=as.png](http://www.plantuml.com:80/plantuml/img/TP5DJiGm38NtbNe7BziD1sWNI8mG4ZzY5Y3M9dNQQDAaYjE1u-CscJ8L8giep_RxyimHGooBvaJ1aRsXRr9pf2gWwwbkoy9eg6vhY0CvgBG9746XjQ1za4V3O1n7T8hgiW0v3HoyErE8y9GcXjbLqk_XTI9tU6vJcVEHqatE1m5Qzg1ovp9_4qUAW-yO0g4QyDCIwE37JJvTkQH7SjtL-1r_GcFZ7NmX0yzA4REURRtDM_Z7oOZDZdTLNd0InbNjihnyR8qX_JPNasNQkStkZvTW6bqMgLHuuJTSgSZgZZuxZZXDul_F0XguCn80XfwXuYKB8NmuljTjbk_JXdDmxOL0omQs4PUQcOth0U4HpmXlWwoWWwFtQrS2vEYd7m00%20&foo=as.png)