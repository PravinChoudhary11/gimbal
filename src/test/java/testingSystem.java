import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

public class testingSystem {
    
    public static void main(String []argu){
        
        File file = new File("c:\\SumanthJupudi");
        if(file.exists()){
            System.out.print("Path is Existing in the system");
        }else{
            System.out.println("Creating new Dir");
            boolean created = file.mkdir();
            if(created){
                System.out.println("file in the dir created");
            }else{
                System.out.println("System falied to create file.");
            }
        }
    }
}
