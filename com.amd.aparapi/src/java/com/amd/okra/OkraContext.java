package com.amd.okra;

public class OkraContext {
/***
	enum OkraStatus {
		OKRA_OK,
		OKRA_OTHER_ERROR
	};
***/
	static {
		loadOkraNativeLibrary();
	} // end static
	
	static void loadOkraNativeLibrary() {
		String arch = System.getProperty("os.arch");
		String okraLibraryName = null;
		String okraLibraryRoot = "okra";

		if (arch.equals("amd64") || arch.equals("x86_64")) {
			okraLibraryName = okraLibraryRoot + "_x86_64";
		} else if (arch.equals("x86") || arch.equals("i386")) {
			okraLibraryName = okraLibraryRoot + "_x86";
		} else {
			System.out.println("Expected property os.arch to contain amd64, x86_64, x86 or i386 but instead found " + arch
							   + " as a result we don't know which okra to attempt to load.");
		}
		if (okraLibraryName != null) {
			try {
				Runtime.getRuntime().loadLibrary(okraLibraryName);
			} catch (UnsatisfiedLinkError e) {
				System.out
					.println("Check your environment. Failed to load okra native library "
							 + okraLibraryName);
			}
		}
	}

	public boolean isValid() {
		return (contextHandle != 0);
	}
		
	private long contextHandle;
	private int[] dummyArray;   // used for pinning if no other arrays passed

	public OkraContext() {
		dummyArray = new int[1];
		// call the JNI routine and store it locally
		contextHandle  = createOkraContextJNI(dummyArray);
	}

	long getContextHandle() {
		return contextHandle;
	}

	// create a c++ okraContext object
	private static native long createOkraContextJNI(int[] ary);

	// create a c++ kernel object from the specified source and entrypoint
	native long createKernelJNI(String source, String entryName);

	// dispose of an environment including all programs
	public native int dispose();

	// I hope this one will go away
	public native int registerObjectMemory(Object obj, int len);

	public native int registerHeapMemory(Object obj);

    public native void setVerbose(boolean b);

	static native long createRefHandle(Object obj);
	// for testing only
	public static native void useRefHandle(long handle);

	public static int version = 1;
}