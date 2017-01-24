package app_kvServer;

class Node{
    String key;
    String value;
    Node pre;
    Node next;
 
    public Node(String key, String value){
        this.key = key;
        this.value = value;
    }
}