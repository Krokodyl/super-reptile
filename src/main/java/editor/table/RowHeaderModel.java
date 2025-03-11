package editor.table;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RowHeaderModel extends AbstractListModel {

    List<String> headers = new ArrayList<>();

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
    
    @Override
    public int getSize() {
        return headers.size();
    }

    @Override
    public Object getElementAt(int index) {
        return headers.get(index );
    }
}
