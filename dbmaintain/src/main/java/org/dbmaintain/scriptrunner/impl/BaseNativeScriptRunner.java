/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dbmaintain.scriptrunner.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.dbsupport.DbSupport;
import org.dbmaintain.script.Script;
import org.dbmaintain.scriptrunner.ScriptRunner;
import org.dbmaintain.util.DbMaintainException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static org.dbmaintain.util.FileUtils.createFile;

/**
 * Implementation of a script runner that uses the db's native
 * command line support, e.g. Oracle's SQL plus.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseNativeScriptRunner implements ScriptRunner {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseNativeScriptRunner.class);

    protected DbSupport defaultDbSupport;
    protected Map<String, DbSupport> nameDbSupportMap;


    public BaseNativeScriptRunner(DbSupport defaultDbSupport, Map<String, DbSupport> nameDbSupportMap) {
        this.defaultDbSupport = defaultDbSupport;
        this.nameDbSupportMap = nameDbSupportMap;
    }

    public void initialize() {
        // override to perform extra initialization
    }

    public void close() {
        // override to perform extra finalization
    }

    /**
     * Executes the given script.
     *
     * @param script The script, not null
     */
    public void execute(Script script) {
        try {
            // Define the target database on which to execute the script
            DbSupport targetDbSupport = getTargetDatabaseDbSupport(script);
            if (targetDbSupport == null) {
                logger.info("Script " + script.getFileName() + " has target database " + script.getTargetDatabaseName() + ". This database is disabled, so the script is not executed.");
                return;
            }

            File scriptFile = createTemporaryScriptFile(script);
            executeScript(scriptFile, targetDbSupport);

        } catch (Exception e) {
            throw new DbMaintainException("Error executing script " + script.getFileName(), e);
        }
    }


    protected abstract void executeScript(File scriptFile, DbSupport targetDbSupport) throws Exception;


    protected File createTemporaryScriptFile(Script script) throws IOException {
        File temporaryScriptsDir = createTemporaryScriptsDir();
        File temporaryScriptFile = new File(temporaryScriptsDir, currentTimeMillis() + script.getFileNameWithoutPath());
        temporaryScriptFile.deleteOnExit();

        Reader scriptContentReader = script.getScriptContentHandle().openScriptContentReader();
        try {
            createFile(temporaryScriptFile, scriptContentReader);
        } finally {
            scriptContentReader.close();
        }
        return temporaryScriptFile;
    }

    protected File createTemporaryScriptsDir() {
        String tempDir = System.getProperty("java.io.tmpdir");
        File temporaryScriptsDir = new File(tempDir, "dbmaintain");
        temporaryScriptsDir.mkdirs();
        return temporaryScriptsDir;
    }

    protected DbSupport getTargetDatabaseDbSupport(Script script) {
        if (script.getTargetDatabaseName() == null) {
            return defaultDbSupport;
        }
        if (!nameDbSupportMap.containsKey(script.getTargetDatabaseName())) {
            throw new DbMaintainException("Error executing script " + script.getFileName() + ". No database initialized with the name " + script.getTargetDatabaseName());
        }
        return nameDbSupportMap.get(script.getTargetDatabaseName());
    }
}