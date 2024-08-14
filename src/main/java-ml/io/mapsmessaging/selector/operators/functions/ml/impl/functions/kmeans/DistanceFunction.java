/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.kmeans;

import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

public class DistanceFunction implements KMeansFunction{

  @Override
  public double compute(SimpleKMeans kmeans, Instance instance) throws Exception {
    Instances centroids = kmeans.getClusterCentroids();
    EuclideanDistance distanceFunction = new EuclideanDistance(centroids);
    Instance centroid = centroids.instance(kmeans.clusterInstance(instance));
    return distanceFunction.distance(centroid, instance);
  }

  @Override
  public String getName() {
    return "distance";
  }

}