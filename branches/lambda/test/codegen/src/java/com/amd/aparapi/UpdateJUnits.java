/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
   774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
   you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
   Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
   Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

 */
package com.amd.aparapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;


import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class UpdateJUnits{
   public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException {
      File rootDir = new File(System.getProperty("root", "."));

      final String rootPackageName = CreateJUnitTests.class.getPackage().getName();
      final String testPackageName = rootPackageName + ".test";
      final File sourceDir = new File(rootDir, "src/java");
      System.out.println(sourceDir.getCanonicalPath());
      File testDir = new File(sourceDir, testPackageName.replace(".", "/"));
      System.out.println(testDir.getCanonicalPath());

      final List<String> classNames = new ArrayList<String>();
      for (File sourceFile : testDir.listFiles(new FilenameFilter(){

               @Override public boolean accept(File dir, String name) {
               return (name.endsWith(".java"));
               }
               })) {
         String fileName = sourceFile.getName();
         String className = fileName.substring(0, fileName.length() - ".java".length());
         classNames.add(className);
      }

      //File outDir = new File(rootDir, "src/tmp");
      //codeGenDir.mkdirs();
      JFrame frame = new JFrame("");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //frame.setSize(800,400);
      JPanel panel = new JPanel();
      //panel.setPreferredSize(new Dimension(400,400));
      final StyledDocument javaDocument = new DefaultStyledDocument();
      final StyledDocument initialOpenCLDocument = new DefaultStyledDocument();
      final StyledDocument finalOpenCLDocument = new DefaultStyledDocument();

      final SimpleAttributeSet boldItalic = new SimpleAttributeSet();
      boldItalic.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
      boldItalic.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);

      JTextPane javaTextPane = new JTextPane(javaDocument);
      javaTextPane.setEditable(false);
      JScrollPane javaScrollPane = new JScrollPane(javaTextPane);
      javaScrollPane.setPreferredSize(new Dimension(400,400));
      panel.add(javaScrollPane);

      JTextPane initialOpenCLTextPane = new JTextPane(initialOpenCLDocument);
      initialOpenCLTextPane.setEditable(false);
      JScrollPane initialOpenCLScrollPane = new JScrollPane(initialOpenCLTextPane);
      initialOpenCLScrollPane.setPreferredSize(new Dimension(400,400));
      panel.add(initialOpenCLScrollPane);

      JTextPane finalOpenCLTextPane = new JTextPane(finalOpenCLDocument);
      finalOpenCLTextPane.setEditable(false);
      JScrollPane finalOpenCLScrollPane = new JScrollPane(finalOpenCLTextPane);
      finalOpenCLScrollPane.setPreferredSize(new Dimension(400,400));
      panel.add(finalOpenCLScrollPane);





      frame.add(panel, BorderLayout.CENTER);



      JMenuBar menu = new JMenuBar();
      JMenu fileMenu = new JMenu();
      fileMenu.setText("File");

      final int[] index = new int[]{0};

      Action nextAction = new AbstractAction() {
         public void actionPerformed(ActionEvent e) {
            try {
            javaDocument.remove(0, javaDocument.getLength());
            index[0]++;
            String className = classNames.get(index[0]);
            Source source = new Source(Class.forName(testPackageName + "." + className), sourceDir);
            StringBuilder sb = new StringBuilder();
            if (source.getOpenCLSectionCount() > 0) {
               for (List<String> opencl : source.getOpenCL()) {
                  sb.append("OpenCL{\n");
                  for (String line : opencl) {
                     sb.append("   +\"" + line + "\\n\"\n");
                  }
                  sb.append("}\n");
               }
            } else {
               sb.append(" NO OpenCL!\n");
            }
               javaDocument.insertString(0, sb.toString(), boldItalic);
            } catch (Throwable t) {
               System.err.println("Bad next "+t);
               System.exit(1);

            }

         }
      };
      nextAction.putValue(Action.NAME, "Next");
      fileMenu.add(nextAction);
      menu.add(fileMenu);
      frame.add(menu, BorderLayout.NORTH);
      frame.setVisible(true);
      frame.pack();


      /*

         for (String className : classNames) {
         Source source = new Source(Class.forName(testPackageName + "." + className), sourceDir);
         StringBuilder sb = new StringBuilder();
         if (source.getOpenCLSectionCount() > 0) {
         for (List<String> opencl : source.getOpenCL()) {
         sb.append("OpenCL{\n");
         for (String line : opencl) {
         sb.append("   +\"" + line + "\\n\"\n");
         }
         sb.append("}\n");
         }
         } else {
         sb.append(" NO OpenCL!\n");
         }

         String exceptions = source.getExceptionsString();
         if (exceptions.length() > 0) {
         sb.append("Throws  com.amd.aparapi." + exceptions+"\n" );
         }
         System.out.println(sb);
         }
       */

   }
}
