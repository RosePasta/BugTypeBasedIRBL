/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.hive;

import java.util.Collection;

import org.apache.hadoop.hive.service.HiveClient;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.HadoopException;
import org.springframework.util.Assert;

/**
 * Hive tasklet running Hive scripts on demand, against a {@link HiveClient}.
 * 
 * @author Costin Leau
 */
public class HiveTasklet implements InitializingBean, Tasklet {

	private HiveClient hive;
	private Collection<Resource> scripts;

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(hive, "A HiveClient instance is required");
		Assert.notEmpty(scripts, "At least one script needs to be specified");
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Exception exc = null;

		try {
			HiveScriptRunner.run(hive, scripts);
			return RepeatStatus.FINISHED;
		} catch (Exception ex) {
			exc = ex;
		}

		throw new HadoopException("Cannot execute Hive script(s)", exc);
	}

	/**
	 * Sets the scripts to be executed by this tasklet.
	 * 
	 * @param scripts The scripts to set.
	 */
	public void setScripts(Collection<Resource> scripts) {
		this.scripts = scripts;
	}

	/**
	 * Sets the hive client for this tasklet.
	 *  
	 * @param hive HiveClient to set
	 */
	public void setHiveClient(HiveClient hive) {
		this.hive = hive;
	}
}