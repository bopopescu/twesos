/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jute.compiler;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * C++ Code generator front-end for Hadoop record I/O.
 */
class CppGenerator {
    private String mName;
    private ArrayList<JFile> mInclFiles;
    private ArrayList<JRecord> mRecList;
    private final File outputDirectory;
    
    /** Creates a new instance of CppGenerator
     *
     * @param name possibly full pathname to the file
     * @param ilist included files (as JFile)
     * @param rlist List of records defined within this file
     * @param outputDirectory 
     */
    CppGenerator(String name, ArrayList<JFile> ilist, ArrayList<JRecord> rlist,
            File outputDirectory)
     {
        this.outputDirectory = outputDirectory;
        mName = (new File(name)).getName();
        mInclFiles = ilist;
        mRecList = rlist;
    }
    
    /**
     * Generate C++ code. This method only creates the requested file(s)
     * and spits-out file-level elements (such as include statements etc.)
     * record-level code is generated by JRecord.
     */
    void genCode() throws IOException {
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new IOException("unable to create output directory "
                        + outputDirectory);
            }
        }
        FileWriter cc = new FileWriter(new File(outputDirectory, mName+".cc"));
        FileWriter hh = new FileWriter(new File(outputDirectory, mName+".hh"));
        hh.write("#ifndef __"+mName.toUpperCase().replace('.','_')+"__\n");
        hh.write("#define __"+mName.toUpperCase().replace('.','_')+"__\n");
        
        hh.write("#include \"recordio.hh\"\n");
        for (Iterator<JFile> i = mInclFiles.iterator(); i.hasNext();) {
            JFile f = i.next();
            hh.write("#include \""+f.getName()+".hh\"\n");
        }
        cc.write("#include \""+mName+".hh\"\n");
        
        for (Iterator<JRecord> i = mRecList.iterator(); i.hasNext();) {
            JRecord jr = i.next();
            jr.genCppCode(hh, cc);
        }
        
        hh.write("#endif //"+mName.toUpperCase().replace('.','_')+"__\n");
        
        hh.close();
        cc.close();
    }
}
