package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Scene {
    public RotationFigure rotationFigure;
    public Integer nearPlaneZ = 1500;
    public Integer farPlaneZ = 2000;
    public Double rotateX = 0.0;
    public Double rotateY = 0.0;

    public boolean isAutoChange = false;

    public Scene() {
        rotationFigure = new RotationFigure();
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    // Восстановление объекта из JSON
    public static Scene fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Scene.class);
    }

}
