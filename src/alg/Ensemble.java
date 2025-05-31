package alg;

import java.util.ArrayList;
import java.util.List;

public class Ensemble {

    private final List<Backprop> models;
    private int numModels;

    public Ensemble(int numModels) {
        this.numModels = numModels;
        this.models = new ArrayList<>();
    }

    public void addModel(Backprop model) {
        models.add(model);
    }

    public double predict(double x) {
        double sum = 0.0;
        for (Backprop model : models) {
            sum += model.predict(x);
        }
        return sum / models.size();
    }
}