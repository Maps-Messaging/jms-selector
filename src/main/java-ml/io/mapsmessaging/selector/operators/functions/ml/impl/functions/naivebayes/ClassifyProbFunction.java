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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.naivebayes;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;

public class ClassifyProbFunction implements NaiveBayesFunction {

  @Override
  public double compute(NaiveBayes naiveBayes, Instance instance) throws ModelException {
    try {
      double[] distribution = naiveBayes.distributionForInstance(instance);
      double maxProbability = Double.NEGATIVE_INFINITY;
      for (double prob : distribution) {
        if (prob > maxProbability) {
          maxProbability = prob;
        }
      }
      return maxProbability;
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String getName() {
    return "classifyprob";
  }
}
