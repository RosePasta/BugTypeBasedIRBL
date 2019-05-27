/*
 * Copyright 2011-2013 the original author or authors.
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
package org.springframework.data.hadoop.batch;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.hadoop.TestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests covering the job parameters/step scope functionality
 * in spring batch.
 * 
 * @author Costin Leau
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JobParamsTest {

	{
		TestUtils.hackHadoopStagingOnWin();
	}

	@Autowired
	ApplicationContext ctx;

	private JobParameters params;

	@Before
	public void setup() {
		Map<String, JobParameter> p = new LinkedHashMap<String, JobParameter>();
		p.put("mr.input", new JobParameter("/batch-param-test/input/"));
		p.put("mr.output", new JobParameter("/batch-param-test/output/"));
		p.put("properties-file", new JobParameter("dummy-1.properties"));
		params = new JobParameters(p);
	}

	@Test
	public void testJobMRJob() throws Exception {
		List<JobExecution> startJobs = JobsTrigger.startJobs(ctx, params);
		assertFalse(startJobs.isEmpty());

		// check records
		Collection<StepExecution> steps = startJobs.get(0).getStepExecutions();
		for (StepExecution stepExecution : steps) {
			if ("do-mr".equals(stepExecution.getStepName())) {
				assertTrue(stepExecution.getReadCount() > 0);
			}
		}
	}
}
