package location.com.arrowlocator.models;

import java.io.Serializable;
import java.util.List;

public class PrimaryLocationArray implements Serializable {
    private List<PrimaryLocation> primaryLocationsList;

    public PrimaryLocationArray(List<PrimaryLocation> locations){
        this.primaryLocationsList = locations;
    }

    public List<PrimaryLocation> getPrimaryLocationsList() {
        return primaryLocationsList;
    }

    public void setPrimaryLocationsList(List<PrimaryLocation> primaryLocationsList) {
        this.primaryLocationsList = primaryLocationsList;
    }
}
