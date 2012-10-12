package com.amd.aparapi.jni;

import com.amd.aparapi.jni.OpenCLJNI.UsedByJNICode;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public class ConfigJNI {

  private static String propPkgName = "com.amd.aparapi";

  /**
   * Allows the user to request a specific Kernel.EXECUTION_MODE enum value for all Kernels.
   * 
   * Usage -Dcom.amd.aparapi.executionMode={SEQ|JTP|CPU|GPU}
   * 
   * @see com.amd.aparapi.Kernel.EXECUTION_MODE
   */
  private String executionMode = System.getProperty(propPkgName + ".executionMode");

  /**
   * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer.
   * 
   * Usage -Dcom.amd.aparapi.enableProfiling={true|false}
   * 
   */
  @UsedByJNICode
  private boolean enableProfiling = Boolean.getBoolean(propPkgName + ".enableProfiling");

  /**
   * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer, this information will be written to CSV file
   * 
   * Usage -Dcom.amd.aparapi.enableProfiling={true|false}
   * 
   */
  @UsedByJNICode
  private boolean enableProfilingCSV = Boolean.getBoolean(propPkgName + ".enableProfilingCSV");

  /**
   * Allows the user to request that verbose JNI messages be dumped to stderr.
   * 
   * Usage -Dcom.amd.aparapi.enableVerboseJNI={true|false}
   * 
   */
  @UsedByJNICode
  private boolean enableVerboseJNI = Boolean.getBoolean(propPkgName + ".enableVerboseJNI");

  /**
   * Allows the user to request tracking of opencl resources.
   * 
   * This is really a debugging option to help locate leaking OpenCL resources, this will be dumped to stderr.
   * 
   * Usage -Dcom.amd.aparapi.enableOpenCLResourceTracking={true|false}
   * 
   */
  @UsedByJNICode
  private boolean enableVerboseJNIOpenCLResourceTracking = Boolean.getBoolean(propPkgName + ".enableVerboseJNIOpenCLResourceTracking");

  /**
   * Allows the user to request that the execution mode of each kernel invocation be reported to stdout.
   * 
   * Usage -Dcom.amd.aparapi.enableExecutionModeReporting={true|false}
   * 
   */
  private boolean enableExecutionModeReporting = Boolean.getBoolean(propPkgName + ".enableExecutionModeReporting");

  /**
   * Allows the user to request that generated OpenCL code is dumped to standard out.
   * 
   * Usage -Dcom.amd.aparapi.enableShowGeneratedOpenCL={true|false}
   * 
   */
  private boolean enableShowGeneratedOpenCL = Boolean.getBoolean(propPkgName + ".enableShowGeneratedOpenCL");

  // Pragma/OpenCL codegen related flags
  private boolean enableAtomic32 = Boolean.getBoolean(propPkgName + ".enableAtomic32");

  private boolean enableAtomic64 = Boolean.getBoolean(propPkgName + ".enableAtomic64");

  private boolean enableByteWrites = Boolean.getBoolean(propPkgName + ".enableByteWrites");

  private boolean enableDoubles = Boolean.getBoolean(propPkgName + ".enableDoubles");

  // Debugging related flags
  private boolean verboseComparitor = Boolean.getBoolean(propPkgName + ".verboseComparitor");

  private boolean dumpFlags = Boolean.getBoolean(propPkgName + ".dumpFlags");

  // Individual bytecode support related flags
  private boolean enablePUTFIELD = Boolean.getBoolean(propPkgName + ".enable.PUTFIELD");

  private boolean enableARETURN = !Boolean.getBoolean(propPkgName + ".disable.ARETURN");

  private boolean enablePUTSTATIC = Boolean.getBoolean(propPkgName + ".enable.PUTSTATIC");

  private boolean enableGETSTATIC = Boolean.getBoolean(propPkgName + ".enable.GETSTATIC");

  private boolean enableINVOKEINTERFACE = Boolean.getBoolean(propPkgName + ".enable.INVOKEINTERFACE");

  private boolean enableMONITOR = Boolean.getBoolean(propPkgName + ".enable.MONITOR");

  private boolean enableNEW = Boolean.getBoolean(propPkgName + ".enable.NEW");

  private boolean enableATHROW = Boolean.getBoolean(propPkgName + ".enable.ATHROW");

  private boolean enableMETHODARRAYPASSING = !Boolean.getBoolean(propPkgName + ".disable.METHODARRAYPASSING");

  private boolean enableARRAYLENGTH = Boolean.getBoolean(propPkgName + ".enable.ARRAYLENGTH");

  private boolean enableSWITCH = Boolean.getBoolean(propPkgName + ".enable.SWITCH");

  // private final int JTPLocalSizeMultiplier = Integer.getInteger(propPkgName + ".JTP.localSizeMul", 2);

  private boolean enableInstructionDecodeViewer = Boolean.getBoolean(propPkgName + ".enableInstructionDecodeViewer");

  private String instructionListenerClassName = System.getProperty(propPkgName + ".instructionListenerClass");

  /**
   * Default constructor<br>
   * This constructor uses the default propPkgName of 'com.amd.aparapi'
   */
  public ConfigJNI() {

  }

  /**
   * Minimal constructor
   * 
   * @param propPkgName
   *          The name of the package for the configuration properties
   */
  public ConfigJNI(String propPkgName) {
    ConfigJNI.propPkgName = propPkgName;
  }

  /**
   * @return the executionMode
   */
  public String getExecutionMode() {
    return executionMode;
  }

  /**
   * @param executionMode
   *          the executionMode to set
   */
  public void setExecutionMode(String executionMode) {
    this.executionMode = executionMode;
  }

  /**
   * @return the enableProfiling
   */
  public boolean isEnableProfiling() {
    return enableProfiling;
  }

  /**
   * @param enableProfiling
   *          the enableProfiling to set
   */
  public void setEnableProfiling(boolean enableProfiling) {
    this.enableProfiling = enableProfiling;
  }

  /**
   * @return the enableProfilingCSV
   */
  public boolean isEnableProfilingCSV() {
    return enableProfilingCSV;
  }

  /**
   * @param enableProfilingCSV
   *          the enableProfilingCSV to set
   */
  public void setEnableProfilingCSV(boolean enableProfilingCSV) {
    this.enableProfilingCSV = enableProfilingCSV;
  }

  /**
   * @return the enableVerboseJNI
   */
  public boolean isEnableVerboseJNI() {
    return enableVerboseJNI;
  }

  /**
   * @param enableVerboseJNI
   *          the enableVerboseJNI to set
   */
  public void setEnableVerboseJNI(boolean enableVerboseJNI) {
    this.enableVerboseJNI = enableVerboseJNI;
  }

  /**
   * @return the enableVerboseJNIOpenCLResourceTracking
   */
  public boolean isEnableVerboseJNIOpenCLResourceTracking() {
    return enableVerboseJNIOpenCLResourceTracking;
  }

  /**
   * @param enableVerboseJNIOpenCLResourceTracking
   *          the enableVerboseJNIOpenCLResourceTracking to set
   */
  public void setEnableVerboseJNIOpenCLResourceTracking(boolean enableVerboseJNIOpenCLResourceTracking) {
    this.enableVerboseJNIOpenCLResourceTracking = enableVerboseJNIOpenCLResourceTracking;
  }

  /**
   * @return the enableExecutionModeReporting
   */
  public boolean isEnableExecutionModeReporting() {
    return enableExecutionModeReporting;
  }

  /**
   * @param enableExecutionModeReporting
   *          the enableExecutionModeReporting to set
   */
  public void setEnableExecutionModeReporting(boolean enableExecutionModeReporting) {
    this.enableExecutionModeReporting = enableExecutionModeReporting;
  }

  /**
   * @return the enableShowGeneratedOpenCL
   */
  public boolean isEnableShowGeneratedOpenCL() {
    return enableShowGeneratedOpenCL;
  }

  /**
   * @param enableShowGeneratedOpenCL
   *          the enableShowGeneratedOpenCL to set
   */
  public void setEnableShowGeneratedOpenCL(boolean enableShowGeneratedOpenCL) {
    this.enableShowGeneratedOpenCL = enableShowGeneratedOpenCL;
  }

  /**
   * @return the enableAtomic32
   */
  public boolean isEnableAtomic32() {
    return enableAtomic32;
  }

  /**
   * @param enableAtomic32
   *          the enableAtomic32 to set
   */
  public void setEnableAtomic32(boolean enableAtomic32) {
    this.enableAtomic32 = enableAtomic32;
  }

  /**
   * @return the enableAtomic64
   */
  public boolean isEnableAtomic64() {
    return enableAtomic64;
  }

  /**
   * @param enableAtomic64
   *          the enableAtomic64 to set
   */
  public void setEnableAtomic64(boolean enableAtomic64) {
    this.enableAtomic64 = enableAtomic64;
  }

  /**
   * @return the enableByteWrites
   */
  public boolean isEnableByteWrites() {
    return enableByteWrites;
  }

  /**
   * @param enableByteWrites
   *          the enableByteWrites to set
   */
  public void setEnableByteWrites(boolean enableByteWrites) {
    this.enableByteWrites = enableByteWrites;
  }

  /**
   * @return the enableDoubles
   */
  public boolean isEnableDoubles() {
    return enableDoubles;
  }

  /**
   * @param enableDoubles
   *          the enableDoubles to set
   */
  public void setEnableDoubles(boolean enableDoubles) {
    this.enableDoubles = enableDoubles;
  }

  /**
   * @return the verboseComparitor
   */
  public boolean isVerboseComparitor() {
    return verboseComparitor;
  }

  /**
   * @param verboseComparitor
   *          the verboseComparitor to set
   */
  public void setVerboseComparitor(boolean verboseComparitor) {
    this.verboseComparitor = verboseComparitor;
  }

  /**
   * @return the dumpFlags
   */
  public boolean isDumpFlags() {
    return dumpFlags;
  }

  /**
   * @param dumpFlags
   *          the dumpFlags to set
   */
  public void setDumpFlags(boolean dumpFlags) {
    this.dumpFlags = dumpFlags;
  }

  /**
   * @return the enablePUTFIELD
   */
  public boolean isEnablePUTFIELD() {
    return enablePUTFIELD;
  }

  /**
   * @param enablePUTFIELD
   *          the enablePUTFIELD to set
   */
  public void setEnablePUTFIELD(boolean enablePUTFIELD) {
    this.enablePUTFIELD = enablePUTFIELD;
  }

  /**
   * @return the enableARETURN
   */
  public boolean isEnableARETURN() {
    return enableARETURN;
  }

  /**
   * @param enableARETURN
   *          the enableARETURN to set
   */
  public void setEnableARETURN(boolean enableARETURN) {
    this.enableARETURN = enableARETURN;
  }

  /**
   * @return the enablePUTSTATIC
   */
  public boolean isEnablePUTSTATIC() {
    return enablePUTSTATIC;
  }

  /**
   * @param enablePUTSTATIC
   *          the enablePUTSTATIC to set
   */
  public void setEnablePUTSTATIC(boolean enablePUTSTATIC) {
    this.enablePUTSTATIC = enablePUTSTATIC;
  }

  /**
   * @return the enableGETSTATIC
   */
  public boolean isEnableGETSTATIC() {
    return enableGETSTATIC;
  }

  /**
   * @param enableGETSTATIC
   *          the enableGETSTATIC to set
   */
  public void setEnableGETSTATIC(boolean enableGETSTATIC) {
    this.enableGETSTATIC = enableGETSTATIC;
  }

  /**
   * @return the enableINVOKEINTERFACE
   */
  public boolean isEnableINVOKEINTERFACE() {
    return enableINVOKEINTERFACE;
  }

  /**
   * @param enableINVOKEINTERFACE
   *          the enableINVOKEINTERFACE to set
   */
  public void setEnableINVOKEINTERFACE(boolean enableINVOKEINTERFACE) {
    this.enableINVOKEINTERFACE = enableINVOKEINTERFACE;
  }

  /**
   * @return the enableMONITOR
   */
  public boolean isEnableMONITOR() {
    return enableMONITOR;
  }

  /**
   * @param enableMONITOR
   *          the enableMONITOR to set
   */
  public void setEnableMONITOR(boolean enableMONITOR) {
    this.enableMONITOR = enableMONITOR;
  }

  /**
   * @return the enableNEW
   */
  public boolean isEnableNEW() {
    return enableNEW;
  }

  /**
   * @param enableNEW
   *          the enableNEW to set
   */
  public void setEnableNEW(boolean enableNEW) {
    this.enableNEW = enableNEW;
  }

  /**
   * @return the enableATHROW
   */
  public boolean isEnableATHROW() {
    return enableATHROW;
  }

  /**
   * @param enableATHROW
   *          the enableATHROW to set
   */
  public void setEnableATHROW(boolean enableATHROW) {
    this.enableATHROW = enableATHROW;
  }

  /**
   * @return the enableMETHODARRAYPASSING
   */
  public boolean isEnableMETHODARRAYPASSING() {
    return enableMETHODARRAYPASSING;
  }

  /**
   * @param enableMETHODARRAYPASSING
   *          the enableMETHODARRAYPASSING to set
   */
  public void setEnableMETHODARRAYPASSING(boolean enableMETHODARRAYPASSING) {
    this.enableMETHODARRAYPASSING = enableMETHODARRAYPASSING;
  }

  /**
   * @return the enableARRAYLENGTH
   */
  public boolean isEnableARRAYLENGTH() {
    return enableARRAYLENGTH;
  }

  /**
   * @param enableARRAYLENGTH
   *          the enableARRAYLENGTH to set
   */
  public void setEnableARRAYLENGTH(boolean enableARRAYLENGTH) {
    this.enableARRAYLENGTH = enableARRAYLENGTH;
  }

  /**
   * @return the enableSWITCH
   */
  public boolean isEnableSWITCH() {
    return enableSWITCH;
  }

  /**
   * @param enableSWITCH
   *          the enableSWITCH to set
   */
  public void setEnableSWITCH(boolean enableSWITCH) {
    this.enableSWITCH = enableSWITCH;
  }

  /**
   * @return the enableInstructionDecodeViewer
   */
  public boolean isEnableInstructionDecodeViewer() {
    return enableInstructionDecodeViewer;
  }

  /**
   * @param enableInstructionDecodeViewer
   *          the enableInstructionDecodeViewer to set
   */
  public void setEnableInstructionDecodeViewer(boolean enableInstructionDecodeViewer) {
    this.enableInstructionDecodeViewer = enableInstructionDecodeViewer;
  }

  /**
   * @return the instructionListenerClassName
   */
  public String getInstructionListenerClassName() {
    return instructionListenerClassName;
  }

  /**
   * @param instructionListenerClassName
   *          the instructionListenerClassName to set
   */
  public void setInstructionListenerClassName(String instructionListenerClassName) {
    this.instructionListenerClassName = instructionListenerClassName;
  }
}
