import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JavaClass {
    static {
        System.loadLibrary("arrow_ffi_poc");
    }

    private static native int rust_implementation(int arg1);
    
    public static void main(String[] args) {
        System.out.println("=== Arrow FFI Demo ===\n");
        
        // Test original Rust function
        System.out.println("1. Testing original Rust function:");
        int result = rust_implementation(42);
        System.out.println("   Result from Rust: " + result + "\n");
        
        // Create Arrow batch with user data
        System.out.println("2. Creating Arrow batch with user data:");
        try {
            createUserDataBatch();
        } catch (Exception e) {
            System.err.println("Error creating Arrow batch: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createUserDataBatch() throws IOException {
        // Create allocator for memory management
        try (RootAllocator allocator = new RootAllocator()) {
            
            // Define schema for user data
            List<Field> fields = Arrays.asList(
                Field.nullable("id", new ArrowType.Int(32, true)),
                Field.nullable("name", new ArrowType.Utf8()),
                Field.nullable("age", new ArrowType.Int(32, true)),
                Field.nullable("email", new ArrowType.Utf8()),
                Field.nullable("salary", ArrowType.Utf8.INSTANCE)
            );
            Schema schema = new Schema(fields);
            
            System.out.println("   Schema: " + schema);
            
            // Create vectors for each field
            try (IntVector idVector = new IntVector("id", allocator);
                 VarCharVector nameVector = new VarCharVector("name", allocator);
                 IntVector ageVector = new IntVector("age", allocator);
                 VarCharVector emailVector = new VarCharVector("email", allocator);
                 VarCharVector salaryVector = new VarCharVector("salary", allocator)) {
                
                // Sample user data
                String[] names = {"Alice Johnson", "Bob Smith", "Carol Williams", "David Brown", "Eva Davis", "Jagdish Parihar"};
                String[] emails = {"alice@example.com", "bob@company.org", "carol@tech.io", "david@startup.net", "eva@corp.com", "jatin6972@gmail.com"};
                int[] ages = {28, 34, 31, 45, 29, 26};
                String[] salaries = {"$75,000", "$85,000", "$92,000", "$110,000", "$68,000", "$50,000"};
                
                int rowCount = names.length;
                
                // Allocate memory for vectors
                idVector.allocateNew(rowCount);
                nameVector.allocateNew(rowCount * 20, rowCount); // Estimate 20 chars per name
                ageVector.allocateNew(rowCount);
                emailVector.allocateNew(rowCount * 25, rowCount); // Estimate 25 chars per email
                salaryVector.allocateNew(rowCount * 10, rowCount); // Estimate 10 chars per salary
                
                // Populate data
                for (int i = 0; i < rowCount; i++) {
                    idVector.set(i, i + 1);
                    nameVector.set(i, names[i].getBytes());
                    ageVector.set(i, ages[i]);
                    emailVector.set(i, emails[i].getBytes());
                    salaryVector.set(i, salaries[i].getBytes());
                }
                
                // Set value counts
                idVector.setValueCount(rowCount);
                nameVector.setValueCount(rowCount);
                ageVector.setValueCount(rowCount);
                emailVector.setValueCount(rowCount);
                salaryVector.setValueCount(rowCount);
                
                // Create vector schema root (batch)
                List<FieldVector> vectors = Arrays.asList(idVector, nameVector, ageVector, emailVector, salaryVector);
                try (VectorSchemaRoot batch = new VectorSchemaRoot(vectors)) {
                    // batch is used for allow ffi to read the data
                    batch.setRowCount(rowCount);
                    
                    System.out.println("   Created batch with " + batch.getRowCount() + " rows");
                    
                    // Print the data
                    System.out.println("   User Data:");
                    for (int i = 0; i < rowCount; i++) {
                        System.out.printf("     Row %d: ID=%d, Name='%s', Age=%d, Email='%s', Salary='%s'%n",
                            i,
                            idVector.get(i),
                            new String(nameVector.get(i)),
                            ageVector.get(i),
                            new String(emailVector.get(i)),
                            new String(salaryVector.get(i))
                        );
                    }
                    
                    
                    // // Serialize to bytes (for potential FFI transfer)
                    // ByteArrayOutputStream out = new ByteArrayOutputStream();
                    // try (ArrowStreamWriter writer = new ArrowStreamWriter(batch, null, out)) {
                    //     writer.start();
                    //     writer.writeBatch();
                    //     writer.end();
                    // }
                    
                    // byte[] serializedData = out.toByteArray();
                    // System.out.println("   Serialized batch size: " + serializedData.length + " bytes");
                    System.out.println("   ✓ Arrow batch created successfully!\n");
                }
            }
        }
    }
}
