package security;

import lombok.Data;

@Data
public class ProcessedItem {
    private String procElem;
    public ProcessedItem(Item item){
        this.procElem = item.getElem();
    }
}
