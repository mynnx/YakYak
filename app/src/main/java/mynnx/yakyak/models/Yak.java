package mynnx.yakyak.models;

/**
 * Created by mynnx on 8/20/14.
 */
public class Yak {
    private String prompt;
    private int id;
    private int color;

    public Yak (String prompt, int id, int color) {
        this.prompt = prompt;
        this.id = id;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
