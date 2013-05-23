package com.amd.okra;

import java.io.*;
import java.util.Arrays;

public class OkraOpenCLtoHSAILCompiler {

	public static native String compileToHSAILJNI(String openCLSource);

	public static String compileToHSAIL(String openCLSource, String baseName, String batchScript) {
		//return compileToHSAILJNI(openCLSource);
		try {
			final String oclFileName = baseName + ".ocl";
			final String binFileName = baseName + ".bin";
			final String hsailFileName = baseName + ".hsail";

			// Set up source input file for hsail tools
			FileOutputStream fos = new FileOutputStream(oclFileName);
			fos.write(openCLSource.getBytes());
			fos.flush();
			fos.close();

			executeCmd("aoc2", "-m64", "-I./", "-march=hsail", oclFileName);
			executeCmd("HSAILasm", "-disassemble", "-o", hsailFileName, binFileName);

			// Now the .hsail file should exist
			FileInputStream fis = new FileInputStream(hsailFileName);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));

			StringBuffer hsailSourceBuffer = new StringBuffer();
			String line;
			String cr = System.getProperty("line.separator");
			do {
				line = d.readLine();
				if (line != null) {
					hsailSourceBuffer.append(line);
					hsailSourceBuffer.append(cr);
				}
			} while (line != null);
			String result = hsailSourceBuffer.toString();
			
			return result;

		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		}
	}

	private static  void executeCmd(String... cmd) {
		System.out.println("spawning" + Arrays.toString(cmd));
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			Process p = pb.start();
			if (true) {
				InputStream in = p.getInputStream();
				BufferedInputStream buf = new BufferedInputStream(in);
				InputStreamReader inread = new InputStreamReader(buf);
				BufferedReader bufferedreader = new BufferedReader(inread);
				String line;
				while ((line = bufferedreader.readLine()) != null) {
					System.err.println(line);
				}				
			}
			p.waitFor();
		} 
		catch(Exception e) {
			System.err.println("could not execute <" + Arrays.toString(cmd) + ">");
		}
	}

}