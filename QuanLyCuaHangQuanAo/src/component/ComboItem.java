package component;

public class ComboItem {
	 private String value;
     private String label;

     public ComboItem(String value, String label) {
         this.value = value;
         this.label = label;
     }

     public String getValue() { return value; }
     public String getLabel() { return label; }

     @Override
     public String toString() { return label; }
 

}
