package deserialization;

import java.util.List;

public class Ingredients {

    private boolean success;
    private List<Data> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Data> getData() {
        return data;
    }

    public void setDates(List<Data> data) {
        this.data = data;
    }

}
