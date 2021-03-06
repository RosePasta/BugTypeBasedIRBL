/*
 * Copyright 2013 the original author or authors.
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

package org.springframework.data.hadoop.store.dataset;

import org.junit.runner.RunWith;
import org.kitesdk.data.PartitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"DatasetTemplateTests-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DatasetTemplatePartitioningParquetTests extends AbstractDatasetTemplatePartitioningTests {

	@Autowired
	public void setDatasetOperations(DatasetOperations datasetOperations) {
		PartitionStrategy partitionStrategy =
				new PartitionStrategy.Builder().year("birthDate").month("birthDate").build();
		((DatasetTemplate) datasetOperations).setDatasetDefinitions(Arrays.asList(
				new DatasetDefinition[]{new DatasetDefinition(SimplePojo.class, "parquet", partitionStrategy)}));
		this.datasetOperations = datasetOperations;
	}

}
