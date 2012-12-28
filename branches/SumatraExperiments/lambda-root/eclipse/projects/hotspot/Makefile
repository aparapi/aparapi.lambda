all:
	LANG=C ALT_BOOTDIR=/usr/lib/jvm/java-7-openjdk-amd64 ALLOW_DOWNLOADS=true CORES=6 HOTSPOT_BUILD_JOBS=6 PARALLEL_COMPILE_JOBS=6 make -C ../../../lambda DEBUG_NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true hotspot
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjvm.so
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjvm.diz
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjvm.debuginfo
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjvm_g.debuginfo
	cp ../../../lambda/build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/fastdebug/libjvm.so ../../../lambda/build/linux-amd64/lib/amd64/server
	unzip -d ../../../lambda/build/linux-amd64/lib/amd64/server ../../../lambda/build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/fastdebug/libjvm.diz 
	ls -l ../../../lambda/build/linux-amd64/lib/amd64/server/libjvm.debuginfo
	ls -l ../../../lambda/build/linux-amd64/lib/amd64/server/libjvm_g.debuginfo
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjsig.so
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjsig.diz
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjsig.debuginfo
	rm -f ../../../lambda/build/linux-amd64/lib/amd64/server/libjsig_g.debuginfo
	cp ../../../lambda/build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/fastdebug/libjsig.so ../../../lambda/build/linux-amd64/lib/amd64/server
	unzip -d ../../../lambda/build/linux-amd64/lib/amd64/server ../../../lambda/build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/fastdebug/libjsig.diz 
	ls -l ../../../lambda/build/linux-amd64/lib/amd64/server/libjsig.debuginfo
	ls -l ../../../lambda/build/linux-amd64/lib/amd64/server/libjsig_g.debuginfo

clean:
	LANG=C ALT_BOOTDIR=/usr/lib/jvm/java-7-openjdk-amd64 ALLOW_DOWNLOADS=true CORES=4 HOTSPOT_BUILD_JOBS=4 PARALLEL_COMPILE_JOBS=4 echo make -C ../../../lambda _NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true clean
