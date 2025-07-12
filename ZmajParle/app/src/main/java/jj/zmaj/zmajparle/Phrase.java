package jj.zmaj.zmajparle;

public class Phrase {
    private int id;
    private String text;
    private String type; // Subject, Verb, Object, Modifier
    private String tags; // comma-separated
    private boolean favorite;

    public Phrase(int id, String text, String type, String tags, boolean favorite) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.tags = tags;
        this.favorite = favorite;
    }

    // For creating a new Phrase before saving (id = -1)
    public Phrase(String text, String type, String tags, boolean favorite) {
        this(-1, text, type, tags, favorite);
    }

    // Getters and setters
    public int getId() { return id; }
    public String getText() { return text; }
    public String getType() { return type; }
    public String getTags() { return tags; }
    public boolean isFavorite() { return favorite; }

    public void setId(int id) { this.id = id; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
