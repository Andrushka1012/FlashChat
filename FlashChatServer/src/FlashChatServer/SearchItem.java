package FlashChatServer;

public class SearchItem {
    private String name;
    private String id;
    private String imageSrc;
    private boolean online;

    public SearchItem(String name, String id, boolean online,String imageSrc) {
        this.name = name;
        this.id = id;
        this.online = online;
        this.imageSrc = imageSrc;
    }
}
