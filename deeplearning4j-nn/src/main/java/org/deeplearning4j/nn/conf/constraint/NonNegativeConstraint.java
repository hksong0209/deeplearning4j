package org.deeplearning4j.nn.conf.constraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.ParamInitializer;
import org.deeplearning4j.nn.api.layers.LayerConstraint;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;

import java.util.Map;

/**
 * Constrain the weights to be non-negative
 *
 * @author Alex Black
 */
@AllArgsConstructor
@Data
public class NonNegativeConstraint implements LayerConstraint {

    protected boolean applyToWeights;
    protected boolean applyToBiases;

    public NonNegativeConstraint(){
        this(true, false);
    }

    @Override
    public void applyConstraint(Layer layer, int iteration, int epoch) {
        Map<String,INDArray> paramTable = layer.paramTable();
        if(paramTable == null || paramTable.isEmpty() ){
            return;
        }

        ParamInitializer i = layer.conf().getLayer().initializer();
        for(Map.Entry<String,INDArray> e : paramTable.entrySet()){
            if(applyToWeights && i.isWeightParam(e.getKey()) || applyToBiases && i.isBiasParam(e.getKey())){
                BooleanIndexing.replaceWhere(e.getValue(), 0.0, Conditions.lessThan(0.0));
            }
        }
    }

    @Override
    public LayerConstraint clone() {
        return new NonNegativeConstraint(applyToWeights, applyToBiases);
    }
}
