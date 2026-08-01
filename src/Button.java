public class Button{
    public int destination;
    public int[] pos;
    public int[] size;

    public Button(int _destination, int x, int y, int length, int width){
        destination = _destination;
        pos = new int[]{x, y};
        size = new int[]{length, width};
    }

    public boolean checkClick(int mouseX, int mouseY){
        return mouseX > pos[0] && mouseX < pos[0] + size[0] && mouseY > pos[1] && mouseY < pos[1] + size[1];
    }
}
