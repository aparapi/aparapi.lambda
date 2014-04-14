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

package com.amd.aparapi.sample.features;

import com.amd.aparapi.Device;

import static com.amd.aparapi.HSA.getClock;
import static com.amd.aparapi.HSA.getComputeUnitId;
import static com.amd.aparapi.HSA.getCountUpLane;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getGridSize;
import static com.amd.aparapi.HSA.getLaneId;
import static com.amd.aparapi.HSA.getMaskLane;
import static com.amd.aparapi.HSA.getWorkGroupId;
import static com.amd.aparapi.HSA.getWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkItemId;

public class Internal{

   static class Dims{
      int gridSize = 0;
      int id = 0;
      int workItemId = 0;
      int workGroupSize = 0;
      int currentWorkGroupSize = 0;
      int workGroupId = 0;
      int laneId = 0;
      int countUpLane = 0;
      long clock = 0L;
      int computeUnitId = 0;
      int maskLane = 0;
   }

   public static void main(String[] _args){
      ;
      int len = 2048;
      Dims[] dims = new Dims[len];
      for (int i = 0; i<len; i++){
         dims[i] = new Dims();
      }

      Device.hsa().forEach(len, id -> {
         dims[id].id = id;
         dims[id].gridSize = getGridSize();
         dims[id].workGroupId = getWorkGroupId();

         dims[id].workGroupSize = getWorkGroupSize();
         dims[id].currentWorkGroupSize = getCurrentWorkGroupSize();
         dims[id].laneId = getLaneId();
         dims[id].workItemId = getWorkItemId();
         if (id%4 == 0){
            dims[id].countUpLane = getCountUpLane();
            dims[id].maskLane = getMaskLane();
         }
         dims[id].computeUnitId = getComputeUnitId();
         dims[id].clock = getClock();

      });
      for (int i = 0; i<len; i++){
         System.out.println(i
               +" id="+dims[i].id
               +" gridsize="+dims[i].gridSize
               +" workitemid="+dims[i].workItemId
               +" workGroupId="+dims[i].workGroupId
               +" laneId="+dims[i].laneId
               +" computeUnitId="+dims[i].computeUnitId
               +" workGroupSize="+dims[i].workGroupSize
               +" currentWorkGroupSize="+dims[i].currentWorkGroupSize
               +" countUpLane="+dims[i].countUpLane
               +" maskLane="+dims[i].maskLane
               +" clock="+dims[i].clock
         );
      }

   }

}
