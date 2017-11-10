
public class CircularLinkedList {
	public int size = 0;
    public Node head = null;
    public Node tail = null;

    public void addNodeAtEnd(double data){
    		Node n = new Node(data);
    		tail.next = n;
    		tail=n;
    		tail.next = head;
    		size++;
    }

    public double elementAt(int index){
        if(index > size){
            return -1;
        }
        Node n = head;
        while(index-1 != 0){
            n = n.next;
            index--;
        }
        return n.data;
    }

    public int getSize(){
        return size;
    }
}

class Node{
    double data;
    Node next;
    public Node(double pm){
        this.data = pm;
    }
}